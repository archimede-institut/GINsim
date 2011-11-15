package org.ginsim.graph.regulatorygraph;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;


/**
 * descriptor for regulatoryGraph.
 */
@ProviderFor( GraphFactory.class)
public class RegulatoryGraphFactory implements GraphFactory {

    private static RegulatoryGraphFactory instance = null;
    
    public RegulatoryGraphFactory(){
    	
    	if( instance == null){
    		instance = this;
    	}
    }
    
    /**
     * @return an instance of this graphDescriptor.
     */
    public static GraphFactory getInstance() {
    	
        if (instance == null) {
            instance = new RegulatoryGraphFactory();
        }
        return instance;
    }
    
    /**
     * Return the type of graph this factory is managing
     * 
     * @return the name of the type of graph this factory is managing
     */
    public String getGraphType() {
    	
        return "regulatory";
    }
    
    /**
     * Return the class of graph this factory is managing
     * 
     * @return the name of the class of graph this factory is managing
     */
	public Class getGraphClass(){
		
		return GsRegulatoryGraph.class;
	}
	


    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
    public Graph create() {
    	
    	GsRegulatoryGraph graph = new GsRegulatoryGraph();
        return graph;
    }
    
    
	/**
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
    public Class getParser() {
    	
    	return GsRegulatoryParser.class;
    }


    
}
