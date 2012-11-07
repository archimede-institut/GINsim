package org.ginsim.servicegui.tool.pathfinding;

import java.util.List;
import java.util.Vector;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;

/**
 * A class to find a path in any graph
 */
public class PathFinding extends Thread {

	private Object start;
	private Object end;
	private ResultHandler resultHandler;
	private Graph graph;

	/**
	 * Create a new thread that will search a path between start and end
	 * The graphManager is used to find the outgoingEdges from a node.
	 * The resultHandler is informed of the progression during the run() and of the results when the run() is finish.
	 * 
	 * @param resultHandler
	 * @param graphManager
	 * @param start a node
	 * @param end a node
	 */
	public PathFinding(ResultHandler resultHandler, Graph graph, Object start, Object end) {
		this.resultHandler = resultHandler;
		this.start = start;
		this.end = end;
		this.graph = graph;
	}

	public void run() {
		List<Edge> path = graph.getShortestPath(start, end);
		if (path != null) {
			Vector nodes = new Vector(path.size()+1);
			nodes.add(start);
			for (Edge e: path) {
				nodes.add(e.getTarget());
			}
			resultHandler.setProgress(100);
			resultHandler.setProgressionText("Path found...");
			resultHandler.setPath(nodes);
		} else {
			resultHandler.setProgressionText("There is no path between "+start+" and "+end);
			resultHandler.setProgress(100);
		}
	}
}
