package org.ginsim.core.graph.dynamicgraph;

import java.util.List;

import org.ginsim.core.graph.common.GraphFactory;
import org.ginsim.core.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraph;
import org.ginsim.core.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraphImpl;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.log.LogManager;
import org.mangosdk.spi.ProviderFor;


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
	

	public DynamicGraph create( List<RegulatoryNode> node_order){
		
    	return new DynamicGraphImpl( node_order);
	}

}
