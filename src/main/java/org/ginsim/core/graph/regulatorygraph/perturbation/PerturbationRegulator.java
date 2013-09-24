package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.Map;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.perturbation.FixedValuePerturbation;
import org.colomoto.logicalmodel.perturbation.InteractionPerturbation;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

public class PerturbationRegulator extends InteractionPerturbation implements Perturbation {

	public PerturbationRegulator(NodeInfo regulator, NodeInfo target, int value) {
		super(regulator, target, value);
	}

	@Override
	@Deprecated
	public void apply(OMDDNode[] t_tree, RegulatoryGraph graph) {
		// FIXME: implement for old MDDs ??
	}

	@Override
	public void toXML(XMLWriter out) throws IOException {
        out.openTag("change");
        out.addAttr("target", target.getNodeID());
        out.addAttr("regulator", regulator.getNodeID());
        out.addAttr("value", ""+regValue);
        out.closeTag();
	}

	@Override
	public boolean affectsNode(NodeInfo node) {
		return target.equals(node);
	}

	@Override
	public Perturbation clone(ListOfPerturbations manager, Map<NodeInfo, NodeInfo> m_nodes, Map<Perturbation, Perturbation> m_perturbations) {
		NodeInfo newTarget = m_nodes.get(target);
		NodeInfo newRegulator = m_nodes.get(regulator);
		if (newTarget != null && newRegulator != null) {
			return manager.addRegulatorPerturbation(newRegulator, newTarget, regValue);
		}
		return null;
	}

}
