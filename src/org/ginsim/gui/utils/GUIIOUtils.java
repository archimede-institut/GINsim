package org.ginsim.gui.utils;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

import fr.univmrs.tagc.common.Debugger;

public class GUIIOUtils {

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
			Debugger.error( "No such file : " + filepath);
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
			Debugger.error( "OpenURI failed : " + uri);
			Debugger.error( e);
			return false;
		}
	}
	
}
