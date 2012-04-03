package org.ginsim.service.tool.graphcomparator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameterList;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.css.NodeStyle;


/**
 * Compare 2 RegulatoryGraph
 * @author Berenguier Duncan
 * @since January 2009
 *
 */
public class RegulatoryGraphComparator extends GraphComparator<RegulatoryGraph> {
	public static final Color COMMON_COLOR_DIFF_FUNCTIONS = new Color(0, 0, 255);
	public static final Color COMMON_COLOR_DIFF_MAXVALUES = new Color(115, 194, 220);
	/**
	 * indicates if the node order of both graph is the same.
	 */
	private boolean sameNodeOrder;
	private List<RegulatoryNode[]> logicalFunctionPending;
	private Map<RegulatoryMultiEdge, RegulatoryMultiEdge> meMap;

	public RegulatoryGraphComparator( RegulatoryGraph g1,  RegulatoryGraph g2, RegulatoryGraph g) {
		super();
        if (g  == null)  return;
        if (g1 == null)  return;
        if (g2 == null)  return;
        
        this.graph_new = g;
        this.graph_1 = g1;
        this.graph_2 = g2;

		logicalFunctionPending = new ArrayList<RegulatoryNode[]>();
		
		sameNodeOrder = compareNodeOrder();
		if (!sameNodeOrder) {
			String comment = "diff: The node order is the same for both graph";
			log(comment+"\n");
			g.getAnnotation().appendToComment(comment);
			log(((RegulatoryGraph) g1).getNodeOrder()+"\n");
			log(((RegulatoryGraph) g2).getNodeOrder()+"\n");
		}
	}
	
	public RegulatoryGraphComparator(RegulatoryGraph g1, RegulatoryGraph g2) {
		this(g1, g2, GraphManager.getInstance().getNewGraph());
	}
	
	public boolean isCommonNode(Object id) {
		NodeStyle style = (NodeStyle)((GraphComparatorStyleStore)stylesMap.get(id)).v;
		return style.background != SPECIFIC_G1_COLOR && style.background != SPECIFIC_G2_COLOR;
	}
	
	public GraphComparatorResult buildDiffGraph() {
		GraphComparatorResult result = super.buildDiffGraph();
		
		meMap = new HashMap<RegulatoryMultiEdge, RegulatoryMultiEdge>();
		EdgeAttributesReader new_ereader = graph_new.getEdgeAttributeReader();
		for (Iterator<RegulatoryMultiEdge> it = graph_new.getEdges().iterator(); it.hasNext();) {
			RegulatoryMultiEdge edge_new = it.next();
			String sid = edge_new.getSource().getId();
			String tid = edge_new.getTarget().getId();
			
			RegulatoryMultiEdge edge_1 = (RegulatoryMultiEdge) graph_1.getEdge(graph_1.getNodeByName(sid), graph_1.getNodeByName(tid));
			RegulatoryMultiEdge edge_2 = (RegulatoryMultiEdge) graph_2.getEdge(graph_2.getNodeByName(sid), graph_2.getNodeByName(tid));
			
			String comment = "The edge "+edge_new.toToolTip()+" ";
			new_ereader.setEdge(edge_new);
			Color col = new_ereader.getLineColor();
			if (col == SPECIFIC_G1_COLOR) comment+= "is specific to g1";
			else if (col == SPECIFIC_G2_COLOR) comment+= "is specific to g2";
			else comment+= "is common to both graphs";
			((RegulatoryMultiEdge) edge_new).getAnnotation().appendToComment(comment);
			log(comment+"\n");
			if (edge_1 != null && edge_2 != null) compareEdges((RegulatoryMultiEdge)edge_1, (RegulatoryMultiEdge)edge_2);
			if (edge_1 != null) meMap.put(edge_1,edge_new);
			else if (edge_2 != null) meMap.put(edge_2,edge_new);
		}
		setAllLogicalFunctions();
		meMap = null;
		logicalFunctionPending = null;
		
		return result;
	}
	
