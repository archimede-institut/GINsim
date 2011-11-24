package fr.univmrs.tagc.common;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
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
	private static PrintStream debugOut = System.err;
	private static int debugmask = 0;
	
	// Logging verboseLevel : 0 = log; 1= info; 2 = trace
	private static int verboseLevel = 0;
	
	private static boolean debugMode = false;
	
	private static PrintWriter logOut = null;
	private static PrintWriter traceOut = null;
	private static PrintWriter infoOut = null;
	
	/**
	 * Initialize the manager
	 * 
	 * @param output_dir the dire where log files will be created
	 * @param verbose the verbose level (0 = log; 1= info; 2 = trace)
	 * @throws IOException
	 */
	public static void init( String output_dir, int verbose, boolean debug) throws IOException{
		
		logOut = new PrintWriter( output_dir + "/error.txt");
		
		traceOut = new PrintWriter( output_dir + "/trace.txt");
		
		infoOut = new PrintWriter( output_dir + "/info.txt");
		
		setVerbose( verbose);
		
		debugMode = debug;
	}
	
	/**
	 * Set the verbose level of the logs
	 * 0 = log; 1= info; 2 = trace
	 * 
	 * @param verbose
	 */
	public static void setVerbose( int verbose){
		
		if( verbose >=0 && verbose <= 2){
			
			verboseLevel = verbose;
		}
		else{
			debug( "Incorrect value for verbose level : " + verboseLevel);
		}
	}
	
	/**
	 * Log an error. The message is logged to all the log files according the verbose level
	 * 
	 * @param msg the message to log
	 */
	public static void error( Object msg){
		
		if( logOut == null){
			return;
		}
		if( msg instanceof Throwable){
			logOut.write( getLineNumber()+":"+getClassName()+"#"+getMethodName()+"():: Exception :");
			((Throwable) msg).printStackTrace( logOut);
		}
		else{
			logOut.write( getLineNumber()+":"+getClassName()+"."+getMethodName()+"():: "+msg.toString());
		}
		logOut.flush();
		
		if( debugMode){
			System.err.print( getLineNumber()+":"+getClassName()+"."+getMethodName()+"():: "+msg.toString());
		}
	}
	
	/**
	 * Log an info. The message is logged to the info and trace files according the verbose level
	 * 
	 * @param msg the message to log
	 */
	public static void info( Object msg){
		
		if( infoOut == null){
			return;
		}
		if( verboseLevel >= 1){
			infoOut.write( getLineNumber()+":"+getClassName()+"."+getMethodName()+"():: "+msg.toString());
			infoOut.flush();
			if( debugMode){
				System.out.print( getLineNumber()+":"+getClassName()+"."+getMethodName()+"():: "+msg.toString());
			}
		}
	}
	
	/**
	 * Log a trace. The message is logged to the trace file only according the verbose level
	 * 
	 * @param msg the message to log
	 */
	public static void trace( Object msg){
		
		Debugger.trace( msg, true);
	}
	
	/**
	 * Log a trace. The message is logged to the trace file only according the verbose level
	 * 
	 * @param msg the message to log
	 */
	public static void trace( Object msg, boolean line_info){
		
		if( traceOut == null){
			return;
		}
		
		if( verboseLevel >= 2){
			if( !line_info){
				traceOut.write( msg.toString());
				traceOut.flush();
				if( debugMode){
					System.out.print( msg.toString());
				}
			}
			else{
				traceOut.write( getLineNumber()+":"+getClassName()+"."+getMethodName()+"():: "+msg.toString());
				traceOut.flush();
				if( debugMode){
					System.out.print( getLineNumber()+":"+getClassName()+"."+getMethodName()+"():: "+msg.toString());
				}
			}

		}
		
	}
	
	// *********************************************************************************
	// **************************   DEBUGGING METHODS **********************************
	// *********************************************************************************
	
	
	/**
	 * print the log in {@link System}.out
	 */
	public static void debug() {
		
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"()");
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void debug(Object msg) {
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line if  mask match the debug mask
	 * @param msg the message to append at the end of the line
	 */
	public static void debug(int mask, Object msg) {
		if ((debugmask & mask) != 0) debug(msg);	
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void debug(int msg) {
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line if  mask match the debug mask
	 * @param msg the message to append at the end of the line
	 */
	public static void debug(int mask, int msg) {
		if ((debugmask & mask) != 0) debug(msg);	
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void debug(boolean msg) {
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line if  mask match the debug mask
	 * @param msg the message to append at the end of the line
	 */
	public static void debug(int mask, boolean msg) {
		if ((debugmask & mask) != 0) debug(msg);	
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void debug(double msg) {
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line if  mask match the debug mask
	 * @param msg the message to append at the end of the line
	 */
	public static void debug(int mask, double msg) {
		if ((debugmask & mask) != 0) debug(msg);	
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line
	 * @param msg the message to append at the end of the line
	 */
	public static void debug(long msg) {
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+msg);
	}
	/**
	 * print the log in {@link System}.out and append msg to the end of the line if  mask match the debug mask
	 * @param msg the message to append at the end of the line
	 */
	public static void debug(int mask, long msg) {
		if ((debugmask & mask) != 0) debug(msg);	
	}

	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void debug_collection(Collection parents) {
		if (parents.size() == 0) {
			debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() []");
			return;
		}
		StringBuffer s = new StringBuffer("[");
		for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
			s.append(iterator.next()+", ");
		}
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s.substring(0, s.length()-2)+"]");
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void debug_collection(int mask, Collection parents) {
		if ((debugmask & mask) != 0) debug_collection(parents);	
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void debug_collection(Object[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+", ");
		}
		s.append(parents[parents.length-1]+"]");
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void debug_collection(int mask, Object[] parents) {
		if ((debugmask & mask) != 0) debug_collection(parents);	
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void debug_collection(int[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+",");
		}
		s.append(parents[parents.length-1]+"]");
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void debug_collection(int mask, int[] parents) {
		if ((debugmask & mask) != 0) debug_collection(parents);	
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void debug_collection(double[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+",");
		}
		s.append(parents[parents.length-1]+"]");
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void debug_collection(int mask, double[] parents) {
		if ((debugmask & mask) != 0) debug_collection(parents);	
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void debug_collection(boolean[] parents) {
		StringBuffer s = new StringBuffer("[");
		for (int i = 0; i < parents.length-1; i++) {
			s.append(parents[i]+", ");
		}
		s.append(parents[parents.length-1]+"]");
		debugOut.println(getLineNumber()+":"+getClassName()+"#"+getMethodName()+"() "+s);
	}
	/**
	 * print the log in {@link System}.out and append the content of the collection to the end of the line if mask match the debug mask
	 */
	public static void debug_collection(int mask, boolean[] parents) {
		if ((debugmask & mask) != 0) debug_collection(parents);	
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
		debugOut.println(s+"\n");
	}

	private static int getLineNumber() {
	    return Thread.currentThread().getStackTrace()[4].getLineNumber();
	}

	private static String getClassName() {
		String clname = Thread.currentThread().getStackTrace()[4].getClassName();
	    return clname.substring(clname.lastIndexOf('.')+1);
	}

	private static String getMethodName() {
	    return Thread.currentThread().getStackTrace()[4].getMethodName();
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
		Debugger.debugOut = out;
	}
 }
