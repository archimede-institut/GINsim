package org.ginsim.core.graph.reducedgraph;

import org.ginsim.core.graph.common.AbstractGraphFactory;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;


/**
 * descriptor for regulatoryGraph.
 */
@ProviderFor( GraphFactory.class)
public class ReducedGraphFactory<G extends Graph<V,E>, V, E extends Edge<V>> extends AbstractGraphFactory<ReducedGraph<G,V,E>> {

	public static final String KEY = "reduced";
    private static ReducedGraphFactory<?,?,?> instance = null;
    
    public ReducedGraphFactory() {
    	super(ReducedGraph.class, KEY);
    	if (instance == null) {
    		instance = this;
    	}
    }
    
    /**
     * @return an instance of this graphDescriptor.
     */
    public static ReducedGraphFactory<?,?,?> getInstance() {
        if (instance == null) {
            instance = new ReducedGraphFactory();
        }
        return instance;
    }
    
    @Override
	public Class<ReducedGraph<G,V,E>> getGraphClass(){
    	Class<? extends ReducedGraph> cl = ReducedGraph.class;
		return (Class<ReducedGraph<G, V, E>>)cl;
	}
    
    @Override
    public Class<ReducedGraphParser> getParser() {
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
