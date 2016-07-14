package lenguaje;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.LinkedList;

import exc.GestionExcepciones;
import tabla.TablaSimbolos;
import lenguaje.VariableParametro.Parametro;
import lenguaje.VariableParametro.Variable;
import lenguaje.ExpresionSimple.*;
import lenguaje.Instruccion.*;

public class Visitante {

	private TablaSimbolos tabla = new TablaSimbolos();
	private int contador = 1;
	private String arbol = "";
	private StringWriter writer;
	private int sigEtiqueta = 0;
	private int sigEtiquetaFun = 0;
	private String start;

	public Visitante(String start) {
		writer = new StringWriter();
		this.start=start;
	}

	public void flush() {
		writer.flush();
	}

	public TablaSimbolos getTabla() {
		return tabla;
	}

	public String getArbol() {
		return arbol;
	}
	
	public void añadeTabYGuarda(String x) {
		contador++;
		for (int i = 0; i < contador; i++)
			arbol += "\t";
		arbol += x;
		arbol += "\n";
	}

	public void quitaTab() {
		contador--;
	}

	public void preVisit(Programa p) {
		añadeTabYGuarda("Programa");
		// CÓDIGO
		writer.write("\tssp 5;\n");
		writer.write("\tsep 50;\n");
	}
	
	public void visit(Programa p){
		// CÓDIGO
		writer.write("\tujp _begin;\n");
	}

	public void postVisit(Programa p) {
		quitaTab();

		// CÓDIGO
		writer.write("_begin:\n\tmst 0;\n");
		writer.write("\tcup 0 "+start+";\n");
		writer.write("\tstp;");
	}

	public void preVisit(ListaVariables iv) {
		añadeTabYGuarda("Lista inicializacion");
	}

	public void postVisit(ListaVariables iv) {
		quitaTab();
	}

	public void preVisit(Tipo tipo) {
		añadeTabYGuarda(tipo.toString());
	}

	public void postVisit(Tipo tipo) {
		quitaTab();
	}

	public void preVisit(Variable iv) throws Exception {
		contador++;// Queremos que salgan tabuladas todas las variables del
					// mismo tipo
		tabla.introduce_id(iv.toString(), iv.getId().getLine(), iv.getId()
				.getColumn(), iv, false);
		añadeTabYGuarda(iv.toString());
	}

	public void postVisit(Variable iv) {
		quitaTab();
		contador--;
	}

	public void preVisit(DeclaracionFuncion df) throws Exception {
		añadeTabYGuarda("Funcion" + df.tipo());
		tabla.introduce_id(df.toString(), df.getId().getLine(), df.getId()
				.getColumn(), df, true);
		tabla.nuevo_bloque();

		// CÓDIGO
		int prof = tabla.getProf(df.toString());
		if (df.toString().equals(start) && prof == 0)
		writer.write(df.toString()+":\n\tssp " + (5 + df.getTamañoEstatico()) + ";\n");
		else
			writer.write(df.toString()+sigEtiquetaFun +":\n\tssp " + (5 + df.getTamañoEstatico()) + ";\n");
		df.setEtiquetaFun(sigEtiquetaFun);
		writer.write("\tsep :n_" + df.toString() + ";\n");
		sigEtiquetaFun++;
	}

	public void postVisit(DeclaracionFuncion df) {
		tabla.quita_bloque();
		quitaTab();
		
		// CÓDIGO
		if (df.esVoid())
			writer.write("\tretp;\n");
	}

	public void preVisit(Instruccion i) {
		añadeTabYGuarda("Instruccion" + i.tipo());
	}

	public void preVisit(InstIteracion i) {
		añadeTabYGuarda("Instruccion" + i.tipo());
		// CODIGO
		writer.write("e" + sigEtiqueta + ":\n");
		i.setEtiquetaCond(sigEtiqueta);
		sigEtiqueta++;
	}

