package org.ginsim.core.graph.regulatorygraph.mutant;

import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.LogicalModelModifier;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;


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
     * Apply this perturbation on a logical model.
     * Warning: the model will be modified. clone it in advance to preserve old functions.
     * 
     * @param model
     * @return
     */
    void apply(LogicalModel model);
}
