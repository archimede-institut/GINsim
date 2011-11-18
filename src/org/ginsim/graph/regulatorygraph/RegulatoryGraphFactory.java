package org.ginsim.graph.regulatorygraph;

import org.ginsim.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Debugger;


/**
 * descriptor for regulatoryGraph.
 */
@ProviderFor( GraphFactory.class)
public class RegulatoryGraphFactory implements GraphFactory<GsRegulatoryGraph> {

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
    	
        return "regulatory";
    }
    
	@Override
	public Class<GsRegulatoryGraph> getGraphClass(){
		
		return GsRegulatoryGraph.class;
	}
	
	@Override
    public GsRegulatoryGraph create() {
    	
    	GsRegulatoryGraph graph = new GsRegulatoryGraph();
        return graph;
    }

    
    
	@Override
    public Class getParser() {
    	
    	return GsRegulatoryParser.class;
    }


    
}
