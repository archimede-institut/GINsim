package fr.univmrs.tagc.GINsim.graph;

import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.univmrs.tagc.GINsim.global.GsEventDispatcher;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;

/**
 * interface that each graphManager should implement, the only implementor should be
 * the jgraphtGraphManager for a while
 */
abstract public class GsGraphManager {

	private static Vector v_layout = null;
	private static Vector v_export = null;
	private static Vector v_action = null;

	protected boolean canUndo;
    private HashMap evsmap = null;
    private HashMap vvsmap = null;
    private GsEdgeAttributesReader fbEReader = null;
    private GsVertexAttributesReader fbVReader = null;
    private GsEventDispatcher eventDispatcher = new GsEventDispatcher(false);

    protected GsMainFrame mainFrame;
	
    /**
     * @return a Jcomponent showing the graph.
     */
	abstract public JComponent getGraphPanel();
    /**
     * @param sp
     * @return a "minimap" for the graphPanel.
     */
	abstract public JPanel getGraphMapPanel( JScrollPane sp);
    /**
     * @return the eventDispatcher for this graph.
     */
    public GsEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    /**
     * add the given vertex to the graph.
     * @param vertex
     * @return the newly created vertex (or null if failed/inapropriate)
     */
	abstract public boolean addVertex (Object vertex);
    /**
     * add the given vertex and place it.
     * 
     * @param vertex
     * @param x
     * @param y
     */
	abstract public void placeVertex (Object vertex, int x, int y);
    /**
     * add an edge beetwen two vertices and attach the given data to it.
     * 
     * @param souce
     * @param target
     * @param data
     * @return the newly created edge (or null if failed/inapropriate)
     */
	abstract public Object addEdge (Object souce, Object target, Object data);
    
    /**
     * select all objects (vertices and edges).
     */
    abstract public void selectAll();
    /**
     * select some objects (vertices and edges).
     * @param l the list of objects to select
     */
    abstract public void select(List l);
    /**
     * select some objects (vertices and edges).
     * @param s the Set of objects to select
     */
    abstract public void select(Set s);
    /**
     * select some objects (vertices and edges) and keep the previous selection.
     * @param l the list of objects to select
     */
    abstract public void addSelection(List l);
    /**
     * select some objects (vertices and edges) and keep the previous selection.
     * @param s the Set of objects to select
     */
    abstract public void addSelection(Set s);
    /** 
     * select a single item.
     * 
     * @param obj the item to select 
     */
    abstract public void select(Object obj);

    /**
     * invert the selection.
     * ie unselect currently selected objects and select all others.
     *
     */
    abstract public void invertSelection();
    /**
     * show/hide the grid.
     * @param b
     */
    abstract public void showGrid(boolean b);

    /**
     * make the grid (in)active.
     * @param b
     */
    abstract public void setGridActive(boolean b);

    /**
     * move all vertex to front
     * @param b
     */
    abstract public void vertexToFront(boolean b);
    
    /**
     * @return the real graph.
     */
    abstract public GsGraph getGsGraph();
    
    /**
     * zoom out the display.
     */
    abstract public void zoomOut();

    /**
     * zoom in the display.
     */
    abstract public void zoomIn();

    /**
     * restore the display to the default zoom level.
     */
    abstract public void zoomNormal();

    /**
     * show/hide edge name on the display.
     * @param b
     */
    abstract public void displayEdgeName(boolean b);

    /**
     * show/hide vertex name on the display.
     * @param b
     */
    abstract public void displayVertexName(boolean b);
    
    /**
     * undo last action.
     */
    abstract public void undo();
    /**
     * redo last undone action.
     */
    abstract public void redo();
    /**
     * delete the selected objects.
     */
    abstract public void delete();

    /**
     * remove the given vertex from the graph.
     * 
     * @param obj
     */
    abstract public void removeVertex(Object obj);
    
    protected void vertexRemoved(Object vertex) {
    	if (vvsmap != null) {
    		vvsmap.remove(vertex);
    	}
    }
    protected void edgeRemoved(Object edge) {
    	if (evsmap != null) {
    		evsmap.remove(edge);
    	}
    }

    /**
     * @param source
     * @param target
     * @return the edge between source and target or null if not found.
     */
    abstract public Object getEdge(Object source, Object target);

    /**
     * @return an iterator to all vertices.
     */
    abstract public Iterator getVertexIterator();
    /**
     * @return an iterator to all edges.
     */
    abstract public Iterator getEdgeIterator();

    /**
     * @return an iterator to selected edges.
     */
	abstract public Iterator getSelectedEdgeIterator();
    /**
     * @return an iterator to selected vertices.
     */
	abstract public Iterator getSelectedVertexIterator();

    
    /**
     * @param vertex
     * @return incoming edges of the given vertex.
     */
    abstract public List getIncomingEdges(Object vertex);
    /**
     * @param vertex
     * @return outgoing edges of the given vertex.
     */
    abstract public List getOutgoingEdges(Object vertex);
    /**
     * @param source
     * @param target
     */
    abstract public void removeEdge(Object source, Object target);
    /**
     * 
     */
    abstract public void ready();
    
