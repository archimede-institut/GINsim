package org.ginsim.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.filechooser.FileFilter;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.AbstractGraphFrontend;
import org.ginsim.graph.common.AssociatedGraph;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.ginsim.graph.dynamicgraph.DynamicGraphImpl;
import org.ginsim.graph.dynamicgraph.GsDynamicGraph;
import org.ginsim.graph.hierachicaltransitiongraph.GsHierarchicalTransitionGraph;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalTransitionGraphImpl;
import org.ginsim.graph.objectassociation.GsGraphAssociatedObjectManager;
import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.reducedgraph.GsReducedGraph;
import org.ginsim.graph.reducedgraph.ReducedGraphImpl;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;

import fr.univmrs.tagc.GINsim.graph.GsGinmlParser;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.common.Debugger;

/**
 * descriptor for regulatoryGraph.
 */
public class GraphManager {

    private static GraphManager instance = null;
    private HashMap<Class, GraphFactory> graphFactories = new HashMap<Class, GraphFactory>();
    private HashMap<Graph, String> graphFilepath = new HashMap<Graph, String>();
    

    private GsFileFilter ffilter;

    public String getGraphType() {
        return "regulatory";
    }
    
    /**
     * The constructor of the manager retrieve the list of available GraphFactory
     * 
     */
    private GraphManager(){
    	
        Iterator<GraphFactory> factory_list = ServiceLoader.load( GraphFactory.class).iterator(); 
        while (factory_list.hasNext()) {
            try {
            	GraphFactory factory = factory_list.next();
            	if( factory != null){
            		graphFactories.put( factory.getGraphClass(), factory);
            	}
            }
            catch (ServiceConfigurationError e){

            }
        }
    }
    
    /**
     * @return an instance of this graphDescriptor.
     */
    public static GraphManager getInstance() {
    	
        if (instance == null) {
            instance = new GraphManager();
        }
        return instance;
    }
    
    /**
     * Create a new default RegulatoryGraph
     * 
     * @return
     */
    public GsRegulatoryGraph getNewGraph(){
    	
    	GraphFactory factory = graphFactories.get( GsRegulatoryGraph.class);
    	GsRegulatoryGraph graph = (GsRegulatoryGraph) factory.create();
    	registerGraph( graph);
    	
    	return graph;
    }
    
    /**
     * Create a new default graph of the given class
     * 
     * @param graph_class the class of the graph to instantiate
     * @return the new instance of the graph
     */
    public <C extends Graph> C getNewGraph( Class<C> graph_class){

    	GraphFactory factory = graphFactories.get( graph_class);
    	if( factory != null){
    		C graph = (C) factory.create();
    		registerGraph( graph);
    		return graph;
    	}
    	
    	return null;
    }

    
    /**
     * Search for the factory managing the given graph type and return the corresponding parser
     * 
     * @param graph_type the type of the graph
     * @return the class of the parser used to read the given graph type
     */
    public Class getParserClass( String graph_type){
    	
    	for (Iterator<GraphFactory> iterator = graphFactories.values().iterator(); iterator.hasNext();) {
    		GraphFactory factory = (GraphFactory) iterator.next();
    		if( factory.getGraphType().equals( graph_type)){
    			return factory.getParser();
    		}
			
		}
    	
    	return null;
    }
    
    
    /**
     * register a graph without associated file path
     * 
     * @param graph the graph
     */
    public void registerGraph( Graph graph){
    	
    	registerGraph(graph, null);
    }
    
    
    /**
     * Memorize the link between the graph and the path of the file the graph has been loaded from or saved to
     * 
     * @param graph the graph
     * @param file_path the path of the file the graph has been loaded from or saved to
     */
    public void registerGraph( Graph graph, String file_path){
    	
    	graphFilepath.put( graph, file_path);
    }
    
    
    /**
     * Return the list of registered graphs
     * 
     * @return
     */
	public Set getAllGraphs() {

		return graphFilepath.keySet();
	}
    
    
    /**
     * Return the path of the file the graph has been loaded from or saved to (if it exists)
     * 
     * @param graph
     * @return the path of the file the graph has been loaded from or saved to if it exsists, null if not.
     */
    public String getGraphPath( Graph graph){
    	
    	return graphFilepath.get( graph);
    }
    
    /**
     * Return the graph associated to the given file path, if it exists
     * 
     * @param path the file path associated to the desired graph
     * @return the graph associated to the given file path, if it exists, null if not
     */
    public Graph getGraphFromPath( String path){
    	
    	if( path != null){
	    	for (Iterator<Entry<Graph,String>> iterator = graphFilepath.entrySet().iterator(); iterator.hasNext();) {
	    		Entry<Graph,String> entry = iterator.next();
				if( path.equals( entry.getValue())){
					return entry.getKey();
				}
			}
    	}
    	
    	return null;
    }
    

