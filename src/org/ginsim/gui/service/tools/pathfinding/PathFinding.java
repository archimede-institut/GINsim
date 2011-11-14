package org.ginsim.gui.service.tools.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;

/**
 * A class to find a path in any graph
 */
public class PathFinding extends Thread {

	private Object start;
	private Object end;
	private Set visitedNodes;
	private Stack path;
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
		this.visitedNodes = new HashSet();
		this.path = new Stack();
		this.graph = graph;
	}

	public void run() {
		boolean found = depthFirstSearch(start);
		if (found) {
			resultHandler.setProgress(100);
			resultHandler.setProgressionText("Path found...");
			resultHandler.setPath(reverse(path));
		} else {
			resultHandler.setProgressionText("There is no path between "+start+" and "+end);
			resultHandler.setProgress(100);
		}
	}
	
	/**
	 * Reverse the order of path
	 * @param path
	 * @return
	 */
	private Vector reverse(Stack path) {
		Vector v = new Vector(path.size());
		for (int i = path.size()-1; i >= 0; i--) {
			v.add(path.get(i));
		}
		return v;
	}

	/**
	 * Simple depth first search algorithm (recursive)
	 * 
	 * 
	 * @param node the current node to visit
	 * @return true if a path is found
	 */
	private boolean depthFirstSearch(Object node) {
		visitedNodes.add(node);
		resultHandler.setProgress(visitedNodes.size());
		if (node.equals(end)) {
			path.push(node);
			return true;
		} else {
			for (Iterator it = getChildren(node).iterator(); it.hasNext();) {
				Object child = (Object) it.next();
				boolean found = depthFirstSearch(child);
				if (found) {
					path.push(node);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Construct the list of the non visited childrens of node
	 * @param node
	 * @return
	 */
	public Collection getChildren(Object node) {
		Collection outgoingEdges = graph.getOutgoingEdges(node);
		List children = new ArrayList(outgoingEdges.size());
		for (Iterator it = outgoingEdges.iterator(); it.hasNext();) {
			GsDirectedEdge e = (GsDirectedEdge) it.next();
			Object child = e.getTarget();
			if (!visitedNodes.contains(child))
				children.add(e.getTarget());
		}
		return children;
	}
}
