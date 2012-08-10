package org.ginsim.gui.utils.data;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JLabel;

/**
 * Provide configuration and helper methods for a list panel.
 * The helper defines capabilities, like reordering, adding and removing,
 * GUI hints such as available actions and methods to create new items.
 * 
 * @author Aurelien Naldi
 * @param <T> the type of objects in the list
 */
public class ListPanelHelper<T> {
	
	private static final String SEL_EMPTY="SEL_EMPTY", SEL_SINGLE="SEL_SINGLE", SEL_MULTIPLE="SEL_MULTIPLE";
	
	public int nbAction = 0;
	
	public int nbcol = 1;
	
	public Map<Class<?>, Component> m_right = null;

	public boolean canFilter = true;
	public boolean canOrder = true;
	public boolean canAdd = true;
	public boolean doInlineAddRemove = false;
	
	List<String> addOptions = null;

	public boolean canRemove = true;

	private ListEditionPanel<T> editPanel;
	
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
	
	public Object[] getCreateTypes() {
		return null;
	}
	
	public int create(Object arg) {
		return -1;
	}
	
	public void selectionChanged(int[] selection) {
		if (editPanel == null) {
			return;
		}
		
		if (selection == null || selection.length < 1) {
			editPanel.showPanel(SEL_EMPTY);
			return;
		}
		
		if (selection.length == 1) {
			// TODO: update edit panel
			editPanel.showPanel(SEL_SINGLE);
			return;
		}
		
		// TODO: update edit panel
		editPanel.showPanel(SEL_MULTIPLE);
	}
	
	public void setEditPanel(ListEditionPanel<T> editPanel) {
		this.editPanel = editPanel;
		editPanel.addPanel(getEmptyPanel(), SEL_EMPTY);
		editPanel.addPanel(getSingleSelectionPanel(), SEL_SINGLE);
		editPanel.addPanel(getMultipleSelectionPanel(), SEL_MULTIPLE);
	}
	
	public Component getEmptyPanel() {
		return new JLabel("no selection");
	}
	
	public Component getSingleSelectionPanel() {
		return new JLabel("single selection");
	}
	
	public Component getMultipleSelectionPanel() {
		return new JLabel("Multiple selection");
	}
	
}
