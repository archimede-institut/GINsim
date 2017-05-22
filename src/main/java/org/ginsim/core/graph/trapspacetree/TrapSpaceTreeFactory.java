package org.ginsim.core.graph.trapspacetree;

import org.colomoto.biolqm.tool.trapspaces.TrapSpace;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceList;
import org.ginsim.core.graph.AbstractGraphFactory;
import org.ginsim.core.graph.GraphFactory;
import org.mangosdk.spi.ProviderFor;

/**
 * 
 * @author Aurelien Naldi
 */
@ProviderFor( GraphFactory.class)
public class TrapSpaceTreeFactory extends AbstractGraphFactory<TrapSpaceTree> {

	public static final String KEY = "trapspacetree";
    private static TrapSpaceTreeFactory instance = null;

    public TrapSpaceTreeFactory() {
		super(TrapSpaceTree.class, KEY);
    	if (instance == null) {
    		instance = this;
    	}
	}

	@Override
	public TrapSpaceTree create() {
		return new TrapSpaceTreeImpl(this, false);
	}

	
	public TrapSpaceTree create(TrapSpaceList solutions) {

		int n = solutions.size();
		TrapSpaceTree tree = create();

		TrapSpaceNode[] nodes = new TrapSpaceNode[solutions.size()];
		int i = 0;
		for (TrapSpace t: solutions) {
			TrapSpaceNode node = new TrapSpaceNode(t);
			nodes[i++] = node;
			tree.addNode(node);
		}
		
		boolean[][] incl = solutions.inclusion();
		for (i=0 ; i<n ; i++) {
			boolean[] links = incl[i];
			for (int j=0 ; j<n ; j++) {
				if (links[j] && i != j) {
					TrapSpaceInclusion edge = new TrapSpaceInclusion(tree, nodes[i], nodes[j]);
					tree.addEdge(edge);
				}
			}
		}
		
		return tree;
	}
}