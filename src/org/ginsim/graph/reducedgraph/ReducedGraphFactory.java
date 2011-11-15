package org.ginsim.graph.reducedgraph;

import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;

/**
 * descriptor for regulatoryGraph.
 */
@ProviderFor( GraphFactory.class)
public class ReducedGraphFactory implements GraphFactory {

    private static GraphFactory instance = null;
    
    private static List<GsGraphAssociatedObjectManager> v_OManager = null;
    
    
    /**
     * @return an instance of this graphDescriptor.
     */
    public static GraphFactory getInstance() {
        if (instance == null) {
            instance = new ReducedGraphFactory();
        }
        return instance;
    }
    
    /**
     * Return the type of graph this factory is managing
     * 
     * @return the name of the type of graph this factory is managing
     */
    public String getGraphType() {
    	
        return "reduced";
    }
    
    /**
     * Return the class of graph this factory is managing
     * 
     * @return the name of the class of graph this factory is managing
     */
	public Class getGraphClass(){
		
		return GsReducedGraph.class;
	}
	


    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
    public Graph create() {
    	
    	GsReducedGraph graph = new GsReducedGraph();
        return graph;
    }
    
    
	/**
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
    public Class getParser() {
    	
    	return GsReducedGraphParser.class;
    }
    
    

	public FileFilter getFileFilter() {
		return null;
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

	public ImageIcon getGraphIcon(int mode) {
		return null;
	}



}
