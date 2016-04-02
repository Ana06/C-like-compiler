

import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;
import exc.GestionExcepciones;
import sintactico.AnalizadorSintactico;
import lenguaje.Programa;
import lenguaje.Visitante;
import lexico.AnalizadorLexico;
import java_cup.runtime.ComplexSymbolFactory;

public class Main {
	public static void main(String argv[]) throws java.io.IOException,
		java.lang.Exception {
		String arg;
		ComplexSymbolFactory csf = new ComplexSymbolFactory();
		System.out.println("WELCOME TO ANA&VICTOR'S COMPILATOR\n");
		Scanner in = new Scanner(System.in);
		System.out.println("Introduce the file to compile: ");
		arg = in.next();
		try{
			FileReader file =new FileReader(arg);
			System.out.println("Introduce the starting function of the program (usually main):");
			String startPoint = in.next();
			String op;
			do {
				System.out.println("Would you like to continue compilation after the first error to look for more errors (if any)? \n 0 - No \n 1 - Yes.");
				op = in.next();
			} while (!op.equals("0") && !op.equals("1"));
			if (op.equals("1"))
				GestionExcepciones.continuar(true);
			else
				GestionExcepciones.continuar(false);
			System.out.println("LEXICAL AND SYNTAX ANALYSIS");
			AnalizadorLexico scanner = new AnalizadorLexico(file, csf);
			AnalizadorSintactico p = new AnalizadorSintactico(scanner, csf);
			Programa result = (Programa) p.parse().value;
			if (GestionExcepciones.error == true)
				System.out.println("LEXICAL AND SYNTAX ANALYSIS were NOT successful.");
			else{
				System.out.println("were successful.");
				System.out.println("SEMANTIC ANALYSIS AND CODE GENERATION");
				Visitante visitante = new Visitante(startPoint);
				result.accept(visitante);
				visitante.flush();
				PrintWriter w = new PrintWriter("code.p");
				w.print(visitante.getCodigoSinEtiquetas());
				w.close();
				if (GestionExcepciones.error == true)
					System.out.println("SEMANTIC ANALYSIS AND CODE GENERATION were NOT successful.");
				else{
					System.out.println("were successful.");
					do {
						System.out.println("Do you wish to generate a file with the tree version of the program? \n 0 - No \n 1 - Yes.");
						op = in.next();
					} while (!op.equals("0") && !op.equals("1"));
					if(op.equals("1")){
						w = new PrintWriter("tree.txt");
						w.print(visitante.getArbol());
						w.close();
					}
					do {
						System.out.println("Do you wish to generate a more readable version of p-code using tags? \n 0 - No \n 1 - Yes.");
						op = in.next();
					} while (!op.equals("0") && !op.equals("1"));
					if(op.equals("1")){
						w = new PrintWriter("code-tags.p");
						w.print(visitante.getCodigoEtiquetas());
						w.close();
					}
				}
			}
			in.close();
			System.out.println("Compilation ended.");
		} catch (java.io.FileNotFoundException e) {
			System.out.print("Compilation ended. ");
			System.err.println("File not found: \"" + arg + "\".");
			System.exit(1);
		}
		catch (Exception e) {
			System.out.print("Compilation ended. ");
			System.err.println(e);
			System.exit(1);
		}
		
	}
}
