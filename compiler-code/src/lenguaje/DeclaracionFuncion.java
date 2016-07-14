package lenguaje;

import java.util.LinkedList;

import exc.GestionExcepciones;
import lenguaje.Identificador;
import lenguaje.Instruccion.InstCompuesta;
import lenguaje.Instruccion.InstRetorno;

public class DeclaracionFuncion extends VariableParametro {

	private Parametros parametros; //parámetros de la función
	private Identificador id; //nombre de la función
	private Tipo tipo = null; // != null sii la funcion no es VOID
	private InstRetorno retorno = null; // != null sii la funcion no es VOID
	private InstCompuesta codigo; //código de la función
	private int sigEtiquetaFun = 0; //Pala generación de etiquetas, para saltar funciones anidadas
	
	public DeclaracionFuncion(Tipo tipo, Parametros p, Identificador identificador, InstCompuesta ic, InstRetorno ins) {
		this.parametros = p;
		this.id = identificador;
		this.retorno = ins;
		this.codigo = ic;
		this.tipo=tipo;
		ic.setProvieneFuncion(true);
	}

	public DeclaracionFuncion(Parametros p, Identificador ident, InstCompuesta ic) {
		this.parametros = p;
		this.id = ident;
		this.codigo = ic;
		
		ic.setProvieneFuncion(true);
	}

	public void accept(Visitante v) throws Exception {
		v.preVisit(this);
		parametros.accept(v);
		codigo.accept(v);
		if(tipo!=null){
			Tipo tipoRet= retorno.accept(v);
			if(!tipo.equals(tipoRet)) GestionExcepciones.gestionaError("ERROR: Returned type don't match the function type in function "+id+" in line "+id.getLine()+", column "+id.getColumn());
		}
		v.postVisit(this);
			
	}

	public String tipo() {
		if (tipo == null) return "Void";
		else return this.tipo.traduceTipo();
	}
	
	public String toString(){
		return id.toString();
	}

	public Tipo getTipo() {
		return tipo;
	}

	public int getTamañoEstatico() {
		return parametros.getTamaño() + codigo.getTamañoDecls();
	}
	
	public int getTamañoParametros() {
		return parametros.getTamaño();
	}

	public Identificador getId() {
		return id;
	}

	public void setEtiquetaFun(int sigEtiquetaFun) {
		this.sigEtiquetaFun = sigEtiquetaFun;
	}
	
	public int getEtiquetaFun(){
		return sigEtiquetaFun;
	}
	
	public boolean esVoid(){
		return tipo == null;
	}

	public LinkedList<Tipo> getListaParametros() {
		return parametros.getListaTipos();
	}

}
