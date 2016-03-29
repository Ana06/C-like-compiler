package lenguaje;

import java.util.LinkedList;
import java_cup.runtime.ComplexSymbolFactory.Location;
import exc.GestionExcepciones;
import lexico.UnidadesLexicas;

public class ExpresionSimple {
	protected ExpresionSimple exp1 = null; //primer operando
	protected ExpresionSimple exp2 = null; //segundo operando
	protected boolean parteDerecha = false; //Si estamos en la parte derecha de una asignación
	protected Location l; //para dar la línea y columna en los errores
	
	public ExpresionSimple(ExpresionSimple e, ExpresionSimple c) {
		this.exp1 = e;
		this.exp2 = c;
	}

	public ExpresionSimple(ExpresionSimple e, ExpresionSimple c, Location l) {
		this.exp1 = e;
		this.exp2 = c;
		this.l = l;
	}

	private ExpresionSimple(ExpresionSimple e, Location l) {
		this.exp1 = e;
		this.l = l;
	}
	
	private ExpresionSimple(ExpresionSimple e) {
		this.exp1 = e;
	}

	public ExpresionSimple() {
	}
	
	public String getOp(Visitante visitante){
		return "or";
	}

	public static class ExpresionConj extends ExpresionSimple {

		public ExpresionConj(ExpresionSimple c, ExpresionSimple eur) {
			super(c, eur);
		}
		
		public String getOp(Visitante v){
			return "and";
		}

	}

	public static class ExpresionUnariaORelacional extends ExpresionSimple {
		
		public ExpresionUnariaORelacional(ExpresionSimple eur, Location l) {
			super(eur, l);
		}

		public Tipo accept(Visitante v) throws Exception {
			//No hacen falta el previsit  porque no hay que hacer nada
			Tipo tipo1 = exp1.accept(v);
			Tipo tipoBool = new Tipo(Tipo.BOOL);
			v.postVisit(this);
			if (tipo1.equals(tipoBool))
				return tipoBool;
			GestionExcepciones.gestionaError(
					"Error: ! operator was applied to a non boolean expression in line " + l.getLine() + ", column "+ l.getColumn());
			return new Tipo(); // Tipo vacio para que se pueda seguir con la compilación al haber error
		}
		
		public String getOp(Visitante visitante){
			return "not";
		}
	}

	public static class ExpresionRelacional extends ExpresionSimple {
		private int oprelacional = -1;

		public ExpresionRelacional(ExpresionSimple ea1, int oprelacional,
				ExpresionSimple ea2, Location l) {
			super(ea1, ea2);
			this.oprelacional = oprelacional;
		}
		
		public Tipo accept(Visitante v) throws Exception {
			//No hacen falta el previsit porque no hay que hacer nada
			Tipo tipo1 = exp1.accept(v);
			Tipo tipo2 = exp2.accept(v);
			v.postVisit(this);
			if (tipo1.equals(tipo2))
				return new Tipo(Tipo.BOOL);
			GestionExcepciones.gestionaError("Error: inequality operator was applied to two expressions with different types in line " + l.getLine() + ", column " + l.getColumn());
			return new Tipo();
		}
		
		public String getOp(Visitante visitante){
			switch(oprelacional){
			case UnidadesLexicas.GREATER: return "grt";
			case UnidadesLexicas.LESS: return "les";
			case UnidadesLexicas.GREATEREQUAL: return "geq";
			case UnidadesLexicas.LESSEQUAL: return "leq";
			case UnidadesLexicas.EQUALEQUAL: return "equ";
			case UnidadesLexicas.DISTINCT: return "neq";
			}
			return "";
		}
	}

	public static class ExpresionAritmetica extends ExpresionSimple {
		protected int op = -1;

		public ExpresionAritmetica(ExpresionSimple ea, int opSum,
				ExpresionSimple t, Location l) {
			super(ea, t, l);
			this.op = opSum;
		}
		
		public ExpresionAritmetica(ExpresionSimple ea, int opSum,
				ExpresionSimple t) {
			super(ea, t);
			this.op = opSum;
		}

		public Tipo accept(Visitante v) throws Exception {
			//No hacen falta el previsit porque no hay que hacer nada
			Tipo tipo1 = exp1.accept(v);
			Tipo tipo2 = exp2.accept(v);
			Tipo tInt = new Tipo(Tipo.INT);
			v.postVisit(this);
			if (tipo1.equals(tInt)&&tipo2.equals(tInt))
				return tInt;
			GestionExcepciones.gestionaError(
					"Error: Non integer type inside an arithmetic expression in line " +l.getLine() + ", column " +l.getColumn());
			return new Tipo();
		}
		
		public String getOp(Visitante visitante){
			switch(op){
			case UnidadesLexicas.SUM: return "add";
			case UnidadesLexicas.SUB: return "sub";
			}
			return "";
		}
	}

	public static class Termino extends ExpresionAritmetica { 

		public Termino(ExpresionSimple t, int opMul, ExpresionSimple eu) {
			super(t, opMul, eu);
		}
		
		public String getOp(Visitante v){
			switch(op){
			case UnidadesLexicas.PROD: return "mul";
			case UnidadesLexicas.DIV: return "div";
			case UnidadesLexicas.MOD:{
				try {
					exp1.accept(v);
					exp2.accept(v);
				} catch (Exception e) {
					//No hacer nada,esta instrucción ya se ha impreso en otro sitio
				}
				return "div;\n\tmul;\n\tsub";			
			}
			}
			return "";
		}
	}

	public static class Factor extends ExpresionSimple { //clase auxiliar para que la gramática quede más clara

		public Factor(ExpresionSimple m) {
			super(m);
		}
	}
	
	public static class Modificable extends ExpresionSimple {
		private Identificador i;
		private LinkedList<ExpresionSimple> le;

