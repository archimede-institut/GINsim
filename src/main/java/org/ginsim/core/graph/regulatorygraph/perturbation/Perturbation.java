package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.Map;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.perturbation.LogicalModelPerturbation;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;


/**
 * A perturbation is a (series of) change that can be applied to a regulatory graph.
 * Applying the perturbation leads to a new set of logical functions.
 * 
 * This interface extends the base perturbations provided by LogicalModel and adds some convenience methods
 * 
 * @author Aurelien Naldi
 */
public interface Perturbation extends LogicalModelPerturbation {

    /**
     * Save the perturbation definition to XML (to save in zginml)
     *
     * @param out
     * @throws IOException
     */
	void toXML(XMLWriter out) throws IOException;

    /**
     * Test if the perturbation affects the function of a specific node
     *
     * @param node
     * @return
     */
	boolean affectsNode(NodeInfo node);

    /**
     * Copy the perturbation to a new graph (in particular to a reduced version of the current graph)
     *
     * @param manager
     * @param m_nodes
     * @param m_perturbations
     * @return
     */
	Perturbation clone(ListOfPerturbations manager, Map<NodeInfo, NodeInfo> m_nodes, Map<Perturbation, Perturbation> m_perturbations);
}
