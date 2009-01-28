package fr.univmrs.tagc.GINsim.interactionAnalysis;

import java.awt.Color;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphDirectedEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

/**
 * 
 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
 * 
 */
public class SearchNonFunctionalInteractions {
	private boolean opt_simplify, opt_color, opt_annotate, opt_verbose;
	private Color opt_color_inactive = Color.red;
	private StringBuffer log; //to output the results

	private GsRegulatoryGraph g;
	private GsGraphManager gm;
	private HashMap m;
	
	private long before; //to know the time elapsed in the algorithm
	
	/**
	 * 
	 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
	 * 
	 * @param g the graph where to search the non functional interactions.
	 * @param opt_color boolean indicating if the non functional edges should be colored in 'opt_color_inactive'.
	 * @param opt_simplify boolean indicating if the non functional edges should be removed from the graph.
	 * @param opt_annotate boolean indicating if the non functional edges should be annotated.
	 * @param opt_verbose boolean indicating if we output more information like node order and logical functions.
	 * @param opt_color_inactive the Color for the non functional edges.
	 */
	public SearchNonFunctionalInteractions(GsRegulatoryGraph  g, boolean opt_color, boolean opt_simplify, boolean opt_annotate, boolean opt_verbose , Color opt_color_inactive) {
		this.opt_simplify 		= opt_simplify;
		this.opt_annotate 		= opt_annotate;
		this.opt_verbose 		= opt_verbose;
		this.opt_color    		= opt_color;
		this.opt_color_inactive = opt_color_inactive;
		this.g = g;
		this.gm = g.getGraphManager();
		//if (opt_simplify) this.g_sim = (GsRegulatoryGraph ) g.clone();
		
		log = new StringBuffer(1024);
		log("Find inactive interactions on ");
		log(g.getGraphName());
		log(" (");
		log(gm.getVertexCount());
		log(" vertices)");
		log("\n    simplify inactive edges : ");
		log(opt_simplify);
		log("\n    annotate inactive edges : ");
		log(opt_annotate);
		log("\n    color inactive edges : ");
		log(opt_color);
		log("\n    display more information : ");
		log(opt_verbose);
		log("\n\n");
		run();
	}

	/**
	 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
	 * 
	 * For each vertices, scan the reduced Omdd representation of the vertex logical function to know which vertices are effective (<=>present). If an edge source vertex incoming on this vertex is not in the Omdd, then its non functional.
	 * 
	 */
	private void run() {
		before = (new Date()).getTime();//measuring the time spend for this algorithm

		List nodeOrder = g.getNodeOrder();
		if (opt_verbose) {
			log("Node order : ");
			int i = 0;
			for (Iterator iterator = nodeOrder.iterator(); iterator.hasNext();) {
				log((i++)+":");
				log(iterator.next());
				if (iterator.hasNext()) log(", ");
			}
			log("\n\n");
		}
		m = new HashMap((int) (gm.getVertexCount()*1.5));										//m.get(vertex) => its position in the nodeOrder as an Integer.
		int i = 0;
		for (Iterator it = nodeOrder.iterator(); it.hasNext();) {								//Build the map m
			m.put(it.next(), Integer.valueOf(i++));
		}
		
		int[] visited = new int[gm.getVertexCount()];											//@see scannOmdd
		
		
		GsEdgeAttributesReader ereader = gm.getEdgeAttributesReader();
		i = 1;
		for (Iterator it = gm.getVertexIterator(); it.hasNext();) {								//  For each vertex v in the graph
			GsRegulatoryVertex v = (GsRegulatoryVertex) it.next();
			List l = gm.getIncomingEdges(v);													//  get the list l of incoming edges
			OmddNode omdd = v.getTreeParameters(g).reduce();
			int res = scannOmdd(omdd, 0, l.size(), visited, i++);								//  scan the logical function of v (update the visit map) and return the number of node found
			if (res != l.size()) {																//  if we haven't found all the incoming nodes, there must be some non functional edges.
				for (Iterator it2 = l.iterator(); it2.hasNext();) {								//    For each edge e in l
					GsJgraphDirectedEdge me = (GsJgraphDirectedEdge) it2.next();
					GsRegulatoryVertex vs = (GsRegulatoryVertex) me.getSourceVertex();			//      get the source vertex vs 
					if (visited[vertex_level(vs)] != i-1) {										//      if the vertex vs not been visited by the scan, the edge vs->v is inactive. (i-1 because we do i++ before)
						log("  The edge ");
						log(me.toString());
						log(" is non functional");
						if (opt_verbose) {
							log(". Function : ");
							log(omdd);
						}
						log("\n");
						if (opt_color) {
							ereader.setEdge(me);
							ereader.setLineColor(opt_color_inactive);
							ereader.refresh();
						}
						if (opt_annotate) {
							String comment = "This edge is non functional\n";
							for (int j = 0; j < ((GsRegulatoryMultiEdge) me.getUserObject()).getEdgeCount(); j++) {
								((GsRegulatoryMultiEdge) me.getUserObject()).getGsAnnotation(j).appendToComment(comment);
							}
						}
						if (opt_simplify) {
		//					gm.removeEdge(vs, v); TODO : supress one edge and get java.util.ConcurrentModificationException ?
						}
					}
				}
			}
		}
		log("\nTime elapsed : ");
		log((new Date()).getTime()-before);
		log(" milliseconds.");
	}

