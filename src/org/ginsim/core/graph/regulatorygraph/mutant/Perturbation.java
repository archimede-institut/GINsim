package org.ginsim.core.graph.regulatorygraph.mutant;

import java.util.List;

import org.ginsim.common.xml.XMLize;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.logicalmodel.LogicalModelModifier;

import fr.univmrs.tagc.javaMDD.MDDFactory;

/**
 * A perturbation is a (series of) change that can be applied to a regulatory graph.
 * Applying the perturbation leads to a new set of logical functions.
 * 
 * @author Aurelien Naldi
 */
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
    
    
    int[] apply(MDDFactory factory, int[] nodes, List<NodeInfo> nodeInfo);
}