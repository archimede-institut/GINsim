package org.ginsim.gui.shell;

import java.awt.Toolkit;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.ginsim.commongui.utils.GUIInfo;
import org.ginsim.gui.graph.GraphGUI;

/**
 * Manager for the list of actions available in a GUI.
 * 
 * @author Aurelien Naldi
 */
public interface FrameActionManager {

	public static final int MASK = GUIInfo.MASK;
	
	public void buildActions(GraphGUI<?, ?, ?> graph, JMenuBar menubar, JToolBar toolbar);
}
