package org.ginsim.gui.utils.data;

import java.awt.Component;
import java.util.List;
import java.util.Map;

/**
 * Provide configuration and helper methods for a list panel.
 * The helper defines capabilities, like reordering, adding and removing,
 * GUI hints such as available actions and methods to create new items.
 * 
 * @author Aurelien Naldi
 * @param <T> the type of objects in the list
 */
public class ListPanelHelper<T> {
	
	public int nbAction = 0;
	
	public int nbcol = 1;
	
	public Map<Class<?>, Component> m_right = null;

	public boolean canFilter = true;
	public boolean canOrder = true;
	public boolean canAdd = true;
	public boolean doInlineAddRemove = true;
	
	List<String> addOptions = null;

	public boolean canRemove = true;
	
	public Object getColumn(T o, int column) {
		return o;
	}
	
	public String getColumnName(int column) {
		return "C"+column;
	}

	public void runAction(int row, int col) {
	}

	public Class getColumnClass(int i) {
		return String.class;
	}

	public Object getAction(int row, int col) {
		return null;
	}
}
