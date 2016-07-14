package exc;

import java.util.HashSet;
import java.util.Set;

public class GestionExcepciones {
	public static boolean continua;
	public static boolean error = false;
	private static Set<String> idsInvalidos = new HashSet<String>();

	public static void gestionaError(String s) throws java.io.IOException {
		if (error == false)
			error = true;
		if (continua == false)
			throw new java.io.IOException(s);
		else
			System.err.println(s);
	}
	public static void continuar(boolean c){
		continua=c;
	}
	public static void añadeIdInvalido(String id) {
		idsInvalidos.add(id);
	}

	public static boolean compruebaIdInvalido(String id) {
		return idsInvalidos.contains(id);
	}

}
