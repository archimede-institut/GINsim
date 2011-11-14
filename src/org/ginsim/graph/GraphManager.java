package org.ginsim.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.ginsim.graph.dynamicgraph.GsDynamicGraph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.BaseAction;
import org.ginsim.gui.service.tools.connectivity.GsReducedGraph;

import fr.univmrs.tagc.GINsim.graph.GsGinmlParser;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalTransitionGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * descriptor for regulatoryGraph.
 */
public class GraphManager {

    private static GraphManager instance = null;
    private HashMap<String,GraphFactory> graphFactories = new HashMap<String,GraphFactory>();
    

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
            		graphFactories.put( factory.getGraphType(), factory);
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
     * 
     * 
     * @param graph_type the type of the graph
     * @return the class of the parser used to read the given graph type
     */
    public Class getParserClass( String graph_type){
    	
    	GraphFactory factory = graphFactories.get( graph_type);
    	
    	if( factory != null){
    		return factory.getParser();
    	}
    	else{
    		return null;
    	}
    }
    
    
    /**
     * Create a new default RegulatoryGraph
     * 
     * @return
     */
    public GsRegulatoryGraph getNewGraph(){
    	
    	return new GsRegulatoryGraph();
    }
    
    /**
     * Create a new default graph of the given class
     * 
     * @param graph_class the class of the graph to instantiate
     * @return the new instance of the graph
     */
    public <C extends Graph> C getNewGraph( Class<C> graph_class){

    	try {
			return graph_class.newInstance();
		} catch (InstantiationException e) {
			Debugger.log( "Unable to create a new Graph of class '" + graph_class + "' : " + e);
			return null;
		} catch (IllegalAccessException e) {
			Debugger.log( "Unable to create a new Graph of class '" + graph_class + "' : " + e);
			return null;
		}
    }


    /**
     * 
     * @param file the File containing the graph to open
     * @return a graph of the correct type read from the given file
     */
	public Graph open(File file) {
	    return open(null, file);
	}
    
    
    /**
     * 
     * @param file the File containing the graph to open
     * @return a graph of the correct type read from the given file
     */
    public Graph open(Map map, File file) {
        try {
            ZipFile f = new ZipFile(file);
            try {
                GsGinmlParser parser = new GsGinmlParser();
                boolean usePrefix = false;
                ZipEntry ze = f.getEntry("ginml");
                if (ze==null) {
                	usePrefix = true;
                	ze = f.getEntry( AbstractGraphFrontend.ZIP_PREFIX+GsRegulatoryGraph.zip_mainEntry);
                	if (ze == null) {
                		ze = f.getEntry( AbstractGraphFrontend.ZIP_PREFIX+GsDynamicGraph.zip_mainEntry);
                    	if (ze == null) {
                    		ze = f.getEntry( AbstractGraphFrontend.ZIP_PREFIX+GsReducedGraph.zip_mainEntry);
                        	if (ze == null) {
                        		ze = f.getEntry( AbstractGraphFrontend.ZIP_PREFIX+GsHierarchicalTransitionGraph.zip_mainEntry);
	                        	if (ze == null) {
	                        		// TODO: nicer error here
	                        		System.out.println("unable to find a known main zip entry");
	                        	}
                        	}
                    	}
                	}
                }
                
                Graph graph = parser.parse(f.getInputStream(ze), map);
                if (map == null) {
                	// try to restore associated data ONLY if no subgraph is selected
                	// TODO: need to load associated entry with subgraphs
                	List v_omanager = graph.getObjectManagerList();
	                if (v_omanager != null) {
	                    for (int i=0 ; i<v_omanager.size() ; i++) {
	                        GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager)v_omanager.get(i);
	                        ze = f.getEntry((usePrefix ? AbstractGraphFrontend.ZIP_PREFIX:"")+manager.getObjectName());
	                        if (ze != null) {
	                            Object o = manager.doOpen(f.getInputStream(ze), graph);
	                            graph.addObject(manager.getObjectName(), o);
	                        }
	                    }
	                }
	                v_omanager = graph.getSpecificObjectManager();
	                if (v_omanager != null) {
	                    for (int i=0 ; i<v_omanager.size() ; i++) {
	                        GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager)v_omanager.get(i);
	                        ze = f.getEntry((usePrefix ? AbstractGraphFrontend.ZIP_PREFIX:"")+manager.getObjectName());
	                        if (ze != null) {
	                            Object o = manager.doOpen(f.getInputStream(ze), graph);
	                            graph.addObject(manager.getObjectName(), o);
	                        }
	                    }
	                }
                }
                graph.setSaveFileName(file.getAbsolutePath());
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
            graph.setSaveFileName(file.getAbsolutePath());
            return graph;
        } catch (FileNotFoundException e) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), null);
            return null;
        }
    }
    
	public FileFilter getFileFilter() {
		if (ffilter == null) {
			ffilter = new GsFileFilter();
			ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
		}
		return ffilter;
	}
}
