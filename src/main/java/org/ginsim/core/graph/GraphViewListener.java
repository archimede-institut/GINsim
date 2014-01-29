package org.ginsim.core.graph;

/**
 * Listen to changes in a graph view.
 *
 * @author Aurelien Naldi
 */
public interface GraphViewListener {

    /**
     * A single item was changed.
     *
     * @param o
     */
	void refresh(Object o);

    /**
     * The full view was changed.
     */
	void repaint();

}
