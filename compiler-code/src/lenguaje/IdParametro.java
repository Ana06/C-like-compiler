package lenguaje;

import lenguaje.Identificador;

public class IdParametro {
	private Identificador i; //nombre del parámetro
	
	public IdParametro(Identificador i){
		this.i = i;
	}
	
	public String toString(){
		return i.toString();
	}

	public Identificador getId() {
		return i;
	}

}