    /**
     * @return an helper to read/change visual info on this graph's edges
     * @see GsVertexAttributesReader
     */
    abstract public GsVertexAttributesReader getVertexAttributesReader();
    /**
     * @return an helper to read/change visual info on this graph's vertices
     * @see GsEdgeAttributesReader
     */
    abstract public GsEdgeAttributesReader getEdgeAttributesReader();
    
	/**
	 * @param layout
	 */
	public static void registerLayoutProvider(GsActionProvider layout) {
		if (v_layout == null) {
			v_layout = new Vector();
		}
		v_layout.add(layout);
	}
	/**
	 * @return a list of avaible layouts.
	 */
	public Vector getLayout() {
		return v_layout;
	}

	/**
	 * @param export
	 */
	public static void registerExportProvider(GsActionProvider export) {
		if (v_export == null) {
			v_export = new Vector();
		}
		v_export.add(export);
	}
	/**
	 * @return a list of avaible export filters.
	 */
	public Vector getExport() {
		return v_export;
	}

	/**
	 * 
	 * @param action
	 */
	public static void registerActionProvider(GsActionProvider action) {
		if (v_action == null) {
			v_action = new Vector();
		}
		v_action.add(action);
	}
	/**
	 * @return a list of avaible actions.
	 */
	public Vector getAction() {
		return v_action;
	}

	/**
	 * @return the number of vertex in this graph.
	 */
	abstract public int getVertexCount();
	
	/**
	 * set the mainFrame containing this graph.
	 * @param m the mainFrame
	 */
	abstract public void setMainFrame(GsMainFrame m);

	/**
	 * @param id name of a vertex
	 * @return the vertex corresponding to this uniq id or null if not found.
	 */
	public Object getVertexByName(String id) {
		Iterator it = getVertexIterator();
		while (it.hasNext()) {
			Object vertex = it.next();
			if (id.equals(vertex.toString())) {
				return vertex;
			}
		}
		return null;
	}

	/**
	 * Please use with caution: this can be a huge memory eater... 
	 * (at least it is for jgrapht graphs which use HashMap to store vertices)
	 * 
	 * @return an array of all vertices
	 */
	abstract public Object[] getVertexArray();
	/**
	 * @return true if this graphManager supports undo/redo
	 */
	public boolean canUndo() {
		return canUndo;
	}
	
	/**
	 * @return the place where local VS data is stored (create it if needed)
	 * @see #hasFallBackVSData()
	 */
    protected Map getEdgeVSMap() {
        if (evsmap == null) {
            evsmap = new HashMap();
        }
        return evsmap;
    }
    
    protected Map getVertexVSMap() {
        if (vvsmap == null) {
            vvsmap = new HashMap();
        }
        return vvsmap;
    }
    
    /**
     * @return a generic edgeAttribute storing it's data to a local hashMap
     */
    protected GsEdgeAttributesReader getFallBackEReader() {
        if (fbEReader == null) {
            fbEReader = new GsFallBackEdgeAttributeReader(getEdgeVSMap());
        }
        return fbEReader;
    }
    /**
     * @return a generic vertexAttributeReader storing it's data to a local hashMap
     */
    protected GsVertexAttributesReader getFallBackVReader() {
        if (fbVReader == null) {
            fbVReader = new GsFallbackVertexAttributeReader(getVertexVSMap());
        }
        return fbVReader;
    }
    /**
     * @return true if generic VS are avaible for this graph
     */
    public boolean hasFallBackVSData() {
        return true;
    }
    
    /**
     * find the byteestPath between two nodes
     * 
     * @param source starting point
     * @param target ending point
     * @return a List describing the path or null if none found
     */
    public abstract List getShortestPath(Object source, Object target);
    
    /**
     * test if the graph contains a given vertex.
     * 
     * @param vertex
     * @return true if this graph contains the vertex.
     */
    public abstract boolean containsVertex(Object vertex);

    /**
     * test if the graph contains a given edge.
     * 
     * @param from
     * @param to
     * @return true if an edge from <code>from</code> to <code>to</code> exists.
     */
    public abstract boolean containsEdge(Object from, Object to);
    
    /**
     * @return the mainFrame showing this graph.
     */
    public GsMainFrame getMainFrame() {
        return mainFrame;
    }
    /**
     * @return true if the grid is visible
     */
    public abstract boolean isGridDisplayed();
    /**
     * @return true if the grid is visible
     */
    public abstract boolean isGridActive();
	public BufferedImage getImage() {
		return null;
	}
}
