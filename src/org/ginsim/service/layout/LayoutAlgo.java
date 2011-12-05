package org.ginsim.service.layout;

import org.ginsim.graph.view.NodeAttributesReader;

/**
 * Some common functions for our kind of layout algo.
 * 
 */
public interface LayoutAlgo {

    /**
     * set some parameters for the layout, getting ready to place all nodes.
     * 
     * @param vreader used to read and change node's properties
     * @param nbRoot number of root nodes
     * @param nbStable number of stable nodes
     * @param nbClassic number of classic nodes
     * @param maxHeight height of the taller node
     * @param maxWidth width of the bigger node
     */
    public void configure (NodeAttributesReader vreader, int nbRoot, int nbStable, int nbClassic, int maxHeight, int maxWidth);
    
    /**
     * place the next root node.
     */
    public void placeNextRoot();
    /**
     * place the next stable node
     */
    public void placeNextStable();
    /**
     * place the next classic node
     */
    public void placeNextClassic();
}
