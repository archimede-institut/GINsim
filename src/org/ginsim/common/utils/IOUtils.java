package org.ginsim.common.utils;

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

import org.ginsim.common.OpenHelper;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.exception.GsException;


public class IOUtils {

	protected static Map<String, OpenHelper> m_helper = new HashMap<String, OpenHelper>();

	public static void addHelperClass( String key, OpenHelper helper) {
		m_helper.put(key, helper);
	}
	
	
	public static OpenHelper getHelper( Object key){
		
		return (OpenHelper) m_helper.get( key);
	}
	
	public static String getLink(Object protocol, Object value) {
		
		OpenHelper helper = (OpenHelper) m_helper.get(protocol);
		if (helper != null) {
			return helper.getLink(protocol.toString(), value.toString());
		}
		return protocol + ":" + value;
	}
	
	public static boolean open(Object protocol, Object value) {
		
		OpenHelper helper = (OpenHelper) IOUtils.getHelper( protocol);
		if (helper != null) {
			return helper.open(protocol.toString(), value.toString());
		}
		return openURI(protocol + ":" + value);
	}
	
	/**
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean openURI(String uri) {
		
		try {
			Desktop.getDesktop().browse( new URI(uri));
			return true;
		} catch (Exception e) {
			LogManager.error( "OpenURI failed : " + uri);
			LogManager.error( e);
			return false;
		}
	}
	
	/**
	 * Open a file at the given path
	 * 
	 * @param filepath the path of the file
	 * @return true if it managed
	 */
	public static boolean openFile(String filepath) {
		
		File f;
		if (filepath.startsWith("//localhost/")) {
			f = new File(filepath.substring(12));
		} else {
			f = new File(filepath);
		}
		if (!f.exists()) {
			LogManager.error( "No such file : " + filepath);
			return false;
		}
		return openURI("file://" + filepath);
	}
	
	
	/**
	 * Produces a FileInputStream initialized with the given path
	 * 
	 * @param path the path for which the InputSTream is desired
	 * @return a FileInputStream initialized with the given path
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static InputStream getStreamForPath(String path) throws IOException,	FileNotFoundException {
		
		URL url = IOUtils.class.getResource( path);
		if (url != null) {
			return url.openStream();
		}
		return new FileInputStream(path);
	}

	/**
	 * Produces a StringBuffer initialized with the given path
	 * 
	 * @param file_path the path for which the StringBuffer is desired
	 * @return a StringBuffer initialized with the given path
	 * @throws IOException
	 */
	public static StringBuffer readFromFile(String file_path) throws IOException {
		
		StringBuffer sb = new StringBuffer(1024);
		readFromFile(file_path, sb);
		return sb;
	}

	/**
	 * Fill the given StringBuffer with the content of the file located at the given path
	 * 
	 * @param file_path the path to the file to read
	 * @param sb the StringBuffer to fill
	 * @throws IOException
	 */
	public static void readFromFile(String file_path, StringBuffer sb) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(file_path));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			sb.append(buf, 0, numRead);
		}
		reader.close();
	}
	
	/**
	 * Verify if the file at the given path can be opened in write mode
	 * 
	 * @param path the path of the file to test
	 * @return true if file exists and is writable or file does not exists and can be created
	 * @throws GsException
	 */
	public static boolean isFileWritable( String path) throws GsException {
		
		if (path == null || path.equals("")) {
			return false;
		}
		
		File file = new File(path);
		if (file.exists()) {

			if (file.isDirectory()) {
				throw new GsException(GsException.GRAVITY_ERROR,
						Translator.getString("STR_error_isdirectory"));
			}
			if (!file.canWrite()) {
				throw new GsException(GsException.GRAVITY_ERROR,
						Translator.getString("STR_error_notWritable"));
			}

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
	

}
