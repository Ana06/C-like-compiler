package lenguaje;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class Identificador {
	//rigth no se usa porque para dar los errores es suficiente con saber donde empieza el 
	//identificador que produce el error, pero se mantiene por si por alguna razón se quisiese usar
	private Location left, right; 
	private String i; //nombre del identificador

	public Identificador(Location l, String i, Location r) {
		this.i = i;
		this.left = l;
		this.right = r;
	}
	
	public String toString(){
		return i;
	}
	
	public int getLine(){
		return left.getLine();
	}
	
	public int getColumn(){
		return left.getColumn();
	}

	
}

