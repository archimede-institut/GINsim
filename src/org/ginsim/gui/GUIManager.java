package org.ginsim.gui;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.ginsim.exception.GsException;
import org.ginsim.graph.GraphManager;
import org.ginsim.graph.backend.GraphBackend;
import org.ginsim.graph.backend.JgraphtBackendImpl;
import org.ginsim.graph.common.AbstractGraph;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.backend.JgraphGUIImpl;
import org.ginsim.gui.graphhelper.GraphGUIHelper;
import org.ginsim.gui.graphhelper.GraphGUIHelperFactory;
import org.ginsim.gui.shell.MainFrame;

import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.managerresources.Translator;
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
	 * Create a new graph (of the default type, i.e. a regulatory graph)
	 * and then a new frame for it.
	 * 
	 * @return the new regulatory graph
	 */
	public GsRegulatoryGraph newFrame() {
		GsRegulatoryGraph graph = GraphManager.getInstance().getNewGraph();
		newFrame(graph);
		return graph;
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
	public Frame newFrame( Graph graph) {
		
		GraphGUI graph_gui;
		try {
			graph_gui = createGraphGUI( graph);
			MainFrame frame = new MainFrame("test", 800, 600, graph_gui);
			frame.setVisible(true);
			
			graphToGUIObject.put( graph, new GUIObject( graph, graph_gui, frame));
			
			return frame;
		} catch (Exception e) {
			error(new GsException(GsException.GRAVITY_ERROR, e), null);
			e.printStackTrace();
		}
		return null;
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
		if (graph instanceof AbstractGraph) {
			GraphBackend<?,?> graph_backend = ((AbstractGraph<?, ?>)graph).getBackend();
			if (graph_backend instanceof JgraphtBackendImpl) {
				graphGUI = new JgraphGUIImpl(graph, (JgraphtBackendImpl<?,?>) graph_backend, helper);
			}
		}
	

		graphToGUIObject.put( graph, new GUIObject( graph, graphGUI, null));
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
	public boolean close( Graph graph){
		
		GUIObject o = graphToGUIObject.get(graph);
		if (!o.graphGUI.isSaved()) {
			// FIXME: better name
			String name = "NAME HERE";
            int aw = JOptionPane.showConfirmDialog(o.frame, Translator.getString("STR_saveQuestion1")+ name +Translator.getString("STR_saveQuestion2"),
                    Translator.getString("STR_closeConfirm"),
                    JOptionPane.YES_NO_CANCEL_OPTION);
            switch (aw) {
                case JOptionPane.YES_OPTION:
                	if (!o.graphGUI.save()) {
                		return false;
                	}
                case JOptionPane.NO_OPTION:
                	break;
                case JOptionPane.CANCEL_OPTION:
                default:
                	return false;
            }
		}
		
		o.graphGUI.fireGraphClose();
		graphToGUIObject.remove( graph);
		if (o.frame != null) {
			o.frame.setVisible(false);
			o.frame.dispose();
		}
		if (graphToGUIObject.size() == 0) {
			OptionStore.saveOptions();
			System.exit(0);
		}
		return true;
	}
	
	/**
	 * try to close all frames
	 */
	public void quit( ){
		int nbobjects = graphToGUIObject.size();
		for (Graph g: graphToGUIObject.keySet()) {
			if (!close(g)) {
				break;
			}
		}
	}
	
	
	// ---------------------- METHODS ON BLOCKS MANAGEMENT -----------------------------------
	
	/**
	 * block editing mode for the graph
	 *
	 * @param key
	 */
	public void addBlockEdit( Graph graph, Object key) {
		
		GUIObject obj = graphToGUIObject.get( graph);
		
		if( obj != null){
			obj.addBlockEdit(key);
		}
	}

	/**
	 * Verify if edition is allowed on the graph
	 * 
	 * @return true if edit isn't blocked
	 */
	public boolean isEditAllowed( Graph graph) {
		
		GUIObject obj = graphToGUIObject.get( graph);
		
		if( obj != null){
			return obj.isEditAllowed();
		}
		
		return true;
	}

	/**
	 * Remove a block on Edition
	 * 
	 * @param key
	 */
	public void removeBlockEdit( Graph graph, Object key) {
		
		GUIObject obj = graphToGUIObject.get( graph);
		
		if( obj != null){
			obj.removeBlockEdit(key);
		}
	}
	
	
	/**
	 * Block closing of the graph
	 *
	 * @param key
	 */
	public void addBlockClose( Graph graph, Object key) {
		
		GUIObject obj = graphToGUIObject.get( graph);
		
		if( obj != null){
			obj.addBlockClose(key);
		}
	}
	
    /**
     * Verify if it is allowed to close the graph can be closed
     * 
     * @return true if closing this graph is allowed
     */
    public boolean canClose( Graph graph) {
    	
		GUIObject obj = graphToGUIObject.get( graph);
		
		if( obj != null){
			return obj.isEditAllowed();
		}
		
		return true;
    }

	/**
	 * Remove a block on closing
	 * 
	 * @param key
	 */
	public void removeBlockClose ( Graph graph, Object key) {
		
		GUIObject obj = graphToGUIObject.get( graph);
		
		if( obj != null){
			obj.removeBlockClose(key);
		}
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
		private Vector<Object> blockEdit;
		private Vector<Object> blockClose;
		
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
		
		/**
		 * block editing mode for the graph
		 *
		 * @param key
		 */
		public void addBlockEdit(Object key) {
			
		    if (key == null) {
		        return;
		    }
		    if (blockEdit == null) {
		        blockEdit = new Vector<Object>();
		    }
		    blockEdit.add(key);
		}

		/**
		 * Verify if edition is allowed on the graph
		 * 
		 * @return true if edit isn't blocked
		 */
		public boolean isEditAllowed() {
			
		    return blockEdit == null;
		}

		/**
		 * Remove a block on Edition
		 * 
		 * @param key
		 */
		public void removeBlockEdit (Object key) {
			
		    if (blockEdit == null) {
		        return;
		    }

		    blockEdit.remove(key);
		    if (blockEdit.size() == 0) {
		        blockEdit = null;
		    }
		}
		
		
		/**
		 * Block closing of the graph
		 *
		 * @param key
		 */
		public void addBlockClose(Object key) {
			
		    if (key == null) {
		        return;
		    }
		    if (blockClose == null) {
		        blockClose = new Vector<Object>();
		    }
		    blockClose.add(key);
		}

		/**
		 * Remove a block on closing
		 * 
		 * @param key
		 */
		public void removeBlockClose (Object key) {
			
		    if (blockClose == null) {
		        return;
		    }

		    blockClose.remove(key);
		    if (blockClose.size() == 0) {
		        blockClose = null;
		    }
		}

	    /**
	     * Verify if it is allowed to close the graph can be closed
	     * 
	     * @return true if closing this graph is allowed
	     */
	    public boolean canClose() {
	    	
	        return blockClose == null;
	    }
		
	}

    public static void error(GsException e, Frame main) {
    	// FIXME: integrate error inside the main frame when possible
//        if (main instanceof GsMainFrame) {
//            Graph graph = ((GsMainFrame)main).getGraph();
//            graph.addNotificationMessage(new NotificationMessage(graph, e));
//            return;
//        }
        Tools.error(e, main);
    }

	public void whatToDoWithGraph(Graph<?, ?> newGraph, boolean b) {
		whatToDoWithGraph(newGraph, null, b);
	}

	public void whatToDoWithGraph(Graph<?, ?> newGraph, Graph<?,?> parentGraph, boolean b) {
		
		if (newGraph.getNodeOrderSize() < 21) {
			newFrame(newGraph);
			return;
		}
		
		// FIXME: create a new WhatToDo frame
		Debugger.log("TODO: implement a new whattodo frame");
	}

}
