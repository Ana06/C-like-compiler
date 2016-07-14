package lenguaje;

import java.util.LinkedList;

public class Programa {

	private LinkedList<ListaVariables> dv; //Lista de declaración de variables globales
	private LinkedList<DeclaracionFuncion> df; //Lista de declaración funciones (una de ellas será el punto de comienzo del programa

	public Programa(LinkedList<ListaVariables> dv,
			LinkedList<DeclaracionFuncion> df) {
		this.dv = dv;
		this.df = df;
	}

	public String toString() {
		String ret = "";
		for (ListaVariables d : dv)
			ret += d;
		for (DeclaracionFuncion f : df)
			ret += f;
		return ret;
	}

	public void accept(Visitante v) throws Exception {
		v.preVisit(this);
		for (ListaVariables d : dv)
			d.accept(v);
		v.visit(this);
		for (DeclaracionFuncion f : df) {
			f.accept(v);
		}
		v.postVisit(this);
	}
	

}
