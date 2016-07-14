package tabla;


import java.util.Hashtable;
import java.util.Stack;
import lenguaje.Tipo;
import lenguaje.VariableParametro;
import exc.*;

public class TablaSimbolos {

	class CeldaTabla {
		private int profundidad;
		private Object punteroArbol = null;
		private Stack<CeldaTabla> pilaVecina = null;
		private String nombreVecina = null;
		private int posicionRelativa;

		public CeldaTabla(int profundidad) {
			this.profundidad = profundidad;

		}

		public CeldaTabla(int profundidad, Object punteroArbol,
				Stack<CeldaTabla> vecina, int pos, String id) {
			this.profundidad = profundidad;
			this.punteroArbol = punteroArbol;
			this.pilaVecina = vecina;
			this.nombreVecina = id;
			this.posicionRelativa = pos;

		}

		public CeldaTabla() {
		}
	}

	private Hashtable<String, Stack<CeldaTabla>> tablaSimbolos;
	private Stack<CeldaTabla> cadenaActual;

	private int profActual;

	public TablaSimbolos() {
		tablaSimbolos = new Hashtable<String, Stack<CeldaTabla>>();
		cadenaActual = new Stack<CeldaTabla>();
		profActual = -1;
		nuevo_bloque();
	}

	public void nuevo_bloque() {
		profActual++;
		CeldaTabla aux = new CeldaTabla(profActual);
		cadenaActual.push(aux);
	}

	public void quita_bloque() {
		CeldaTabla aux = cadenaActual.pop();

		Stack<CeldaTabla> pilaVecinaAux = aux.pilaVecina;
		CeldaTabla cimaNuestra;
		while (pilaVecinaAux != null) {
			cimaNuestra = pilaVecinaAux.pop();
			if (pilaVecinaAux.isEmpty())
				tablaSimbolos.remove(cimaNuestra.nombreVecina);
			pilaVecinaAux = cimaNuestra.pilaVecina;
		}
		profActual--;
	}

	public void introduce_id(String id, int linea, int columna, Object nodo, boolean fun)
			throws Exception {
		Stack<CeldaTabla> pilaId = tablaSimbolos.get(id);
		if (pilaId != null) {
			int profDeclarada = pilaId.peek().profundidad;
			if (profDeclarada == profActual) {
				GestionExcepciones.gestionaError("Error: Identificator " + id
						+ " duplicated in the same scope (at depth "
						+ profActual + "), " + infoError(linea, columna));
			}
		} else {
			pilaId = new Stack<CeldaTabla>();
			tablaSimbolos.put(id, pilaId);
		}
		CeldaTabla ambitoActual = cadenaActual.peek();
		Stack<CeldaTabla> vecinaActual = ambitoActual.pilaVecina;
		int posActualNueva = 5; // Porque las 5 primeras palabras de cada marco
								// están reservadas en la máquina-p
		if (vecinaActual != null){
			posActualNueva = vecinaActual.peek().posicionRelativa;
			if (!fun) posActualNueva += ((VariableParametro)vecinaActual.peek().punteroArbol).size();
		}
		CeldaTabla nueva = new CeldaTabla(profActual, nodo, vecinaActual,
				posActualNueva, id);

		pilaId.push(nueva);
		ambitoActual.pilaVecina = pilaId;

	}

	public Object busca_id_arbol(String id) throws Exception {
		if (GestionExcepciones.error == true
				&& GestionExcepciones.compruebaIdInvalido(id))
			return new VariableParametro(new Tipo());
		return busca_id(id, 0, 0).punteroArbol;
	}

	public CeldaTabla busca_id(String id, int linea, int columna)
			throws Exception {
		Stack<CeldaTabla> valor = tablaSimbolos.get(id);
		if (valor == null) {
			GestionExcepciones.añadeIdInvalido(id);
			GestionExcepciones.gestionaError("Error: Identificator " + id
					+ " not declared " + infoError(linea, columna));
			return null;
		} else
			return valor.peek();
	}

	public int getDifAnidamiento(String string) throws Exception {
		if (GestionExcepciones.error == true
				&& GestionExcepciones.compruebaIdInvalido(string))
			return -1; // Para que en el código generado sepamos que ha habido
						// un error
		else
			return profActual - busca_id(string, 0, 0).profundidad;
	}

	public int getPosRelativa(String string) throws Exception {
		if (GestionExcepciones.error == true && GestionExcepciones.compruebaIdInvalido(string))
			return -1;
		else
			return busca_id(string, 0, 0).posicionRelativa;
	}
	
	public int getProf(String id) throws Exception{
		return busca_id(id, 0, 0).profundidad;
	}

	public String infoError(int l, int c) {
		return "beginning at line " + l + ", column " + c + ".";
	}
}
