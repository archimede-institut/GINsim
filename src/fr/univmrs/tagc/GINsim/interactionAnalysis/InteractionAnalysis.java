package fr.univmrs.tagc.GINsim.interactionAnalysis;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.univmrs.tagc.GINsim.css.CascadingStyle;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphDirectedEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;

/**
 * 
 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
 * 
 */
public class InteractionAnalysis {
	private boolean opt_annotate;
	private boolean opt_verbose;
	private StringBuffer log; //to output the results
	private GsRegulatoryMutantDef mutant;

	private GsRegulatoryGraph g;
	private GsGraphManager gm;
	private HashMap node_to_position;
	private Map functionalityMap = null;
	private CascadingStyle cs = null;
	private InteractionAnalysisSelector selector = null;
	
	private long before; //to know the time elapsed in the algorithm
	private int i_leafs;
	
	static final int FUNC_NON = 1;
	static final int FUNC_POSITIVE = 2;
	static final int FUNC_NEGATIVE = 3;
	static final int FUNC_DUAL = 4;
	
	
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
	public InteractionAnalysis(GsRegulatoryGraph  g, boolean opt_annotate, boolean opt_debug, GsRegulatoryMutantDef mutant) {
		this.opt_annotate 		= opt_annotate;
		this.opt_verbose 		= opt_debug;
		this.mutant = mutant;
		this.g = g;
		this.gm = g.getGraphManager();
		
		log = new StringBuffer(1024);
		log("Find inactive interactions on ");
		log(g.getGraphName());
		log(" (");
		log(gm.getVertexCount());
		log(" vertices)\n\n");
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
		
		int total_level;		//The total number of node in a complete omdd tree. (products of levels of each interactor)
		int [] leafs;			//The values of all the leafs for a complete omdd from all the node to 0, to all the node to max.
		int [] subtree_size;	//The size of all the complete subtree (tree of a child) of the current node
		HashMap node_in_subtree;//The level of each node in the subtree regarding the nodeOrder

		int [] small_node_order_level; //The node order in the omdd.
		GsRegulatoryVertex [] small_node_order_vertex; //The node order in the omdd.

		OmddNode[] t_tree =  g.getAllTrees(true);
		if (mutant != null) {
			mutant.apply(t_tree, g);
		}
		
		List nodeOrder = g.getNodeOrder();
		node_to_position = new HashMap((int) (gm.getVertexCount()*1.5));					//m.get(vertex) => its position in the nodeOrder as an Integer.
		int i = 0;
		for (Iterator it = nodeOrder.iterator(); it.hasNext();) {							//Build the map m
			node_to_position.put(it.next(), Integer.valueOf(i++));
		}
				
		functionalityMap = new HashMap();
		
		//Prepare colorisation
		selector = new InteractionAnalysisSelector();
		selector.setCache(functionalityMap);
	
		if (opt_verbose) {
			i = 0;
			log("Node order : ");
			for (Iterator it = g.getNodeOrder().iterator(); it.hasNext();) {				//  For each vertex v in the graph
				GsRegulatoryVertex v = (GsRegulatoryVertex) it.next();
				log(v.getId()+"["+(i++)+"]:"+v.getMaxValue()+"  ");
			}
			log("\n\n");			
		}
		
		node_in_subtree = new HashMap();
		i = 0;
		for (Iterator it = g.getNodeOrder().iterator(); it.hasNext();) {					//  For each vertex v in the graph
			GsRegulatoryVertex v = (GsRegulatoryVertex) it.next();
			List l = gm.getIncomingEdges(v);												//  get the list l of incoming edges
			OmddNode omdd = t_tree[i++];
			
			total_level = 1;																//  Compute the total number of level in the omdd tree
			for (Iterator it2 = l.iterator(); it2.hasNext();) {
				GsRegulatoryVertex vs = (GsRegulatoryVertex) ((GsJgraphDirectedEdge) it2.next()).getSourceVertex();
				total_level *= vs.getMaxValue()+1;
			}
			leafs = new int[total_level];
			i_leafs = 0;
						
			subtree_size = new int[l.size()+1];											//Compute the size of the subtrees
			subtree_size[0] = total_level;
			small_node_order_vertex = new GsRegulatoryVertex[l.size()];
			small_node_order_level = new int[l.size()];
			int m = 0;
			for (Iterator it2 = nodeOrder.iterator(); it2.hasNext();) {
				GsRegulatoryVertex vs = (GsRegulatoryVertex) it2.next();				
				if (gm.getEdge(vs, v) != null) {
					node_in_subtree.put(vs, new Integer(m));
					subtree_size[m+1] = 1;
					small_node_order_vertex[m] = vs;
					small_node_order_level[m] = ((Integer)node_to_position.get(vs)).intValue();
					for (int n = 0; n < m; n++) {
						subtree_size[n+1] *= (vs.getMaxValue()+1);
					}
					m++;
				}
			}

			scannOmdd(omdd, 0, leafs, subtree_size, small_node_order_vertex, small_node_order_level);												//  scan the logical function of v

			for (Iterator it2 = l.iterator(); it2.hasNext();) {									//	For each incoming edge
				GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge) ((GsJgraphDirectedEdge)it2.next()).getUserObject();
				GsRegulatoryVertex vs = (GsRegulatoryVertex) me.getSourceVertex();
				for (int k = 0; k < me.getEdgeCount(); k++) {									// 		For each sub-edge of the multiedge
					GsRegulatoryEdge e = me.getEdge(k);
					if (opt_verbose) {
						log(e.getShortInfo()+"->"+v.getId()+" : \n");
						log("  Decision diagram :  "+omdd+"\n");
					}
					int functionality = computeFunctionality(vs.getMaxValue()+1, ((Integer)node_in_subtree.get(vs)).intValue(), leafs, subtree_size, small_node_order_vertex); //Compute its functionality
					String res;
					if (functionality == FUNC_POSITIVE) {
						res = "positive";
						functionalityMap.put(e.me, InteractionAnalysisSelector.CAT_POSITIVE);
					}
					else if (functionality == FUNC_NEGATIVE) {
						res = "negative";
						functionalityMap.put(e.me, InteractionAnalysisSelector.CAT_NEGATIVE);
					}
					else if (functionality == FUNC_DUAL) {
						res = "dual";
						functionalityMap.put(e.me, InteractionAnalysisSelector.CAT_DUAL);
					}
					else {
						res = "non functional";
						functionalityMap.put(e.me, InteractionAnalysisSelector.CAT_NONFUNCTIONNAL);

					}
					if (opt_verbose) {
						log("is "+res+"\n\n");
					} else {
						log(e.getShortInfo()+"->"+v.getId()+" is "+res+"\n");
					}
					if (opt_annotate) {
						for (int j = 0; j < ((GsRegulatoryMultiEdge) e.me.getUserObject()).getEdgeCount(); j++) {
							((GsRegulatoryMultiEdge) e.me.getUserObject()).getGsAnnotation(j).appendToComment("This edge is "+res+"\n");
						}
					}
				}

			}
		}
				
