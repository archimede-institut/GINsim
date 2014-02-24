package org.ginsim.service.tool.graphcomparator;

/**
 * Hold information on an item in a compared graph: allow to associated extra information to the ComparedItemStatus.
 */
public class ComparedItemInfo<T> {
    public final T item, first, second;
    public boolean changed= false;
    public boolean metaChanged = false;

    public ComparedItemInfo(T item, T first, T second) {
        this.item = item;
        this.first = first;
        this.second = second;
    }
}
