package org.ginsim.graph.tree;

import java.util.Vector;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;

/**
 * descriptor for dynamic hierarchical graphs.
 */
@ProviderFor( GraphFactory.class)
public class TreeFactory implements GraphFactory {
	
    private static GraphFactory instance = null;
	
	private static Vector v_OManager = null;
    
	/**
     * @return an instance of this graphDescriptor.
     */
    public static GraphFactory getInstance() {
    	
        if (instance == null) {
            instance = new TreeFactory();
        }
        return instance;
    }


    /**
     * Return the type of graph this factory is managing
     * 
     * @return the name of the type of graph this factory is managing
     */
    public String getGraphType() {
    	
        return "tree";
    }
    
    /**
     * Return the class of graph this factory is managing
     * 
     * @return the name of the class of graph this factory is managing
     */
	public Class getGraphClass(){
		
		return GsTree.class;
	}
	


    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
    public Graph create() {
    	
        return null;
    }
    
    
	/**
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
    public Class getParser() {
    	
    	return GsTreeParser.class;
    }
    

}
