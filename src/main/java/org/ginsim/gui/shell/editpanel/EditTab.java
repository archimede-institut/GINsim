package org.ginsim.gui.shell.editpanel;

import java.awt.Component;

import org.ginsim.gui.graph.GraphSelection;

/**
 * Simple interface for edition panels.
 * They provide a title, a component, and can decide to be disabled depending on the selection.
 */
public interface EditTab {

	/**
	 * @return the title to be used by this tab
	 */
	public String getTitle();

	public Component getComponent();
	
	public boolean isActive( GraphSelection<?, ?> selection);
}
