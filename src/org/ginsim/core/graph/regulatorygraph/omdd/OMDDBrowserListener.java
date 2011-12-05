package org.ginsim.core.graph.regulatorygraph.omdd;

public interface OMDDBrowserListener {
	public void leafReached(int value, int depth, int[][] path);
}
