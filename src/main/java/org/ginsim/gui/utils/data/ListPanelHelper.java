package org.ginsim.gui.utils.data;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

/**
 * Provide configuration and helper methods for a list panel.
 * The helper defines capability switches (reordering, adding, removing),
 * GUI hints such as available actions, and methods to create new items.
 * 
 * @author Aurelien Naldi
 * @param <T> the type of objects in the list
 */
abstract public class ListPanelHelper<T> {
	
	public static final String SEL_EMPTY="SEL_EMPTY", SEL_SINGLE="SEL_SINGLE", SEL_MULTIPLE="SEL_MULTIPLE";
	
	public int nbAction = 0;
	
	public int nbcol = 1;
	
	public Map<Class<?>, Component> m_right = null;

	public boolean canFilter = true;
	public boolean canOrder = true;
	public boolean canAdd = true;
	public boolean doInlineAddRemove = false;
	
	List<String> addOptions = null;

	public boolean canRemove = true;

	public ListEditionPanel<T> editPanel = null;
	public ListPanel<T> listPanel = null;

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

    public int doCreate(Object arg) {
        return -1;
    }

    public void create(Object arg) {
        int idx = doCreate(arg);
        if (idx > -1 && listPanel != null) {
            listPanel.refresh();
            listPanel.selectItem(idx);
        }
    }

	public void selectionChanged(int[] selection) {
		if (editPanel == null) {
			return;
		}
		if (selection == null) {
			selection = listPanel.getSelection();
		}
		selectionUpdated(selection);
	}
	
	abstract public void selectionUpdated(int[] selection);

	public void refresh() {
		if (listPanel != null) {
			listPanel.refresh();
		}
	}
	
	public void setListPanel(ListPanel<T> listPanel) {
		this.listPanel = listPanel;
	}
	
	public void setEditPanel(ListEditionPanel<T> editPanel) {
		this.editPanel = editPanel;
		fillEditPanel();
		selectionChanged(null);
	}
	
	public abstract void fillEditPanel();
		
	public boolean doRemove(int[] sel) {
        return false;
	}
	
	public boolean remove(int[] sel) {
		if (sel == null || sel.length < 1 || !canRemove) {
			return false;
		}
		
		if (doRemove(sel)) {
			refresh();
            listPanel.selectItem(sel[0]-1);
			return true;
		}
		return false;
	}
	
}
