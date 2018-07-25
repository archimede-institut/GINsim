package org.ginsim.core.graph.dynamicgraph;


import org.colomoto.biolqm.LogicalModel;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;

public interface TransitionGraph<V,E extends Edge<V>> extends Graph<V,E> {

    /**
     * Get the list of known extra components names.
     * These components have no explicitly assigned value in the STG,
     * but their values can be retrieved based on a given state.
     *
     * @return the list of names, or null if none
     */
    String[] getExtraNames();

    /**
     * Retrieve the values for all extra components for a given state.
     * If the provided array to fill is null or of the wrong size, a new array will be created and returned.
     * Otherwise, extraValues will be filled and returned.
     *
     * @param state
     * @param extraValues array in which to put the values.
     *
     * @return extraValues properly filled or a new array
     */
    byte[] fillExtraValues(byte[] state, byte[] extraValues);


    /**
     * Associate a logicalModel with this STG, notably to retrieve extra values.
     * @param model
     */
    void setLogicalModel(LogicalModel model);

}
