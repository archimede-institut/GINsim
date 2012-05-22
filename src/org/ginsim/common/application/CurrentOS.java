package org.ginsim.common.application;

/**
 * Detect the current Operating System and provide the information
 * in an easier way for some OS-dependent operations.
 * 
 * @author Aurelien Naldi
 */
public class CurrentOS {

	public final static int os;
	
	public final static int SYS_UNKNOWN = 0;
	public final static int SYS_LINUX   = 1;
	public final static int SYS_MACOSX  = 2;
	public final static int SYS_WINDOWS = 3;

	static {
		String os_name = System.getProperty( "os.name").toLowerCase();
		if (os_name.startsWith( "windows")) {
			os = SYS_WINDOWS;
		} else if (os_name.startsWith( "mac")) {
			os = SYS_MACOSX;
		} else if (os_name.startsWith( "linux")) {
			os = SYS_LINUX;
		} else {
			LogManager.debug("Unrecognized OS: "+ os_name);
			os = SYS_UNKNOWN;
		}
	}
}
