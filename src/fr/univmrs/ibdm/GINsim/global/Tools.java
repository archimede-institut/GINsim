package fr.univmrs.ibdm.GINsim.global;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jgraph.util.BrowserLauncher;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * some little utilities, might be replaced later by call to some external code, 
 * but stays here for now.
 */
public class Tools {

	public static final Integer IZ = new Integer(0);

	
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
	 * get an hexa color code for a given Color.
	 * @param color
	 * @return an haxadecimal color code (like 00FF00 for green)
	 */
	public static String getColorCode(Color color) {
		String scol;
		String ret = "";
		scol = Integer.toHexString(color.getRed()).toUpperCase();
		if (scol.length() == 1) {
			ret += "0"+scol;
		} else {
			ret += scol;
		}
		scol = Integer.toHexString(color.getGreen()).toUpperCase();
		if (scol.length() == 1) {
			ret += "0"+scol;
		} else {
			ret += scol;
		}
		scol = Integer.toHexString(color.getBlue()).toUpperCase();
		if (scol.length() == 1) {
			ret += "0"+scol;
		} else {
			ret += scol;
		}
		return ret;
	}
	/**
	 * get a Color corresponding to a given color code. 
	 * @param code the hexadecimal color code
	 * @return the correponding Color
	 */
	public static Color getColorFromCode(String code) {
		return Color.decode(code);
	}
	/**
	 * Open a file.
	 * On UNIX, let's rely on xdg-open and ignore the old mess.
	 * TODO: make it also work on mac/win
	 * 
	 * @param fileName
	 * @return true if it managed
	 */
	public static boolean openFile(String fileName) {
		try {
			Runtime.getRuntime().exec("xdg-open "+fileName);
			return true;
		} catch (Exception e1) {
		}
		System.out.println("how to open a file ??");
		return false;
	}

	/**
	 * run a web browser to visit "url"
	 * @param url the url to visit
	 */
	public static void webBrowse(String url) {
		try {
			try {
				Runtime.getRuntime().exec("xdg-open "+url);
			} catch (Exception e1) {
				BrowserLauncher.openURL(url);
			}
		} catch (IOException e) {
			GsEnv.error(new GsException(GsException.GRAVITY_NORMAL, Translator.getString("STR_browserError")), null);
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

}
