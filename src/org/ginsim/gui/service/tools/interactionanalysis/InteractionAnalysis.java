package org.ginsim.gui.service.tools.interactionanalysis;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.graph.view.css.CascadingStyle;

import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.document.DocumentStyle;
import fr.univmrs.tagc.common.document.DocumentWriter;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * 
 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
 * 
 */
public class InteractionAnalysis {
	private boolean opt_annotate;
	private GsRegulatoryMutantDef mutant;

	private RegulatoryGraph g;
	private HashMap node_to_position;
	private Map functionalityMap = null;
	private CascadingStyle cs = null;
	private InteractionAnalysisSelector selector = null;
	
	private long before; //to know the time elapsed in the algorithm
	private int i_leafs;
	
	private Report report;
	private List currentPath, currentSource;
	private Set selectedNodes;
	
	static final byte FUNC_NON = 1;
	static final byte FUNC_POSITIVE = 2;
	static final byte FUNC_NEGATIVE = 3;
	static final byte FUNC_DUAL = 4;
	static final String STYLE_POSITIVE = "positive"; 
	static final String STYLE_NEGATIVE = "negative"; 
	static final String STYLE_NONFUNCTIONAL = "nonFunctional"; 
	static final String STYLE_DUAL = "dual"; 	
	
	/**
	 * 
	 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
	 * 
	 * @param g the graph where to search the non functional interactions.
	 * @param opt_annotate boolean indicating if the non functional edges should be annotated.
	 * @param mutant the mutant definition
	 * @param selectedNodes the set of selected nodes to run the analysis on.
	 * 
	 */
	public InteractionAnalysis(RegulatoryGraph  g, boolean opt_annotate, GsRegulatoryMutantDef mutant, HashSet selectedNodes) {
		this.opt_annotate 		= opt_annotate;
		this.mutant = mutant;
		this.g = g;
		this.report = new Report();
		this.selectedNodes = selectedNodes;
		run();
	}
	public InteractionAnalysis(RegulatoryGraph  g, boolean opt_annotate, GsRegulatoryMutantDef mutant) {
		this(g, opt_annotate, mutant, null);
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
		byte [] leafs;			//The values of all the leafs for a complete omdd from all the node to 0, to all the node to max.
		int [] subtree_size;	//The size of all the complete subtree (tree of a child) of the current node
		HashMap node_in_subtree;//The level of each node in the subtree regarding the nodeOrder

		int [] small_node_order_level; //The node order in the omdd.
		RegulatoryVertex [] small_node_order_vertex; //The node order in the omdd.

		OmddNode[] t_tree =  g.getAllTrees(true);
		if (mutant != null) {
			mutant.apply(t_tree, g);
		}
		
		node_to_position = new HashMap((int) (g.getVertexCount()*1.5));					//m.get(vertex) => its position in the nodeOrder as an Integer.
		int i = 0;
		for (Iterator it = g.getNodeOrder().iterator(); it.hasNext();) {							//Build the map m
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
			RegulatoryVertex target = (RegulatoryVertex) it.next();
			if (target.isInput() || (selectedNodes != null && !selectedNodes.contains(target))) { 	//skip the inputs or unselected nodes
			    continue;
			}
			Collection<RegulatoryMultiEdge> l = g.getIncomingEdges(target);											//  get the list l of incoming edges
			OmddNode omdd = t_tree[i];
			
			total_level = 1;																//  Compute the total number of level in the omdd tree
			for (RegulatoryMultiEdge edge: l) {
				RegulatoryVertex source = edge.getSource();
				total_level *= source.getMaxValue()+1;
			}
			leafs = new byte[total_level];
			i_leafs = 0;
						
			subtree_size = new int[l.size()+1];											//Compute the size of the subtrees
			subtree_size[0] = total_level;
			small_node_order_vertex = new RegulatoryVertex[l.size()];
			small_node_order_level = new int[l.size()];
			int m = 0;
			for (Object obj: g.getNodeOrder()) {
				RegulatoryVertex source = (RegulatoryVertex) obj;				
				if (g.getEdge(source, target) != null) {
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

			for (RegulatoryMultiEdge me: l) {									//	For each incoming edge
				RegulatoryVertex source = me.getSource();
//				for (int k = 0; k < me.getEdgeCount(); k++) {									// 		For each sub-edge of the multiedge
//					RegulatoryEdge e = me.getEdge(k);
					SourceItem sourceItem = report.reportFor(target, source);
					currentSource = sourceItem.reportItems;
					byte functionality = computeFunctionality(source.getMaxValue()+1, ((Integer)node_in_subtree.get(source)).intValue(), leafs, subtree_size, small_node_order_vertex); //Compute its functionality
					sourceItem.sign = functionality;
					String res;
					if (functionality == FUNC_POSITIVE) {
						res = "positive";
						functionalityMap.put(me, InteractionAnalysisSelector.CAT_POSITIVE);
					}
					else if (functionality == FUNC_NEGATIVE) {
						res = "negative";
						functionalityMap.put(me, InteractionAnalysisSelector.CAT_NEGATIVE);
					}
					else if (functionality == FUNC_DUAL) {
						res = "dual";
						functionalityMap.put(me, InteractionAnalysisSelector.CAT_DUAL);
					}
					else {
						res = "non functional";
						functionalityMap.put(me, InteractionAnalysisSelector.CAT_NONFUNCTIONNAL);

					}
					if (opt_annotate) {
						for (int j = 0; j < me.getEdgeCount(); j++) {
							me.getGsAnnotation(j).appendToComment("This edge is "+res+"\n");
						}
					}
	//			}

			}
		}
				
		report.timeSpent = new Date().getTime()-before;
	}
	/**
	 * 
	 * @return the Map of functionality or null if it has not been computed
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
		
		EdgeAttributesReader ereader = g.getEdgeAttributeReader();
		for (Iterator iterator = functionalityMap.keySet().iterator(); iterator.hasNext();) {
			RegulatoryMultiEdge me = (RegulatoryMultiEdge) iterator.next();
			ereader.setEdge(me);
			if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_POSITIVE && me.getSign() != RegulatoryMultiEdge.SIGN_POSITIVE) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_NEGATIVE && me.getSign() != RegulatoryMultiEdge.SIGN_NEGATIVE) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_NONFUNCTIONNAL && me.getSign() != RegulatoryMultiEdge.SIGN_UNKNOWN) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_DUAL) {
				cs.applyOnEdge(selector, me, ereader);
			}
		}
		
	}
	
	public void undoColorize() {
		cs.restoreAllEdges(functionalityMap.keySet(), g.getEdgeAttributeReader());
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
	private int scannOmdd(OmddNode omdd, int deep, byte [] leafs, int[] subtree_size, RegulatoryVertex[] small_node_order_vertex, int[] small_node_order_levels) {
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
			for (int i = 0; i < subtree_size[current_deep]/added-1; i++) { //The first pass has ben computed, copy the results for the others
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
	private byte computeFunctionality(int count_childs, int node_index, byte[] leafs, int[] subtree_size_t, RegulatoryVertex[] small_node_order) {
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
					ri.targetValue_low = (byte) low;
					ri.targetValue_high = (byte) high;
					currentPath = new LinkedList();
					log_path(index, node_index, ri, subtree_size_t, small_node_order);
					
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
	private void log_path(int index, int node_index, ReportItem ri, int[] subtree_size_t, RegulatoryVertex[] small_node_order) {
		for (int k = 0; k < small_node_order.length; k++) {
			RegulatoryVertex v = small_node_order[k];
			byte count = (byte) (index/subtree_size_t[k+1]%(v.getMaxValue()+1));
			if (k != node_index) {
				PathItem pi = new PathItem();
				pi.targetValue_low = count;
				pi.vertex = v;
				currentPath.add(pi);
			} else {
				ri.sourceValue_low = count;
			}
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
		DocumentStyle style = new DocumentStyle();
		style.addStyle(STYLE_POSITIVE);
		style.addProperty(DocumentStyle.COLOR, new Color(67, 200, 75));
		style.addStyle(STYLE_NEGATIVE);
		style.addProperty(DocumentStyle.COLOR, new Color(246, 57, 53));
		style.addStyle(STYLE_NONFUNCTIONAL);
		style.addProperty(DocumentStyle.COLOR, new Color(0, 0, 0));
		style.addStyle(STYLE_DUAL);
		style.addProperty(DocumentStyle.COLOR, new Color(16, 0, 255));
		dw.setStyles(style);
		
		StringBuffer css = dw.getDocumentExtra("css");
		if (css != null) {
			css.append("  h2,h3,h4 {display:none;}\n" +
					"  th, td, tr {border: 1px solid black;}\n" +
					"  table {width: auto; margin: 2px;}\n" +
					"  .summary>tbody>tr>th {background-color: blue; color: white}\n" +
					"  .summary td {background-color: lightblue}\n" +
					"  .summary td table, .summary td table td {background-color: lightgreen}\n" +
					"  .summary table th {background-color: green; color: white}\n" +
					"  th span {font-size: 60%;}"
				);
		}
		
		
		StringBuffer javascript = dw.getDocumentExtra("javascript");
		if (javascript != null) {
			javascript.append(Tools.readFromFile("src/fr/univmrs/tagc/GINsim/interactionAnalysis/interactionAnalysis.js"));
		}
		
		dw.startDocument();
		dw.openHeader(1, Translator.getString("STR_interactionAnalysis"), null);
		dw.openParagraph(null);
		dw.writeTextln("Analizing interactions of "+g.getGraphName()+" ("+g.getVertexCount()+" vertices)");
		dw.closeParagraph();		
		
		writeSummary(dw);
				
		dw.openHeader(2, "Report", null);
		for (Iterator it_target = report.iterator(); it_target.hasNext();) {
			RegulatoryVertex target = (RegulatoryVertex) it_target.next();
			dw.openHeader(3, target.getId(), null);

			for (Iterator it_sources = report.get(target).iterator(); it_sources.hasNext();) {
				SourceItem sourceItem = (SourceItem) it_sources.next();
				
				if (sourceItem.sign == InteractionAnalysis.FUNC_NON) {
					dw.openHeader(4,sourceItem.source.getId()+" -> "+target.getId()+" is non functional.", STYLE_NONFUNCTIONAL);
				} else if (sourceItem.sign == InteractionAnalysis.FUNC_POSITIVE) {
					dw.openHeader(4,sourceItem.source.getId()+" -> "+target.getId()+" is positive.", STYLE_POSITIVE);
				} else if (sourceItem.sign == InteractionAnalysis.FUNC_NEGATIVE) {
					dw.openHeader(4,sourceItem.source.getId()+" -> "+target.getId()+" is negative.", STYLE_NEGATIVE);
				} else {
					dw.openHeader(4,sourceItem.source.getId()+" -> "+target.getId()+" is dual.", STYLE_DUAL);
				}
				
				dw.openTable(null, null, null);
				dw.openTableRow();
				if (dw.doesDocumentSupportExtra("javascript")) {
					dw.openTableCell("Id", true);
				}
				dw.openTableCell("Result", true);
				dw.openTableCell("Source level", true);
				dw.openTableCell("Target level", true);
				ReportItem r0 = (ReportItem)sourceItem.reportItems.get(0);
				for (Iterator it_path = r0.path.iterator(); it_path.hasNext();) {
					PathItem pathItem = (PathItem) it_path.next();
					dw.openTableCell(pathItem.vertex.getId(), true);
				}
				dw.closeTableRow();			
				
				
				int i = 0;
				for (Iterator it_report = sourceItem.reportItems.iterator(); it_report.hasNext();) {
					ReportItem reportItem = (ReportItem) it_report.next();
					if (dw.doesDocumentSupportExtra("javascript")) {
						dw.openTableCell(""+i++);
					}
					if (reportItem.sign == InteractionAnalysis.FUNC_NON) {
						dw.openTableCell(1, 1, "=", STYLE_NONFUNCTIONAL, false);
					} else if (reportItem.sign == InteractionAnalysis.FUNC_POSITIVE) {
						dw.openTableCell(1, 1, "+", STYLE_POSITIVE, false);
					} else if (reportItem.sign == InteractionAnalysis.FUNC_NEGATIVE) {
						dw.openTableCell(1, 1, "-", STYLE_NEGATIVE, false);
					}
					dw.openTableCell(reportItem.sourceValue_low+"/"+(reportItem.sourceValue_low+1));
					dw.openTableCell(reportItem.targetValue_low+"/"+reportItem.targetValue_high);
					for (Iterator it_path = reportItem.path.iterator(); it_path.hasNext();) {
						PathItem pathItem = (PathItem) it_path.next();
						if (pathItem.targetValue_high == -1) {
							dw.openTableCell(""+pathItem.targetValue_low);
						} else {
							dw.openTableCell(pathItem.targetValue_low+"/"+pathItem.targetValue_high+" ");
						}
					}
					dw.closeTableRow();
				}
				dw.closeTable();
			}
			
		}
		dw.close();
	}
	private void writeSummary(DocumentWriter dw) throws IOException {
		dw.openHeader(2, "Summary", null);
		
		dw.openTable("Summary", null, null);
		dw.openTableRow();
		dw.openTableCell("Source", true);
		dw.openTableCell("Target", true);
		dw.openTableCell("User's sign", true);
		dw.openTableCell("Computed sign", true);
		if (dw.doesDocumentSupportExtra("javascript")) {
			dw.openTableCell("View", true);
		}
		dw.closeTableRow();
		
		
		for (Iterator it_target = report.iterator(); it_target.hasNext();) {
			RegulatoryVertex target = (RegulatoryVertex) it_target.next();
			for (Iterator it_sources = report.get(target).iterator(); it_sources.hasNext();) {
				SourceItem sourceItem = (SourceItem) it_sources.next();
								
				RegulatoryMultiEdge e = g.getEdge(sourceItem.source, target);
				dw.openTableRow();
				dw.openTableCell(sourceItem.source.getId());
				dw.openTableCell(target.getId());
				if (e.getSign() == RegulatoryMultiEdge.SIGN_UNKNOWN) {
					dw.openTableCell(1, 1, "unknown", STYLE_NONFUNCTIONAL, false);
				} else if (e.getSign() == RegulatoryMultiEdge.SIGN_POSITIVE) {
					dw.openTableCell(1, 1, "positive", STYLE_POSITIVE, false);
				} else if (e.getSign() == RegulatoryMultiEdge.SIGN_NEGATIVE) {
					dw.openTableCell(1, 1, "negative", STYLE_NEGATIVE, false);
				} else {
					dw.openTableCell(1, 1, "dual", STYLE_DUAL, false);
				}
				if (sourceItem.sign == InteractionAnalysis.FUNC_NON) {
					dw.openTableCell(1, 1, "non functional", STYLE_NONFUNCTIONAL, false);
				} else if (sourceItem.sign == InteractionAnalysis.FUNC_POSITIVE) {
					dw.openTableCell(1, 1, "positive", STYLE_POSITIVE, false);
				} else if (sourceItem.sign == InteractionAnalysis.FUNC_NEGATIVE) {
					dw.openTableCell(1, 1, "negative", STYLE_NEGATIVE, false);
				} else {
					dw.openTableCell(1, 1, "dual", STYLE_DUAL, false);
				}
				if (dw.doesDocumentSupportExtra("javascript")) {
					dw.openTableCell(null);
					dw.addLink("#"+sourceItem.source.getId()+"__"+target.getId(), "view");
				}
				dw.closeTableRow();
			}
		}
		
		dw.closeTable();		
	}

}

class SourceItem {
	List reportItems = new LinkedList();
	RegulatoryVertex source;
	byte sign;
}

class PathItem {
	byte targetValue_low, targetValue_high = -1;
	RegulatoryVertex vertex;
}

/**
 * targetValue_low : value of the target for a low source
 * targetValue_high : value of the target for a high source
 * sourceValue_low : value of the low source, the high value is the low+1
 * sign : represent the sign of the interaction (-1, 0, +1)
 */
class ReportItem {
	byte targetValue_low, targetValue_high, sourceValue_low, sign;
	List path;
}


/**
 * Map{
 *   target :=> List[
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....)
 *              ],
 *   target :=> List[
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....)
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

	public SourceItem reportFor(RegulatoryVertex target, RegulatoryVertex source) {
		List l = (List) report.get(target);
		if (l == null) {
			l = new LinkedList();
			report.put(target, l);
		}
		SourceItem si = new SourceItem();
		si.source = source;
		l.add(si);
		return si;
	}
	
	public List get(RegulatoryVertex target) {
		return (List) report.get(target);
	}
}
