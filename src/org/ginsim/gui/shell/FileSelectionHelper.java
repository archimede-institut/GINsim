package org.ginsim.gui.shell;

import java.awt.Frame;
import java.io.File;
import java.util.Map;

import javax.swing.JFileChooser;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.utils.log.LogManager;


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
		return selectSaveFilename(parent, (GsFileFilter) null);
	}
	
	public static String selectSaveFilename( Frame parent, String[] extensions, String description){
		
		GsFileFilter file_filter = new GsFileFilter(extensions, description);
		return selectSaveFilename( parent, file_filter);
	}
	
	public static String selectSaveFilename( Frame parent, GsFileFilter file_filter) {
		JFileChooser chooser = new JFileChooser();
		if( file_filter != null){
			chooser.setFileFilter( file_filter);
		}
		chooser.showSaveDialog(parent);
		// TODO: file filter and automatic extension
		File f = chooser.getSelectedFile();
		if (f == null) {
			return null;
		}
		return f.getAbsolutePath();
	}
	
	public static String selectOpenFilename( Frame parent) {
		return selectOpenFilename(parent, (GsFileFilter) null);
	}
	
	public static String selectOpenFilename( Frame parent, String[] extensions, String description){
		
		GsFileFilter file_filter = new GsFileFilter( extensions, description);
		return selectOpenFilename( parent, file_filter);
	}
	
	public static String selectOpenFilename( Frame parent, GsFileFilter file_filter) {
		// TODO: remember path and so on
		JFileChooser chooser = new JFileChooser();
		if( file_filter != null){
			chooser.setFileFilter( file_filter);
		}
		chooser.showOpenDialog(parent);
		File f = chooser.getSelectedFile();
		if (f == null) {
			return null;
		}
		return f.getAbsolutePath();
	}
}
