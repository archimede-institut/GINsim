package fr.univmrs.tagc.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * some little utilities, might be replaced later by call to some external code,
 * but stays here for now.
 */
public class Tools {

	public static final Integer IZ = new Integer(0);

	public final static int os;
	public final static int SYS_UNKNOWN = 0;
	public final static int SYS_LINUX = 1;
	public final static int SYS_MACOSX = 2;
	public final static int SYS_WINDOWS = 3;

	protected static Map<String, OpenHelper> m_helper = new HashMap<String, OpenHelper>();

	public static void addHelperClass(String key, OpenHelper helper) {
		m_helper.put(key, helper);
	}

	public static boolean HASGUI = true;

	static {
		String os_name = System.getProperty("os.name").toLowerCase();
		if (os_name.startsWith("windows")) {
			os = SYS_WINDOWS;
		} else if (os_name.startsWith("mac")) {
			os = SYS_MACOSX;
		} else if (os_name.startsWith("linux")) {
			os = SYS_LINUX;
		} else {
			os = SYS_UNKNOWN;
		}
	}

	/**
	 * Sort in ascending order the specified arrays T and N in the same time
	 * 
	 * @param T
	 *            - array of integer
	 * @param N
	 *            - array of Object
	 * @return N sorted
	 */
	public static Object[] decrease(int[] T, Object[] N) {
		int i;
		int key;
		Object nodekey;
		for (int j = 1; j < T.length; j++) {
			key = T[j];
			nodekey = N[j];
			i = j - 1;
			while (i >= 0 && T[i] < key) {
				T[i + 1] = T[i];
				N[i + 1] = N[i];
				i = i - 1;
			}
			T[i + 1] = key;
			N[i + 1] = nodekey;

		}
		return N;
	}

	/**
	 * Sort in descending order the specified arrays T and N in the same time
	 * 
	 * @param T
	 *            - array of integer
	 * @param N
	 *            - array of Object
	 * @return N sorted
	 */
	public static Object[] increase(int[] T, Object[] N) {
		int i;
		int key;
		Object nodekey;
		for (int j = 1; j < T.length; j++) {
			key = T[j];
			nodekey = N[j];
			i = j - 1;
			while (i >= 0 && T[i] > key) {
				T[i + 1] = T[i];
				N[i + 1] = N[i];
				i = i - 1;
			}
			T[i + 1] = key;
			N[i + 1] = nodekey;

		}
		return N;
	}

	/**
	 * test if a string is a valid integer
	 * 
	 * @param s
	 *            the string
	 * @return true if it is a valid integer
	 */
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {

			return false;
		}
		return true;
	}

	/**
	 * Convert a 8-bit Color into a CSS-like string (without the #).<br>
	 * <i>Exemple : Color(255,127,0) -> "FF7F00"</i>
	 * 
	 * @param color
	 *            the color to convert.
	 * @return String : a string representation.
	 * 
	 */
	public static String getColorCode(Color color) {
		return Integer.toHexString(color.getRGB() & 0xffffff | 0x1000000).substring(1);
	}

	/**
	 * get a Color corresponding to a given color code.
	 * 
	 * @param code
	 *            the hexadecimal color code
	 * @return the corresponding Color
	 */
	public static Color getColorFromCode(String code) throws NumberFormatException {
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
		return openURI("file://" + fileName);
	}

	public static boolean openURI(String uri) {
		try {
			Desktop.getDesktop().browse(new URI(uri));
			return true;
		} catch (Exception e) {
			System.out.println("openURI failed: " + e);
			return false;
		}
	}

	public static String getLink(Object protocol, Object value) {
		OpenHelper helper = (OpenHelper) m_helper.get(protocol);
		if (helper != null) {
			return helper.getLink(protocol.toString(), value.toString());
		}
		return protocol + ":" + value;
	}

	public static boolean open(Object protocol, Object value) {
		OpenHelper helper = (OpenHelper) m_helper.get(protocol);
		if (helper != null) {
			return helper.open(protocol.toString(), value.toString());
		}
		return openURI(protocol + ":" + value);
	}

	/**
	 * @param string
	 * @param frame
	 * @return true if we can create this file or if the user accepts to
	 *         overwrite it
	 * @throws GsException
	 *             if an error occured
	 */
	public static boolean isFileWritable(String string, Component frame) throws GsException {
		if (string == null || string.equals("")) {
			return false;
		}
		File file = new File(string);
		if (file.exists()) {

			if (file.isDirectory()) {
				throw new GsException(GsException.GRAVITY_ERROR,
						Translator.getString("STR_error_isdirectory"));
			}
			if (!file.canWrite()) {
				throw new GsException(GsException.GRAVITY_ERROR,
						Translator.getString("STR_error_notWritable"));
			}
			int a = JOptionPane.showConfirmDialog(frame,
					Translator.getString("STR_question_overwrite"));
			return a == JOptionPane.OK_OPTION;
		}
		try {
			if (!file.createNewFile()) {
				throw new GsException(GsException.GRAVITY_ERROR,
						Translator.getString("STR_error_cantcreate"));
			}
			file.delete();
			return true;
		} catch (Exception e) {
			throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_error_io"));
		}
	}

	/**
	 * @param t
	 *            the array we want to convert to vector
	 * @return the new Vector
	 */
	public static Vector getVectorFromArray(Object[] t) {
		Vector vect = new Vector(t.length);

		for (int i = 0; i < t.length; i++) {
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
			for (int i = 0; i < t.length; i++) {
				if (t[i] == null) {
					return i;
				}
			}
			return -1;
		}
		for (int i = 0; i < t.length; i++) {
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
		for (int i = 0; i < t.length; i++) {
			if (t[i] == val) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isValidId(String id) {
		return Pattern.compile("^[a-zA-Z0-9_-]+$").matcher(id).find();
	}

	public static int addMask(int value, int mask) {
		return value - (value & mask) + mask;
	}

	public static int removeMask(int value, int mask) {
		return value - (value & mask);
	}

	public static boolean hasMask(int value, int mask) {
		return (value & mask) == mask;
	}

	/**
	 * an error occurred, give the user some feedback.
	 * 
	 * @param e
	 * @param main
	 */
	public static void error(GsException e, Component main) {
		int i = -1;
		switch (e.getGravity()) {
		case GsException.GRAVITY_INFO:
		case GsException.GRAVITY_NORMAL:
			i = JOptionPane.INFORMATION_MESSAGE;
			break;
		default:
			i = JOptionPane.ERROR_MESSAGE;
		}
		if (HASGUI) {
			JOptionPane.showMessageDialog(main, e.getMessage() + "\n", e.getTitle(), i);
		} else {
			System.err.println(e.getTitle());
			System.err.println(e.getMessage());
		}
	}

	/**
	 * an error occurred, give the user some feedback.
	 * 
	 * @param s
	 * @param main
	 */
	public static void error(String s, Component main) {
		if (HASGUI) {
			JOptionPane.showMessageDialog(main, s + "\n", "error", JOptionPane.ERROR_MESSAGE);
		} else {
			System.err.println("error: " + s);
		}
	}

	public static InputStream getStreamForPath(String path) throws IOException,
			FileNotFoundException {
		URL url = Tools.class.getResource(path);
		if (url != null) {
			return url.openStream();
		}
		return new FileInputStream(path);
	}

	public static StringBuffer readFromFile(String file_path) throws IOException {
		StringBuffer sb = new StringBuffer(1024);
		readFromFile(file_path, sb);
		return sb;
	}

	public static void readFromFile(String file_path, StringBuffer sb) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file_path));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			sb.append(buf, 0, numRead);
		}
		reader.close();
	}
}
