package org.ginsim.gui.utils.data;

import java.util.List;

/**
 * Complete a ListPanel by providing additional panels reacting to the selection
 */
public interface ListPanelCompanion<T, L extends List<T>> {

    void setList(L list);

    void selectionUpdated(int[] selection);
}
