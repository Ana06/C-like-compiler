# C-like compiler

A C-like language compiler written in Java that generates assembly code for the p-machine. This project was done during a university course (Language processors) and finished in July 2015. Most comments are in Spanish.


## Lexical Analysis

**AnalizadorLexico.jflex** - Contains a formal specification of the lexic of the language, in jFlex

**AnalizadorLexico.java** - Class that implements the lexer. Generated from AnalizadorLexico.jflex

**UnidadesLexicas.java** - Class with some lexical units (see also /src/sintactico/ClaseLexica.java). Generated from AnalizadorLexico.jflex


## Syntactic Analysis

**Gramatica.cup** - Contains a formal specification of the grammar of the language, in CUP

**AnalizadorSintactico.java** - Class that implements the parser. Generated from Gramatica.cup

**ClaseLexica.java** - Class with some syntactic units (see also /src/lexico/UnidadesLexicas.java). Generated from Gramatica.cup


## Classes for the language

These classes help to implement the specification of our language. They constitute the nodes of the syntax/semantic tree of the program.

**DeclaracionFuncion.java** - Represents the declaration of a function

**Expresion.java** - Implements an expression of the language, consisting of a left-hand side and a right-hand side subexpressions

**ExpresionSimple.java** - Implements the components of the previous expressions. They can be arithmetical, logical, etc

**Identificador.java** - Represents an identifier (i.e., the name of a variable)

**IdParametro.java** - Represents a function parameter's identifier

**Instruccion.java** - Represents a statement from our language. It can be a compound statement, an expression, a selection statement,
a looping statement or a return statement

**ListaDecVarFun.java** - Represents a list of declarations of functions and/or variables

**ListaVariables.java** - Represents a list of variables of the same type

**Parametros.java** - Represents the parameters of a given function

**Programa.java** - Represents the program, i.e., it is the root node

**Tipo.java** - It represents a type of a variable/function

**VariableParametro.java** - Represents a variable or a function parameter


## Other classes

**GestionExcepciones.java** - An exception handler for the errors encountered during the analysis of a source code

**TablaSimbolos.java** - Contains the data structure used during the analysis of the symbols from the program


## Visitor design pattern

The compiler is based on the visitor pattern. The helper class Visitante.java implements the pre/postVisit methods used during the processing of every node from the tree.


## Usage of the program

Just simply run Main.java. You'll be prompted to provide the name of the program and its starting point (function) and whether you would like to continue compilation after a first error is found.
After the compilation process finishes, if there are no errors the resultant code.p file is generated. In addition, you'll be prompted to provide whether
you would like to create a tree.txt file with a tree version of the code and/or a more readable version of the generated code using tags for branching.


## Examples

### Example 1

The main function has a nested function, which calculates the mirror image of an integer given, declared inside it. The main function calls the nested function with an input value a, initialized to 376, and then returns the output of the function. 


### Example 2

Declare a matrix with nested structs and access to their fields.

### Example 3

Code with lexical and syntactic errors.


### Example 4

Code with errors.


## Authors

This project was developed by Ana María Martínez Gómez and Víctor Adolfo Gallego Alcalá.



## Licence

Code published under MIT License (see [LICENSE](LICENSE))
