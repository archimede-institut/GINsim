package org.ginsim.graph.hierachicaltransitiongraph;

import java.util.List;

import org.ginsim.graph.common.GraphFactory;
import org.ginsim.graph.dynamicgraph.DynamicGraphImpl;
import org.ginsim.graph.dynamicgraph.GsDynamicGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Debugger;

/**
 * descriptor for hierarchical transition graphs.
 */
@ProviderFor( GraphFactory.class)
public class HierarchicalTransitionGraphFactory implements GraphFactory<GsHierarchicalTransitionGraph> {
	
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
	public Class<GsHierarchicalTransitionGraph> getGraphClass(){
		
		return GsHierarchicalTransitionGraph.class;
	}
	
    @Override
	public String getGraphType() {
		
		return "hierarchicalTransitionGraph";
	}
	
    @Override
	public Class getParser(){
		
		return GsHierarchicalTransitionGraphParser.class;
	}
	
    @Override
	public GsHierarchicalTransitionGraph create(){
		
		return new HierarchicalTransitionGraphImpl();
	}
	
    
    
	public GsHierarchicalTransitionGraph create( boolean bool){
		
		return new HierarchicalTransitionGraphImpl( bool);
	}
	



}
