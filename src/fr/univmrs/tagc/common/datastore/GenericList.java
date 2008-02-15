package fr.univmrs.tagc.common.datastore;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


/**
 * generic interface for list of objects, a common UI will allow to show it,
 * and eventually offer generic services: reordering, adding/removing objects...
 */
abstract public class GenericList {

	public boolean canOrder = false;
	public boolean canEdit = false;
	public boolean canAdd = false;
	public boolean canRemove = false;
	public boolean doInlineAddRemove = false;
	public int nbAction = 0;
	public int nbcol = 1;
	
	protected String title = "";
	
	protected Class[] t_type = null;
	protected Map m_editor = null;
	
	public MultiColHelper mcolHelper = null;
	protected Vector v_listeners = new Vector();
	
	
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
    abstract public int getNbElements(String filter);
    
    /**
     * @param filter TODO
     * @param i
     * @return the ith element of the list
     */
    abstract public Object getElement(String filter, int i);
    
    
    /**
     * edit an element.
     * @param filter TODO
     * @param row the suggested position
     * @param col TODO
     * @param o the result of the edit
     * @return true if the list has changed
     */
    abstract public boolean edit(String filter, int row, int col, Object o);
    
    public int add() {
    	return add(getNbElements(null), 0, 0);
    }
    /**
     * add an element.
     * @param x TODO
     * @param y TODO
     * @return the index of the added element, or -1 if none
     */
    abstract public int add(int position, int x, int y);
    
    /**
     * add an element.
     * @param filter TODO
     * @param t_index the elements to remove
     * @return true if the list has changed
     */
    abstract public boolean remove(String filter, int[] t_index);
    
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
	abstract public void run(String filter, int row, int col);
	
	public String getTitle() {
		return title;
	}
	public String getColName(int col) {
		return null;
	}
    public int getNbCol() {
    	return nbcol+nbAction;
    }
	public Object getAction(String filter, int row, int col) {
		return "->";
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
		return i;
	}
	public int indexOf(Object anItem) {
		for (int i=0 ; i<getNbElements(null) ; i++) {
			if (getElement(null, i).equals(anItem)) {
				return i;
			}
		}
		return -1;
	}
}
