package fr.univmrs.tagc.common;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;

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
	private static PrintStream out = System.out;
	
	/**
	 * print the log in {@link System}.out
	 */
	public static void log() {
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"()");
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(Object msg) {
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(int msg) {
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(boolean msg) {
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(double msg) {
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(long msg) {
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
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
	public static void log_collection(Collection parents) {
		if (parents.size() == 0) {
			out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() []");
			return;
		}
		StringBuffer s = new StringBuffer("[");
		for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
			s.append(iterator.next()+", ");
		}
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s.substring(0, s.length()-2)+"]");
	}
	public static void log_collection(Object[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+", ");
		}
		s.append(parents[parents.length-1]+"]");
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	public static void log_collection(int[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+",");
		}
		s.append(parents[parents.length-1]+"]");
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	public static void log_collection(double[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+",");
		}
		s.append(parents[parents.length-1]+"]");
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	public static void log_collection(boolean[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+", ");
		}
		s.append(parents[parents.length-1]+"]");
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	
	/**
	 * print the last "levels" stack trace lines
	 * @param level
	 */
	public static void printStackTrace() {
		printStackTrace(Thread.currentThread().getStackTrace().length);
	}
	public static void printStackTrace(int levels) {
		StringBuffer s = new StringBuffer("");
		for (int i = 3; i < levels; i++) {
			StackTraceElement st = Thread.currentThread().getStackTrace()[i];
			s.append(st.toString());
//		    s.append(st.getLineNumber());
//		    s.append(':');
//			String clname = st.getClassName();
//		    s.append(clname.substring(clname.lastIndexOf('.')+1));
//		    s.append('#');
//		    s.append(st.getMethodName());
		    s.append("()\n");
		}
		out.println(s+"\n");
	}
 }
