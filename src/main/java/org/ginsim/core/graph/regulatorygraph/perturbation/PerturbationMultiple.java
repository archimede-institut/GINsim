package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.List;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.perturbation.MultiplePerturbation;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

public class PerturbationMultiple extends MultiplePerturbation<Perturbation> implements Perturbation {

	/**
	 * Create a multiple perturbation
	 * 
	 * @param perturbations list of perturbations to apply.
	 */
	public PerturbationMultiple(List<Perturbation> perturbations) {
		super(perturbations);
	}
	
	@Override
	@Deprecated
	public void apply(OMDDNode[] t_tree, RegulatoryGraph graph) {
		for (Perturbation perturbation: perturbations) {
			perturbation.apply(t_tree, graph);
		}
	}

	@Override
	public void toXML(XMLWriter out) throws IOException {
        out.openTag("mutant");
        out.addAttr("name", toString());
        for (Perturbation p: perturbations) {
            p.toXML(out);
        }
        out.closeTag();
	}

	@Override
	public boolean affectsNode(NodeInfo node) {
		for (Perturbation p: perturbations) {
			if (p.affectsNode(node)) {
				return true;
			}
		}
		return false;
	}
}
