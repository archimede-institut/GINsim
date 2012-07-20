package org.ginsim.core.graph.regulatorygraph.mutant;

import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.LogicalModelModifier;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.perturbation.LogicalModelPerturbation;
import org.colomoto.mddlib.MDDManager;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;


/**
 * A perturbation is a (series of) change that can be applied to a regulatory graph.
 * Applying the perturbation leads to a new set of logical functions.
 * 
 * This deprecated interface adds a compatibility method to apply a peturbation using the old OMDD structure.
 * 
 * @author Aurelien Naldi
 */
@Deprecated
public interface Perturbation extends LogicalModelPerturbation {

    /**
     * Apply this perturbation on the OMDD.
     * 
     * @param t_tree OMDD for all genes of the model
     * @param graph the regulatory graph
     */
	@Deprecated
    void apply(OMDDNode[] t_tree, RegulatoryGraph graph);

}
