package org.ginsim.common.utils;

public class EnvUtils {

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
			os = SYS_UNKNOWN;
		}
	}
}
