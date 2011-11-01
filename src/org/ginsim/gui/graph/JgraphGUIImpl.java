package org.ginsim.gui.graph;

import java.awt.Component;

import org.ginsim.graph.Edge;
import org.ginsim.graph.GraphViewBackend;
import org.ginsim.graph.JgraphtBackendImpl;
import org.ginsim.gui.graph.helper.GraphGUIHelper;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgrapht.ext.JGraphModelAdapter;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.jgraph.GsCellViewFactory;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraph;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphEdgeAttribute;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphVertexAttribute;

public class JgraphGUIImpl<V, E extends Edge<V>> implements GraphGUI<V, E>, GraphViewBackend {

	private final JgraphtBackendImpl<V, E> backend;
    private JGraphModelAdapter<V,E> m_jgAdapter;
    private GsJgraph jgraph;
    GraphGUIHelper helper;

	public JgraphGUIImpl(JgraphtBackendImpl<V, E> backend, GraphGUIHelper helper) {
		this.backend = backend;
		this.m_jgAdapter = new JGraphModelAdapter<V, E>(backend);
		this.jgraph = new GsJgraph(m_jgAdapter);
		jgraph.setEdgeLabelDisplayed(false);
		this.helper = helper;
		backend.setGraphViewBackend(this);
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
}
