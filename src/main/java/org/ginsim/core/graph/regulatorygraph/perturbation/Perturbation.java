package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.Map;

import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.modifier.perturbation.LogicalModelPerturbation;
import org.ginsim.common.xml.XMLWriter;


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
     * Copy the perturbation to a new graph (in particular to a reduced version of the current graph)
     *
     * @param manager
     * @param m_nodes
     * @param m_perturbations
     * @return
     */
	Perturbation clone(ListOfPerturbations manager, Map<NodeInfo, NodeInfo> m_nodes, Map<Perturbation, Perturbation> m_perturbations);

    /**
     * Get a human-readable description of the perturbation
     *
     * @return a string describing the changes
     */
    String getDescription();
}
