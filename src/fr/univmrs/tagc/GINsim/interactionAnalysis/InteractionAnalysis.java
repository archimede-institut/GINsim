package fr.univmrs.tagc.GINsim.interactionAnalysis;

import java.awt.Color;
import java.io.IOException;
import java.util.*;

import fr.univmrs.tagc.GINsim.css.CascadingStyle;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphDirectedEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.common.document.DocumentStyle;
import fr.univmrs.tagc.common.document.DocumentWriter;
import fr.univmrs.tagc.common.manageressources.Translator;

/**
 * 
 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
 * 
 */
public class InteractionAnalysis {
	private boolean opt_annotate;
	private GsRegulatoryMutantDef mutant;

	private GsRegulatoryGraph g;
	private GsGraphManager gm;
	private HashMap node_to_position;
	private Map functionalityMap = null;
	private CascadingStyle cs = null;
	private InteractionAnalysisSelector selector = null;
	
	private long before; //to know the time elapsed in the algorithm
	private int i_leafs;
	
	private Report report;
	private List currentPath, currentSource;
	
	static final byte FUNC_NON = 1;
	static final byte FUNC_POSITIVE = 2;
	static final byte FUNC_NEGATIVE = 3;
	static final byte FUNC_DUAL = 4;
	
	
	/**
	 * 
	 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
	 * 
	 * @param g the graph where to search the non functional interactions.
	 * @param opt_color boolean indicating if the non functional edges should be colored in 'opt_color_inactive'.
	 * @param opt_simplify boolean indicating if the non functional edges should be removed from the graph.
	 * @param opt_annotate boolean indicating if the non functional edges should be annotated.
	 * @param opt_verbose boolean indicating if we out more information like node order and logical functions.
	 * @param opt_color_inactive the Color for the non functional edges.
	 */
	public InteractionAnalysis(GsRegulatoryGraph  g, boolean opt_annotate, GsRegulatoryMutantDef mutant) {
		this.opt_annotate 		= opt_annotate;
		this.mutant = mutant;
		this.g = g;
		this.gm = g.getGraphManager();
		this.report = new Report();
		run();
	}

	/**
	 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
	 * 
	 * For each vertices, scan the reduced Omdd representation of the vertex logical function to know which vertices are effective (<=>present). If an edge source vertex incoming on this vertex is not in the Omdd, then its non functional.
	 * 
	 */
	private void run() {
		before = new Date().getTime();//measuring the time spend for this algorithm
		
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
	
		node_in_subtree = new HashMap();
		i = -1;
		for (Iterator it = g.getNodeOrder().iterator(); it.hasNext();) {					//  For each vertex v in the graph
		    i++;
			GsRegulatoryVertex target = (GsRegulatoryVertex) it.next();
			if (target.isInput()) {
			    continue;
			}
			List l = gm.getIncomingEdges(target);												//  get the list l of incoming edges
			OmddNode omdd = t_tree[i];
			
			total_level = 1;																//  Compute the total number of level in the omdd tree
			for (Iterator it2 = l.iterator(); it2.hasNext();) {
				GsRegulatoryVertex source = (GsRegulatoryVertex) ((GsJgraphDirectedEdge) it2.next()).getSourceVertex();
				total_level *= source.getMaxValue()+1;
			}
			leafs = new int[total_level];
			i_leafs = 0;
						
			subtree_size = new int[l.size()+1];											//Compute the size of the subtrees
			subtree_size[0] = total_level;
			small_node_order_vertex = new GsRegulatoryVertex[l.size()];
			small_node_order_level = new int[l.size()];
			int m = 0;
			for (Iterator it2 = nodeOrder.iterator(); it2.hasNext();) {
				GsRegulatoryVertex source = (GsRegulatoryVertex) it2.next();				
				if (gm.getEdge(source, target) != null) {
					node_in_subtree.put(source, new Integer(m));
					subtree_size[m+1] = 1;
					small_node_order_vertex[m] = source;
					small_node_order_level[m] = ((Integer)node_to_position.get(source)).intValue();
					for (int n = 0; n < m; n++) {
						subtree_size[n+1] *= source.getMaxValue()+1;
					}
					m++;
				}
			}
		    scannOmdd(omdd, 0, leafs, subtree_size, small_node_order_vertex, small_node_order_level);												//  scan the logical function of v

			for (Iterator it2 = l.iterator(); it2.hasNext();) {									//	For each incoming edge
				GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge) ((GsJgraphDirectedEdge)it2.next()).getUserObject();
				GsRegulatoryVertex source = (GsRegulatoryVertex) me.getSourceVertex();
				for (int k = 0; k < me.getEdgeCount(); k++) {									// 		For each sub-edge of the multiedge
					GsRegulatoryEdge e = me.getEdge(k);
					SourceItem sourceItem = report.reportFor(target, source, e.threshold);
					currentSource = sourceItem.reportItems;
					int functionality = computeFunctionality(source.getMaxValue()+1, ((Integer)node_in_subtree.get(source)).intValue(), leafs, subtree_size, small_node_order_vertex); //Compute its functionality
					sourceItem.sign = (byte) functionality;
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
					if (opt_annotate) {
						for (int j = 0; j < ((GsRegulatoryMultiEdge) e.me.getUserObject()).getEdgeCount(); j++) {
							((GsRegulatoryMultiEdge) e.me.getUserObject()).getGsAnnotation(j).appendToComment("This edge is "+res+"\n");
						}
					}
				}

			}
		}
				
