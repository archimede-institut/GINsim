package org.ginsim.graph.reducedgraph;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Debugger;


/**
 * descriptor for regulatoryGraph.
 */
@ProviderFor( GraphFactory.class)
public class ReducedGraphFactory implements GraphFactory<GsReducedGraph> {

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
	public Class<GsReducedGraph> getGraphClass(){
		return GsReducedGraph.class;
	}
    
    
    @Override
    public Class getParser() {
    	return GsReducedGraphParser.class;
    }
    
    @Override
    public GsReducedGraph create() {
    	GsReducedGraph graph = new ReducedGraphImpl();
        return graph;
    }
    
    @Override
    public GsReducedGraph create( Object param) {
    	
    	if (param instanceof Boolean) {    	
    		return new ReducedGraphImpl( (Boolean)param);
    	}
    	
    	if (param instanceof Graph) {    	
    		return new ReducedGraphImpl( (Graph)param);
    	}
    	
    	Debugger.log("Unsupported parameter type when creating a Reduced graph: "+param);
    	return create();
    }

}
