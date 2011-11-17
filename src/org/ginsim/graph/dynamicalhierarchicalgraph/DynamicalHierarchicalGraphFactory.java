package org.ginsim.graph.dynamicalhierarchicalgraph;

import java.util.List;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;
import org.mangosdk.spi.ProviderFor;

/**
 * descriptor for dynamic hierarchical graphs.
 */
@ProviderFor( GraphFactory.class)
public class DynamicalHierarchicalGraphFactory implements GraphFactory {
	
    private static DynamicalHierarchicalGraphFactory instance = null;
    
    public DynamicalHierarchicalGraphFactory(){
    	
    	if( instance == null){
    		instance = this;
    	}
    }
    
	/**
     * @return an instance of this graphDescriptor.
     */
    public static GraphFactory getInstance() {
    	
        if (instance == null) {
            instance = new DynamicalHierarchicalGraphFactory();
        }
        return instance;
    }

	
    /**
     * Return the class of graph this factory is managing
     * 
     * @return the name of the class of graph this factory is managing
     */
	public Class getGraphClass(){
		
		return GsDynamicalHierarchicalGraph.class;
	}
	
    /**
     * Return the type of graph this factory is managing
     * 
     * @return the name of the type of graph this factory is managing
     */
	public String getGraphType() {
		
		return "dynamicalHierarchicalGraph";
	}
	
	/**
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
	public Class getParser(){
		
		return GsDynamicalHierarchicalParser.class;
	}
	
    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
	public GsDynamicalHierarchicalGraph create(){
		
		return new DynamicalHierarchicalGraphImpl();
	}
	
	
    /**
     * Create a new graph of the type factory is managing from a boolean
     * 
     * @return an instance of the graph type the factory is managing
     */
	public GsDynamicalHierarchicalGraph create( boolean bool){
		
		return new DynamicalHierarchicalGraphImpl( bool);
	}
	
	
    /**
     * Create a new graph of the type factory is managing from a NodeOrder
     * 
     * @return an instance of the graph type the factory is managing
     */
	public  GsDynamicalHierarchicalGraph create(List<GsRegulatoryVertex> nodeOrder){
		
		return new DynamicalHierarchicalGraphImpl( nodeOrder);
	}
	

}
