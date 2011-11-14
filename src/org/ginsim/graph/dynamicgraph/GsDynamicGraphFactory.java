package org.ginsim.graph.dynamicgraph;

import java.util.List;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.ginsim.graph.dynamicalhierarchicalgraph.GsDynamicalHierarchicalGraph;

import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;

/**
 * descriptor for dynamic (state transition) graphs.
 */
public class GsDynamicGraphFactory implements GraphFactory {

    private static GsDynamicGraphFactory instance = null;
	
    private static GsFileFilter ffilter = null;
    private static List v_OManager;

    
    /**
     * @return an instance of this graphDescriptor.
     */
    public static GraphFactory getInstance() {
    	
        if (instance == null) {
            instance = new GsDynamicGraphFactory();
        }
        return instance;
    }
    
    /**
     * Return the class of graph this factory is managing
     * 
     * @return the name of the class of graph this factory is managing
     */
	public Class getGraphClass(){
		
		return GsDynamicGraph.class;
	}
	
    /**
     * Return the type of graph this factory is managing
     * 
     * @return the name of the type of graph this factory is managing
     */
	public String getGraphType() {
		
		return "dynamic";
	}
	
	/**
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
	public Class getParser(){
		
		return GsDynamicParser.class;
	}
	
    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
	public Graph create(){
		
		return new GsDynamicGraph();
	}



	public FileFilter getFileFilter() {
	    if (ffilter == null) {
	        ffilter = new GsFileFilter();
	        ffilter.setExtensionList(new String[] {"ginml"}, "ginml files");
	    }
		return ffilter;
	}
    /**
     * @param manager
     */
    public static void registerObjectManager(GsGraphAssociatedObjectManager manager) {
        if (v_OManager == null) {
            v_OManager = new Vector();
        }
        v_OManager.add(manager);
    }
    /**
     * @return associated object manager
     */
    public static List getObjectManager() {
    	
        return v_OManager;
    }

}
