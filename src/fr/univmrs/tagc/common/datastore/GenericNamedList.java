package fr.univmrs.tagc.common.datastore;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage a list of named objects, and add some convenience methods
 * to create, reorder and filter elements.
 * It should help dealing with lists and have a common GUI to edit them.
 */
abstract public class GenericNamedList<T> extends ArrayList<T> {
	private static final long serialVersionUID = -6886269451843627038L;
	
	/**
	 * Get the names of available add modes.
	 * @return a list of names, null if unneeded 
	 */
	public List<String> getAddModes() {
		return null;
	}

	/**
	 * Get the capabilities of this list.
	 * Defaults to reorder only.
	 * 
	 * @return the capabilities object
	 */
	public GenericNamedListCapabilities getCapabilities() {
		return GenericNamedListCapabilities.REORDER;
	}
	
    /**
     * @return true if the entries have an associated action (see <code>run(int)</code> to launch it)
     */
    public int getActionCount() {
    	return 0;
    }

    /**
     * edit internal data of an element.
     * 
     * @param object the edited object
     * @param col the "column" number identifying the edited part
     * @param value the value that is set
     * @return true if the list has changed
     */
    abstract public boolean doEdit(T object, int col, Object value);

    /**
     * Signal forwarding method, does nothing for non-listenable lists
     */
    protected void refresh() {
    }

    /**
     * Signal forwarding method, does nothing for non-listenable lists
     */
	protected void removed(T item, int index) {
	}

    public boolean edit(int row, int col, Object value) {
    	T object = get(row);
    	boolean changed = doEdit(object, col, value);
    	if (changed) {
    		refresh();
    	}
    	return changed;
    }

    /**
     * create a new element.
     * @param name
     * @param mode
     * @return the created element, null if failed
     */
    abstract public T doCreate(String name, int mode);
    
    /**
     * Add a new element at the selected position.
     * 
     * @param pos the desired position, off-bound values means at the end.
     * @param mode add option if necessary
     * 
     * @return the position of the created item or -1 if failed
     */
    public int add(int pos, int mode) {
    	T o = doCreate(null, mode);
    	if (o == null) {
    		return -1;
    	}
    	if (pos < 0 || pos >= size()) {
    		pos = size();
    	}
    	add(pos, o);
    	return pos;
    }
    
    /**
     * Move a set of elements.
     * 
     * @param selection the elements to move
     * @param diff how much do we want to move it
     * 
     * @return true if the list was changed
     */
	public boolean move(int[] selection, int diff) {
		if (diff == 0 || selection == null || selection.length == 0 ||
				diff < 0 && selection[0] <= -(diff+1) ||
				diff > 0 && selection[selection.length-1] >= size() - diff) {
			return false;
		}
		if (diff > 0) {
			doMoveDown(selection, diff);
		} else {
			doMoveUp(selection, diff);
		}
	    return true;
	}

	protected void doMoveUp(int[] sel, int diff) {
	    for (int i=0 ; i<sel.length ; i++) {
		    int a = sel[i];
		    if (a >= diff) {
			    moveElement(a, a+diff);
			    sel[i] += diff;
		    }
	    }
	}
	protected void doMoveDown(int[] sel, int diff) {
	    for (int i=sel.length-1 ; i>=0 ; i--) {
		    int a = sel[i];
		    if (a < size()+diff) {
			    moveElement(a, a+diff);
			    sel[i] += diff;
		    }
	    }
	}

	protected boolean moveElement(int src, int dst) {
		if (!getCapabilities().order) {
			return false;
		}
		if (src < 0 || dst < 0 || src >= size() || dst >= size()) {
			return false;
		}
		T o = remove(src);
		add(dst, o);
		return true;
	}

	public boolean remove(int[] t_index) {
		if (!getCapabilities().remove) {
			return false;
		}
		for (int i = t_index.length - 1 ; i > -1 ; i--) {
			T item = remove(t_index[i]);
			removed(item, t_index[i]);
		}
		return true;
	}

	/**
	 * Does the object match the filter?
	 * By default it checks if the name contains the filter
	 * 
	 * @param filter
	 * @param o
	 * @return
	 */
	public boolean match(String filter, T o) {
		return o.toString().toLowerCase().contains(filter.toLowerCase());
	}

    /**
	 * Run the action associated to an entry.
	 * 
	 * @param row index of the entry
	 * @param col index of the action
	 */
	public void run(int row, int col) {
	}
	
	public String getColName(int col) {
		return null;
	}
    public int getNbCol() {
    	return 1 + getActionCount();
    }
	public Object getAction(int row, int col) {
		return "->";
	}
}