	protected void setNodesColor() {
		for (Iterator<String> it=verticesIdsSet.iterator() ; it.hasNext() ;) {	//For all the vertices
			RegulatoryNode v, v1, v2;
			String id = (String)it.next();
			v1 = (RegulatoryNode)graph_1.getNodeByName(id);
			v2 = (RegulatoryNode)graph_2.getNodeByName(id);
			String comment = null;
			
			//Check which graph own the node, set the appropriate color to it and if it is owned by both graph, compare its attributes.
			if (v1 == null) {
				comment = "The node "+id+" is specific to "+graph_2.getGraphName()+"\n";
				v = graph_new.addNewNode(id, v2.getName(), v2.getMaxValue());
				mergeNodeAttributes(v, v2, null, graph_new.getNodeAttributeReader(), graph_2.getNodeAttributeReader(), null, SPECIFIC_G2_COLOR);
				setLogicalFunction(v, v2, graph_2);
			} else if (v2 == null) {
				comment = "The node "+id+" is specific to "+graph_1.getGraphName()+"\n";
				v = graph_new.addNewNode(id, v1.getName(), v1.getMaxValue());
				mergeNodeAttributes(v, v1, null, graph_new.getNodeAttributeReader(), graph_1.getNodeAttributeReader(), null, SPECIFIC_G1_COLOR);
				setLogicalFunction(v, v1, graph_1);
			} else {
				comment = "The node "+id+" is common to both graphs\n";
				v = graph_new.addNewNode(id, v1.getName(), (byte) Math.max(v1.getMaxValue(), v2.getMaxValue()));
				Color[] color = {COMMON_COLOR};
				comment += compareNodes(v ,v1, v2, color);
				mergeNodeAttributes(v, v1, v2, graph_new.getNodeAttributeReader(), graph_1.getNodeAttributeReader(), graph_2.getNodeAttributeReader(), color[0]);
				setLogicalFunction(v, v1, graph_1);
			}
			Annotation gsa = v.getAnnotation();
			if (v1 == null) {
				gsa.copyFrom(v2.getAnnotation());
			} else {
				gsa.copyFrom(v1.getAnnotation());
				if (v2 != null) {
					addtoannotation(gsa, v2.getAnnotation());
				}
			}
			gsa.appendToComment(comment);
			log(comment);
		}		
	}

	protected void addtoannotation(Annotation a, Annotation a1) {
		if (!a.getComment().contains(a1.getComment())) {
			a.appendToComment(a1.getComment());
		}
		
		int nblinks = a1.getLinkList().size();
		for (int i=0 ; i<nblinks ; i++) {
			String link = a1.getLink(i);
			if (!a.containsLink(link)) {
				a.addLink(link, graph_new);
			}
		}
	}
	
	protected void addNodesFromGraph( RegulatoryGraph gm) {
		for (RegulatoryNode vertex: gm.getNodes()) {
			verticesIdsSet.add(vertex.getId());
		}
	}

	
	protected void addEdgesFromGraph( RegulatoryGraph gm_main, RegulatoryGraph gm_aux, String id, Color vcol, Color pcol, EdgeAttributesReader ereader) {
		RegulatoryNode v = gm_main.getNodeByName(id);
		if (v == null) {
			return;
		}
		RegulatoryEdge e = null;
		EdgeAttributesReader e1reader = gm_main.getEdgeAttributeReader();
		EdgeAttributesReader e2reader = gm_aux.getEdgeAttributeReader();

		//If v is a node from the studied graph, we look at its edges
		RegulatoryNode source = graph_new.getNodeByName(id);
		for (RegulatoryMultiEdge me1: gm_main.getOutgoingEdges(v)) {
			String tid = me1.getTarget().getId();
			RegulatoryNode target = graph_new.getNodeByName(tid);
			
			if (graph_new.getEdge(source, target) != null) {
				continue;
			}

			RegulatoryMultiEdge me2 = null;
			if (vcol != SPECIFIC_G1_COLOR && vcol != SPECIFIC_G2_COLOR && isCommonNode(target)) {
				me2 = gm_aux.getEdge(gm_aux.getNodeByName(id), gm_aux.getNodeByName(tid));
			}
			
			for (int i = 0; i < me1.getEdgeCount(); i++) {
				try{
					e = graph_new.addNewEdge(id, tid, me1.getMin(i) , me1.getSign(i));
				}
				catch( GsException gs_exception){
					LogManager.error( "Unable to create new edge between vertices '" + id + "' and '" + tid + "' : one of the vertex was not found in the graph");
					LogManager.error( gs_exception);
				}
			}

			// copy edge annotations to the merged graph
			Annotation new_gsa = e.me.getAnnotation();
			new_gsa.copyFrom(me1.getAnnotation());
			if (me2 != null) {
				addtoannotation(new_gsa, me2.getAnnotation());
			}
			
			if (me2 == null) { //The edge's vertices are specific to a graph therefore the edge is specific, and we add it with the right color.
				mergeEdgeAttributes(e.me, me1, null, pcol, ereader, e1reader, null);
			} else { //source and target are common to both graph.
				mergeEdgeAttributes(e.me, me1, me2, vcol, ereader, e1reader, e2reader);
			}
		}
	}


