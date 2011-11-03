package org.ginsim.gui.testgraph;

import javax.swing.JPanel;

import org.ginsim.graph.Graph;
import org.ginsim.graph.testGraph.TestEdge;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.graph.testGraph.TestGraphImpl;
import org.ginsim.graph.testGraph.TestVertex;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.helper.GraphGUIHelper;
import org.mangosdk.spi.ProviderFor;

/**
 * Simple GUI helper for the test graph.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor( GraphGUIHelper.class)
public class TestGraphGUIHelper implements GraphGUIHelper<TestGraph, TestVertex, TestEdge> {

	@Override
	public GUIEditor<TestGraph> getMainEditionPanel(TestGraph graph) {
		return new TestGraphEditionPanel();
	}

	@Override
	public String getEditingTabLabel(TestGraph graph) {
		return "Test graph";
	}

	@Override
	public GUIEditor<TestVertex> getNodeEditionPanel(TestGraph graph) {
		return new TestVertexEditionPanel();
	}

	@Override
	public GUIEditor<TestEdge> getEdgeEditionPanel(TestGraph graph) {
		return new TestEdgeEditionPanel();
	}
	
	@Override
	public Class getGraphClass() {
		return TestGraph.class;
	}

	@Override
	public JPanel getInfoPanel(TestGraph graph) {
		return null;
	}
}