	public void postVisit(InstIteracion i) {
		quitaTab();
		// CODIGO
		writer.write("\tujp e" + i.getEtiquetaCond() + ";\n");
		writer.write("e" + i.getEtiquetaFin() + ":\n");
	}

	public void postVisit(InstRetorno i) {
		quitaTab();
		// CODIGO
		if (i.esVoid())
			writer.write("\tretp;\n");
		else { // Si no es void, hay que guardar el resultado antes en la
				// primera
				// posición del marco
			writer.write("\tstr 0 0;\n");
			writer.write("\tretf;\n");
		}

	}

	public void postVisit(Instruccion i) {
		quitaTab();
	}

	public void preVisit(InstCompuesta i) {
		añadeTabYGuarda("InstruccionCompuesta");
		if (!i.getProvieneFuncion()) {
			tabla.nuevo_bloque();

			// CÓDIGO
			writer.write("\tmst 0;\n");
			writer.write("\tcup 0 e" + sigEtiqueta + ";\n");
			sigEtiqueta++;
			writer.write("\tujp e" + sigEtiqueta + ";\n");
			i.setEtiqueta(sigEtiqueta);
			writer.write("e" + (sigEtiqueta - 1) + ":\n" + "\tssp "
					+ (5 + i.getTamañoDecls()) + ";\n");
			writer.write("\tsep :X;\n");
			sigEtiqueta++;
		}
	}

	public void postVisit(InstCompuesta i) {
		quitaTab();
		if (!i.getProvieneFuncion()) {
			tabla.quita_bloque();
			writer.write("\tretp;\n");
			writer.write("e" + i.getEtiqueta() + ":\n");
		}
	}

	public void preVisitIf(InstSeleccion i) {
		añadeTabYGuarda("InstruccionSeleccionIF");
	}

	public void preVisitElse(InstSeleccion i) {
		añadeTabYGuarda("InstruccionSeleccionELSE");
		// CODIGO
		writer.write("\tujp e" + sigEtiqueta + ";\n");
		writer.write("e" + i.getEtiquetaIf() + ":\n");
		i.setEtiquetaElse(sigEtiqueta);
		sigEtiqueta++;
	}

	public void preVisit(Parametros parametros) {
		añadeTabYGuarda("Parametros");
	}

	public void postVisit(Parametros parametros) {
		quitaTab();
	}

	public void preVisit(Parametro p) throws Exception {
		contador++;// Queremos que salgan tabuladas todas las variables del
					// mismo tipo
		tabla.introduce_id(p.toString(), p.getId().getLine(), p.getId()
				.getColumn(), p, false);
		añadeTabYGuarda(p.toString());
	}

	public void postVisit(Parametro parametro) {
		quitaTab();
		contador--;
	}

	public void preVisit(Expresion expresion) {
		añadeTabYGuarda("Expresion");
	}

	public void postVisit(Expresion expresion) throws Exception {
		writer.write("\t" + expresion.getCode(this) + ";\n");
	}

	public void preVisit(Modificable m) throws Exception {
	}

	public void postVisit(Modificable m) throws Exception {
		// CODIGO
		writer.write("\tlda " + tabla.getDifAnidamiento(m.toString())
				+ " " + tabla.getPosRelativa(m.toString()) + ";\n");	
		
		LinkedList<ExpresionSimple> indices = m.getLe();
		if (indices != null) {// Si es una matriz
			VariableParametro varPar =((VariableParametro) tabla.busca_id_arbol(m.toString()));
			LinkedList<Integer> listaDim = varPar.getListaDim();
			// El siguiente bucle calcula el tamaño total, y será de ayuda para obtener los d^i
			int tamaño = 1;
			for(int i: listaDim) { tamaño *= i; }
			for(int i=0; i<indices.size(); i++){
				indices.get(i).accept(this);
				writer.write("\tchk 0 " + (listaDim.get(i)-1) + ";\n");
				tamaño /= listaDim.get(i);
				writer.write("\tixa " + tamaño*varPar.size() + ";\n"); // ixa g*d^i, g=1. 
			}
			writer.write("\tdec 0;\n"); // g*d = 0
		}
		if (m.esParteDerecha())
			writer.write("\tind;\n");
	}