		report.timeSpent = new Date().getTime()-before;
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
		if (functionalityMap == null) {
            return;
        }
		if (cs == null) {
            cs = new CascadingStyle(true);
        } else {
            cs.shouldStoreOldStyle = false;
        }
		
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
			} // else
			if (omdd.value == 0) {
				i_leafs += subtree_size[deep];		//if value is 0, skip them because the array is initialized to 0
			} else {													//else add the unreal leaf value to each of the real leafs 
				for (int i = 0; i < subtree_size[deep]; i++) {
					leafs[i_leafs++] = omdd.value;
				}
			}
			return subtree_size[deep];
		}
		
		boolean hasJumpedNode = false;
		int current_i = i_leafs, current_deep = deep;
		while (omdd.level != small_node_order_levels[deep]) {
			deep++;
			hasJumpedNode = true;
		}

		int res = -1, max = 0;
		for (int i = 0; i < omdd.next.length; i++) {		//Scan all the childs
			res = scannOmdd(omdd.next[i], deep+1, leafs, subtree_size, small_node_order_vertex, small_node_order_levels);
			if (res > max) {
                max = res;
            }
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
	private byte computeFunctionality(int count_childs, int node_index, int[] leafs, int[] subtree_size_t, GsRegulatoryVertex[] small_node_order) {
		int size_of_subtree = subtree_size_t[node_index+1];
		
		ReportItem ri = null;
		byte res = FUNC_NON;
		boolean containsPositive = false, containsNegative = false;
		
		int index = 0;
		while (index+size_of_subtree < leafs.length) {
			for (int i_childs = 0; i_childs < count_childs - 1; i_childs++) {
				for (int i_subtree = 0; i_subtree < size_of_subtree; i_subtree++) {
					int low = leafs[index];
					int high = leafs[index+size_of_subtree];
					
					ri = new ReportItem();
					ri.valL = (byte) low;
					ri.valR = (byte) high;
					currentPath = new LinkedList();
					log_path(index, node_index, subtree_size_t, small_node_order);
					
					if (low < high) {
						containsPositive = true;
						res = FUNC_POSITIVE;
					} else if (low > high) {
						containsNegative = true;
						res = FUNC_NEGATIVE;
					} else {
						res = FUNC_NON;
					}
					index++;
					ri.sign = res;
					ri.path = currentPath;
					currentSource.add(ri);
				}
			}
			index+=size_of_subtree;
		}
		if (containsNegative) {
	        if (containsPositive) {
	            return FUNC_DUAL;
	        }
	        return FUNC_NEGATIVE;
		    
		}
		if (containsPositive) {
		    return FUNC_POSITIVE;
		}
		return FUNC_NON;
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
			PathItem pi = new PathItem();
			pi.valL = (byte) count;
			pi.vertex = v;
			if (k == node_index) {
				pi.valR = (byte) (count+1);
			} 
			currentPath.add(pi);
			k--;
		}
	}

	
	protected void finalize() {
		if (functionalityMap != null) {
			if (selector != null) {
                selector.flush(); //remove nonFunctionalInteractions from the cache.
            }
		}
	}

	public void saveReport(DocumentWriter dw) throws IOException {	
		final String STYLE_POSITIVE = "positive"; 
		final String STYLE_NEGATIVE = "negative"; 
		final String STYLE_NONFUNCTIONAL = "nonFunctional"; 
		final String STYLE_DUAL = "dual"; 
		
		DocumentStyle style = new DocumentStyle();
		style.addStyle(STYLE_POSITIVE);
		style.addProperty(DocumentStyle.COLOR, new Color(67, 200, 75));
		style.addStyle(STYLE_NEGATIVE);
		style.addProperty(DocumentStyle.COLOR, new Color(246, 57, 53));
		style.addStyle(STYLE_NONFUNCTIONAL);
		style.addProperty(DocumentStyle.COLOR, new Color(230, 230, 0));
		style.addStyle(STYLE_DUAL);
		style.addProperty(DocumentStyle.COLOR, new Color(16, 0, 255));
		dw.setStyles(style);
		
		dw.startDocument();
		dw.openHeader(1, Translator.getString("STR_interactionAnalysis"), null);
		dw.openParagraph(null);
		dw.writeTextln("Analizing interactions of "+g.getGraphName()+" ("+gm.getVertexCount()+" vertices)");
		dw.closeParagraph();
		dw.openHeader(2, "Report", null);
		dw.openList(null);
		for (Iterator it_target = report.iterator(); it_target.hasNext();) {
			GsRegulatoryVertex target = (GsRegulatoryVertex) it_target.next();
			dw.openListItem(null);
			dw.openParagraph(null);
			dw.writeText(target.getName());
			dw.closeParagraph();
			dw.openList(null);
			for (Iterator it_sources = report.get(target).iterator(); it_sources.hasNext();) {
				SourceItem sourceItem = (SourceItem) it_sources.next();
				dw.openListItem(null);
				if (sourceItem.sign == InteractionAnalysis.FUNC_NON) {
					dw.openParagraph(STYLE_NONFUNCTIONAL);
					dw.writeText(sourceItem.source.getName()+"["+sourceItem.level+"] -> "+target.getName()+" is ");
					dw.writeText("non functional.");
				} else if (sourceItem.sign == InteractionAnalysis.FUNC_POSITIVE) {
					dw.openParagraph(STYLE_POSITIVE);
					dw.writeText(sourceItem.source.getName()+"["+sourceItem.level+"] -> "+target.getName()+" is ");
					dw.writeText("positive.");
				} else if (sourceItem.sign == InteractionAnalysis.FUNC_NEGATIVE) {
					dw.openParagraph(STYLE_NEGATIVE);
					dw.writeText(sourceItem.source.getName()+"["+sourceItem.level+"] -> "+target.getName()+" is ");
					dw.writeText("negative.");
				} else {
					dw.openParagraph(STYLE_DUAL);
					dw.writeText(sourceItem.source.getName()+"["+sourceItem.level+"] -> "+target.getName()+" is ");
					dw.writeText("dual.");
				}
				dw.closeParagraph();
				dw.openList(null);
				for (Iterator it_report = sourceItem.reportItems.iterator(); it_report.hasNext();) {
					ReportItem reportItem = (ReportItem) it_report.next();
					dw.openListItem(null);
					if (reportItem.sign == InteractionAnalysis.FUNC_NON) {
						dw.openParagraph(STYLE_NONFUNCTIONAL);
						dw.writeText("Non functional ");
					} else if (reportItem.sign == InteractionAnalysis.FUNC_POSITIVE) {
						dw.openParagraph(STYLE_POSITIVE);
						dw.writeText("Positive       ");
					} else if (reportItem.sign == InteractionAnalysis.FUNC_NEGATIVE) {
						dw.openParagraph(STYLE_NEGATIVE);
						dw.writeText("Negative       ");
					} else {
						dw.openParagraph(STYLE_DUAL);
						dw.writeText("Dual           ");
					}
					dw.writeText(" : the level of "+sourceItem.source.getName()+" goes from "+reportItem.valL+" to "+reportItem.valR+" for path ");
					for (Iterator it_path = reportItem.path.iterator(); it_path.hasNext();) {
						PathItem pathItem = (PathItem) it_path.next();
						if (pathItem.valR == -1) {
							dw.writeText(pathItem.vertex.getName()+"="+pathItem.valL+" ");
						} else {
							dw.writeText(pathItem.vertex.getName()+"="+pathItem.valL+"/"+pathItem.valR+" ");
						}
					}
					dw.closeParagraph();
				}
				dw.closeList();
			}
			dw.closeList();
		}
		dw.closeList();	
		dw.close();
	}

}