	public String compareNodes(RegulatoryNode v, RegulatoryNode v1, RegulatoryNode v2, Color[] color) {
		String comment = "";
		if (!v1.getName().equals(v2.getName())) {
			String n1 = v1.getName();
			String n2 = v2.getName();
			if (n1.equals("")) {
				v.setName(n2);
				n1 = "no name";
			}
			if (n2.equals("")) n2 = "no name";
			comment += "   names are differents : "+n1+" and "+n2+"\n";
		}
		if (v1.getMaxValue() != v2.getMaxValue()) {
			byte mv1 = v1.getMaxValue();
			byte mv2 = v2.getMaxValue();
			comment += "   max values are differents : "+mv1+" and "+mv2+"\n";
			color[0] = COMMON_COLOR_DIFF_MAXVALUES;
		} else if (sameNodeOrder) comment += compareLogicalFunction(v1, v2, color); //Compare logical function only if they have the same maxValue.
		return comment;
	}

	
	/**
	 * Compare the logical function of node 'v1' and 'v2'.
	 * @param v1 
	 * @param v2
	 */
	private String compareLogicalFunction(RegulatoryNode v1, RegulatoryNode v2, Color[] color) {
		String comment = "";
		OMDDNode omdd1 = v1.getTreeParameters(((RegulatoryGraph)graph_1));
		OMDDNode omdd2 = v2.getTreeParameters(((RegulatoryGraph)graph_2));
		if (!compareLogicalFunction(omdd1, omdd2)) {
			comment = "   logical functions are differents : \n      "+omdd1+"\n      "+omdd2;
			color[0] = COMMON_COLOR_DIFF_FUNCTIONS;
		}
		return comment;
	}
	private boolean compareLogicalFunction(OMDDNode omdd1, OMDDNode omdd2) {
		if (omdd1.level != omdd2.level) return false;
		//if (omdd1.min 	!= omdd2.min) 	return false; //TODO : usefull to compare ?
		//if (omdd1.max 	!= omdd2.max) 	return false;
		if (omdd1.value != omdd2.value) return false;
		if (omdd1.next != null && omdd2.next != null) {
			if (omdd1.next.length != omdd2.next.length) 	return false;
			int i = 0;
			while (i < omdd1.next.length) {
				if (compareLogicalFunction(omdd1.next[i], omdd2.next[i]) == false) return false;
				i++;
			}
		} 
		return true;
	}

	/**
	 * Set the logical function for the new node 'v' to the logical function contained in the node 'v_source' of the graph 'g_source'
	 * 
	 * @param v 
	 * @param v_source
	 * @param g_source
	 */
	private void setLogicalFunction(RegulatoryNode v, RegulatoryNode v_source, Graph g_source) { //TODO : do we really want to do that ?
		RegulatoryNode[] t = new RegulatoryNode[2];
		t[0] = v;
		t[1] = v_source;
		logicalFunctionPending.add(t);
	}
	
	private void setAllLogicalFunctions() {
		for (RegulatoryNode[] t: logicalFunctionPending) {
			LogicalParameterList lpl = t[1].getV_logicalParameters();
			lpl.applyNewGraph(t[0], meMap);			
		}
	}
	

	
	/**
	 * Compare the node order from both g1 and g2 
	 */
	private boolean compareNodeOrder() {
		String[] no1 = nodeOrderListToStringArray(graph_1.getNodeOrder());
		String[] no2 = nodeOrderListToStringArray(graph_2.getNodeOrder());

		int i1 = 0;//index for the current item in the list
		int i2 = 0;
		boolean shouldReturnFalseIfFailAgain = false;
		while (i1 < no1.length && i2 < no2.length) {
			
			if (no1[i1].equals(no2[i2])) { //The node are the same, go to next node.
				i1++;i2++;
			} else {
				int next = nodeOrderContainsIdAfter(no2, i2, no1[i1]);
				if (next != -1) { 			//if 1 is in 2, its bad if both in the other
					shouldReturnFalseIfFailAgain = true;
				} else { 									//if 1 is not in 2, its OK
					i1++;
				}
				next = nodeOrderContainsIdAfter(no1, i1, no2[i2]);
				if (next != -1) { 			//if 2 is in 1, its bad if both in the other
					if (shouldReturnFalseIfFailAgain == true) return false;
				} else { 									//if 2 is not in 1, its OK
					i2++;
				}
			}
		}
		return true;
	}
	
	/**
	 * does nodeOrder "no" contains the id "id" after the index "depart"
	 * @param no node order
	 * @param depart index to start search
	 * @param id search id
	 * @return the position of the id if found else -1
	 */
	private int nodeOrderContainsIdAfter(String[] no, int depart, String id) {
		for (int i = depart + 1; i < no.length; i++) {
			if (id.equals(no[i])) return i;
		}
		return -1; //Not found
	}
	
	private String[] nodeOrderListToStringArray(List<RegulatoryNode> nodeOrder) {
		String[] s = new String[nodeOrder.size()];
		int i = 0;
		Iterator<RegulatoryNode> it = nodeOrder.iterator();
		while (it.hasNext()) {
			RegulatoryNode v = (RegulatoryNode) it.next();
			s[i++] = v.getId();
		}
		return s;

	}

	public String compareEdges(RegulatoryMultiEdge e1, RegulatoryMultiEdge e2) {
		String comment = "";
		if (e1.getEdgeCount() != e2.getEdgeCount()) comment += "   multiarcs have different number of edges: "+e1.getEdgeCount()+" and "+e2.getEdgeCount()+"\n";
		return comment;
	}
}