	public void preVisit(Llamada l) throws Exception {
		tabla.busca_id(l.toString(), l.getId().getLine(), l.getId().getColumn());
		// CÓDIGO
		writer.write("\tmst " + tabla.getDifAnidamiento(l.toString()) + ";\n");
	}

	public void postVisit(Llamada llamada) throws Exception {
		DeclaracionFuncion df = (DeclaracionFuncion) tabla
				.busca_id_arbol(llamada.toString());
		// CÓDIGO
		writer.write("\tcup " + df.getTamañoParametros() + " " + df.toString()+df.getEtiquetaFun()
				+ ";\n");
	}

	public void postVisit(Constante constante) {
		// CODIGO
		writer.write("\tldc " + constante.toString() + ";\n");
	}

	public void postVisit(ExpresionSimple expresionSimple) {
		// CODIGO
		writer.write("\t" + expresionSimple.getOp(this) + ";\n");

	}

	public void visit(InstSeleccion instSeleccion) {
		// CODIGO
		writer.write("\tfjp e" + sigEtiqueta + ";\n");
		instSeleccion.setEtiquetaIf(sigEtiqueta);
		sigEtiqueta++;
	}

	public void postVisitIf(InstSeleccion instSeleccion) {
		// CODIGO
		writer.write("e" + instSeleccion.getEtiquetaIf() + ":\n");

	}

	public void posVisitElse(InstSeleccion instSeleccion) {
		// CODIGO
		writer.write("e" + instSeleccion.getEtiquetaElse() + ":\n");

	}

	public void visit(InstIteracion instIteracion) {
		// CODIGO
		writer.write("\tfjp e" + sigEtiqueta + ";\n");
		instIteracion.setEtiquetaFin(sigEtiqueta);
		sigEtiqueta++;
	}
	
	public void escribeStr(int posRelativa){
		writer.write("\tstr 0 " + posRelativa + ";\n");
	}
	
	public String getCodigoEtiquetas(){
		return writer.toString();
	}

	public String getCodigoSinEtiquetas() throws IOException {
		Hashtable<String, Integer> et = new Hashtable<String, Integer>();
		String[] codigo = writer.toString().split("\n");
		int linea = 0;
		String res = "";

		// Recorremos buscando y guardando las etiquetas y sus líneas asociadas
		for (String i : codigo) {
			if (i.endsWith(":")) {
				et.put(i.substring(0, i.length() - 1), linea);
			} else
				linea++;
		}

		// Sustituimos las etiquetas por las referencias
		for (String i : codigo) {
			if (!i.endsWith(":")) {
				if (i.startsWith("\tujp") || i.startsWith("\tfjp")
						|| i.startsWith("\tcup")) {
					String[] parte = i.split(" ");
					for (int j = 0; j < parte.length - 1; j++)
						res += parte[j] + " ";
					Integer line = et.get(parte[parte.length - 1].substring(0,
							parte[parte.length - 1].length() - 1));
					if (line != null)
						res += line + ";\n";
					else{
						GestionExcepciones.gestionaError("Error: The starting function of the program doesn't exist");
						res += "main_function \n";
					}
				} else if (i.startsWith("\tsep :")) {
					String[] parte = i.split(" ");
					res += parte[0] + " 40;\n";
				}

				else
					res += i + "\n";
			}
		}
		
		return res;

	}

	public int escribeUjp() {
		writer.write("\tujp e" + sigEtiqueta + ";\n");
		sigEtiqueta++;
		return sigEtiqueta-1;
	}

	public void escribeEtiqueta(int etiqueta) {
		writer.write("e" + etiqueta +":\n");	
	}

	public void postVisitEspecial(int desp) {
		writer.write("\tinc "+ desp +";\n");
		
	}

}
