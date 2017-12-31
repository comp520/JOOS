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


package joosc;

/*
 * Assert - mimics the assert() C standard library function
 *
 * History:
 *   6 Mar 2000 - created
 */
public class Assert {
	public static void that(boolean proposition) {
		if (proposition == false) {
			System.err.println("Panic! Assertion failed.");
			System.exit(-1);
		}
	}
}