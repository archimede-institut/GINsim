package org.ginsim.graph.dynamicgraph;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;

/**
 * descriptor for dynamic (state transition) graphs.
 */
@ProviderFor( GraphFactory.class)
public class GsDynamicGraphFactory implements GraphFactory {

    private static GsDynamicGraphFactory instance = null;
	
    public GsDynamicGraphFactory(){
    	
    	if( instance == null){
    		instance = this;
    	}
    }
    
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


}
