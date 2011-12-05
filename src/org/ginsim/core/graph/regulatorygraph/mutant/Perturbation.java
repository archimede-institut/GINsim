package org.ginsim.core.graph.regulatorygraph.mutant;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;



public interface Perturbation {

    /**
     * apply this perturbation on the OMDD.
     * 
     * @param t_tree OMDD for all genes of the model
     * @param graph the regulatory graph
     */
    public void apply(OMDDNode[] t_tree, RegulatoryGraph graph);

}