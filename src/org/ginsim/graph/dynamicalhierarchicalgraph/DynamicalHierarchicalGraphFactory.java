package org.ginsim.graph.dynamicalhierarchicalgraph;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;

import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * descriptor for dynamic hierarchical graphs.
 */
public class DynamicalHierarchicalGraphFactory implements GraphFactory {
	
    private static DynamicalHierarchicalGraphFactory instance = null;
    
    private static List<GsGraphAssociatedObjectManager> v_OManager = null;
    private static GsFileFilter ffilter = null;

    
	/**
     * @return an instance of this graphDescriptor.
     */
    public static GraphFactory getInstance() {
    	
        if (instance == null) {
            instance = new DynamicalHierarchicalGraphFactory();
        }
        return instance;
    }

	public FileFilter getFileFilter() {
	    if (ffilter == null) {
	        ffilter = new GsFileFilter();
	        ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
	    }
		return ffilter;
	}

	
    /**
     * Return the class of graph this factory is managing
     * 
     * @return the name of the class of graph this factory is managing
     */
	public Class getGraphClass(){
		
		return GsDynamicalHierarchicalGraph.class;
	}
	
    /**
     * Return the type of graph this factory is managing
     * 
     * @return the name of the type of graph this factory is managing
     */
	public String getGraphType() {
		
		return "dynamicalHierarchicalGraph";
	}
	
	/**
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
	public Class getParser(){
		
		return GsDynamicalHierarchicalParser.class;
	}
	
    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
	public Graph create(){
		
		return new GsDynamicalHierarchicalGraph();
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
     * @return associates object managers
     */
    public static List getObjectManager() {
    	
        return v_OManager;
    }


}
