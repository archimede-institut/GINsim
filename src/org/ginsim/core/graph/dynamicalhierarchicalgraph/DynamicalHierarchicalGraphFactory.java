package org.ginsim.core.graph.dynamicalhierarchicalgraph;

import java.util.List;

import org.ginsim.core.graph.common.GraphFactory;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.log.LogManager;
import org.mangosdk.spi.ProviderFor;


/**
 * descriptor for dynamic hierarchical graphs.
 */
@ProviderFor( GraphFactory.class)
public class DynamicalHierarchicalGraphFactory implements GraphFactory<DynamicalHierarchicalGraph> {
	
    private static DynamicalHierarchicalGraphFactory instance = null;
    
    public DynamicalHierarchicalGraphFactory(){
    	
    	if( instance == null){
    		instance = this;
    	}
    }
    
	/**
     * @return an instance of this graphDescriptor.
     */
    public static DynamicalHierarchicalGraphFactory getInstance() {
    	
        if (instance == null) {
            instance = new DynamicalHierarchicalGraphFactory();
        }
        return instance;
    }


    @Override
	public Class<DynamicalHierarchicalGraph> getGraphClass(){
		
		return DynamicalHierarchicalGraph.class;
	}
	
    @Override
	public String getGraphType() {
		
		return "dynamicalHierarchicalGraph";
	}
	
    @Override
	public Class getParser(){
		
		return DynamicalHierarchicalParser.class;
	}
	
    @Override
	public DynamicalHierarchicalGraph create(){
		
		return new DynamicalHierarchicalGraphImpl();
	}
    

	public DynamicalHierarchicalGraph create( boolean bool){
		
		return new DynamicalHierarchicalGraphImpl( bool);
	}
	

	public DynamicalHierarchicalGraph create( List<RegulatoryNode> node_order){
		
    	return new DynamicalHierarchicalGraphImpl( node_order);
	}
}
