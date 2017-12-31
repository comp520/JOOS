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

/**
 * MyError - based on Error from the WIG compiler developed in CS520
 *           renamed to avoid conflict with java.lang.Error.<p>
 *
 * History:
 * <ul>
 *   <li>Aug 18, 2003 - fixed documentation
 *   <li>6 Jun 2000 - added new debug variations
 *   <li>14 May 2000 - added globalError
 *   <li>4 May 2000 - error counting now done by error methods
 *   <li>5 Mar 2000 - created
 * </ul>
 */
public class MyError {
	public static boolean debug = false;
	public static boolean pretty_print = false;
	public static boolean sym_pretty_print = false;
	private static int nb_errors = 0;
	
	public static void debugln(String phase, String msg) {
		System.out.println(""+phase+": "+msg+"");
	}
	public static void debugln(String msg) {
		System.out.println(""+msg+"");
	}
	public static void debug(String phase, String msg) {
		System.out.print(""+phase+": "+msg+"");
	}
	public static void debug(String msg) {
		System.out.print(""+msg+"");
	}

	public static void error(String file, String msg, int lineno) {
		nb_errors++;
		System.out.println(file+":"+lineno+": "+msg);
	}
	public static void error(String file, String msg, int lineno, int pos) {
		nb_errors++;
		System.out.println(file+":"+lineno+","+pos+": "+msg);
	}

	public static void globalError(String msg) {
		nb_errors++;
		System.out.println(msg);
	}
	
	public static void fatalError(String file, String phase, 
	                              String msg, int lineno) {
		System.out.println("Fatal error in "+phase+
		                   " at line "+lineno+": "+msg);
		System.out.println(
			"This should never happen! Please submit a bug report.");
		System.exit(1);
	}
	public static void fatalError(String phase, String msg) {
		System.out.println("Fatal error in "+phase+": "+msg);
		System.out.println(
			"This should never happen! Please submit a bug report.");
		System.exit(1);
	}
	
	public static void noErrors() {
		if (nb_errors > 0) {
			System.out.println(nb_errors+" error"+(nb_errors>1 ? "s" : ""));
			System.exit(1);
		}
	}
}   
