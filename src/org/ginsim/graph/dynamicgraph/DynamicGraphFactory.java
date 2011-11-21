package org.ginsim.graph.dynamicgraph;

import java.util.List;

import org.ginsim.graph.common.GraphFactory;
import org.ginsim.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraphImpl;
import org.ginsim.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Debugger;

/**
 * descriptor for dynamic (state transition) graphs.
 */
@ProviderFor( GraphFactory.class)
public class DynamicGraphFactory implements GraphFactory<DynamicGraph> {

    private static DynamicGraphFactory instance = null;
	
    public DynamicGraphFactory(){
    	
    	if( instance == null){
    		instance = this;
    	}
    }
    
    /**
     * @return an instance of this graphDescriptor.
     */
    public static DynamicGraphFactory getInstance() {
    	
        if (instance == null) {
            instance = new DynamicGraphFactory();
        }
        return instance;
    }

    @Override
	public Class<DynamicGraph> getGraphClass(){
		
		return DynamicGraph.class;
	}
	
    @Override
	public String getGraphType() {
		
		return "dynamic";
	}
	
    @Override
	public Class getParser(){
		
		return DynamicParser.class;
	}
	
    @Override
	public DynamicGraph create(){
		
		return new DynamicGraphImpl();
	}
	
    
	public DynamicGraph create( boolean bool){
		
		return new DynamicGraphImpl( bool);
	}
	

	public DynamicGraph create( List<RegulatoryVertex> node_order){
		
    	return new DynamicGraphImpl( node_order);
	}

}
