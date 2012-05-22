package org.ginsim.core.graph.common;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.backend.GraphBackend;
import org.ginsim.core.graph.backend.GraphViewListener;
import org.ginsim.core.graph.backend.JgraphtBackendImpl;
import org.ginsim.core.graph.common.EdgeAttributeReaderImpl.EdgeVSdata;
import org.ginsim.core.graph.common.NodeAttributeReaderImpl.NodeVSdata;
import org.ginsim.core.graph.objectassociation.GraphAssociatedObjectManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;

/**
 * Base class for graphs using a storage backend: it provides generic methods and storage abstraction.
 * The actual structure is stored in the graph backend, and classes deriving from this one
 * can provide specialised methods.
 * 
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
abstract public class AbstractGraph<V, E extends Edge<V>> implements Graph<V, E> {
	
	private final GraphBackend<V,E> graphBackend;
	
	// view data
	private GraphViewListener listener;
    private Map<Edge<?>,EdgeVSdata> evsmap = null;
    private Map<Object, NodeVSdata> vvsmap = null;

    
	// The name of the graph
	protected String graphName = "default_name";
	
    // The annotation associated with the graph
    protected Annotation graphAnnotation = null;
    
    // The mode the graph must use when saved
    private int saveMode;

    
    // TODO === List of variables that could be removed if a better solution is found =============
    private boolean isParsing = false;
    protected boolean annoted = false;
    
    private static final List<GraphAssociatedObjectManager> v_OManager = new ArrayList<GraphAssociatedObjectManager>();
    public static final String ZIP_PREFIX = "GINsim-data/";
	
	/**
	 * Create a new graph with the default back-end.
	 */
	public AbstractGraph() {
		this( false);
	}
	
    /**
     *
     * @param parsing
     */
    public AbstractGraph( boolean parsing) {
        this( (GraphBackend<V, E>)JgraphtBackendImpl.getGraphBackend(), parsing);
    }

	
	/**
	 * Create a new graph with a back-end of choice.
	 * @param backend
	 */
	private AbstractGraph(GraphBackend<V, E> backend, boolean parsing) {
		this.graphBackend = backend;
        this.isParsing = parsing;
	}
	
	
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
	
	

    //----------------------   GRAPH SAVING MANAGEMENT METHODS -------------------------------
    
    /**
     * Set the mode of saving the graph must used when saved
     * 
     * @param save_mode the mode of saving
     */
    public void setSaveMode( int save_mode){
    	
    	saveMode = save_mode;
    }
    
    /**
     * Return the mode the graph must used when saved
     * 
     * @return the mode the graph must used when saved
     */
    public int getSaveMode(){
    	
    	return saveMode;
    }
    
	/**
	 * Return the zip extension for the graph type
	 * 
	 * @return the zip extension for the graph type
	 */
    protected String getGraphZipName() {
    	
    	return "ginml";
    }
    
    
    //----------------------   GRAPH VERTICES AND EDGES MANAGEMENT METHODS -------------------------------

	
	/**
	 * Add a node to this graph structure
	 * 
	 * @param node
	 * @return the created node
	 */
	@Override
	public boolean addNode( V node) {
		
		if (graphBackend.addNodeInBackend(node)) {
			fireGraphChange(GraphChangeType.NODEADDED, node);
			return true;
		}
		
		return false;
	}

	/**
	 * Add an edge to this graph structure.
	 * 
	 * @param edge
	 * @return the created edge
	 */
	@Override
	public boolean addEdge(E edge) {
		
		if (graphBackend.addEdgeInBackend(edge)) {
			fireGraphChange(GraphChangeType.EDGEADDED, edge);
			return true;
		}
		return false;
	}
	
    /**
     * Remove a node from the graph.
     * 
     * @param node
     * @return true if the node was effectively removed
     */ 
	@Override
	public boolean removeNode(V node) {
		
		return graphBackend.removeNode(node);
	}

	
    /**
     * Remove an edge from the graph.
     * 
     * @param edge
     * @return true if the edge was effectively removed
     */
	@Override
	public boolean removeEdge(E edge) {
		
		return graphBackend.removeEdge(edge);
	}

	
	/**
	 * Hack required to forward the back-end to the GUI...
	 * @return the backend in use
	 */
	public GraphBackend<V, E> getBackend() {
		return graphBackend;
	}

	
	/**
	 * @return the number of node in this graph.
	 */
	@Override
	public int getNodeCount() {
		return graphBackend.getNodeCount();
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
	public Collection<V> getNodes() {
		return graphBackend.getNodes();
	}

	/**
	 * Return a list of set of node, each set containing a strongly connected component of the graph
	 * 
	 * @return a list of set of node, each set containing a strongly connected component of the graph
	 */
	@Override
	public List<Set<V>> getStronglyConnectedComponents() {
		return graphBackend.getStronglyConnectedComponents();
	}

	/**
	 * Give access to the node named with the given name
	 * 
	 * @param id name of a node
	 * @return the node corresponding to this unique id or null if not found.
	 */
	@Override
	public V getNodeByName( String id) {
		
		return graphBackend.getNodeByName( id);
	}

	
    /**
     * Return the size of the node order
     * 
     * @return the size of the node order
     */
    @Override
	public abstract int getNodeOrderSize();
	
	
	/**
	 * Search the vertices with ID matching the given regular expression. 
	 * Other kind of graph could overwrite this method. 
	 * 
	 * @param regexp the regular expression node ID must match to be selected
	 * @return a Vector of vertices
	 */
	public Vector<V> searchNodes( String regexp) {
		
		Vector<V> v = new Vector<V>();
		
		Pattern pattern = Pattern.compile(regexp, Pattern.COMMENTS | Pattern.CASE_INSENSITIVE);
		
		for (Iterator<V> it = getNodes().iterator(); it.hasNext();) {
			V vertex = (V) it.next();
			Matcher matcher = pattern.matcher(vertex.toString());
			if (matcher.find()) {
				v.add(vertex);
			}
		}
		return v;
	}
	
	@Override
    public boolean containsNode(V node) {
        return graphBackend.containsNode(node);
    }
    
	@Override
	public V getExistingNode(V node) {
		return graphBackend.getExistingNode(node);
	}
	
	@Override
    public boolean containsEdge(V from, V to) {
        return graphBackend.containsEdge(from, to);
    }	
	
	@Override
	public Collection<E> getIncomingEdges(V node) {
		return graphBackend.getIncomingEdges(node);
	}
	
	@Override
	public Collection<E> getOutgoingEdges(V node) {
		return graphBackend.getOutgoingEdges(node);
	}
	
	@Override
	public List<E> getShortestPath( V source, V target){
		
		return graphBackend.getShortestPath( source, target);
	}
	
	/**
	 * @return the place where local VS data is stored (create it if needed)
	 * @see #hasFallBackVSData()
	 */
    private Map<Edge<?>, EdgeVSdata> getEdgeVSMap() {
        if (evsmap == null) {
            evsmap = new HashMap<Edge<?>, EdgeVSdata>();
        }
        return evsmap;
    }
    
    private Map<Object, NodeVSdata> getNodeVSMap() {
        if (vvsmap == null) {
            vvsmap = new HashMap<Object, NodeVSdata>();
        }
        return vvsmap;
    }

	@Override
	public void addViewListener(GraphViewListener listener) {
		this.listener = listener;
	}
	
	/**
	 * Declare an object visual setting change
	 * 
	 * @param o
	 */
	public void refresh(Object o) {
		// TODO: fire graph change?
		if (listener != null) {
			listener.refresh(o);
		} else if (o instanceof Edge) {
			fireGraphChange(GraphChangeType.EDGEUPDATED, o);
		} else {
			fireGraphChange(GraphChangeType.NODEUPDATED, o);
		}
	}

	
	@Override
	public EdgeAttributesReader getEdgeAttributeReader() {
		return new EdgeAttributeReaderImpl(this, getEdgeVSMap(), getNodeAttributeReader());
	}
	
	@Override
	public NodeAttributesReader getNodeAttributeReader() {
		return new NodeAttributeReaderImpl(this, getNodeVSMap());
	}
	
	public Dimension getDimension() {
    	int width = 0;
    	int height = 0;
		NodeAttributesReader nreader = getNodeAttributeReader();
		for (V node: getNodes()) {
			nreader.setNode(node);
    		int x = nreader.getX() + nreader.getWidth();
    		if (x > width) {
    			width = x;
    		}
    		int y = nreader.getY() + nreader.getHeight();
    		if (y > height) {
    			height = y;
    		}
		}
		
		EdgeAttributesReader ereader = getEdgeAttributeReader();
        for (Edge e: getEdges()) {
            ereader.setEdge(e);
            List<Point> points = ereader.getPoints();
            if (points == null) {
                continue;
            }
            for (Point2D pt: points ) {
                int x = (int)pt.getX();
        		if (x > width) {
        			width = x;
        		}
                int y = (int)pt.getY();
        		if (y > height) {
        			height = y;
        		}
            }
        }

		
		return new Dimension(width+5, height+5);
	}

	
    /**
     * @return true is the graph is empty
     */
    public boolean isEmpty() {
        return !annoted && getAnnotation().isEmpty() && getNodeCount() == 0;
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
        	fireGraphChange( GraphChangeType.GRAPHMERGED, v);
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
	 * @param node the list of node to include in the desired sub-graph
	 * @param edges the list of edges to include in the sub-graph
	 * @return a Graph containing clones of initial vertices and edges structured as they are in the current graph
	 */
	@Override
	public abstract Graph<V, E> getSubgraph(Collection<V> node, Collection<E> edges);
    
    
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
	
	
	// ----------------------   SAVING METHODS -----------------------------------------------
	
	public void save(String save_path) throws GsException {
		save(save_path, null, null, 2);
	}
	public void save(String save_path, Collection<V> vertices, Collection<E> edges) throws GsException {
		save(save_path, vertices, edges, 2);
	}
	/**
	 * 
	 * @param save_path
	 * @param vertices
	 * @param edges
	 * @param saveMode
	 * @throws GsException
	 * 
	 * 	 TODO should we keep saveMode? 
	 *   all interactive-notifications should be replaced by exceptions...
	 * 
	 */
	private void save(String save_path, Collection<V> vertices, Collection<E> edges, int saveMode) throws GsException {

		if (vertices == null) {
			vertices = getNodes();
			edges = getEdges();
		}
		// TODO: make sure that selected edges don't refer non-selected nodes

		File f;
		if ( save_path != null ){
			f = new File( save_path);
		}
		else {
			String save_filepath = GraphManager.getInstance().getGraphPath( this);
			if ( save_filepath != null){
				f = new File( save_filepath);
			}
			else {
				throw new GsException( GsException.GRAVITY_ERROR, "No save path has been set for this file");
			}
		}
		File ftmp = null;
		String fileName = f.getAbsolutePath();;
		if (f.exists()) {
			// create a temporary file to avoid destroying a good file in case save does not work
			try {
				ftmp = File.createTempFile(f.getName(), null, f.getParentFile());
				fileName = ftmp.getAbsolutePath();
			} catch (Exception e) {
				// TODO : REFACTORING ACTION
				// TODO : introduce a clean permission checking
				LogManager.error( "Could not use a tmp file in the same directory");
				LogManager.error( e);
				//       			ftmp = File.createTempFile(f.getName(), null);
				//       			fileName = ftmp.getAbsolutePath();
			}
		}

		try {
			ZipOutputStream zos = new ZipOutputStream( new FileOutputStream(fileName));
			// FIXME: uncompressed Zip require to set the size and CRC by hand!!
			// this must be done for each ZipEntry: save it to a tmpfile,
			// mesure the CRC and the size, then put it in the uncompressed zip...
			zos.putNextEntry(new ZipEntry(ZIP_PREFIX + getGraphZipName()));
			OutputStreamWriter osw = new OutputStreamWriter(zos, "UTF-8");
			
			// TODO: doSave should take the selection as parameter.
			// note: this method should be the only caller and ensures that the selection is defined and consistent
			doSave(osw, vertices, edges, saveMode);
			osw.flush();
			zos.closeEntry();
			// now save associated objects
			if (v_OManager != null) {
				for (int i=0 ; i<v_OManager.size() ; i++) {
					GraphAssociatedObjectManager manager = (GraphAssociatedObjectManager)v_OManager.get(i);
					if (manager.needSaving(this)) {
						zos.putNextEntry(new ZipEntry(ZIP_PREFIX+manager.getObjectName()));
						try {
							manager.doSave(osw, this);
						} catch (GsException e) {
							throw e;
						} finally {
							osw.flush();
							zos.closeEntry();
						}
					}
				}
			}
			List v_specManager = ObjectAssociationManager.getInstance().getObjectManagerList( this.getClass());
			if (v_specManager != null) {
				for (int i=0 ; i<v_specManager.size() ; i++) {
					GraphAssociatedObjectManager manager = (GraphAssociatedObjectManager)v_specManager.get(i);
					if (manager.needSaving(this)) {
						zos.putNextEntry(new ZipEntry(ZIP_PREFIX+manager.getObjectName()));
						manager.doSave(osw, this);
						osw.flush();
					}
				}
			}
			zos.close();
		} catch (Exception e) {
			throw new GsException(GsException.GRAVITY_ERROR, e);
		}

		if (ftmp != null) {
			// Everything went fine, rename the temporary file
			boolean r = ftmp.renameTo(f);
			if (!r) {
				if (f.exists()) {
					f.delete();
					r = ftmp.renameTo(f);
				}
				if (!r) {
					new GsException( GsException.GRAVITY_ERROR, "Enable to save the file to chosen path : "+ftmp.getAbsolutePath());
				}
			}
		}
	}

	
	
	// ====================================================================================
	// ====================================================================================
	// METHODS THAT DO NOT APPEAR ON GRAPH INTERFACE 
	// ====================================================================================
	// ====================================================================================
    
	// ----------------------   SAVING METHODS -----------------------------------------------
   

	/**
	 * save implementation for a specific graph type.
	 * 
	 * @param osw		stream writer
	 * @param vertices 	vertices that should be saved (can not be null)
	 * @param edges		edges that should be saved (can not be null)
	 * @param saveMode	save mode, will probably go away
	 */
	protected abstract void doSave(OutputStreamWriter osw, Collection<V> vertices, Collection<E> edges, int saveMode) throws GsException;
   

    
	// -------------------------  EVENT MANAGEMENT METHODS ---------------------------------

	public void fireMetaChange() {
    	
        fireGraphChange(GraphChangeType.METADATACHANGE, null);
    }

	@Override
	public void fireGraphChange(GraphChangeType type, Object data) {
		GraphManager.getInstance().fireGraphChange(this, type, data);
	}

    /**
     * 
     * @return True if parsing is active, Flase if not
     */
    public boolean isParsing() {
    	return isParsing;
    }
	
}
