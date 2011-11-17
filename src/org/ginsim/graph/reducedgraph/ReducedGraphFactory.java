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
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
    public Class getParser() {
    	
    	return GsReducedGraphParser.class;
    }
    
    
    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
    public GsReducedGraph create() {
    	
    	GsReducedGraph graph = new ReducedGraphImpl();
        return graph;
    }
    
    


    /**
     * Create a new graph of the type factory is managing from a boolean
     * 
     * @return an instance of the graph type the factory is managing
     */
    public GsReducedGraph create( boolean bool) {
    	
    	return new ReducedGraphImpl( bool);
    }
    
    
    /**
     * Create a new graph of the type factory is managing from a graph
     * 
     * @return an instance of the graph type the factory is managing
     */
    public GsReducedGraph create( Graph graph) {
    	
    	return new ReducedGraphImpl( graph);
    }






}
