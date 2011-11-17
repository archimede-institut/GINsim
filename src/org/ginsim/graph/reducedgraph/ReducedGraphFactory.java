package org.ginsim.graph.reducedgraph;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;


/**
 * descriptor for regulatoryGraph.
 */
@ProviderFor( GraphFactory.class)
public class ReducedGraphFactory implements GraphFactory {

    private static GraphFactory instance = null;
    
    public ReducedGraphFactory(){
    	
    	if( instance == null){
    		instance = this;
    	}
    }
    
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
    	
    	GsReducedGraph graph = new ReducedGraphImpl();
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






}
