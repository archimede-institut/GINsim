package org.ginsim.common.utils;

import java.util.concurrent.Callable;

public class Timing {
	
	/**
	 * Get the time taken for each run in a series of calls
	 * 
	 * @param c
	 * @param nbruns
	 * @return
	 * @throws Exception
	 */
	public static int[] time(Callable<?> c, int nbruns) throws Exception {
		int[] results = new int[nbruns];
		for (int i=0 ; i<nbruns ; i++) {
			c.call();
			results[i] = time(c);
		}
		return results;
	}

	/**
	 * Get the time taken by a Callable's call method
	 * 
	 * @param c
	 * @return
	 * @throws Exception
	 */
	public static int time(Callable<?> c) throws Exception {
		long t = System.currentTimeMillis();
		c.call();
		return (int)(System.currentTimeMillis()-t);
	}

}
