package org.ginsim.graph.regulatorygraph.mutant;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;



public interface Perturbation {

    /**
     * apply this perturbation on the OMDD.
     * 
     * @param t_tree OMDD for all genes of the model
     * @param graph the regulatory graph
     */
    public void apply(OMDDNode[] t_tree, RegulatoryGraph graph);

}