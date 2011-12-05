package org.ginsim.core.graph.tree;

import org.ginsim.core.graph.common.GraphFactory;
import org.ginsim.core.utils.log.LogManager;
import org.mangosdk.spi.ProviderFor;



@ProviderFor( GraphFactory.class)
public class TreeFactory implements GraphFactory<Tree> {
	
    private static TreeFactory instance = null;
	
	/**
	 * Return an instance of this TreeFactory.
     * @return an instance of this TreeFactory.
     */
    public static TreeFactory getInstance() {
        if (TreeFactory.instance == null) {
        	TreeFactory.instance = new TreeFactory();
        }
        return TreeFactory.instance;
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
    /**
     * This method MUST NOT be used, use create(TreeBuilder parser) instead
     */
    public Tree create() {
    	LogManager.error( "Tree canot be instancied without a parser");
        return null;
    }

    public Tree create( TreeBuilder parser) {
    	
        return new TreeImpl(parser);
    }

    @Override
    public Class getParser() {
    	
    	return null; //There is no parser. Tree is only for viewing purposes
    }

}
