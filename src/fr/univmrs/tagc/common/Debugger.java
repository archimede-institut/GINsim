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
	private static PrintStream out = System.err;
	private static int debugmask = 0;
	
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
	 * print the log in {@link System}.out and append msg to the end of the line if  mask match the debug mask
	 * @param msg the message to append at the end of the line
	 */
	public static void log(int mask, Object msg) {
		if ((debugmask & mask) != 0) log(msg);	
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(int msg) {
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line if  mask match the debug mask
	 * @param msg the message to append at the end of the line
	 */
	public static void log(int mask, int msg) {
		if ((debugmask & mask) != 0) log(msg);	
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(boolean msg) {
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line if  mask match the debug mask
	 * @param msg the message to append at the end of the line
	 */
	public static void log(int mask, boolean msg) {
		if ((debugmask & mask) != 0) log(msg);	
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(double msg) {
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line if  mask match the debug mask
	 * @param msg the message to append at the end of the line
	 */
	public static void log(int mask, double msg) {
		if ((debugmask & mask) != 0) log(msg);	
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void log(long msg) {
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line if  mask match the debug mask
	 * @param msg the message to append at the end of the line
	 */
	public static void log(int mask, long msg) {
		if ((debugmask & mask) != 0) log(msg);	
	}

	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
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
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void log_collection(int mask, Collection parents) {
		if ((debugmask & mask) != 0) log_collection(parents);	
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void log_collection(Object[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+", ");
		}
		s.append(parents[parents.length-1]+"]");
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void log_collection(int mask, Object[] parents) {
		if ((debugmask & mask) != 0) log_collection(parents);	
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void log_collection(int[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+",");
		}
		s.append(parents[parents.length-1]+"]");
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void log_collection(int mask, int[] parents) {
		if ((debugmask & mask) != 0) log_collection(parents);	
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void log_collection(double[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+",");
		}
		s.append(parents[parents.length-1]+"]");
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void log_collection(int mask, double[] parents) {
		if ((debugmask & mask) != 0) log_collection(parents);	
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void log_collection(boolean[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+", ");
		}
		s.append(parents[parents.length-1]+"]");
		out.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void log_collection(int mask, boolean[] parents) {
		if ((debugmask & mask) != 0) log_collection(parents);	
	}
	
	/**
	 * print the last "levels" stack trace lines
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

	/**
	 * Give the mask of the log you want to display
	 * @param debug the debug to set
	 */
	public static void setDebug(int debug) {
		Debugger.debugmask = debug;
	}
	/**
	 * Define the outputStream. (<tt>System.err</tt> by default)
	 * @param out the outputStream to set
	 */
	public static void setOut(PrintStream out) {
		Debugger.out = out;
	}
 }
