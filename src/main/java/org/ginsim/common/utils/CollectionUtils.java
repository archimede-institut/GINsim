package org.ginsim.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * manipulate collections.
 * 
 * @author Aurelien Naldi
 */
public class CollectionUtils {

	/**
	 * Transform an array to a Vector
	 * 
	 * @param t the array we want to convert to vector
	 * @return the new Vector
	 */
	public static List getVectorFromArray(Object[] t) {
		
		List list = new ArrayList(t.length);

		for (int i = 0; i < t.length; i++) {
			list.add(t[i]);
		}
		return list;
	}

}