    /**
     * 
     * @param file the File containing the graph to open
     * @return a graph of the correct type read from the given file
     */
	public Graph open(File file)  throws GsException{
		
		return open(null, file);
	}
    
    
    /**
     * 
     * @param file the File containing the graph to open
     * @return a graph of the correct type read from the given file
     */
    public Graph open(Map map, File file) throws GsException{
        try {
            ZipFile f = new ZipFile(file);
            try {
                GsGinmlParser parser = new GsGinmlParser();
                boolean usePrefix = false;
                ZipEntry ze = f.getEntry("ginml");
                if (ze==null) {
                	usePrefix = true;
                	ze = f.getEntry( AbstractGraphFrontend.ZIP_PREFIX + GsRegulatoryGraph.GRAPH_ZIP_NAME);
                	if (ze == null) {
                		ze = f.getEntry( AbstractGraphFrontend.ZIP_PREFIX + DynamicGraphImpl.GRAPH_ZIP_NAME);
                    	if (ze == null) {
                    		ze = f.getEntry( AbstractGraphFrontend.ZIP_PREFIX + ReducedGraphImpl.GRAPH_ZIP_NAME);
                        	if (ze == null) {
                        		ze = f.getEntry( AbstractGraphFrontend.ZIP_PREFIX + HierarchicalTransitionGraphImpl.GRAPH_ZIP_NAME);
	                        	if (ze == null) {
	                        		throw new GsException( GsException.GRAVITY_ERROR, "Unable to find a known main zip entry");
	                        	}
                        	}
                    	}
                	}
                }
                
                Graph graph = parser.parse(f.getInputStream(ze), map);
                if (map == null) {
                	// try to restore associated data ONLY if no subgraph is selected
                	// TODO: need to load associated entry with subgraphs
                	List v_omanager = ObjectAssociationManager.getInstance().getObjectManagerList();
	                if (v_omanager != null) {
	                    for (int i=0 ; i<v_omanager.size() ; i++) {
	                        GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager)v_omanager.get(i);
	                        ze = f.getEntry((usePrefix ? AbstractGraphFrontend.ZIP_PREFIX:"")+manager.getObjectName());
	                        if (ze != null) {
	                            Object o = manager.doOpen(f.getInputStream(ze), graph);
	                            ObjectAssociationManager.getInstance().addObject( graph, manager.getObjectName(), o);
	                        }
	                    }
	                }
	                v_omanager = ObjectAssociationManager.getInstance().getObjectManagerList( graph.getClass());
	                if (v_omanager != null) {
	                    for (int i=0 ; i<v_omanager.size() ; i++) {
	                        GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager)v_omanager.get(i);
	                        ze = f.getEntry((usePrefix ? AbstractGraphFrontend.ZIP_PREFIX:"")+manager.getObjectName());
	                        if (ze != null) {
	                            Object o = manager.doOpen(f.getInputStream(ze), graph);
	                            ObjectAssociationManager.getInstance().addObject( graph, manager.getObjectName(), o);
	                        }
	                    }
	                }
                }
                registerGraph( graph, file.getAbsolutePath());
                return graph; 
            } catch (Exception e) {
                System.out.println("error opening");
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {// opening as zip failed, try the old method instead
        }

        // not a zip file
        GsGinmlParser parser = new GsGinmlParser();
        try {
            Graph graph = parser.parse(new FileInputStream(file), map);
            registerGraph( graph, file.getAbsolutePath());
            return graph;
        } catch (FileNotFoundException e) {
            throw new GsException(GsException.GRAVITY_ERROR, e);
        }
    }
    
	public FileFilter getFileFilter() {
		if (ffilter == null) {
			ffilter = new GsFileFilter();
			ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
		}
		return ffilter;
	}
	
	/**
	 * Remove all the server side association to the graph
	 * 
	 * @param graph the graph to close
	 */
	public void close( Graph graph){
		
		graphFilepath.remove( graph);
		
		// Remove the graph associated objects
		ObjectAssociationManager.getInstance().removeAllObjects( graph);
		
		// Remove the references to the graph as associated graph
		for( Graph other_graph : graphFilepath.keySet()){
			if( other_graph instanceof AssociatedGraph){
				try{
					Graph associated_graph = ((AssociatedGraph) other_graph).getAssociatedGraph();
				    if (graph == associated_graph) {
				    	((AssociatedGraph) other_graph).setAssociatedGraphID( getGraphPath( graph));
				    	((AssociatedGraph) other_graph).setAssociatedGraph(null);
				    }
				}
				catch( GsException ge){
					Debugger.log( "Unable to verify the associated graph of graph : " + other_graph.getGraphName());
				}
			}
		}
	}


}
