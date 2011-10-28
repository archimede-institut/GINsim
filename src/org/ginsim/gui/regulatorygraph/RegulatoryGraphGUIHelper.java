package org.ginsim.gui.regulatorygraph;

import org.ginsim.graph.regulatoryGraph.RegulatoryEdge;
import org.ginsim.graph.regulatoryGraph.RegulatoryGraph;
import org.ginsim.graph.regulatoryGraph.RegulatoryVertex;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.helper.GraphGUIHelper;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(GraphGUIHelper.class)
public class RegulatoryGraphGUIHelper implements GraphGUIHelper<RegulatoryGraph, RegulatoryVertex, RegulatoryEdge> {

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
