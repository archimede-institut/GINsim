package org.ginsim;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;

public class OSXCallBack {
	   public void loadGINMLfile(String filename) {
			try {
				Graph<?,?> graph = GraphManager.getInstance().open(filename);
				GUIManager.getInstance().newFrame( graph);
			} catch (GsException e) {
				LogManager.error(e);
			}
	    }
	    
	    public void quit() {
	    	GUIManager.getInstance().quit();
	    }

}
