package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.perturbation.FixedValuePerturbation;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

public class PerturbationFixed extends FixedValuePerturbation implements Perturbation {

	public PerturbationFixed(NodeInfo target, int value) {
		super(target, value);
	}

	@Override
	@Deprecated
	public void apply(OMDDNode[] t_tree, RegulatoryGraph graph) {
		int index = graph.getNodeOrderForSimulation().indexOf(component);
		t_tree[index] = OMDDNode.TERMINALS[value];
	}

	@Override
	public void toXML(XMLWriter out) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
