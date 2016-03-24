package lexico;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import exc.*;

%%

%public
%class AnalizadorLexico
%cup
%extends sintactico.ClaseLexica
%implements lexico.UnidadesLexicas
%char
%line
%column

%{
    StringBuffer string = new StringBuffer();
    public AnalizadorLexico(java.io.Reader in, ComplexSymbolFactory sf){
	this(in);
	symbolFactory = sf;
    }
    ComplexSymbolFactory symbolFactory;

  private Symbol symbol(String name, int sym) {
      return symbolFactory.newSymbol(name, sym, new Location(yyline+1,yycolumn+1,yychar), new Location(yyline+1,yycolumn+yylength(),yychar+yylength()));
  }
  
  private Symbol symbol(String name, int sym, Object val) {
      Location left = new Location(yyline+1,yycolumn+1,yychar);
      Location right= new Location(yyline+1,yycolumn+yylength(), yychar+yylength());
      return symbolFactory.newSymbol(name, sym, left, right,val);
  } 
  private Symbol symbol(String name, int sym, Object val,int buflength) {
      Location left = new Location(yyline+1,yycolumn+yylength()-buflength,yychar+yylength()-buflength);
      Location right= new Location(yyline+1,yycolumn+yylength(), yychar+yylength());
      return symbolFactory.newSymbol(name, sym, left, right,val);
  }       
 private void error(String message) throws java.io.IOException {
  GestionExcepciones.gestionaError("Lexical error at line "+(yyline+1)+", column "+(yycolumn+1)+" : "+message);
  
  }
%} 

%eofval{
return symbolFactory.newSymbol("EOF", EOF, new Location(yyline+1,yycolumn+1,yychar), new Location(yyline+1,yycolumn+1,yychar+1));
%eofval}

identificador = {letra}({letra}|{digito})*
letra  = ([A-Z]|[a-z])
digitoPositivo = [1-9]
cero=[0]
digito = ({digitoPositivo}|{cero})
parteEntera = ({digitoPositivo}{digito}*|{cero})
numeroEnteroNeg=[\-]{parteEntera}
numeroEntero = {parteEntera}
separador = [ \t\r\b\n]
comentario = \~\~[^\n]*\~\~
booleano = true | false

%state STRING

%%

<YYINITIAL>{
 /* Palabras reservadas */

"globales"			{ return symbol("globals", GLOBALS);}
"codigo"			{ return symbol("code", CODE); }
"const"				{ return symbol("const", CONST); } 
"int"				{return symbol("int", TYPEPRIM, new Integer( INT ));}
"bool"				{return symbol("bool", TYPEPRIM, new Integer( BOOL ));}
"void"				{return symbol("void", VOID);}
"struct"			{return symbol("struct", STRUCT);}
"if"				{return symbol("if", IF);}
"else"				{return symbol("else", ELSE);}
"end" 				{return symbol("end", END);}
"while"				{return symbol("while",WHILE);}
"return"			{return symbol("return", RETURN);}
"skip"				{return symbol("skip", SKIP);}
"fun"			{return symbol("fun", FUN); }


 /* constante */
 
{numeroEnteroNeg} { return symbol("Integer",CONSTENT, new Integer(Integer.parseInt(yytext()))); }
{numeroEntero} { return symbol("Integer",CONSTENT, new Integer(Integer.parseInt(yytext()))); }
{booleano} 	{ return symbol("Boolean",CONSTBOOL, new Boolean(Boolean.parseBoolean(yytext()))); }

 
 /* operadores */
  \" 				{ string.setLength(0); yybegin(STRING); }
";"               	{ return symbol(";",SEMI); }
","					{ return symbol(",",COMMA); } 
"{"              	{ return symbol("{",BRACEOP); }
"}"              	{ return symbol("}",BRACECL); }
"("                 { return symbol("(",POP); }
")"                 { return symbol(")",PCL); }
"["                 { return symbol("[",BRACKETOP); }
"]"                 { return symbol("]",BRACKETCL); }
"->"				{ return symbol("->", ARROW); }
"||"				{ return symbol("||", OR); }
"&&"				{ return symbol("&&", AND); }
"+="				{ return symbol("+=", EQUALWITHOPSUM, new Integer(SUM)); }
"-="				{ return symbol("-=", EQUALWITHOPSUM, new Integer(SUB)); }
"*="				{ return symbol("*=", EQUALWITHOPMUL, new Integer(PROD)); }
"/="				{ return symbol("/=", EQUALWITHOPMUL, new Integer(DIV)); }
"%="				{ return symbol("%=", EQUALWITHOPMUL, new Integer(MOD)); }
"++"				{ return symbol("++", INCREMENT, new Integer(SUMSUM)); }
"--"				{ return symbol("--", INCREMENT, new Integer(SUBSUB)); }
"=="				{ return symbol("<", OPRELATIONAL, new Integer(EQUALEQUAL)); }
"="					{ return symbol("=", EQUAL); }
"!="				{ return symbol(">", OPRELATIONAL, new Integer(DISTINCT)); }
"!"					{ return symbol("!", NO); }
"<="				{ return symbol("<=", OPRELATIONAL, new Integer(LESSEQUAL)); }
">="				{ return symbol(">=", OPRELATIONAL, new Integer(GREATEREQUAL)); }
"<"					{ return symbol("<", OPRELATIONAL, new Integer(LESS)); }
">"					{ return symbol(">", OPRELATIONAL, new Integer(GREATER)); }
"+"					{ return symbol("+", OPSUM, new Integer(SUM)); }
"-"					{ return symbol("-", OPSUM, new Integer(SUB)); }
"*"					{ return symbol("*", OPMUL, new Integer(PROD)); }
"/"					{ return symbol("/", OPMUL, new Integer(DIV)); }
"%"					{ return symbol("%", OPMUL, new Integer(MOD)); }

 /* nombres */
 {identificador}	{return symbol("Id", ID, yytext());}


/* separador */
{separador}			{ }
{comentario}		{ } 
}

/* error  */
[^]             {  
		    error("Illegal character <"+ yytext()+">");
                  }