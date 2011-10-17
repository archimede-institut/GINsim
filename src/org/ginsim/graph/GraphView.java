package org.ginsim.graph;

/**
 * View was done through "attribute readers" for nodes and edges.
 * When no GUI was here a fallback datastructure was used, otherwise it delegates to JGraph.
 * Copies between the two was used when displaying the graph for example.
 * 
 * These things should be moved here, with a cleaner separation, but how?
 * It should be uniform and stay in sync with the GUI without depending on it, can we do better than what we already had?
 * 
 * @author Aurelien Naldi
 */
public interface GraphView {

}
