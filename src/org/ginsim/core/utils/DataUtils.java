package org.ginsim.core.utils;

import java.awt.Color;
import java.util.Vector;
import java.util.regex.Pattern;

public class DataUtils {

	public static final Integer IZ = new Integer(0);
	
	/**
	 * Sort in ascending order the specified arrays T and N in the same time
	 * 
	 * @param T array of integer
	 * @param N array of Object
	 * @return N sorted
	 */
	public static Object[] decrease(int[] T, Object[] N) {
		
		int i;
		int key;
		Object nodekey;
		for (int j = 1; j < T.length; j++) {
			key = T[j];
			nodekey = N[j];
			i = j - 1;
			while (i >= 0 && T[i] < key) {
				T[i + 1] = T[i];
				N[i + 1] = N[i];
				i = i - 1;
			}
			T[i + 1] = key;
			N[i + 1] = nodekey;

		}
		return N;
	}

	/**
	 * Sort in descending order the specified arrays T and N in the same time
	 * 
	 * @param T array of integer
	 * @param N array of Object
	 * @return N sorted
	 */
	public static Object[] increase(int[] T, Object[] N) {
		
		int i;
		int key;
		Object nodekey;
		for (int j = 1; j < T.length; j++) {
			key = T[j];
			nodekey = N[j];
			i = j - 1;
			while (i >= 0 && T[i] > key) {
				T[i + 1] = T[i];
				N[i + 1] = N[i];
				i = i - 1;
			}
			T[i + 1] = key;
			N[i + 1] = nodekey;

		}
		return N;
	}
	
	/**
	 * @param t
	 * @param obj
	 * @return the index of obj in the array t, or -1 if not found
	 */
	// TODO : REFACTORING ACTION
	// TODO : Remove if not used 
//	public static int arrayIndexOf(Object[] t, Object obj) {
//		if (obj == null) {
//			for (int i = 0; i < t.length; i++) {
//				if (t[i] == null) {
//					return i;
//				}
//			}
//			return -1;
//		}
//		for (int i = 0; i < t.length; i++) {
//			if (obj.equals(t[i])) {
//				return i;
//			}
//		}
//		return -1;
//	}

	/**
	 * @param t
	 * @param val
	 * @return the index of val in the array t, or -1 if not found
	 */
	// TODO : REFACTORING ACTION
	// TODO : Remove if not used 
//	public static int arrayIndexOf(int[] t, int val) {
//		for (int i = 0; i < t.length; i++) {
//			if (t[i] == val) {
//				return i;
//			}
//		}
//		return -1;
//	}
	
	
	/**
	 * Transform an array to a Vector
	 * 
	 * @param t the array we want to convert to vector
	 * @return the new Vector
	 */
	public static Vector getVectorFromArray(Object[] t) {
		
		Vector vect = new Vector(t.length);

		for (int i = 0; i < t.length; i++) {
			vect.add(t[i]);
		}
		return vect;
	}

	/**
	 * Indicates if the given string is a valid GINsim ID (contains only a-z, A-Z, 0-9, "_" or "-" characters)
	 * 
	 * @param id the string to test
	 * @return true if the given string can be used as ID
	 */
	public static boolean isValidId(String id) {
		return Pattern.compile("^[a-zA-Z0-9_-]+$").matcher(id).find();
	}

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
	
	/**
	 * Convert a 8-bit Color into a CSS-like string (without the #).<br>
	 * <i>Exemple : Color(255,127,0) -> "FF7F00"</i>
	 * 
	 * @param color the color to convert.
	 * @return String a string representation.
	 * 
	 */
	public static String getColorCode(Color color) {
		return Integer.toHexString(color.getRGB() & 0xffffff | 0x1000000).substring(1);
	}

	/**
	 * get a Color corresponding to a given color code.
	 * 
	 * @param code the hexadecimal color code
	 * @return the corresponding Color
	 */
	public static Color getColorFromCode(String code) throws NumberFormatException {
		return Color.decode(code);
	}

}
