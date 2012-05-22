package org.ginsim;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;

/**
 * Provide Callbacks for default actions in an OSX application.
 * 
 * @author Duncan Berenguier
 */
public class OSXCallBack {
	/**
	 * Callback to handle opening associated files.
	 * 
	 * @param filename
	 */
	public void loadGINMLfile(String filename) {
		try {
			Graph<?,?> graph = GraphManager.getInstance().open(filename);
			GUIManager.getInstance().newFrame( graph);
		} catch (GsException e) {
			LogManager.error(e);
		}
	}
	    
	/**
	 * Quit callback: forward the event to the GUIManager.
	 */
	public void quit() {
		GUIManager.getInstance().quit();
	}
}
