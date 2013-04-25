package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.Map;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.perturbation.RangePerturbation;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

public class PerturbationRange extends RangePerturbation implements Perturbation {

	public PerturbationRange(NodeInfo target, int min, int max) {
		super(target, min, max);
	}

	@Override
	@Deprecated
	public void apply(OMDDNode[] t_tree, RegulatoryGraph graph) {
		
		int index = graph.getNodeOrderForSimulation().indexOf(component);
		throw new RuntimeException("Range perturbation not yet fully implemented");
		
	}

	@Override
	public void toXML(XMLWriter out) throws IOException {
        out.openTag("change");
        out.addAttr("target", component.getNodeID());
        out.addAttr("min", ""+min);
        out.addAttr("max", ""+max);
        out.closeTag();
	}

	@Override
	public boolean affectsNode(NodeInfo node) {
		return component.equals(node);
	}

	@Override
	public Perturbation clone(ListOfPerturbations manager, Map<NodeInfo, NodeInfo> m_nodes, Map<Perturbation, Perturbation> m_perturbations) {
		NodeInfo newComponent = m_nodes.get(component);
		if (newComponent != null) {
			return new PerturbationRange(newComponent, min, max);
		}
		return null;
	}
}
