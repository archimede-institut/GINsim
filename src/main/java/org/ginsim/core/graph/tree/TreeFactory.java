package org.ginsim.core.graph.tree;

import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.AbstractGraphFactory;
import org.ginsim.core.graph.GraphFactory;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( GraphFactory.class)
public class TreeFactory extends AbstractGraphFactory<Tree> {
	
	private static final String KEY = "tree";
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

    public TreeFactory() {
		super(Tree.class, KEY);
		if (instance == null) {
			instance = this;
		}
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

    @Override
	public NodeStyle<TreeNode> createDefaultNodeStyle(Tree graph) {
		return new DefaultTreeNodeStyle();
	}

	@Override
	public EdgeStyle<TreeNode, TreeEdge> createDefaultEdgeStyle(Tree graph) {
		return new DefaultTreeEdgeStyle();
	}
}