		if (opt_verbose) {
			log("\nTime elapsed : ");
			log((new Date()).getTime()-before);
			log(" milliseconds.");
		}
	}
	/**
	 * 
	 * @return the Set of nonFunctionalInteractions or null if it has not been computed
	 */
	public Map getFunctionality() {
		return functionalityMap;
	}
	
	/**
	 * Colorize the edges in the Set nonFunctionalInteractions.
	 */
	public void doColorize() {
		if (functionalityMap == null) return;
		if (cs == null) cs = new CascadingStyle(true);
		else cs.shouldStoreOldStyle = false;
		
		GsEdgeAttributesReader ereader = gm.getEdgeAttributesReader();
		for (Iterator iterator = functionalityMap.keySet().iterator(); iterator.hasNext();) {
			GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge) iterator.next();
			ereader.setEdge(me);
			if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_POSITIVE && me.getSign() != GsRegulatoryMultiEdge.SIGN_POSITIVE) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_NEGATIVE && me.getSign() != GsRegulatoryMultiEdge.SIGN_NEGATIVE) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_NONFUNCTIONNAL && me.getSign() != GsRegulatoryMultiEdge.SIGN_UNKNOWN) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_DUAL) {
				cs.applyOnEdge(selector, me, ereader);
			}
		}
		
