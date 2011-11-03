package org.ginsim.gui.shell;

import org.ginsim.graph.EditGroup;
import org.ginsim.graph.EditMode;
import org.ginsim.graph.Graph;

/**
 * Tentative replacement API for GsAction.
 * 
 * FIXME: plan/implement/polish FrameActions
 * 
 * @author Aurelien Naldi
 */
public interface FrameActions {

	
	public void setCurrentMode(EditMode mode, boolean lock);
	public EditMode getCurrentEditMode();
	public EditGroup getCurrentGroup();

	public void changeModeIfUnlocked();
	public boolean shouldAutoAddNewElements();

	public void setGraph(Graph<?, ?> graph);
}
