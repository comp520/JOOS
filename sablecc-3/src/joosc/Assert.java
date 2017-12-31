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


package joosc;

/**
 * Assert - mimics the assert() C standard library function<p>
 *
 * History:
 * <ul>
 *   <li>Aug 18, 2003 - obseleted, to be replaced by <b>assert</b> keyword from Java 1.4
 *   <li>6 Mar 2000 - created
 * </ul>
 */
public class Assert {
	public static void that(boolean proposition) {
		if (proposition == false) {
			System.err.println("Panic! Assertion failed.");
			System.exit(-1);
		}
	}
}
