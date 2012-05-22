package org.ginsim.common.utils;

import java.awt.Color;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Manipulation of simple integer masks.
 * 
 * @author Aurelien Naldi
 * @author Duncan Berenguier
 */
public class MaskUtils {

	/**
	 * 
	 * @param value
	 * @param mask
	 * @return
	 */
	public static int addMask(int value, int mask) {
		return value - (value & mask) + mask;
	}

	/**
	 * 
	 * @param value
	 * @param mask
	 * @return
	 */
	public static int removeMask(int value, int mask) {
		return value - (value & mask);
	}

	/**
	 * 
	 * @param value
	 * @param mask
	 * @return
	 */
	public static boolean hasMask(int value, int mask) {
		return (value & mask) == mask;
	}
}
