package org.ginsim.gui;

import java.util.HashMap;

import org.ginsim.graph.AbstractGraphFrontend;
import org.ginsim.graph.Graph;
import org.ginsim.graph.backend.GraphBackend;
import org.ginsim.graph.backend.JgraphtBackendImpl;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.backend.JgraphGUIImpl;
import org.ginsim.gui.graph.helper.GraphGUIHelper;
import org.ginsim.gui.graph.helper.GraphGUIHelperFactory;
import org.ginsim.gui.shell.MainFrame;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.common.widgets.Frame;

public class GUIManager {

	private static GUIManager manager;
	
	private HashMap<Graph,GUIObject> graphToGUIObject = new HashMap<Graph, GUIObject>();
	

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
	
	/**
	 * Build a new frame for the given Graph by creating a suitable GraphGUI and memorize their relationship
	 * 
	 * @param graph the graph for which a Frame is desired
	 * @return a new frame for the given Graph
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Frame newFrame( Graph graph) throws ClassNotFoundException, IllegalAccessException, InstantiationException{
		
		GraphGUI graph_gui = createGraphGUI( graph);
		MainFrame frame = new MainFrame("test", 800, 600, graph_gui);
		frame.setVisible(true);
		
		graphToGUIObject.put( graph, new GUIObject( graph, graph_gui, frame));
		
		return frame;
	}
	
	
	/**
	 * Build a GraphGUI instance for the given graph
	 * @param <V>
	 * @param <E>
	 * 
	 * @param graph the graph for which a GraphGUi is desired
	 * @return the GraphGUI built for the given Graph
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private GraphGUI createGraphGUI( Graph graph) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		
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
	

		return graphGUI;
	}
	
	
	/**
	 * Give access to the GraphGUI corresponding to the given Graph
	 * 
	 * @param graph the graph for which the GraphGUI is desired
	 * @return the GraphGUI corresponding to the given Graph
	 */
	public GraphGUI getGraphGUI( Graph graph){
		
		GUIObject gui_object = graphToGUIObject.get( graph);
		
		if( gui_object != null){
			return gui_object.getGraphGUI();
		}
		
		return null;
	}
	
	
	/**
	 * Give access to the Frame corresponding to the given Graph
	 * 
	 * @param graph the graph for which the GraphGUI is desired
	 * @return the Frame corresponding to the given Graph
	 */
	public Frame getFrame( Graph graph){
		
		GUIObject gui_object = graphToGUIObject.get( graph);
		
		if( gui_object != null){
			return gui_object.getFrame();
		}
		
		return null;
	}
	
	/**
	 * Unmemorize the relationship between a graph and its GraphGUI and Frame
	 * 
	 * @param graph the graph to close
	 */
	public void close( Graph graph){
		
		graphToGUIObject.remove( graph);
		GsEnv.unregisterGraph( graph.getGraphID());
	}
	
	
	/**
	 * Class containing the relationship between a Graph, its GraphGUI the corresponding Frame
	 * 
	 * @author spinelli
	 *
	 */
	private class GUIObject{
		
		private Graph graph;
		private GraphGUI graphGUI;
		private Frame frame;
		
		
		public GUIObject( Graph graph, GraphGUI graph_gui, Frame frame){
			
			this.graph = graph;
			this.graphGUI = graph_gui;
			this.frame = frame;
		}
		
		/**
		 * 
		 * @return the frame
		 */
		public Frame getFrame() {
			
			return frame;
		}
		
		/**
		 * 
		 * @return the GraphGUI
		 */
		public GraphGUI getGraphGUI() {
			
			return graphGUI;
		}
		
	}
	
}
