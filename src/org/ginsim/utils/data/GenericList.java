package org.ginsim.utils.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * generic interface for list of objects, a common UI will allow to show it,
 * and eventually offer generic services: reordering, adding/removing objects...
 */
abstract public class GenericList<T> {

	public boolean canOrder = false;
	public boolean canEdit = false;
	public boolean canAdd = false;
	public boolean canCopy = false;
	public boolean canRemove = false;
	public boolean doInlineAddRemove = false;
	public int nbAction = 0;
	public int nbcol = 1;
	public List<String> addOptions = null;
	
	protected String title = "";
	
	protected Class[] t_type = null;
	protected Map m_editor = null;
	
	public MultiColHelper<T> mcolHelper = null;
	protected List<GenericListListener> v_listeners = new ArrayList<GenericListListener>();
	
	public List getAddOptions() {
		return addOptions;
	}
    /**
     * can elements of this list be reordered interactively ?
     * @return true if it is reorder-friendly
     */
    public boolean canOrder() {
    	return canOrder;
    }
    /**
     * can elements of this list be edited interactively ?
     * @return true if one can edit elements
     */
    public boolean canEdit() {
    	return canEdit;
    }
    /**
     * can new elements of this list be obtained by copying existing ones ?
     * @return true if one can copy elements
     */
    public boolean canCopy() {
    	return canCopy;
    }
    /**
     * can elements of this list be added interactively ?
     * @return true if one can add elements
     */
    public boolean canAdd() {
    	return canAdd;
    }
    /**
     * can elements of this list be removed interactively ?
     * @return true if one can remove elements
     */
    public boolean canRemove() {
    	return canRemove;
    }
    public boolean doInlineAddRemove() {
    	return doInlineAddRemove;
    }
    /**
     * @return true if the entries have an associated action (see <code>run(int)</code> to launch it)
     */
    public int getActionCount() {
    	return nbAction;
    }

    public void addListListener(GenericListListener l) {
    	v_listeners.add(l);
    }
    public void removeListListener(GenericListListener l) {
    	v_listeners.remove(l);
    }
    /**
     * if this list can deal with object of several types,
     * @return the list of available types, or null if none.
     */
    public Class[] getObjectType() {
    	return t_type;
    }
    
    public Map getCellEditor() {
    	return m_editor;
    }

    /**
     * @param filter TODO
     * @return the size of the list
     */
    abstract public int getNbElements(String filter, int startIndex);
    public int getNbElements(String filter) {
    	return getNbElements(filter, 0);
    }
    public int getNbElements() {
    	return getNbElements(null, 0);
    }
    
    /**
     * @param filter TODO
     * @param i
     * @return the ith element of the list
     */
    abstract public T getElement(String filter, int startIndex, int i);
    public Object getElement(String filter, int i) {
    	return getElement(filter, 0, i);
    }
    
    /**
     * edit an element.
     * @param filter TODO
     * @param row the suggested position
     * @param col TODO
     * @param o the result of the edit
     * @return true if the list has changed
     */
    abstract public boolean edit(String filter, int startIndex, int row, int col, T o);
    public boolean edit(String filter, int row, int col, T o) {
    	return edit(filter, 0, row, col, o);
    }
    
    public int add() {
    	return add(getNbElements(null,0), 0);
    }
    /**
     * add an element.
     * @param mode
     * @return the index of the added element, or -1 if none
     */
    abstract public int add(int position, int mode);
    
    /**
     * add an element.
     * @param filter TODO
     * @param t_index the elements to remove
     * @return true if the list has changed
     */
    abstract public boolean remove(String filter, int startIndex, int[] t_index);
    public boolean remove(String filter, int[] t_index) {
    	return remove(filter, 0, t_index);
    }
    
    /**
     * move a set of elements.
     * @param selection the elements to move
     * @param dst its new position
     * @return true if the list was changed
     */
    abstract public boolean move(int[] selection, int dst);
	/**
	 * Run the action associated to an entry
	 * @param row index of the entry
	 * @param col index of the action
	 */
	abstract public void run(String filter, int startIndex, int row, int col);
	public void run(String filter, int row, int col) {
		run(filter, 0, row, col);
	}
	
	public String getTitle() {
		return title;
	}
	public String getColName(int col) {
		return null;
	}
    public int getNbCol() {
    	return nbcol+nbAction;
    }
	public Object getAction(String filter, int startIndex, int row, int col) {
		return "->";
	}
	public Object getAction(String filter, int row, int col) {
		return getAction(filter, 0, row, col);
	}
	public void refresh() {
		if (v_listeners == null) {
			return;
		}
		Iterator it = v_listeners.iterator();
		while (it.hasNext()) {
			((GenericListListener)it.next()).contentChanged();
		}
	}
	public int getRealIndex(String filter, int i) {
		return getRealIndex(filter, 0, i);
	}
	public int getRealIndex(String filter, int startIndex, int i) {
		return i-startIndex;
	}
	public int indexOf(Object anItem) {
		for (int i=0 ; i<getNbElements(null,0) ; i++) {
			if (getElement(null, 0, i).equals(anItem)) {
				return i;
			}
		}
		return -1;
	}
}