//		cs.applySelectorOnEdges(selector, nonFunctionalInteractions.keySet(), gm.getEdgeAttributesReader());
	}
	
	public void undoColorize() {
		cs.restoreAllEdges(functionalityMap.keySet(), gm.getEdgeAttributesReader());
	}

	/**
	 * Recursive function scanning an OmddNode 'omdd' and call itself on the 'omdd' children.
	 * When the function end, the array 'leaf' contains the value of all the real leafs.
	 * 	 * 
	 * @param omdd the current OmddNode to scan. Should be the root at the first call.
	 * @param deep the deep call.
	 * @param leafs the resulting array.
	 * @param small_node_order 
	 * @param subtree_size 
	 */
	private int scannOmdd(OmddNode omdd, int deep, int [] leafs, int[] subtree_size, GsRegulatoryVertex[] small_node_order_vertex, int[] small_node_order_levels) {
		if (omdd.next == null) { 								//If the current node is leaf
			if (subtree_size[deep] == 1) { 							//a real leaf, ie. all the inputs are present in the branch
				leafs[i_leafs++] = omdd.value;							//Save the current value.
				return i_leafs;
			} else {												//not real, ie. some inputs are not present in the branch
				if (omdd.value == 0) {
					i_leafs += subtree_size[deep];		//if value is 0, skip them because the array is initialized to 0
				} else {													//else add the unreal leaf value to each of the real leafs 
					for (int i = 0; i < subtree_size[deep]; i++) {
						leafs[i_leafs++] = omdd.value;
					}
				}
				//log(" small jump of "+small_node_order_vertex[deep].getId()+"="+omdd.value+" "+(i_leafs-subtree_size[deep])+"->"+i_leafs+" ("+subtree_size[deep]+")\n");
				return subtree_size[deep];
			}
		}
		
		boolean hasJumpedNode = false;
		int current_i = i_leafs, current_deep = deep;
		while (omdd.level != small_node_order_levels[deep]) {
			//log(" jump for "+small_node_order_vertex[deep].getId()+" @ "+i_leafs+"\n");
			deep++;
			hasJumpedNode = true;
		}

		int res = -1, max = 0;
		for (int i = 0; i < omdd.next.length; i++) {		//Scan all the childs
			res = scannOmdd(omdd.next[i], deep+1, leafs, subtree_size, small_node_order_vertex, small_node_order_levels);
			if (res > max) max = res;
		}
		if (hasJumpedNode) {
			int added = i_leafs-current_i;
			//log(" jump of "+current_i+"->"+i_leafs+" "+added+"  "+current_deep+" "+subtree_size[current_deep]+"\n");
			for (int i = 0; i < subtree_size[current_deep]/added-1; i++) {
				for (int j = i_leafs; j < i_leafs+added; j++) {
					leafs[j] = leafs[j-added];
				}
				i_leafs += added;
			} 
			return i_leafs;
		}
		return res;
	}
	
	/**
	 * Compute the functionality of the 'node_index'-nth node in the omdd represented by 'leafs'.
	 * 
	 * @param count_childs the count of child above 'node_index'
	 * @param node_index the node to consider
	 * @param leafs a table of all the leafs of the complete omdd tree.
	 * @param subtree_size_t the size of the subtree
	 * @param small_node_order the node order in the subtree
	 * @return
	 */
	private int computeFunctionality(int count_childs, int node_index, int[] leafs, int[] subtree_size_t, GsRegulatoryVertex[] small_node_order) {
		int size_of_subtree = subtree_size_t[node_index+1];
		
		if (opt_verbose) {
			log("  Leafs ");
			print_t(leafs);
		}		
		int res = FUNC_NON;
		int index = 0;
		while (index+size_of_subtree < leafs.length) {
			for (int i_childs = 0; i_childs < count_childs - 1; i_childs++) {
				for (int i_subtree = 0; i_subtree < size_of_subtree; i_subtree++) {
					int low = leafs[index];
					int high = leafs[index+size_of_subtree];
					if (opt_verbose) {
						log("  "+(low==high?"=":(low>high?"-":"+"))+" ("+low+"/"+high+", "+index+"/"+(index+size_of_subtree)+") for state ");
						log_path(index, node_index, subtree_size_t, small_node_order);
						log("\n");
					}
					if (low < high) {
						if (res == FUNC_NEGATIVE) return FUNC_DUAL;
						else res = FUNC_POSITIVE;
					} else if (low > high) {
						if (res == FUNC_POSITIVE) return FUNC_DUAL;
						else res = FUNC_NEGATIVE;
					}
					index++;
				}
			}
			index+=size_of_subtree;
		}

		return res;
	}

	/**
	 * Log the path corresponding to the 'index'-nth leaf.
	 *
	 * @param index the leaf to consider.
	 * @param node_index the index of the source node of the current interaction.
	 * @param subtree_size_t a table of all the subtree size.
	 * @param small_node_order the node order in the subtree.
	 */
	private void log_path(int index, int node_index, int[] subtree_size_t, GsRegulatoryVertex[] small_node_order) {
		int k = small_node_order.length - 1; //The last node
		while (k >= 0) {
			GsRegulatoryVertex v = small_node_order[k];
			int count = index/subtree_size_t[k+1]%(v.getMaxValue()+1);
			if (k == node_index) log(v.getId()+":"+count+"/"+(count+1)+" ");
			else log(v.getId()+":"+count+" ");
			k--;
		}
	}

	/**
	 * append the string 's' to the log
	 * @param s
	 */
	private void log(String s) {
		log.append(s);
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
	 * get the content of the log
	 */
	public StringBuffer getLog() {
		return log;
	}
	
	protected void finalize() {
		if (functionalityMap != null) {
			if (selector != null) selector.flush(); //remove nonFunctionalInteractions from the cache.
		}
	}
	
	private void print_t(int[] t) {
		log("[");
		for (int i = 0; i < t.length - 1; i++) {
			log(t[i]+", ");
		}
		log(t[t.length - 1]+"]\n");
	}

}
