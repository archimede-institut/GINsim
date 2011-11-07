package org.ginsim.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.ginsim.graph.backend.GraphBackend;
import org.ginsim.graph.backend.GraphViewBackend;
import org.ginsim.graph.backend.JgraphtBackendImpl;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGinsimGraphDescriptor;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.tagc.GINsim.graph.GsGraphEventCascadeNotificationAction;
import fr.univmrs.tagc.GINsim.graph.GsGraphListener;
import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.managerresources.Translator;

abstract public class AbstractGraphFrontend<V, E extends Edge<V>> implements Graph<V, E>, GraphView {

    private static List<GsGraphAssociatedObjectManager> OBJECT_MANAGERS = null;
	
	private final GraphBackend<V,E> graphBackend;
	private final GraphViewBackend viewBackend;
    
	// The name of the graph
	protected String graphName = "default_name";
	// The list of vertices ordered as defined by the model
	protected List<V> nodeOrder = new ArrayList<V>();
	
	// List of the registered graph listeners
	protected List< GsGraphListener<V,E>> listeners = new ArrayList<GsGraphListener<V,E>>();
    
    // The annotation associated with the graph
    protected Annotation graphAnnotation = null;

    
    // TODO === List of variables that could be removed if a better solution is found =============
    private boolean isParsing = false;
    protected Graph<?,?> associatedGraph = null;
    protected String associatedID = null;
    protected boolean annoted = false;
	
	/**
	 * Create a new graph with the default back-end.
	 */
	public AbstractGraphFrontend() {
		this( new JgraphtBackendImpl<V, E>());
		
	}

	
	/**
	 * Create a new graph with a back-end of choice.
	 * @param backend
	 */
	public AbstractGraphFrontend(GraphBackend<V, E> backend) {
		this.graphBackend = backend;
		viewBackend = graphBackend.getGraphViewBackend();
	}

	/**
	 * Add a vertex to this graph structure
	 * 
	 * @param vertex
	 * @return the created vertex
	 */
	public boolean addVertex( V vertex) {
		
		return graphBackend.addVertexInBackend(vertex);
	}
	
	/**
	 * Add an edge to this graph structure.
	 * 
	 * @param edge
	 * @return the created edge
	 */
	public boolean addEdge(E edge) {
		
		return graphBackend.addEdgeInBackend(edge);
	}
	
    /**
     * Remove a vertex from the graph.
     * 
     * @param vertex
     * @return true if the vertex was effectively removed
     */ 
	public boolean removeVertex(V vertex) {
		return graphBackend.removeVertex(vertex);
	}

	
    /**
     * Remove an edge from the graph.
     * 
     * @param edge
     * @return true if the edge was effectively removed
     */
	public boolean removeEdge(E edge) {
		return graphBackend.removeEdge(edge);
	}

	
	/**
	 * Hack required to forward the back-end to the GUI...
	 * @return
	 */
	public GraphBackend<V, E> getBackend() {
		return graphBackend;
	}

	
	/**
	 * @return the number of vertex in this graph.
	 */
	@Override
	public int getVertexCount() {
		return graphBackend.getVertexCount();
	}

	
    /**
     * @param source
     * @param target
     * @return the edge between source and target or null if not found.
     */
	@Override
	public E getEdge(V source, V target) {
		
		return graphBackend.getEdge(source, target);
	}

	
    /**
     * @return a Collection of the graph edges.
     */
	@Override
	public Collection<E> getEdges() {
		return graphBackend.getEdges();
	}

	
    /**
     * @return a Collection of the graph vertices.
     */
	@Override
	public Collection<V> getVertices() {
		return graphBackend.getVertices();
	}
	
	/**
	 * Give access to the vertex named with the given name
	 * 
	 * @param id name of a vertex
	 * @return the vertex corresponding to this unique id or null if not found.
	 */
	@Override
	public V getVertexByName( String id) {
		
		return graphBackend.getVertexByName( id);
	}
	
	
    /**
     * @param vertex
     * @return true if the vertex is in the graph, false if not.
     */
	@Override
    public boolean containsVertex(V vertex) {
        return graphBackend.containsVertex(vertex);
    }
    
	
    /**
     * @param from
     * @param to
     * @return true if an edge between the two provided vertices exists in the graph, false if not.
     */
	@Override
    public boolean containsEdge(V from, V to) {
        return graphBackend.containsEdge(from, to);
    }	
	

    /**
     * @param vertex
     * @return a Collection of the incoming edges of the given vertex.
     */
	@Override
	public Collection<E> getIncomingEdges(V vertex) {
		return graphBackend.getIncomingEdges(vertex);
	}
	
    
    /**
     * @param vertex
     * @return a Collection of the outgoing edges of the given vertex.
     */
	@Override
	public Collection<E> getOutgoingEdges(V vertex) {
		return graphBackend.getOutgoingEdges(vertex);
	}
	
	@Override
	public GsEdgeAttributesReader getEdgeReader() {
		return viewBackend.getEdgeAttributeReader();
	}
	
