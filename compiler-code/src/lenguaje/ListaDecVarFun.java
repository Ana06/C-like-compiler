package lenguaje;

import java.util.LinkedList;

import lenguaje.VariableParametro.Variable;

public class ListaDecVarFun {
	LinkedList<VarFun> l =new LinkedList<VarFun>();//Lista de declaraciones (funciones o variables)
	private int etiqueta; //Pala generación de etiquetas, para saltar funciones anidadas
	
	static class VarFun{
		public Variable v=null;
		public DeclaracionFuncion f=null;
	}
	public void addListaVar(ListaVariables lv) {
		l.addAll(lv.varFun());
	}

	public void addFun(DeclaracionFuncion df) {
		VarFun aux =new VarFun(); 
		aux.f = df;
		l.add(aux);
		
	}

	public void accept(Visitante v) throws Exception {
		for (VarFun vf: l){
			if(vf.v != null) {vf.v.accept(v);}
			else { 
				etiqueta =v.escribeUjp();
				vf.f.accept(v); 
				v.escribeEtiqueta(etiqueta);
			}
		}
	}

	public int size() {
		int size=0;
		for (VarFun vf: l)
			if(vf.v != null) size+=vf.v.size();
		return size;
	}

	public int tam() {
		int size=0;
		for (VarFun vf: l)
			if(vf.v != null) size+=vf.v.size()*vf.v.getTamMatriz();
		return size;
	}

}
