package org.ginsim.graph.regulatorygraph.omdd;

public interface OMDDBrowserListener {
	public void leafReached(int value, int depth, int[][] path);
}
