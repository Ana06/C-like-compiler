package lenguaje;

import java.util.LinkedList;

import lenguaje.ListaDecVarFun.VarFun;
import lenguaje.VariableParametro.Variable;

public class ListaVariables {

	private LinkedList<Variable> iv; //Lista de variables

	public ListaVariables() {
		iv = new LinkedList<Variable>();
	}

	public void add(Variable v) {
		iv.add(v);
	}

	public void accept(Visitante v) throws Exception {
		v.preVisit(this);
		for (Variable d : iv) {
			d.accept(v);
		}
		v.postVisit(this);
	}

	public ListaVariables tipar(Tipo t) {
		for(Variable v : iv)
			v.tipar(t);
		return this;
	}
	
	public int size(){
		int size=0;
		for (Variable d : iv) {
			size+=d.size();
		}
		return size;
	}
	
	public LinkedList<VarFun> varFun(){
		LinkedList<VarFun> lvf=new LinkedList<VarFun>();
		for (Variable v : iv) {
			VarFun aux =new VarFun(); 
			aux.v = v;
			lvf.add(aux);
		}
		return lvf;
		
	}
}
