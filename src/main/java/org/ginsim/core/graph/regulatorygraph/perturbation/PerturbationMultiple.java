package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.perturbation.AbstractPerturbation;
import org.colomoto.logicalmodel.perturbation.LogicalModelPerturbation;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

public class PerturbationMultiple extends AbstractPerturbation implements Perturbation {

	private final List<Perturbation> perturbations;

	/**
	 * Create a multiple perturbation
	 * 
	 * @param perturbations list of perturbations to apply.
	 */
	public PerturbationMultiple(List<Perturbation> perturbations) {
		this.perturbations = perturbations;
	}
	
	@Override
	public void update(LogicalModel model) {
		for (LogicalModelPerturbation perturbation: perturbations) {
			perturbation.update(model);
		}
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
		// TODO Auto-generated method stub
		
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Perturbation p: perturbations) {
			sb.append(" ");
			sb.append(p.toString());
		}
		return sb.toString();
	}
}
