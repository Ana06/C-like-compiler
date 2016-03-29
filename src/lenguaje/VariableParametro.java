package lenguaje;

import java.util.LinkedList;

import exc.GestionExcepciones;
import lenguaje.ExpresionSimple;
import lenguaje.Identificador;

public class VariableParametro {
	protected Identificador i; //nombre de la variable o el parámetro
	protected Tipo tipo = null; //tipo de la variable o el parámetro
	protected LinkedList<Integer> listaDim; //Las dimensiones en el caso de ser una matriz
	
	public VariableParametro(Identificador i,Tipo tipo) {
		this.i=i;
		this.tipo = tipo;
	}
	
	public VariableParametro(Tipo tipo) {
		this.tipo = tipo;
		tipo.añadeLista(listaDim);
	}
	
	public VariableParametro(){}

	public static class Variable extends VariableParametro {
		public ExpresionSimple e = null; //ID =expresionSimple
		public LinkedList<ExpresionSimple> le = null; //ID listaDimesiones =...

		public Variable(Identificador i) {
			this.i = i;
		}

		public Variable(Identificador i, ExpresionSimple e) {
			this.i = i;
			this.e = e;
		}

		public Variable(Identificador i, LinkedList<Integer> ld) {
			this.i = i;
			this.listaDim = ld;
		}

		public Variable(Identificador i, LinkedList<Integer> ld, LinkedList<ExpresionSimple> le) {
			this.i = i;
			this.listaDim = ld;
			this.le = le;
		}

		public void accept(Visitante v) throws Exception {
			v.preVisit(this);
			tipo.accept(v);
			int tamListaDim=1;
			if(e!= null){
				e.accept(v);
				v.escribeStr(v.getTabla().getPosRelativa(i.toString()));
			}
			else if(listaDim !=null){
				boolean error = false;
				for (Integer in : listaDim){
					if (in <= 0) {
						GestionExcepciones.gestionaError("Error:Indexes must be bigger than 0 in the declaration of "+i+" in line "+i.getLine()+", column "+i.getColumn() + ".");
						error =true;
						break;
					}
					tamListaDim*=in;
				}
				if(le!=null && !error){
					if(tamListaDim != le.size()) GestionExcepciones.gestionaError("Error:Number of components doesn't match in the initialization of "+i+" in line "+i.getLine()+", column "+i.getColumn() + ".");
					int ind=0;
					for (ExpresionSimple e : le){
						if(!tipo.equals(e.accept(v)))  GestionExcepciones.gestionaError("Error:Type of the expresion doesn't match the type of the matrix in the initialization of "+i+" in line "+i.getLine()+", column "+i.getColumn() + ".");
						v.escribeStr(v.getTabla().getPosRelativa(i.toString())+ind);
						ind++;
					}
				}
			}
			v.postVisit(this);
		}

		public int getTamMatriz() {
			int res = 1;
			if(listaDim == null) return 1;
			else for(Integer i: listaDim) res*=i;
			return res;
		}

	}

	public static class Parametro extends VariableParametro {

		public Parametro(Identificador identificador, Tipo t) {
			super(identificador,t);
			tipo.añadeLista(listaDim);
		}

		public Parametro(Identificador identificador, Tipo t, LinkedList<Integer> ld) {
			super(identificador,t);
			listaDim=ld;
			tipo.añadeLista(listaDim);
		}

		public void accept(Visitante v) throws Exception {
			v.preVisit(this);
			tipo.accept(v);
			if(listaDim !=null){
				for (Integer in : listaDim){
					if (in <= 0) {
						GestionExcepciones.gestionaError("Error:Indexes must be bigger than 0 in the declaration of "+i+" in line "+i.getLine()+", column "+i.getColumn() + ".");
						break;
					}
				}
			}
			v.postVisit(this);
		}

	}

	public void tipar(Tipo tipo) {
		this.tipo = tipo;
	}
	
	public int getNumCorchetes(){
		if(listaDim != null)
			return listaDim.size();
		else return 0;
	}
	
	public LinkedList<Integer> getListaDim(){
		return listaDim;
	}
	
	public int size() {
		return tipo.size();
	}
	
	public Tipo getTipo() {
		return tipo;
	}

	public boolean esConstante() {
		return tipo.esConstante();
	}
	
	public String toString() {
		return i.toString();
	}

	public Identificador getId() {
		return i;
	}

}