	@Override
	public GsVertexAttributesReader getVertexReader() {
		return viewBackend.getVertexAttributeReader();
	}
	
	
    /**
     * @return true is the graph is empty
     */
    public boolean isEmpty() {
        return !annoted && getAnnotation().isEmpty() && getVertexCount() == 0;
    }
	
	
    /**
     * Launch the merge method on the specialized graph level, merging the current graph with the given one
     * and fire a graph change event
     * 
     * @param graph the graph to merge with the current one
     * @return 
     */
	@Override
	public List<?> merge( Graph<V, E> graph) {
		
		List<?> v = this.doMerge(graph);
        if (v != null) {
        	fireGraphChange( CHANGE_MERGED, v);
        	//TODO Move the select on the GUI side
        	//graphManager.select(v);
        }
        
    	return v;
	}
	
	
	/**
	 * Specialized method that execute the merging of the given graph with the current one
	 * Must be override at specialized graph level
	 * 
	 * @param graph
	 * @return
	 */
	abstract protected List<?> doMerge( Graph<V, E> graph);
	

	/**
	 * Specialized method that build the sub-graph corresponding to the given lists of vertices and edges
	 * The returned graph contains clones of the given graph objects structured as they are in the current graph
	 * 
	 * @param vertex the list of vertex to include in the desired sub-graph
	 * @param edges the list of edges to include in the sub-graph
	 * @return a Graph containing clones of initial vertices and edges structured as they are in the current graph
	 */
	@Override
	public abstract Graph<V, E> getSubgraph(Collection<V> vertex, Collection<E> edges);

	
    /**
     * Give access to the name of the graph
     * 
     * @return the name associated with this graph.
     */
    public String getGraphName() {
    	
        return graphName;
    }
    
    
    /**
     * changes (if success) the name associated with this graph.
     * By default only valid xmlid are accepted.
     *
     * @param graphName the new name.
     * @throws GsException if the name is invalid.
     */
    public void setGraphName( String graph_name) throws GsException {

		if (!graph_name.matches("[a-zA-Z_]+[a-zA-Z0-9_-]*")) {
		    throw new GsException(GsException.GRAVITY_ERROR, "Invalid name");
		}
        this.graphName = graph_name;
        annoted = true;
        fireMetaChange();
    }
    
    
	
    //----------------------   EVENT MANAGEMENT METHODS --------------------------------------------
	
	/**
	 * Register a listener on this graph
	 * 
	 * @param g_listener the graph listener
	 */
	public void addGraphListener(GsGraphListener<V,E> g_listener) {
		
		listeners.add( g_listener);
	}
	
	
	/**
	 * Remove a graph listener from this graph
	 * 
	 * @param g_listener the graph listener to remove
	 */
	public void removeGraphListener( GsGraphListener<V,E> g_listener) {
		
		listeners.remove( g_listener);
	}
	
    /**
     * Associate the given graph to the current one
     * 
     * @param associated_graph
     */
    public void setAssociatedGraph( Graph<?,?> associated_graph) {

        if (associated_graph == null || !isAssociationValid( associated_graph)) {
            return;
        }

        if (associatedGraph != null) {
            associatedGraph.removeGraphListener( this);
            associatedGraph.getGraphManager().getEventDispatcher().removeGraphChangeListener(this);
            associatedGraph = null;
            return;
        }
        associatedGraph = associated_graph;
        associatedGraph.addGraphListener(this);
        associated_graph.getGraphManager().getEventDispatcher().addGraphChangedListener(this);
    }
	
    
    
    //----------------------   ASSOCIATED GRAPH METHODS --------------------------------------------

	
    /**
     * @return the graph associated with this one.
     */
    public Graph<?,?> getAssociatedGraph() {

        if ( associatedGraph == null && getAssociatedGraphID() != null) {
            Graph<?,?> ag = GsEnv.getRegistredGraph( associatedID);
            if (ag != null) {
                setAssociatedGraph( ag);
            } else {
                File f = new File(associatedID);
                if (f.exists()) {
                    ag = GsGinsimGraphDescriptor.getInstance().open(f);
                    GsEnv.newMainFrame(ag);
                    setAssociatedGraph(ag);
                } else {
                    GsEnv.error(new GsException(GsException.GRAVITY_INFO, "STR_openAssociatedGraphFailed"+"\n"+associatedID), mainFrame);
                }
            }
        }

        // check association
        if (associatedGraph != null && !isAssociationValid(associatedGraph)) {
            associatedGraph = null;
            associatedID = null;
        }

        return associatedGraph;
    }
    

    
    //----------------------   ANNOTATION METHODS --------------------------------------------

    
    
	/**
     * Give access to the annotation associated with this graph.
     * 
	 * @return the association associated with this graph
	 */
	public Annotation getAnnotation() {
		if (graphAnnotation == null) {
			graphAnnotation = new Annotation();
		}
		return graphAnnotation;
	}
	
	
	// -----------------------  ATTRIBUTE READERS METHODS ------------------------------------
	
