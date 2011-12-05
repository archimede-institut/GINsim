package org.ginsim.gui.shell;

import java.awt.Frame;
import java.io.File;
import java.util.Map;

import javax.swing.JFileChooser;

import org.ginsim.graph.common.Graph;
import org.ginsim.utils.log.LogManager;


public class FileSelectionHelper {

	// TODO: remember previous path
	// and store it in the option file
	
	public static Graph<?,?> open(String path) {
		LogManager.error( "TODO: open files");
		return null;
	}
	public static Graph<?,?> open(String path, Map filter) {
		LogManager.error( "TODO: open filtered files");
		return null;
	}
	
	
	public static String selectSaveFilename( Frame parent) {
		return selectSaveFilename(parent, null);
	}
	public static String selectSaveFilename( Frame parent, String extension) {
		JFileChooser chooser = new JFileChooser();
		chooser.showSaveDialog(parent);
		// TODO: file filter and automatic extension
		File f = chooser.getSelectedFile();
		if (f == null) {
			return null;
		}
		return f.getAbsolutePath();
	}
	
	public static String selectOpenFilename( Frame parent) {
		return selectOpenFilename(parent, null);
	}
	public static String selectOpenFilename( Frame parent, String extension) {
		// TODO: remember path and so on
		JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog(parent);
		File f = chooser.getSelectedFile();
		if (f == null) {
			return null;
		}
		return f.getAbsolutePath();
	}
}