		public Modificable(Identificador i) {
			this.i = i;
		}

		public Modificable(Identificador i, ExpresionSimple e) {
			super(e);
			this.i = i;
		}

		public Modificable(Identificador i, LinkedList<ExpresionSimple> le) {
			this.le=le;
			this.i = i;
		}

		public Modificable(Identificador i, LinkedList<ExpresionSimple> le,	ExpresionSimple m2) {
			super(m2);
			this.le=le;
			this.i = i;
		}
		
		public String toString() {
			return i.toString();
		}

		public Tipo accept(Visitante v) throws Exception {
			v.preVisit(this);
			VariableParametro var = (VariableParametro) v.getTabla().busca_id_arbol(i.toString());
			Tipo tipoVar =var.getTipo();
			String tipoVarString =tipoVar.traduceTipo();
			if(!parteDerecha && var.esConstante()){
				GestionExcepciones.gestionaError("Error:Assigment of the constant variable "+i+" in line "+i.getLine()+", column "+i.getColumn() + ".");
			}
			v.postVisit(this);
			return acceptAux(var.getNumCorchetes(),tipoVar,tipoVarString,v);
		}
		
		public Tipo acceptStruct(Tipo tipo,Visitante v) throws Exception{
			Tipo tipoReal = tipo.esta(i.toString());
			if(tipoReal == null) {
				GestionExcepciones.gestionaError("Error: Access to a non existing member " +i+ " of a struct in line "+i.getLine()+", column "+i.getColumn() + ".");
				return new Tipo();}
			if(!parteDerecha && tipoReal.esConstante()){
				GestionExcepciones.gestionaError("Error:Assigment of the constant variable "+i+" in line "+i.getLine()+", column "+i.getColumn() + ".");
				return new Tipo();
			}
			v.postVisitEspecial(tipo.getDesp(toString()));
			return acceptAux(tipo.numDim(), tipoReal, tipoReal.traduceTipo(), v);
		}
		
		public Tipo acceptAux(int numDim, Tipo tipo, String tipoVarString,Visitante v) throws Exception{
			if (exp1 != null && !tipoVarString.equals("Struct")) 
				GestionExcepciones.gestionaError("Error:Invalid access to the non struct variable " +i+ " in line "+i.getLine()+", column "+i.getColumn() + ".");
			else if(exp1 == null && tipoVarString.equals("Struct"))
				GestionExcepciones.gestionaError("Error:Invalid access to the struct variable " +i+ " in line "+i.getLine()+", column "+i.getColumn() + ".");
			else if(exp1 != null && tipoVarString.equals("Struct")) return ((Modificable)exp1).acceptStruct(tipo,v);
			return tipo;
		}
		
		public Identificador getId(){
			return i;
		}

		public boolean esParteDerecha() {
			return parteDerecha;
		}

		public Modificable copia() {
			return new Modificable(i,le,exp1);
		}
		
		public LinkedList<ExpresionSimple> getLe(){
			return le;
		}

	}

	public static class Llamada extends ExpresionSimple {
		private Identificador i;
		private LinkedList<ExpresionSimple> listaArgs;

		public Llamada(Identificador i, LinkedList<ExpresionSimple> la) {
			this.i = i;
			this.listaArgs = la;
		}

		public String toString() {
			return i.toString();
		}

		public Tipo accept(Visitante v) throws Exception {
			v.preVisit(this);
			DeclaracionFuncion df = (DeclaracionFuncion) v.getTabla().busca_id_arbol(i.toString());
			LinkedList<Tipo> listaTipos = df.getListaParametros();
			if((listaArgs == null && listaTipos != null) || (listaArgs != null && listaTipos == null) || listaArgs.size() != listaTipos.size()){
				GestionExcepciones.gestionaError("Error: incompatible number of arguments in function " + i.toString() + " in line " + i.getLine() + ", column " + i.getColumn() );
				return df.getTipo();
			}
			for(int j=0; j<listaArgs.size(); j++){
				if (!listaArgs.get(j).accept(v).equals(listaTipos.get(j)))
					GestionExcepciones.gestionaError("Error: incompatible type of arguments in function " + i.toString() + " in line " + i.getLine() + ", column " + i.getColumn() );
			}
			v.postVisit(this);
			return df.getTipo();
		}
		
		public Identificador getId(){
			return i;
		}
	}

	public static class Constante extends ExpresionSimple {
		private boolean cBool;
		private int cInt;
		private Integer tipo;


		public Constante(boolean c) {
			this.cBool = c;
			tipo = Tipo.BOOL;
		}

		public Constante(Integer c) {
			this.cInt = c;
			tipo = Tipo.INT;
		}
		
		public String toString(){
			switch(tipo){
			case Tipo.BOOL: if (cBool == true) return "true"; else return "false";
			case Tipo.INT: return ((Integer) cInt).toString();
			}
			return null;
		}
		
		public Tipo accept(Visitante v) throws Exception {
			//No hace falta el previsit porque no hay que hacer nada
			v.postVisit(this);
			return new Tipo(tipo);
		}
	}

	public Tipo accept(Visitante v) throws Exception {
		//No hacen falta el previsit porque no hay que hacer nada
		Tipo tipo1 = exp1.accept(v);
		Tipo tipo2 =exp2.accept(v);
		Tipo tipoBool = new Tipo(Tipo.BOOL);
		v.postVisit(this);
		if (tipo1.equals(tipoBool) && tipo2.equals(tipoBool))
			return tipoBool;
		GestionExcepciones.gestionaError("Error: Non boolean expression inside OR or AND in line " + l.getLine() + ", column " + l.getColumn());
		return new Tipo();
		
	}
	
	public void parteDerecha(boolean b){
		parteDerecha = b;
	}


}
