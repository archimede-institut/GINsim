package fr.univmrs.tagc.common;

import java.util.Iterator;
import java.util.List;

/**
 * A simple helper class to log things in System.out or System.err
 * 
 * The log schema is :
 *    lineNumber:className#methodName() msg\n
 *    
 * eg. log("a fancy message") will print the following line on System.out : 
 *    43:Debugger#getLineNumber() a fancy message\n
 *
 */
public class Debugger {
	/**
	 * print the log in {@link System}.out
	 */
	public static void log() {
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"()");
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(Object msg) {
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(int msg) {
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(boolean msg) {
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(double msg) {
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(long msg) {
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}

	/**
	 * print the log in {@link System}.err
	 */
	public static void logr() {
		System.err.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"()");
	}
	/**
	 * print the log in {@link System}.err and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void logr(Object msg) {
		System.err.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}

	private static int getLineNumber() {
	    return Thread.currentThread().getStackTrace()[3].getLineNumber();
	}

	private static String getClassName() {
		String clname = Thread.currentThread().getStackTrace()[3].getClassName();
	    return clname.substring(clname.lastIndexOf('.')+1);
	}

	private static String getMethodName() {
	    return Thread.currentThread().getStackTrace()[3].getMethodName();
	}
	public static void log_collection(List parents) {
		StringBuffer s = new StringBuffer("[");
		for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
			s.append(iterator.next()+", ");
		}
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s.substring(0, s.length()-2)+"]");
	}
	public static void log_collection(Object[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+", ");
		}
		s.append(parents[parents.length-1]+"]");
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	public static void log_collection(int[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+",");
		}
		s.append(parents[parents.length-1]+"]");
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	public static void log_collection(double[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+",");
		}
		s.append(parents[parents.length-1]+"]");
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	public static void log_collection(boolean[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+", ");
		}
		s.append(parents[parents.length-1]+"]");
		System.out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
}
