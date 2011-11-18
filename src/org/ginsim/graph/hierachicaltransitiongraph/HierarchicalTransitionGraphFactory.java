package org.ginsim.graph.hierachicaltransitiongraph;

import org.ginsim.graph.common.GraphFactory;
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
	
    @Override
	public GsHierarchicalTransitionGraph create( Object param){
		
    	if (param instanceof Boolean) {
    		return new HierarchicalTransitionGraphImpl( (Boolean)param);
    	}

    	// FIXME: finish HTG Factory
//    	if (param instanceof Array) {
//    		return new HierarchicalTransitionGraphImpl( nodeOrder, transientCompactionMode);
//    	}
    	
    	Debugger.log("HTG factory is not finished, ignoring parameters");
    	
    	return create();
	}

}
