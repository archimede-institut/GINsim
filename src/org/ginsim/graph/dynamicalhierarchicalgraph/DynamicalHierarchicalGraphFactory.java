package org.ginsim.graph.dynamicalhierarchicalgraph;

import java.util.List;

import org.ginsim.graph.common.GraphFactory;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Debugger;

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
