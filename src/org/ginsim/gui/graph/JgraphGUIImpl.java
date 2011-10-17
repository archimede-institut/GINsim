package org.ginsim.gui.graph;

import java.awt.Component;

import org.ginsim.graph.Edge;
import org.ginsim.graph.GraphBackend;
import org.ginsim.graph.JgraphtBackendImpl;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgrapht.ext.JGraphModelAdapter;

import fr.univmrs.tagc.GINsim.jgraph.GsJgraph;

public class JgraphGUIImpl<V, E extends Edge<V>> implements GraphGUI<V, E> {

	private final GraphBackend<V, E> backend;
    private JGraphModelAdapter<V,E>	 m_jgAdapter;
    private GsJgraph 				 jgraph;

	
	public JgraphGUIImpl(JgraphtBackendImpl<V, E> backend) {
		this.backend = backend;
		this.m_jgAdapter = new JGraphModelAdapter<V, E>(backend);
		// FIXME: we had a custom cell view factory, why was it already?
		CellViewFactory cvf = new DefaultCellViewFactory();
		this.jgraph = new GsJgraph(m_jgAdapter, cvf);
		jgraph.setEdgeLabelDisplayed(false);
	}
	
	@Override
	public Component getGraphComponent() {
		return jgraph;
	}
}
