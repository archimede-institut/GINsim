package org.ginsim.utils.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage a list of named objects, and add some convenience methods
 * to create, reorder and filter elements.
 * It should help dealing with lists and have a common GUI to edit them.
 */
abstract public class ListenableGenericNamedList<T> extends GenericNamedList<T> {
	
	protected List<GenericListListener> v_listeners = new ArrayList<GenericListListener>();
	
    public void addListListener(GenericListListener l) {
    	v_listeners.add(l);
    }
    public void removeListListener(GenericListListener l) {
    	v_listeners.remove(l);
    }
    
    protected void refresh() {
		if (v_listeners == null) {
			return;
		}
		for (GenericListListener l: v_listeners) {
			l.contentChanged();
		}
    }

	protected void removed(T item, int index) {
		if (v_listeners == null) {
			return;
		}
		for (GenericListListener l: v_listeners) {
			l.itemRemoved(item, index);
		}
	}
}
