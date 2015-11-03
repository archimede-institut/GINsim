package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.Map;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.perturbation.FixedValuePerturbation;
import org.ginsim.common.xml.XMLWriter;

public class PerturbationFixed extends FixedValuePerturbation implements Perturbation {

	public PerturbationFixed(NodeInfo target, int value) {
		super(target, value);
	}

	@Override
	public void toXML(XMLWriter out) throws IOException {
        out.openTag("change");
        out.addAttr("target", component.getNodeID());
        out.addAttr("min", ""+value);
        out.addAttr("max", ""+value);
        out.closeTag();
	}

	@Override
	public Perturbation clone(ListOfPerturbations manager, Map<NodeInfo, NodeInfo> m_nodes, Map<Perturbation, Perturbation> m_perturbations) {
		NodeInfo newComponent = m_nodes.get(component);
		if (newComponent != null) {
			return manager.addFixedPerturbation(newComponent, value);
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "The component "+ component.getNodeID() + " will be fixed at value " + value;
	}
}