	/**
	 * Give access to the attribute reader of edges
	 * 
	 * @return the attribute reader of edges
	 */
	public GsEdgeAttributesReader getEdgeAttributeReader() {
		
		return viewBackend.getEdgeAttributeReader();
	}
	
	
	/**
	 * Give access to the attribute reader of vertices
	 * 
	 * @return the attribute reader of vertices
	 */
	public GsVertexAttributesReader getVertexAttributeReader() {
		
		return viewBackend.getVertexAttributeReader();
	}

	
	// ====================================================================================
	// ====================================================================================
	// METHODS THAT DO NOT APPEAR ON GRAPH INTERFACE 
	// ====================================================================================
	// ====================================================================================
    
	
	// -------------------------  ASSOCIATED OBJECTS METHODS ---------------------------------
	
    /**
     * Register a manager to open/save associated objects
     *
     * @param manager
     */
    public static void registerObjectManager(GsGraphAssociatedObjectManager manager) {
    	
        if (OBJECT_MANAGERS == null) {
        	OBJECT_MANAGERS = new Vector<GsGraphAssociatedObjectManager>();
        }
        OBJECT_MANAGERS.add( manager);
    }
    
    
    /**
     * Give access to the list of registered object managers
     * 
     * @return the list of registered object managers
     */
    public List<GsGraphAssociatedObjectManager> getObjectManagerList() {
    	
        return OBJECT_MANAGERS;
    }

    /**
     * Give access to the Object manager in charge of the given object
     * 
     * @return the Object manager in charge of the given object, null if no Manager is defined for this object
     */
    public GsGraphAssociatedObjectManager getObjectManager(Object key) {
    	
    	if (OBJECT_MANAGERS == null) {
    		return null;
    	}
        for (int i=0 ; i < OBJECT_MANAGERS.size() ; i++) {
        	GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager) OBJECT_MANAGERS.get(i);
        	if (manager.getObjectName().equals( key)) {
        		return manager;
        	}
        }
        return null;
    }

    
	// -------------------------  ASSOCIATED GRAPH METHODS ---------------------------------

    
    
    /**
     * test if a graph can be associated with this one.
     * this is a default implementation and will always return false, override to do something usefull.
     *
     * @param graph
     * @return true if this is a valid associated graph.
     */
    private boolean isAssociationValid( Graph<?,?> graph) {
    	
        return false;
    }
    
    
    /**
     * set the path to the associated graph.
     * @param value
     */
    public void setAssociatedGraphID(String value) {
        associatedID = value;
    }
    
    
    /**
     * @return the ID (path) of the associated graph.
     */
    public String getAssociatedGraphID() {
        if (associatedGraph != null) {
            associatedID = associatedGraph.getSaveFileName();
            if (associatedID == null) {
                GsEnv.error(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_save")), mainFrame);
                return null;
            }
        }

        if (associatedID != null) {
            File f = new File(associatedID);
            if (!f.exists() || !f.canRead()) {
                GsEnv.error(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_notfound")+associatedID), mainFrame);
                associatedID = null;
            }
        } else {
            GsEnv.error(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_manual")), mainFrame);
        }

        if (associatedID == null) {
            associatedID = GsOpenAction.selectFileWithOpenDialog( mainFrame);
        }

        return associatedID;
    }
	
    
	// -------------------------  EVENT MANAGEMENT METHODS ---------------------------------

	
	/**
	 * the graph has changed, all listeners will be notified.
	 * it will also be marked as unsaved.
	 * @param change
     * @param data
	 */
	// TODO Move to AbstractGraphFrontend
	public void fireGraphChange(int change, Object data) {
		
		if (saved && !opening) {
		    saved = false;
		    if (mainFrame != null) {
		        mainFrame.updateTitle();
		    }
		}

        // TODO: extend this to support undo/redo and more events!
        List<GsGraphEventCascade> l_cascade = new ArrayList<GsGraphEventCascade>();
		switch (change) {
		case CHANGE_EDGEADDED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.edgeAdded((E) data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
			}
			break;
		case CHANGE_EDGEREMOVED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.edgeRemoved((E) data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
			}
			break;
		case CHANGE_VERTEXADDED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.vertexAdded((V) data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
			}
			break;
        case CHANGE_VERTEXREMOVED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.vertexRemoved((V) data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
            }
            break;
        case CHANGE_MERGED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.graphMerged((List<V>) data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
            }
            break;
        case CHANGE_VERTEXUPDATED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.vertexUpdated((V) data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
            }
            break;
        case CHANGE_EDGEUPDATED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.edgeUpdated((E) data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
            }
            break;
		}
        if (l_cascade.size() > 0) {
            addNotificationMessage(new GsGraphNotificationMessage(this, "cascade update", new GsGraphEventCascadeNotificationAction(), l_cascade, GsGraphNotificationMessage.NOTIFICATION_INFO_LONG));
        }
	}
	

    /**
     * 
     * @return True if parsing is active, Flase if not
     */
    public boolean isParsing() {
    	return isParsing;
    }
	
    /**
     * Inform the listeners of the graph that the parsing is finished
     * 
     */
	public void endParsing() {
    	isParsing = false;
    	for (GsGraphListener<V,E> l: listeners) {
    		l.endParsing();
    	}
    }


}
