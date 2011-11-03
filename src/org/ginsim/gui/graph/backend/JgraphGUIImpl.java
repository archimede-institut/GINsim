package org.ginsim.gui.graph.backend;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JPanel;

import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;
import org.ginsim.graph.backend.GraphViewBackend;
import org.ginsim.graph.backend.JgraphtBackendImpl;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.helper.GraphGUIHelper;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraphSelectionChangeEvent;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraph;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphEdgeAttribute;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphVertexAttribute;

public class JgraphGUIImpl<G extends Graph<V,E>, V, E extends Edge<V>> implements GraphGUI<G,V, E>, GraphViewBackend {

	private final G graph;
	private final JgraphtBackendImpl<V, E> backend;
    private JGraphModelAdapter<V,E> m_jgAdapter;
    private GsJgraph jgraph;
    private final GraphGUIHelper<G,V,E> helper;

    private static final int GRID=0, ACTIVE_GRID=1;
    private static final int[] PROPERTIES = {GRID, ACTIVE_GRID};
    
    Collection<E> sel_edges;
    Collection<V> sel_vertices;
    
	public JgraphGUIImpl(G g, JgraphtBackendImpl<V, E> backend, GraphGUIHelper<G,V,E> helper) {
		this.graph = g;
		this.backend = backend;
		this.m_jgAdapter = new JGraphModelAdapter<V, E>(backend);
		this.jgraph = new GsJgraph(m_jgAdapter);
		jgraph.setEdgeLabelDisplayed(false);
		this.helper = helper;
		backend.setGraphViewBackend(this);
		
		// FIXME: enable marquee handler when the actions are ready
		// new MarqueeHandler<V, E>(this);
	}
	
	public JGraph getJGraph() {
		return jgraph;
	}
	
	@Override
	public Component getGraphComponent() {
		return jgraph;
	}
	
	@Override
	public GsEdgeAttributesReader getEdgeReader() {
		return new GsJgraphEdgeAttribute(backend, m_jgAdapter, null);
	}

	@Override
	public GsVertexAttributesReader getVertexReader() {
		return new GsJgraphVertexAttribute(m_jgAdapter, null);
	}

	@Override
	public void setProperty(int property, boolean b) {
		switch (property) {
		case GRID:
			jgraph.setGridVisible(b);
			break;
		case ACTIVE_GRID:
			jgraph.setGridEnabled(b);
			break;
		}
	}

	@Override
	public boolean hasProperty(int property) {
		switch (property) {
		case GRID:
			return jgraph.isGridVisible();
		case ACTIVE_GRID:
			return jgraph.isGridEnabled();
		}
		return false;
	}

	@Override
	public String getPropertyName(int property) {
		switch (property) {
		case GRID:
			return "Grid visible";
		case ACTIVE_GRID:
			return "Grid active";
		}
		return "";
	}

	@Override
	public int[] getProperties() {
		return PROPERTIES;
	}

	@Override
	public Collection<V> getSelectedVertices() {
		return sel_vertices;
	}

	@Override
	public Collection<E> getSelectedEdges() {
		return sel_edges;
	}

	public void SelectionChanged(GsGraphSelectionChangeEvent event) {
		sel_edges = event.getV_edge();
		sel_vertices = event.getV_vertex();
		// TODO propagate selection change event
	}

	@Override
	public Graph<V, E> getGraph() {
		return graph;
	}

	@Override
	public GUIEditor<G> getMainEditionPanel() {
		return helper.getMainEditionPanel(graph);
	}

	@Override
	public String getEditingTabLabel() {
		return helper.getEditingTabLabel(graph);
	}

	@Override
	public GUIEditor<V> getNodeEditionPanel() {
		return helper.getNodeEditionPanel(graph);
	}

	@Override
	public GUIEditor<E> getEdgeEditionPanel() {
		return helper.getEdgeEditionPanel( graph);
	}

	@Override
	public JPanel getInfoPanel() {
		return helper.getInfoPanel( graph);
	}
}

enum GUIProperties {
	GRID, GRIDACTIVE;
}
