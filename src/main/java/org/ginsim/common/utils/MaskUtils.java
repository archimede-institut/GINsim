package org.ginsim.common.utils;


/**
 * Manipulation of simple integer masks.
 * 
 * @author Aurelien Naldi
 * @author Duncan Berenguier
 */
public class MaskUtils {

	/**
	 * Add a mask to a value.
	 * 
	 * @param value
	 * @param mask
	 * @return the new value
	 */
	public static int addMask(int value, int mask) {
		return value - (value & mask) + mask;
	}

	/**
	 * Remove a mask from a value.
	 * 
	 * @param value
	 * @param mask
	 * @return the new value
	 */
	public static int removeMask(int value, int mask) {
		return value - (value & mask);
	}

	/**
	 * Test if a value contains a mask.
	 * 
	 * @param value
	 * @param mask
	 * @return true if the value has this mask, false otherwise.
	 */
	public static boolean hasMask(int value, int mask) {
		return (value & mask) == mask;
	}
}
