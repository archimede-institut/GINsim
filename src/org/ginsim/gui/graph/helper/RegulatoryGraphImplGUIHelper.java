package org.ginsim.gui.graph.helper;

import org.ginsim.graph.regulatoryGraph.RegulatoryEdge;
import org.ginsim.graph.regulatoryGraph.RegulatoryGraph;
import org.ginsim.graph.regulatoryGraph.RegulatoryVertex;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.regulatorygraph.RegulatoryEdgeEditionPanel;
import org.ginsim.gui.regulatorygraph.RegulatoryGraphEditionPanel;
import org.ginsim.gui.regulatorygraph.RegulatoryVertexEditionPanel;

/**
 * Simple GUI helper for the regulatory graph.
 * 
 * FIXME: the name is based on the name of the implementation, we want to do better!
 * 
 * @author Aurelien Naldi
 */
//@ProviderFor(GraphGUIHelper.class)  // requires SPI, could be a more solid solution for service discovery
public class RegulatoryGraphImplGUIHelper implements GraphGUIHelper<RegulatoryGraph, RegulatoryVertex, RegulatoryEdge> {

	@Override
	public GUIEditor<RegulatoryGraph> getMainEditionPanel(RegulatoryGraph graph) {
		return new RegulatoryGraphEditionPanel();
	}

	@Override
	public String getEditingTabLabel(RegulatoryGraph graph) {
		return "LRG";
	}

	@Override
	public GUIEditor<RegulatoryVertex> getNodeEditionPanel(RegulatoryGraph graph) {
		return new RegulatoryVertexEditionPanel();
	}

	@Override
	public GUIEditor<RegulatoryEdge> getEdgeEditionPanel(RegulatoryGraph graph) {
		return new RegulatoryEdgeEditionPanel();
	}

}
