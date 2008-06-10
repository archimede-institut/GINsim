package fr.univmrs.tagc.common;

import java.awt.Color;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jgraph.util.BrowserLauncher;

import fr.univmrs.tagc.common.manageressources.Translator;

/**
 * some little utilities, might be replaced later by call to some external code, 
 * but stays here for now.
 */
public class Tools {

	public static final Integer IZ = new Integer(0);

	public static String OPEN_COMMAND = null;
	public static Object o_desktop = null;
	protected static Method met_browse = null;
	
	protected static Map m_helper = new HashMap();
	
	private static final ClassLoader cloader = ClassLoader.getSystemClassLoader();
	
	public static void addHelperClass(String key, OpenHelper helper) {
		m_helper.put(key, helper);
	}

	
	static {
		String os = System.getProperty("os.name").toLowerCase();
		Class cl_desktop;
		boolean supported = false;
		try {
			cl_desktop = cloader.loadClass("java.awt.Desktop");
			Method m = cl_desktop.getMethod("isDesktopSupported", null);
			if (((Boolean)m.invoke(null, null)).booleanValue()) {
				m = cl_desktop.getMethod("getDesktop", null);
				o_desktop = m.invoke(null, null);
				met_browse = cl_desktop.getMethod("browse", new Class[] {URI.class});
				supported = true;
			}
		} catch (Exception e) {
		}
		if (!supported) {
			System.out.println("open will use dirty hacks, consider upgrading to java6");
			if (os.startsWith("windows")) {
				OPEN_COMMAND = "open";
			} else if (os.startsWith("mac")) {
				OPEN_COMMAND = "open";
			} else if (os.startsWith("linux")) {
				OPEN_COMMAND = "xdg-open";
			}
		}
	}
	
	/**
	 * Sort in ascending order the specified arrays T and N in the same time
	 * @param T - array of integer
	 * @param N - array of Object
	 * @return N sorted
	 */
	public static Object[] decrease(int[] T,Object[] N){
		int i;
		int key;
		Object nodekey;
		for(int j=1;j<T.length;j++){
			key = T[j];
			nodekey = N[j];
			i = j-1;
			while(i>=0 && T[i]<key){
				T[i+1] = T[i];
				N[i+1] = N[i];
				i = i-1;
			}
			T[i+1]=key;
			N[i+1]=nodekey;
				
		}
		return N;
	}

	/**
	 * Sort in descending order the specified arrays T and N in the same time
	 * @param T - array of integer
	 * @param N - array of Object
	 * @return N sorted
	 */
	public static Object[] increase(int[] T,Object[] N){
		int i;
		int key;
		Object nodekey;
		for(int j=1;j<T.length;j++){
			key = T[j];
			nodekey = N[j];
			i = j-1;
			while(i>=0 && T[i]>key){
				T[i+1] = T[i];
				N[i+1] = N[i];
				i = i-1;
			}
			T[i+1]=key;
			N[i+1]=nodekey;
			
		}
		return N;
	}
	/**
	 * test if a string is a valid integer
	 * @param s the string
	 * @return true if it is a valid integer
	 */
	public static boolean isInteger(String s){
		try{
			Integer.parseInt(s);
		 }
		 catch (NumberFormatException e) {
			
			return false;
			}
		 return true;
	}
	
	/**
	 * Convert a 8-bit Color into a CSS-like string (without the #).<br>
	 * <i>Exemple : Color(255,127,0) -> "FF7F00"</i>
	 * 
	 * @param color the color to convert.
	 * @return String : a string representation.
	 * 
	 */
	public static String getColorCode(Color color) {
		return Integer.toHexString(color.getRGB() & 0xffffff | 0x1000000).substring(1);
	}
	
	/**
	 * get a Color corresponding to a given color code. 
	 * @param code the hexadecimal color code
	 * @return the corresponding Color
	 */
	public static Color getColorFromCode(String code) {
		return Color.decode(code);
	}
	
	/**
	 * Open a file.
	 * 
	 * @param fileName
	 * @return true if it managed
	 */
	public static boolean openFile(String fileName) {
		File f;
		if (fileName.startsWith("//localhost/")) {
			f = new File(fileName.substring(12));
		} else {
			f = new File(fileName);
		}
		if (!f.exists()) {
			System.out.println("no such file");
			return false;
		}
		return openURI("file://"+fileName);
	}

	public static boolean openURI(String uri) {
		if (met_browse != null) {
			try {
				met_browse.invoke(o_desktop, new Object[] {new URI(uri)});
				return true;
			} catch (Exception e) {
				System.out.println("open call failed!");
				e.printStackTrace();
			}
		}
		if (OPEN_COMMAND == null) {
			System.out.println("no open command is defined");
			return false;
		}
		try {
			Process process = Runtime.getRuntime().exec(new String[] {OPEN_COMMAND, uri});
			if (process.exitValue() != 0) {
				System.out.println("execution failed");
				return false;
			}
			return true;
		} catch (Exception e1) {
		}
		System.out.println("execution failed");
		return false;
	}
	
