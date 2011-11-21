package org.ginsim.graph.regulatorygraph.omdd;

import java.io.IOException;

/**
 * Browse a OMDD and call a method when reaching a leaf.
 * 
 * @author Aurelien Naldi
 */
public class OMDDNodeBrowser  {

	OMDDBrowserListener listener;
	int nbnodes;
	int[][] parcours;
	
	public OMDDNodeBrowser(OMDDBrowserListener listener, int nbnodes) {
		this.listener = listener;
		this.nbnodes = nbnodes;
		parcours = new int[nbnodes][4];
	}
	
	public void browse(OMDDNode node) {
		exploreNode(0, node);
	}
	
    /**
     * browse a OMDD and write the result for the leaves of interest.
     * This code was stolen and adapted from the Snake export.
     * It could (should?) be transformed into an iterator but this
     * was easier and will do the job for the time being.
     * 
     * @param parcours
     * @param depth
     * @param node
     * @throws IOException
     */
	protected void exploreNode(int depth, OMDDNode node) {

		if (node.next == null) {
			listener.leafReached(node.value, depth, parcours);
			return ;
		}

		OMDDNode currentChild;
		for (int i = 0 ; i < node.next.length ; i++) {
			currentChild = node.next[i];
			int begin = i;
			int end;
			for (end=i+1 ; end < node.next.length && currentChild == node.next[end]; end++, i++) {
				// nothing to do
			}
			parcours[depth][0] = begin;
			parcours[depth][1] = end;
			parcours[depth][2] = node.level;
			parcours[depth][3] = node.next.length;
			exploreNode(depth+1, node.next[begin]);
		}
	}
}
