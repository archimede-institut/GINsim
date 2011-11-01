package org.ginsim.gui.graph.helper;

import org.ginsim.graph.testGraph.TestEdge;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.graph.testGraph.TestVertex;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.testgraph.TestEdgeEditionPanel;
import org.ginsim.gui.testgraph.TestGraphEditionPanel;
import org.ginsim.gui.testgraph.TestVertexEditionPanel;

/**
 * Simple GUI helper for the regulatory graph.
 * 
 * FIXME: the name is based on the name of the implementation, we want to do better!
 * 
 * @author Aurelien Naldi
 */
//@ProviderFor(GraphGUIHelper.class)  // requires SPI, could be a more solid solution for service discovery
public class TestGraphImplGUIHelper implements GraphGUIHelper<TestGraph, TestVertex, TestEdge> {

	@Override
	public GUIEditor<TestGraph> getMainEditionPanel(TestGraph graph) {
		return new TestGraphEditionPanel();
	}

	@Override
	public String getEditingTabLabel(TestGraph graph) {
		return "LRG";
	}

	@Override
	public GUIEditor<TestVertex> getNodeEditionPanel(TestGraph graph) {
		return new TestVertexEditionPanel();
	}

	@Override
	public GUIEditor<TestEdge> getEdgeEditionPanel(TestGraph graph) {
		return new TestEdgeEditionPanel();
	}

}