	public static String getLink(Object protocol, Object value) {
		OpenHelper helper = (OpenHelper)m_helper.get(protocol);
		if (helper != null) {
			return helper.getLink(protocol.toString(), value.toString());
		}
		return protocol+":"+value;
	}
	public static boolean open(Object protocol, Object value) {
		OpenHelper helper = (OpenHelper)m_helper.get(protocol);
		if (helper != null) {
			return helper.open(protocol.toString(), value.toString());
		}
		return openURI(protocol+":"+value);
	}
	/**
	 * run a web browser to visit "url"
	 * @param url the url to visit
	 */
	public static void webBrowse(String url) {
		try {
			try {
				Runtime.getRuntime().exec("xdg-open "+url);
				System.getProperty("os.name");
			} catch (Exception e1) {
				BrowserLauncher.openURL(url);
			}
		} catch (IOException e) {
			error(new GsException(GsException.GRAVITY_NORMAL, Translator.getString("STR_browserError")), null);
		}
	}

	/**
	 * @param string
	 * @param frame
	 * @return true if we can create this file or if the user accepts to overwrite it
	 * @throws GsException if an error occured
	 */
	public static boolean isFileWritable(String string, JFrame frame) throws GsException {
		if (string == null || string.equals("")) {
			return false;
		}
		File file = new File(string);
		if (file.exists()) {
			
			if (file.isDirectory()) {
                throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_error_isdirectory"));
			}
			if( !file.canWrite() ){
                throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_error_notWritable"));
			}
			int a = JOptionPane.showConfirmDialog(frame, Translator.getString("STR_question_overwrite"));
			return a==JOptionPane.OK_OPTION;
		}
		try {
			if (!file.createNewFile()) {
                throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_error_cantcreate"));
			}
            file.delete();
			return true;
		} catch (Exception e) {
            throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_error_io"));
		}
	}

	/**
	 * @param t the array we want to convert to vector
	 * @return the new Vector
	 */
	public static Vector getVectorFromArray(Object[] t) {
		Vector vect = new Vector(t.length);
		
		for (int i=0 ; i<t.length ; i++) {
			vect.add(t[i]);
		}
		return vect;
	}

    /**
     * @param msg
     * @param title
     * @return true if the user accepted
     */
    public static boolean ask(String msg, String title) {
        int ret = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.OK_CANCEL_OPTION);
        return ret == JOptionPane.OK_OPTION;
    }

    /**
     * @param t
     * @param obj
     * @return the index of obj in the array t, or -1 if not found
     */
    public static int arrayIndexOf(Object[] t, Object obj) {
        if (obj == null) {
            for (int i=0 ; i<t.length ; i++) {
                if (t[i] == null) {
                    return i;
                }
            }
            return -1;
        }
        for (int i=0 ; i<t.length ; i++) {
            if (obj.equals(t[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param t
     * @param val
     * @return the index of val in the array t, or -1 if not found
     */
    public static int arrayIndexOf(int[] t, int val) {
        for (int i=0 ; i<t.length ; i++) {
            if (t[i] == val) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean isValidId(String id) {
    	return Pattern.compile("^[a-zA-Z0-9_-]+$").matcher(id).find();
    }
	public static int addMask (int value, int mask) {
		return value - (value&mask) +mask;
	}
	public static int removeMask (int value, int mask) {
		return value - (value&mask);
	}
	public static boolean hasMask(int value, int mask) {
		return (value&mask) == mask;
	}

    /**
     * an error occured, give the user some feedback.
     * 
     * @param e
     * @param main
     */
    public static void error(GsException e, JFrame main) {
        int i = -1;
        switch (e.getGravity()) {
            case GsException.GRAVITY_INFO:
            case GsException.GRAVITY_NORMAL:
                i = JOptionPane.INFORMATION_MESSAGE;
                break;
        	default:
                i = JOptionPane.ERROR_MESSAGE;
        }
        JOptionPane.showMessageDialog(main, e.getMessage()+"\n", e.getTitle(),i);
    }

    /**
     * an error occured, give the user some feedback.
     * 
     * @param s
     * @param main
     */
    public static void error(String s, JFrame main) {
        JOptionPane.showMessageDialog(main, s+"\n", "error",JOptionPane.ERROR_MESSAGE);
    }

	public static InputStream getStreamForPath(String path) throws IOException, FileNotFoundException {
        URL url = Tools.class.getResource(path);
        if (url != null) {
        	return url.openStream();
        }
        return new FileInputStream(path);
	}
}
