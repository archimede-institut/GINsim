package org.ginsim.gui.utils.data;

import java.util.List;

/**
 * Complete a ListPanel by providing additional panels reacting to the selection
 * @param <L>  list extend
 * @param <T>  companion
 */
public interface ListPanelCompanion<T, L extends List<T>> {

    void setParentList(L list);

    void selectionUpdated(int[] selection);
}