class SourceItem {
	List reportItems = new LinkedList();
	GsRegulatoryVertex source;
	byte level;
	byte sign;
}

class PathItem {
	byte valL, valR = -1;
	GsRegulatoryVertex vertex;
}

class ReportItem {
	byte valL, valR, sign;
	List path;
}


/**
 * {
 *   target :=> [
 *                SourceItem(source, level, reportItem([pathItem, pathItem, ....]), reportItem([pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem([pathItem, pathItem, ....]), reportItem([pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem([pathItem, pathItem, ....]), reportItem([pathItem, pathItem, ....]), ....)
 *              ],
 *   target :=> [
 *                SourceItem(source, level, reportItem([pathItem, pathItem, ....]), reportItem([pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem([pathItem, pathItem, ....]), reportItem([pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem([pathItem, pathItem, ....]), reportItem([pathItem, pathItem, ....]), ....)
 *              ]
 * }
 * 
 * 
 *
 */
class Report {
	public long timeSpent;
	private Map report;
	public Report() {
		report = new HashMap();
	}
	
	public Iterator iterator() {
		return report.keySet().iterator();
	}

	public SourceItem reportFor(GsRegulatoryVertex target, GsRegulatoryVertex source, byte level) {
		List l = (List) report.get(target);
		if (l == null) {
			l = new LinkedList();
			report.put(target, l);
		}
		SourceItem si = new SourceItem();
		si.source = source;
		si.level = level;
		l.add(si);
		return si;
	}
	
	public List get(GsRegulatoryVertex target) {
		return (List) report.get(target);
	}
}
