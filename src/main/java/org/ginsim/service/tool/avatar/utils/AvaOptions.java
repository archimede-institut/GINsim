package org.ginsim.service.tool.avatar.utils;

/** 
 * Functions to extract values from text commands
 * @author Rui Henriques
 * @version 1.0
 */
public class AvaOptions {

	/**
	 * Get the integer value of a given parameter
	 * @param param the name of the target parameter
	 * @param args arguments of a command
	 * @return the associated value for the given parameter
	 */
	public static int getIntValue(String param, String[] args) {
		for(int i=0, l=args.length; i<l; i++)
			if(param.equals(args[i])) return Integer.valueOf(args[i+1]);
		return -1;
	}
	
	/**
	 * Get the double value of a given parameter
	 * @param param the name of the target parameter
	 * @param args arguments of a command
	 * @return the associated value for the given parameter
	 */
	public static double getDoubleValue(String param, String[] args) {
		for(int i=0, l=args.length; i<l; i++)
			if(param.equals(args[i])) return Double.valueOf(args[i+1]);
		return -1;
	}

	/**
	 * Checks whether a given parameter is present
	 * @param param the name of the target parameter
	 * @param args arguments of a command
	 * @return true if the parameter is present
	 */
	public static boolean getBoolValue(String param, String[] args) {
		for(int i=0, l=args.length; i<l; i++)
			if(param.equals(args[i])) return true;
		return false;
	}

	/**
	 * Get the string value of a given parameter
	 * @param param the name of the target parameter
	 * @param args arguments of a command
	 * @return the associated value for the given parameter
	 */
	public static String getStringValue(String param, String[] args) {
		for(int i=0, l=args.length; i<l; i++)
			if(param.equals(args[i])) return args[i+1];
		return null;
	}


}
