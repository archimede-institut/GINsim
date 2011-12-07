package org.ginsim.core.graph.hierachicaltransitiongraph;

import java.util.List;

import org.ginsim.core.graph.common.GraphFactory;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
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
	
	public HierarchicalTransitionGraph create( List<RegulatoryNode> nodeOrder, int transientCompactionMode){
		
		return new HierarchicalTransitionGraphImpl( nodeOrder, transientCompactionMode);
	}
    
	public HierarchicalTransitionGraph create( boolean bool){
		
		return new HierarchicalTransitionGraphImpl( bool);
	}
	



}
