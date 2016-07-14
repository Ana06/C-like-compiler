package lenguaje;

import java.util.LinkedList;

import lenguaje.VariableParametro.Parametro;

public class Parametros {

	private LinkedList<Parametro> lp = null; //Lista de parámetros, podría no haber ninguno.
	private LinkedList<Tipo> lt = null; // Lista de tipos de los parametros.

	public Parametros(LinkedList<Parametro> lp) {
		this.lp = lp;
		if (lp != null){
			lt = new LinkedList<Tipo>();
			for(Parametro p:lp){
				lt.add(p.getTipo());
			}
		}
	}

	public Parametros() {}

	public void accept(Visitante v) throws Exception {
		v.preVisit(this);
		if (lp != null)
			for (Parametro p : lp) {
				p.accept(v);
			}
		v.postVisit(this);
	}

	public int getTamaño() {
		if (lp != null)
			return lp.size();
		else return 0;
	}

	public LinkedList<Tipo> getListaTipos() {
		return lt;
	}



}
