package org.ginsim.core.graph.regulatorygraph;

import org.ginsim.core.graph.common.AbstractGraphFactory;
import org.ginsim.core.graph.common.GraphFactory;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.mangosdk.spi.ProviderFor;


/**
 * Factory for regulatory graphs.
 */
@ProviderFor( GraphFactory.class)
public class RegulatoryGraphFactory extends AbstractGraphFactory<RegulatoryGraph> {

	public static final String KEY = "regulatory";
    private static RegulatoryGraphFactory instance = null;
    
    public RegulatoryGraphFactory() {
    	super(RegulatoryGraph.class, KEY);
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

	@Override
	public EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> createDefaultEdgeStyle(RegulatoryGraph graph) {
		return new DefaultRegulatoryEdgeStyle();
	}

}