	private int vertex_level(GsRegulatoryVertex v) {
		return ((Integer)m.get(v)).intValue();
	}

	/**
	 * Recursive function scanning an OmddNode 'omdd' and call itself on the 'omdd' children.
	 * When the function end, in the array 'visited' each element with value 'current_node' is present in the logical formula 'omdd'.
	 *     visited[i] = current_node => the node 'i' (in nodeOrder sense) has been scanned by this function.
	 * 
	 * If the node is not a leaf, mark the 'visited' array corresponding to 'omdd.level' to 'current_node'
	 * The function stop when it has scanned all the tree or all the nodes (that is 'nodeFound' = 'nodeCount')
	 * 
	 * @param omdd the current OmddNode to scan. Should be the root at the first call.
	 * @param nodeFound indicates how many node have been found at this call
	 * @param nodeCount indicates the maximum number of node that can be found (to return prematurely)
	 * @param visited an array of integer to indicate if a node has been visited.
	 * @param current_node a unique identifier (the current node index in the nodeOrder normally) to mark the visited array.
	 * @return 'nodeFound' to stop the algorithm when all the node have been found, -1 to continue
	 */
	private int scannOmdd(OmddNode omdd, int nodeFound, int nodeCount, int visited[], int current_node) {
		if (omdd.next == null) return -1;//leaf => end
		if (visited[omdd.level] != current_node) {											//we visit the current node and test if we have already visited it.
			visited[omdd.level] = current_node;
			nodeFound++;
			if (nodeFound == nodeCount) return nodeFound; 										//we found all the nodes
		}

		for (int i = 0; i < omdd.next.length; i++) {										//scan all the childs
			int res = scannOmdd(omdd.next[i], nodeFound, nodeCount, visited, current_node);
			if (res > 0) return res;
		}
		return -1;
	}
	
	/**
	 * append the string 's' to the log
	 * @param s
	 */
	private void log(String s) {
		log.append(s);
	}
	/**
	 * append the boolean b to the log
	 * @param b
	 */
	private void log(boolean b) {
		log.append(b);
	}

	/**
	 * append the long l to the log
	 * @param l
	 */
	private void log(long l) {
		log.append(l);
	}

	/**
	 * append the int i to the log
	 * @param i
	 */
	private void log(int i) {
		log.append(i);
	}

	/**
	 * append the object 'o' to the log
	 * @param o
	 */
	private void log(Object o) {
		log.append(o);
	}
	/**
	 * get the content of the log
	 */
	public StringBuffer getLog() {
		return log;
	}
	
//	private void print_t(int[] t) {
//		log("[");
//		for (int i = 0; i < t.length; i++) {
//			log(t[i]+", ");
//		}
//		log("]\n");
//	}

}