package lenguaje;

import java.util.LinkedList;

import exc.GestionExcepciones;
import lenguaje.ExpresionSimple;

public class Instruccion {

	public static class InstCompuesta extends Instruccion {

		private ListaDecVarFun ld; //Declaraciones de variables o funciones anidadas
		private LinkedList<Instruccion> li; //instrucciones dentro de una instrucción compuesta
		private boolean provieneFuncion = false; //Si proviene de función, se usa para generar código
		private int etiqueta; //para generar los saltos y que retorne del ámbito de la instrucción compuesta

		public InstCompuesta(ListaDecVarFun l, LinkedList<Instruccion> li) {
			this.ld = l;
			this.li = li;
		}

		public InstCompuesta(LinkedList<Instruccion> li) {
			this.li = li;
		}

		public Tipo accept(Visitante v) throws Exception {
			v.preVisit(this);
			ld.accept(v);
			for (Instruccion i : li)
				i.accept(v);
			v.postVisit(this);
			return null;
		}

		public String tipo() {
			return "Compuesta";
		}

		public void setProvieneFuncion(boolean b) {
			provieneFuncion = b;
		}

		public boolean getProvieneFuncion() {
			return provieneFuncion;
		}

		public int getTamañoDecls() {
			return ld.tam();
		}

		public void setEtiqueta(int sigEtiqueta) {
			etiqueta = sigEtiqueta;
		}
		
		public int getEtiqueta() {
			return etiqueta;
		}

	}

	public static class InstExpresion extends Instruccion {

		Expresion expr;//Expresión

		public InstExpresion(Expresion e) {
			this.expr = e;
		}

		public String tipo() {
			return "Asignacion";
		}

		public Tipo accept(Visitante v) throws Exception {
			v.preVisit(this);
			expr.accept(v);
			v.postVisit(this);
			return null;
		}
	}

	public static class InstSeleccion extends Instruccion {
		ExpresionSimple cond; //Condición para selección la rama
		Instruccion iIf; //Rama if
		Instruccion iElse = null; //Podria no haber rama else
		private int etiquetaIf, etiquetaElse; //para generar los saltos y que retorne del ámbito

		public InstSeleccion(ExpresionSimple c, Instruccion i) {
			cond = c;
			iIf = i;
		}

		public InstSeleccion(ExpresionSimple c, Instruccion i1, Instruccion i2) {
			cond = c;
			iIf = i1;
			iElse = i2;
		}

		public String tipo() {
			return "Seleccion";
		}

		public Tipo accept(Visitante v) throws Exception {
			v.preVisitIf(this);
			if (!cond.accept(v).equals(new Tipo(Tipo.BOOL)))
				GestionExcepciones.gestionaError(
						"Error: If condition is not a boolean expression.");
			v.visit(this);
			iIf.accept(v);
			if (iElse != null) {
				v.preVisitElse(this);
				iElse.accept(v);
				v.posVisitElse(this);
			}
			else v.postVisitIf(this);
			return null;
		}

		public void setEtiquetaIf(int sigEtiqueta) {
			etiquetaIf=sigEtiqueta;			
		}
		
		public int getEtiquetaIf(){
			return etiquetaIf;
		}
		
		public void setEtiquetaElse(int sigEtiqueta) {
			etiquetaElse=sigEtiqueta;			
		}
		
		public int getEtiquetaElse(){
			return etiquetaElse;
		}

	}

	public static class InstIteracion extends Instruccion {
		public Instruccion i; //cuerpo del while
		private int etiquetaCond; //para generar los saltos y que retorne del ámbito
		private int etiquetaFin; //para generar los saltos y que retorne del ámbito
		public ExpresionSimple cond = null;//condición del while

		public InstIteracion(ExpresionSimple e, Instruccion i) {
			cond = e;
			this.i = i;
		}

		public String tipo() {
			return "IteracionWhile";
		}

		public Tipo accept(Visitante v) throws Exception {
			v.preVisit(this);
			if (!cond.accept(v).equals(new Tipo(Tipo.BOOL)))
				GestionExcepciones.gestionaError(
						"Error: While condition is not a boolean expression.");
			v.visit(this);
			i.accept(v);
			v.postVisit(this);
			return null;
		}

		public void setEtiquetaCond(int sigEtiqueta) {
			etiquetaCond = sigEtiqueta;			
		}
		
		public int getEtiquetaCond() {
			return etiquetaCond;		
		}
		
		public void setEtiquetaFin(int sigEtiqueta) {
			etiquetaFin = sigEtiqueta;			
		}
		
		public int getEtiquetaFin() {
			return etiquetaFin;		
		}

	}

	public static class InstRetorno extends Instruccion {
		public ExpresionSimple expr = null;

		public InstRetorno() {
		}

		public InstRetorno(ExpresionSimple e) {
			expr = e;
		}

		public String tipo() {
			return "Retorno";
		}

		public Tipo accept(Visitante v) throws Exception {
			v.preVisit(this);
			Tipo tipo =null;
			if (!esVoid())
				tipo = expr.accept(v);
			v.postVisit(this);
			return tipo;
		}
		
		public boolean esVoid(){
			// Se utiliza en la generación de código, para distinguir entre retp o retv
			return expr == null;
		}
	}

	public String tipo() {
		return "";
	}

	public Tipo accept(Visitante v) throws Exception {
		v.preVisit(this);
		v.postVisit(this);
		return null;
	}

}
