package org.ginsim.gui.shell;

import org.ginsim.graph.EditGroup;
import org.ginsim.graph.EditMode;

/**
 * Tentative replacement API for GsAction.
 * 
 * FIXME: plan/implement/polish FrameActions
 * 
 * @author Aurelien Naldi
 */
public interface FrameActions {

	
	public void updateRecentMenu();
	
	@Deprecated
	public void setCurrentMode(int mode, int submode, boolean lock);
	@Deprecated
	public int getCurrentMode();
	@Deprecated
	public int getCurrentSubmode();

	public void setCurrentMode(EditMode mode, boolean lock);
	public EditMode getCurrentEditMode();
	public EditGroup getCurrentGroup();

	public void changeModeIfUnlocked();
	public boolean shouldAutoAddNewElements();
}
