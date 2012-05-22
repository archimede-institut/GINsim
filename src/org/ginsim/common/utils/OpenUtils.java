package org.ginsim.common.utils;

import java.awt.Desktop;
import java.io.File;
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

	public static void addHelperClass( String key, OpenHelper helper) {
		m_helper.put(key, helper);
	}
	
	public static void addHelperClassAlias(String alias, String key) {
		OpenHelper helper = m_helper.get(key);
		if (helper != null) {
			m_helper.put(alias, helper);
		}
	}
	
	public static OpenHelper getHelper( Object key){
		
		return m_helper.get( key);
	}
	
	public static String getLink(Object protocol, Object value) {
		
		OpenHelper helper = m_helper.get(protocol);
		if (helper != null) {
			return helper.getLink(protocol.toString(), value.toString());
		}
		return protocol + ":" + value;
	}
	
	public static boolean open(Object protocol, Object value) {
		
		OpenHelper helper = getHelper( protocol);
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

}
