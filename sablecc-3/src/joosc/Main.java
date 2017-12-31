/*
 * JOOSJ is Copyright (C) 2000 Othman Alaoui
 *
 * Reproduction of all or part of this software is permitted for
 * educational or research use on condition that this copyright notice is
 * included in any copy. This software comes with no warranty of any
 * kind. In no event will the author be liable for any damages resulting from
 * use of this software.
 *
 * email: oalaou@po-box.mcgill.ca
 */

/*
 * August 2003
 *
 * Updated JOOSJ which works with the new version of
 * SableCC (SableCC3.0)
 *
 * Update by Ahmer Ahmedani & Feng Qian
 * email: ahmer.ahmedani@mail.mcgill.ca & fqian@cs.mcgill.ca
 */


package joosc;

import joosc.node.*;
import joosc.lexer.*;
import joosc.parser.*;
import joosc.fixer.*;
import joosc.abstracter.*;
import joosc.weeder.*;
import joosc.symbol.*;
import joosc.type.*;
import joosc.resource.*;
import joosc.code.*;
import java.io.*;
import java.util.*;


/**
 * Main - JOOS compiler. <p>
 *
 * Generates jasmin files (.j)<p>
 *
 * History:
 * <ul>
 *   <li> 19 Aug 2003 - change to SableCC 3 AST grammar, fix documentation
 *   <li> 8 Jun 2000 - added command line help
 *   <li> 7 Jun 2000 - hid analysis data away into centralized manager class
 *   <li>3 Jun 2000 - improved verbose mode
 *   <li>2 Apr 2000 - restructured for multifile JOOS program compilation
 *   <li>1 Apr 2000 - added command line processing framework
 *   <li>6 Mar 2000 - restructured for clarity
 *   <li>5 Mar 2000 - created
 *</ul>
 */
public class Main {

	public static void main(String[] argv) {
		Start tree = null;
		Parser p;
		Iterator filesIter;
		String file;
		LinkedList theProgram = new LinkedList();
		AnalysisDataManager analysisData = new AnalysisDataManager();

		filesIter = processCmdLine(argv).iterator();

		/* iterate on list of input classfiles */
		if (MyError.debug) MyError.debugln("Building syntax tree:");
		while (filesIter.hasNext()) {
			file = (String)filesIter.next();

			if (MyError.debug)
				MyError.debug(" - Parsing "+file+"...");

			try {

				/* lexical-analyze and parse the input */
				p = new Parser (new Lexer(new PushbackReader(
				    new FileReader(file),5000)));
				tree = p.parse();
				if (MyError.debug) MyError.debugln(" done");
				/* lump classfile together into 'the program' */
				theProgram.add(new ClassFile(file,tree));

			/* handle all exceptions */
			} catch(IOException e) {
				System.err.println(" - IOException: cannot parse input file: "+
				                   e.getMessage());
				System.exit(1);
			} catch(LexerException e) {
				System.out.println(file+": lexical error: "+e.getMessage());
				System.exit(1);
			} catch(ParserException e) {
				System.out.println(file+": syntax error: "+e.getMessage());
				System.exit(1);
			}
		} // end loop

		/* "fix" ast */
		if (MyError.debug) MyError.debug("Fixing tree...");
		AstFixer.fix(theProgram);
		if (MyError.debug) MyError.debugln(" done");

		/* "abstract" ast */
		if (MyError.debug) MyError.debug("Abstracting program...");
		analysisData.initAbstracter();
		AstAbstracter.walk(theProgram,analysisData);
		if (MyError.debug) MyError.debugln(" done");

		/* Line numbers for nodes */
		if (MyError.debug) MyError.debug("Adding line numbers to nodes...");
		Lines.setLines(theProgram);
		if (MyError.debug) MyError.debugln(" done");

		/* weeding */
		if (MyError.debug) MyError.debug("Performing weeding...");
		Weeder.weed(theProgram);
		MyError.noErrors();
		if (MyError.debug) MyError.debugln(" done");

		/* [debugging] pretty print ast (and terminate) */
		if (MyError.pretty_print) {
			PrettyPrinter.print(theProgram);
			return;
		}

		/* symbol table */
		if (MyError.debug) MyError.debug("Building symbol table...");
		analysisData.initSymbolTable();
		SymInterfaceWalker.walk(theProgram,analysisData);
		SymInterfaceTypesWalker.walk(theProgram,analysisData);
		SymImplementationWalker.walk(theProgram,analysisData);
		if (MyError.debug) MyError.debugln(" done");

		/* [debugging] pretty print symbol table (and terminate) */
		if (MyError.sym_pretty_print) {
			SymPrettyPrinter.print(theProgram,analysisData);
			return;
		}
		analysisData.symAstToScopeMap = null; // allow garbage collection

		/* type checking */
		if (MyError.debug) MyError.debug("Type checking...");
		analysisData.initTypeChecking();
		TypeHierarchyWalker.walk(theProgram,analysisData);
		TypeImplementationWalker.walk(theProgram,analysisData);
		MyError.noErrors();
		if (MyError.debug) MyError.debugln(" done");

		/* resources for code generation */
		if (MyError.debug) MyError.debug("Generating resources...");
		analysisData.initResources();
		ResourceGenerator.walk(theProgram,analysisData);
		if (MyError.debug) MyError.debugln(" done");

		/* code IR generation */
		if (MyError.debug) MyError.debug("Generating code...");
		analysisData.initCodeGenerator();
		CodeGenerator.walk(theProgram,analysisData);
		if (MyError.debug) MyError.debugln(" done");

		/* code emission */
		if (MyError.debug) MyError.debug("Emitting code...");
		CodeEmitter.walk(theProgram,analysisData);
		if (MyError.debug) MyError.debugln(" done");
	} // end main

	/*
	 * Processes options and extracts the actual arguments (filenames).
	 */
	private static List processCmdLine(String[] args) {
		String arg;
		List files = new LinkedList();

		for (int i = 0; i < args.length; i++) {
			arg = args[i].intern(); // optimization: can use '==' for .equals
			if ((arg == "-v") || (arg == "--verbose"))
				MyError.debug = true;
			else if ((arg == "-h") || (arg == "--help"))  {
				System.out.println("Usage: joos [OPTION]... [FILE]...");
				System.out.println("Options:");
				System.out.println("  -v, --verbose            show progress");
				System.out.println("  -P, --pretty-print       (debug) print ast in a parsable form and exit");
				System.out.println("  -S, --sym-pretty-print   (debug) print symbol table and exit");
				System.out.println("  -h, --help               display this help and exit");
				System.out.println();
				System.out.println("Input files should have extension '.java' for regular classes");
				System.out.println("and '.joos' for external class declarations.");
				System.exit(0);
			}
			else if ((arg == "-P") || (arg == "--pretty-print"))
				MyError.pretty_print = true;
			else if ((arg == "-S") || (arg == "--sym-pretty-print"))
				MyError.sym_pretty_print = true;
			else if (arg.startsWith("-")) {
				System.err.println("Unrecognized option: "+arg);
				System.exit(-1);
			}
			else
				files.add(arg);
		}

		return files;
	}

}
