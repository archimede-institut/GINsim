package org.ginsim.gui.graph;

import java.awt.Component;

/**
 * An object in charge of graphical edition of an object type
 * 
 * @author Aurelien Naldi
 *
 * @param <T>
 */
public interface GUIEditor<T> {

	public void setEditedItem(T item);
	
	public Component getComponent();
}
