package lenguaje;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

public class Tipo {
	public static final int INT  = 1;
	public static final int BOOL  = 2;
	
	protected LinkedList<Integer> listaDim = null; //Lista de dimensiones en caso de ser una matriz
	protected int tipo; // tipo INT, BOOL
	protected boolean constante; //indica si es constante
	
	public Tipo(Integer t) {
		tipo = t;
	}

	public Tipo() {}

	public String toString() {
		return traduceTipo();
	}

	public String traduceTipo() {
		switch (tipo) {
		case 1:
			return "Int";
		case 2:
			return "Bool";
		}
		return "";
	}

	public void constante(boolean b) {
		constante = b;

	}

	public void accept(Visitante v) {
		v.preVisit(this);
		v.postVisit(this);
	}
	
	public boolean equals(Tipo t){
		if (t.tipo == tipo) return true;
		return false;		
	}

	public static class TipoStruct extends Tipo {
		//Tabla hash con el nombre del campo de un struct y la clase auxiliar que contiene su dirección y su tipo
		private Hashtable<String, ClaseAux> ls = new Hashtable<String, ClaseAux>(); 
		protected static int contador=0; //contador de posición relativa
		protected static int guardado; //Para guardar el contador si hay structs anidados
		
		public TipoStruct() {
			guardado=contador;
			contador =0;
		}
		
		public static class ClaseAux{
			private Tipo t;
			private int dir;
			public ClaseAux(Tipo t) {
				this.t=t;
				this.dir=contador;
				contador += t.size();
			}
			
			public int getDir() { return dir; }
		}
		public int size() {
			int tamStruct = 0;
			Enumeration<ClaseAux> e = ls.elements();
			ClaseAux valor;
			while( e.hasMoreElements() ){
			  valor = e.nextElement();
			  tamStruct += valor.t.size();
			}
			if(listaDim!=null) {
				int size=1;
				if(listaDim!=null){
					for (Integer in : listaDim){
						size *=in;
					}
				}
				return size*tamStruct;
			}
			else return tamStruct;	
		}

		public void tomaLista(Hashtable<String, ClaseAux> ls){
			contador=guardado;
			this.ls=ls;
		}
		public int getDesp(String string) {
			return ls.get(string).dir;
		}
		
		public boolean equals(TipoStruct t){
			return false; //Los tipos anónimos se consideran distintos
		}
		
		public String traduceTipo() {
			return "Struct";

		}
		public Tipo esta(String string) {
			ClaseAux aux =ls.get(string);
			if(aux == null) return null;
			return aux.t;
			
		}
	}

	public boolean esConstante() {
		return constante;
	}

	public int size() {
		if(listaDim!=null) {
			int size=1;
			for (Integer in : listaDim){
				size *=in;
			}
			return size;
		}
		else return 1;	
	}

	public Tipo esta(String string) {
		return null;
	}

	public void añadeLista(LinkedList<Integer> listaDim) {
		this.listaDim=listaDim;		
	}

	public int numDim() {
		if (listaDim == null) return 0;
		return listaDim.size();
	}

	public int getDesp(String string) {
		return 0;
	}

}
