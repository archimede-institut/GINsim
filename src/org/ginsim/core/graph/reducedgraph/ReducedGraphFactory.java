package org.ginsim.core.graph.reducedgraph;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphFactory;
import org.ginsim.core.utils.log.LogManager;
import org.mangosdk.spi.ProviderFor;



/**
 * descriptor for regulatoryGraph.
 */
@ProviderFor( GraphFactory.class)
public class ReducedGraphFactory implements GraphFactory<ReducedGraph> {

    private static ReducedGraphFactory instance = null;
    
    public ReducedGraphFactory() {
    	
    	if( instance == null){
    		instance = this;
    	}
    }
    
    /**
     * @return an instance of this graphDescriptor.
     */
    public static ReducedGraphFactory getInstance() {
        if (instance == null) {
            instance = new ReducedGraphFactory();
        }
        return instance;
    }
    
    @Override
    public String getGraphType() {
        return "reduced";
    }
    
    @Override
	public Class<ReducedGraph> getGraphClass(){
		return ReducedGraph.class;
	}
    
    
    @Override
    public Class getParser() {
    	return ReducedGraphParser.class;
    }
    
    @Override
    public ReducedGraph create() {
    	ReducedGraph graph = new ReducedGraphImpl();
        return graph;
    }
    

    public ReducedGraph create( boolean bool){
    	
    	return new ReducedGraphImpl( bool);
    }
    
    
    public ReducedGraph create( Graph graph){
    	
    	return new ReducedGraphImpl( graph);
    }
    


}
