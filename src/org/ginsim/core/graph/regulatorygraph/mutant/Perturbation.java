package org.ginsim.core.graph.regulatorygraph.mutant;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

import fr.univmrs.tagc.javaMDD.MDDFactory;



public interface Perturbation {

    /**
     * Apply this perturbation on the OMDD.
     * 
     * @param t_tree OMDD for all genes of the model
     * @param graph the regulatory graph
     */
    void apply(OMDDNode[] t_tree, RegulatoryGraph graph);

    /**
     * Apply this perturbation on the OMDD.
     * 
     * @param factory
     * @param nodes
     * @param graph
     * @return the roots for the modified MDDs
     */
    int[] apply(MDDFactory factory, int[] nodes, RegulatoryGraph graph);
}