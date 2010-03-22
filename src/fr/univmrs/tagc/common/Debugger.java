package fr.univmrs.tagc.common;

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
	public static void log(String msg) {
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
	public static void logr(String msg) {
		System.err.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}

	private static int getLineNumber() {
	    return Thread.currentThread().getStackTrace()[3].getLineNumber();
	}

	private static String getClassName() {
	    return Thread.currentThread().getStackTrace()[3].getClassName();
	}

	private static String getMethodName() {
	    return Thread.currentThread().getStackTrace()[3].getMethodName();
	}
}
