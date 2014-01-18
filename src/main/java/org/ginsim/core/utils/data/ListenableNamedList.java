package org.ginsim.core.utils.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aurelien Naldi
 */
public class ListenableNamedList<T extends NamedObject> extends NamedList<T> {

    private final List<GenericListListener> listeners = new ArrayList<GenericListListener>();


    public void addListListener(GenericListListener l) {
        listeners.add(l);
    }
    public void removeListListener(GenericListListener l) {
        listeners.remove(l);
    }

    private void fireAdded(T item, int idx) {
        for (GenericListListener l: listeners) {
            l.itemAdded(item, idx);
        }
    }

    public T remove(int idx) {
        T item = super.remove(idx);
        if (item != null) {
            for (GenericListListener l: listeners) {
                l.itemRemoved(item, idx);
            }
        }
        return item;
    }

    @Override
    public boolean add(T t) {
        boolean b = super.add(t);
        if (b) {
            fireAdded(t, size()-1);
        }
        return b;
    }

    @Override
    public void add(int idx, T t) {
        super.add(idx, t);
        fireAdded(t, idx);
    }
}