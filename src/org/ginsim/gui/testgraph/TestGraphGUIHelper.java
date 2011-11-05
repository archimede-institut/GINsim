package org.ginsim.gui.testgraph;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.ginsim.graph.testGraph.TestEdge;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.graph.testGraph.TestVertex;
import org.ginsim.gui.graph.AddEdgeAction;
import org.ginsim.gui.graph.AddVertexAction;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.helper.GraphGUIHelper;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;

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

	@Override
	public List<EditAction> getEditActions(TestGraph graph) {
		List<EditAction> actions = new ArrayList<EditAction>();
		GsVertexAttributesReader reader = graph.getVertexReader();
		actions.add(new AddTestVertexAction(graph, "+ TV", reader));
		actions.add(new AddTestEdgeAction(graph, "+ TE"));
		return actions;
	}
}

class AddTestVertexAction extends AddVertexAction<TestVertex> {

	private final TestGraph graph;
	public AddTestVertexAction(TestGraph graph, String name, GsVertexAttributesReader reader) {
		super(name, reader);
		this.graph = graph;
	}

	@Override
	protected TestVertex getNewVertex() {
		return graph.addVertex();
	}
}

class AddTestEdgeAction extends AddEdgeAction<TestVertex, TestEdge> {

	private final TestGraph graph;
	public AddTestEdgeAction(TestGraph graph, String name) {
		super(name);
		this.graph = graph;
	}

	@Override
	protected TestEdge getNewEdge(TestVertex source, TestVertex target) {
		return graph.addEdge(source, target);
	}
}