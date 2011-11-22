package org.ginsim.graph.tree;

import org.ginsim.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Debugger;

/**
 * descriptor for dynamic hierarchical graphs.
 */
@ProviderFor( GraphFactory.class)
public class TreeFactory implements GraphFactory<Tree> {
	
    private static TreeFactory instance = null;
	
	/**
     * @return an instance of this graphDescriptor.
     */
    public static TreeFactory getInstance() {
    	
        if (instance == null) {
            instance = new TreeFactory();
        }
        return instance;
    }


    @Override
    public String getGraphType() {
        return "tree";
    }
    
    @Override
	public Class<Tree> getGraphClass(){
		
		return Tree.class;
	}
	
    @Override
    public Tree create() {
    	Debugger.log("Tree factory not finished");
        return null;
    }
    

    @Override
    public Class getParser() {
    	
    	return TreeParser.class;
    }

}
