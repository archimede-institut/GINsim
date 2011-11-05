package org.ginsim.gui.shell;

import java.awt.Toolkit;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.ginsim.gui.graph.GraphGUI;

/**
 * Tentative replacement API for GsAction.
 * 
 * FIXME: plan/implement/polish FrameActions
 * 
 * @author Aurelien Naldi
 */
public interface FrameActions {

	public static final int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	public void setGraphGUI(GraphGUI<?, ?, ?> graph, JMenuBar menubar, JToolBar toolbar);
}
