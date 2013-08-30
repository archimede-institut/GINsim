package org.ginsim.core.graph.regulatorygraph;

import org.ginsim.core.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;


/**
 * Factory for regulatory graphs.
 */
@ProviderFor( GraphFactory.class)
public class RegulatoryGraphFactory implements GraphFactory<RegulatoryGraph> {

	public static final String KEY = "regulatory";
    private static RegulatoryGraphFactory instance = null;
    
    public RegulatoryGraphFactory(){
    	if( instance == null){
    		instance = this;
    	}
    }
    
    /**
     * @return an instance of this graphDescriptor.
     */
    public static RegulatoryGraphFactory getInstance() {
        if (instance == null) {
            instance = new RegulatoryGraphFactory();
        }
        return instance;
    }
    
	@Override
    public String getGraphType() {
        return KEY;
    }
    
	@Override
	public Class<RegulatoryGraph> getGraphClass(){
		return RegulatoryGraph.class;
	}
	
	@Override
    public RegulatoryGraph create() {
    	RegulatoryGraph graph = new RegulatoryGraphImpl();
        return graph;
    }
	
    public RegulatoryGraph create( boolean bool) {
    	RegulatoryGraph graph = new RegulatoryGraphImpl( bool);
        return graph;
    }

	@Override
    public Class getParser() {
    	return RegulatoryParser.class;
    }
}
