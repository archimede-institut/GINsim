package org.ginsim.gui.utils;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

import org.ginsim.common.OpenHelper;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.core.utils.log.LogManager;



public class GUIIOUtils {

	
	public static boolean open(Object protocol, Object value) {
		
		OpenHelper helper = (OpenHelper) IOUtils.getHelper( protocol);
		if (helper != null) {
			return helper.open(protocol.toString(), value.toString());
		}
		return openURI(protocol + ":" + value);
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
	
}
