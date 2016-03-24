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



## Authors

This project was developed by Ana María Martínez Gómez and Víctor Adolfo Gallego Alcalá.



## Licence

Code published under MIT License (see [LICENSE](LICENSE))
