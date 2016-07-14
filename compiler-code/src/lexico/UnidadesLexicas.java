package lexico;

public interface UnidadesLexicas {
	
	// TIPONUMERICO y TIPONONUMERICO
	public static final int INT  = 1;
	public static final int BOOL  = 2;
	
	// OPSUM
	public static final int SUM = 1;
	public static final int SUB = 2;
	
	// OPMUL
	public static final int PROD = 1;
	public static final int DIV = 2;
	public static final int MOD = 3;
	
	// OPRELACIONAL
	public static final int GREATER = 1;
	public static final int LESS = 2;
	public static final int GREATEREQUAL = 3;
	public static final int LESSEQUAL = 4;
	public static final int EQUALEQUAL = 5;
	public static final int DISTINCT = 6;
	
	//INCREMENTA
	public static final int SUMSUM = 1;
	public static final int SUBSUB = 0;
}
