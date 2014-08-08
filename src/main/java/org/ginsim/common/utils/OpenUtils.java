package org.ginsim.common.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.ginsim.common.application.LogManager;

/**
 * Open files and links (providing extensible "protocols" for specialised links handlers)
 * 
 * @author Aurelien Naldi
 */
public class OpenUtils {

	protected static Map<String, OpenHelper> m_helper = new HashMap<String, OpenHelper>();

	/**
	 * Register a protocol handler.
	 * 
	 * @param key the name of the "protocol" to handle
	 * @param helper the handler itself
	 */
	public static void addHelperClass( String key, OpenHelper helper) {
		m_helper.put(key, helper);
	}

	/**
	 * Add an alias for an existing handler.
	 * Note that this will register the existing handler for the alias: it will not remember the alias
	 * and thus it will fail is called before registering the main handler.
	 * 
	 * @param alias the new name
	 * @param key the existing "protocol"
	 */
	public static void addHelperClassAlias(String alias, String key) {
		OpenHelper helper = m_helper.get(key);
		if (helper != null) {
			m_helper.put(alias, helper);
		}
	}

	/**
	 * Get the registered helper for a given "protocol"
	 * @param key the "protocol" name
	 * @return the registered handler or null.
	 */
	public static OpenHelper getHelper( Object key){
		
		return m_helper.get( key);
	}

	/**
	 * Get a standard link (URI) for a given (protocol,value) pair
	 * 
	 * @param protocol
	 * @param value
	 * @return an URI for this link.
	 */
	public static String getLink(Object protocol, Object value) {
		
		OpenHelper helper = m_helper.get(protocol);
		if (helper != null) {
			return helper.getLink(protocol.toString(), value.toString());
		}
		return protocol + ":" + value;
	}

	/**
	 * Open a link.
	 * It will look for a handler to open it and fallback to opening the URI obtained
	 * with the getLink() method.
	 * 
	 * @param protocol
	 * @param value
	 * 
	 * @return true if the link could be opened, false otherwise.
	 */
	public static boolean open(Object protocol, Object value) {
		
		OpenHelper helper = getHelper( protocol);
		if (helper != null) {
			return helper.open(protocol.toString(), value.toString());
		}
		return openURI(protocol + ":" + value);
	}
	
	/**
	 * Open an URI.
	 * This uses java's Desktop.browse() method and will fail if this method does not works.
	 * It should work on most recent java installations (tested of Linux, OSX and Windows)
	 * 
	 * @param uri the URI to open
	 * @return true if the link could be opened, false otherwise.
	 */
	public static boolean openURI(String uri) {
		
		try {
			Desktop.getDesktop().browse( new URI(uri));
			return true;
		} catch (Exception e) {
			try {
				// If java.awt.Desktop class is not supported tries xdg-open
				Runtime.getRuntime().exec("xdg-open " + uri);
				return true;
			} catch (IOException e1) {
				LogManager.error( "OpenURI failed : " + uri);
				LogManager.error( e);
				return false;
			}
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

}
