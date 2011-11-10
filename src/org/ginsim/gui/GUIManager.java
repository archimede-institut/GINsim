package org.ginsim.gui;

import java.util.HashMap;

import org.ginsim.graph.AbstractGraphFrontend;
import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;
import org.ginsim.graph.backend.GraphBackend;
import org.ginsim.graph.backend.JgraphtBackendImpl;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.backend.JgraphGUIImpl;
import org.ginsim.gui.graph.helper.GraphGUIHelper;
import org.ginsim.gui.graph.helper.GraphGUIHelperFactory;

import fr.univmrs.tagc.common.widgets.Frame;

public class GUIManager {

	private static GUIManager manager;
	
	private HashMap<Graph,GraphGUI> graphToGUI = new HashMap<Graph, GraphGUI>();
	

	/**
	 * Give access to the manager singleton
	 * 
	 * @return
	 */
	static public GUIManager getInstance(){
		
		if( manager == null){
			manager = new GUIManager();
		}
		
		return manager; 
	}
	
	public Frame newFrame( Graph graph){
		
		
	}
	
	
	/**
	 * Build a GraphGUI instance for the given graph and memorize their relationship
	 * @param <V>
	 * @param <E>
	 * 
	 * @param graph the graph for which a GraphGUi is desired
	 * @return the GraphGUI built for the given Graph
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public GraphGUI createGraphGUI( Graph graph) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		
		// get a graph GUI helper
		GraphGUIHelper<?,?,?> helper = GraphGUIHelperFactory.getFactory().getGraphGUIHelper(graph);
		
		// find the GUI component and show the graph...
		GraphGUI<?,?,?> graphGUI = null;
		if (graph instanceof AbstractGraphFrontend) {
			GraphBackend<?,?> graph_backend = ((AbstractGraphFrontend<?, ?>)graph).getBackend();
			if (graph_backend instanceof JgraphtBackendImpl) {
				graphGUI = new JgraphGUIImpl(graph, (JgraphtBackendImpl<?,?>) graph_backend, helper);
			}
		}
		
		graphToGUI.put( graph, graphGUI);

		return graphGUI;
	}
	
	
	/**
	 * Give access to the GraphGUI corresponding to the given Graph
	 * 
	 * @param graph the graph for which the GraphGUI is desired
	 * @return the GraphGUI corresponding to the given Graph
	 */
	public GraphGUI getGraphGUI( Graph graph){
		
		return graphToGUI.get( graph);
	}
	
}
