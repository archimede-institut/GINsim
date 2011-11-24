package org.ginsim.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
import org.ginsim.graph.common.AbstractGraph;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphAssociation;
import org.ginsim.graph.common.GraphFactory;
import org.ginsim.graph.dynamicgraph.DynamicGraphImpl;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalTransitionGraphImpl;
import org.ginsim.graph.objectassociation.GraphAssociatedObjectManager;
import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.reducedgraph.ReducedGraphImpl;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraphImpl;
import org.ginsim.gui.shell.GsFileFilter;

import fr.univmrs.tagc.common.Debugger;

/**
 * descriptor for regulatoryGraph.
 */
public class GraphManager {

    private static GraphManager instance = null;
    private HashMap<Class<Graph>, GraphFactory> graphFactories = new HashMap<Class<Graph>, GraphFactory>();
    private HashMap<Graph, String> graphFilepath = new HashMap<Graph, String>();
    
    private GsFileFilter ffilter;

    public String getGraphType() {
        return "regulatory";
    }
    
    /**
     * The constructor of the manager retrieve the list of available GraphFactory
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
    public RegulatoryGraph getNewGraph(){
    	
    	GraphFactory factory = graphFactories.get( RegulatoryGraph.class);
    	RegulatoryGraph graph = (RegulatoryGraph) factory.create();
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
     * 
     * @param graph_class
     * @param args
     * @return
     */
    public <C extends Graph> C getNewGraph( Class<C> graph_class, Object... args){
    	
    	C graph = null;
    	
    	GraphFactory<C> factory = graphFactories.get( graph_class);
    	if( factory == null) {
    		Debugger.error( "No declared factory for graph class : " + graph_class);
    		return null;
    	}
    	
    	Method[] methods = factory.getClass().getMethods();
    	
    	Method found_method = null;
    	for( Method method : methods){
    		if( method.getName().equals( "create")){
    			Class[] method_params = method.getParameterTypes();
    			if( method_params.length == args.length){
    				boolean good_method = true;
    				for( int i = 0; i < method_params.length && good_method; i++){
    					Class param_class = method_params[i];
    					if( param_class.isPrimitive()){
    						String arg_class_name = args[i].getClass().getName();
    						if( !arg_class_name.substring( arg_class_name.lastIndexOf( ".")+1).toLowerCase().startsWith( param_class.getName().toLowerCase())){
    							good_method = false;
    						}
    					}
    					else{
        					try{
	    						param_class.cast( args[i]);
        					}
        					catch( ClassCastException cce){
        						good_method = false;
        					}
    					}
    				}
    				if( good_method){
    					found_method = method;
    					break;
    				}
    			}
    		}
    	}
    	
    	if( found_method != null){
	    	try{
	    		graph =(C) found_method.invoke( factory, args);
	    	}
			catch( InvocationTargetException ite){
				Debugger.error( "Unable to create graph of class " + graph_class);
				Debugger.error( ite);
			}
			catch( IllegalAccessException iae){
				Debugger.error( "Unable to create graph of class " + graph_class);
				Debugger.error( iae);
			}
			
			registerGraph( graph);
    	}
    	
		return graph;
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
     * Retrieve the Graph stored in the file at the given file path
     * 
     * @param file_path the file path of the graph file
     * @return the Graph stored in the file at the given file path
     * @throws GsException
     */
    public Graph open( String file_path) throws GsException{
    	
    	File file = new File( file_path);
    	if( !file.exists() || !file.isFile()){
    		throw new GsException( GsException.GRAVITY_ERROR, "No file on path " + file_path);
    	}
    	
    	return open(null, file);
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
                GinmlParser parser = new GinmlParser();
                boolean usePrefix = false;
                ZipEntry ze = f.getEntry("ginml");
                if (ze==null) {
                	usePrefix = true;
                	ze = f.getEntry( AbstractGraph.ZIP_PREFIX + RegulatoryGraphImpl.GRAPH_ZIP_NAME);
                	if (ze == null) {
                		ze = f.getEntry( AbstractGraph.ZIP_PREFIX + DynamicGraphImpl.GRAPH_ZIP_NAME);
                    	if (ze == null) {
                    		ze = f.getEntry( AbstractGraph.ZIP_PREFIX + ReducedGraphImpl.GRAPH_ZIP_NAME);
                        	if (ze == null) {
                        		ze = f.getEntry( AbstractGraph.ZIP_PREFIX + HierarchicalTransitionGraphImpl.GRAPH_ZIP_NAME);
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
	                        GraphAssociatedObjectManager manager = (GraphAssociatedObjectManager)v_omanager.get(i);
	                        ze = f.getEntry((usePrefix ? AbstractGraph.ZIP_PREFIX:"")+manager.getObjectName());
	                        if (ze != null) {
	                            Object o = manager.doOpen(f.getInputStream(ze), graph);
	                            ObjectAssociationManager.getInstance().addObject( graph, manager.getObjectName(), o);
	                        }
	                    }
	                }
	                v_omanager = ObjectAssociationManager.getInstance().getObjectManagerList( graph.getClass());
	                if (v_omanager != null) {
	                    for (int i=0 ; i<v_omanager.size() ; i++) {
	                        GraphAssociatedObjectManager manager = (GraphAssociatedObjectManager)v_omanager.get(i);
	                        ze = f.getEntry((usePrefix ? AbstractGraph.ZIP_PREFIX:"")+manager.getObjectName());
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
            	Debugger.error( "Error while opening Graph : " + file.getPath());
            	Debugger.error( e);
                return null;
            }
        } catch (Exception e) {// opening as zip failed, try the old method instead
        }

        // not a zip file
        GinmlParser parser = new GinmlParser();
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
			if( other_graph instanceof GraphAssociation){
				try{
					Graph associated_graph = ((GraphAssociation) other_graph).getAssociatedGraph();
				    if (graph == associated_graph) {
				    	((GraphAssociation) other_graph).setAssociatedGraphID( getGraphPath( graph));
				    	((GraphAssociation) other_graph).setAssociatedGraph(null);
				    }
				}
				catch( GsException ge){
					Debugger.error( "Unable to verify the associated graph of graph : " + other_graph.getGraphName());
					Debugger.error( ge);
				}
			}
		}
	}


}
