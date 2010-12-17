package fr.univmrs.tagc.GINsim.regulatoryGraph;

public interface OMDDBrowserListener {
	public void leafReached(int value, int depth, int[][] path);
}
