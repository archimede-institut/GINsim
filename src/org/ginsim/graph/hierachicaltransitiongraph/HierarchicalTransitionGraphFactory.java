package org.ginsim.graph.hierachicaltransitiongraph;

import java.util.List;

import org.ginsim.graph.common.GraphFactory;
import org.ginsim.graph.dynamicgraph.DynamicGraphImpl;
import org.ginsim.graph.dynamicgraph.DynamicGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.utils.log.LogManager;
import org.mangosdk.spi.ProviderFor;


/**
 * descriptor for hierarchical transition graphs.
 */
@ProviderFor( GraphFactory.class)
public class HierarchicalTransitionGraphFactory implements GraphFactory<HierarchicalTransitionGraph> {
	
    private static HierarchicalTransitionGraphFactory instance = null;
    
    public HierarchicalTransitionGraphFactory(){
    	
    	if( instance == null){
    		instance = this;
    	}
    }
    
	/**
     * @return an instance of this graphDescriptor.
     */
    public static HierarchicalTransitionGraphFactory getInstance() {
    	
        if (instance == null) {
            instance = new HierarchicalTransitionGraphFactory();
        }
        return instance;
    }

    @Override
	public Class<HierarchicalTransitionGraph> getGraphClass(){
		
		return HierarchicalTransitionGraph.class;
	}
	
    @Override
	public String getGraphType() {
		
		return "hierarchicalTransitionGraph";
	}
	
    @Override
	public Class getParser(){
		
		return HierarchicalTransitionGraphParser.class;
	}
	
    @Override
	public HierarchicalTransitionGraph create(){
		
		return new HierarchicalTransitionGraphImpl();
	}
	
    
    
	public HierarchicalTransitionGraph create( boolean bool){
		
		return new HierarchicalTransitionGraphImpl( bool);
	}
	



}
