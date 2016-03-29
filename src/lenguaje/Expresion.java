package lenguaje;

import java_cup.runtime.ComplexSymbolFactory.Location;
import exc.GestionExcepciones;
import lenguaje.ExpresionSimple.Modificable;
import lexico.UnidadesLexicas;

public class Expresion {

	private Modificable lhs = null; //parte izquierda de la asignación
	private ExpresionSimple rhs = null; //parte derecha de la asignación
	private int incrementa = -1; // 0 sii --, 1 sii ++
	private Location l; //para dar la línea y columna en los errores

	public Expresion() {}

	// modificable = expresionDerecha y modificable +=,-=,*=,/= expresionDerecha
	public Expresion(ExpresionSimple m, ExpresionSimple e, Location l) {
		lhs = (Modificable) m;
		rhs = e;
		this.l = l;
	}

	// modificable ++,--
	public Expresion(ExpresionSimple m, int i, Location l) {
		lhs = (Modificable) m;
		incrementa = i;
		this.l = l;
	}

	public void accept(Visitante v) throws Exception {
		v.preVisit(this);
		Tipo tipolhs = lhs.accept(v);
		if(rhs != null){
			Tipo tiporhs = rhs.accept(v);
			if(!tipolhs.equals(tiporhs)) GestionExcepciones.gestionaError("Error: Assignment of an incompatible typed value in line " + l.getLine() + ", column " +l.getColumn());	
		}
		else{
			if(incrementa != -1){//Incrementa es 0 si es --, 1 si es ++ y -1 en otro caso
				if(!tipolhs.equals(new Tipo(Tipo.INT))) {
					switch (incrementa){
						case UnidadesLexicas.SUMSUM:GestionExcepciones.gestionaError("Error: ++ aplied to a not integer variable in line " + l.getLine() + ", column " +l.getColumn());break;	//Incrementa es 1 si es ++
						case UnidadesLexicas.SUBSUB:GestionExcepciones.gestionaError("Error: -- aplied to a not integer variable in line " + l.getLine() + ", column " +l.getColumn());	//Incrementa es 0 si es --
				
					}
				}
			}
		}
		v.postVisit(this);
	}
	
	//para la generación de código
	public String getCode(Visitante v) throws Exception{
		if (incrementa == -1) return "sto";
		else {switch (incrementa){
		case UnidadesLexicas.SUMSUM: lhs.accept(v);return "ind;\n\tinc 1;\n\tsto";
		case UnidadesLexicas.SUBSUB: lhs.accept(v); return "ind;\n\tdec 1;\n\tsto";
		}
		}
		return null;
			
	}

}
