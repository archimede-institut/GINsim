package org.ginsim.gui.service.tool.graphcomparator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.annotation.Annotation;
import org.ginsim.exception.GsException;
import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.regulatorygraph.logicalfunction.LogicalParameterList;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.graph.view.css.NodeStyle;

import fr.univmrs.tagc.common.Debugger;

/**
 * Compare 2 RegulatoryGraph
 * @author Berenguier Duncan
 * @since January 2009
 *
 */
public class RegulatoryGraphComparator extends GraphComparator {
	public static final Color COMMON_COLOR_DIFF_FUNCTIONS = new Color(0, 0, 255);
	public static final Color COMMON_COLOR_DIFF_MAXVALUES = new Color(115, 194, 220);
	private RegulatoryGraph g, g1, g2; //g is the graph merging g1 and g2, the graphs to compare.
	/**
	 * indicates if the node order of both graph is the same.
	 */
	private boolean sameNodeOrder;
	private List logicalFunctionPending;
	private Map meMap;

	public RegulatoryGraphComparator( Graph g1,  Graph g2, Graph g) {
		
        if (g  == null || !(g  instanceof RegulatoryGraph))  return;
        if (g1 == null || !(g1 instanceof RegulatoryGraph))  return;
        if (g2 == null || !(g2 instanceof RegulatoryGraph))  return;
       	this.g = (RegulatoryGraph)g; 
       	this.g1 = (RegulatoryGraph)g1; 
       	this.g2 = (RegulatoryGraph)g2;
       	
		g1m = g1; g2m = g2; gm = g;
		stylesMap = new HashMap();
		logicalFunctionPending = new ArrayList();
		
		sameNodeOrder = compareNodeOrder();
		if (!sameNodeOrder) {
			String comment = "diff: The node order is the same for both graph";
			log(comment+"\n");
			g.getAnnotation().appendToComment(comment);
			log(((RegulatoryGraph) g1).getNodeOrder()+"\n");
			log(((RegulatoryGraph) g2).getNodeOrder()+"\n");
		}
		buildDiffGraph();
	}
	
	public RegulatoryGraphComparator(RegulatoryGraph g1, RegulatoryGraph g2) {
		this(g1, g2, GraphManager.getInstance().getNewGraph());
	}
	
	public boolean isCommonNode(Object id) {
		NodeStyle style = (NodeStyle)((ItemStore)stylesMap.get(id)).v;
		return style.background != SPECIFIC_G1_COLOR && style.background != SPECIFIC_G2_COLOR;
	}
	
	public void buildDiffGraph() {
		super.buildDiffGraph();
		
		meMap = new HashMap();
		EdgeAttributesReader ereader = gm.getEdgeAttributeReader();
		for (Iterator<RegulatoryMultiEdge> it = gm.getEdges().iterator(); it.hasNext();) {
			RegulatoryMultiEdge me = it.next();
			String sid = me.getSource().getId();
			String tid = me.getTarget().getId();
			
			Edge e1 = g1m.getEdge(g1m.getNodeByName(sid), g1m.getNodeByName(tid));
			Edge e2 = g2m.getEdge(g2m.getNodeByName(sid), g2m.getNodeByName(tid));
			
			String comment = "The edge "+me.toToolTip()+" ";
			ereader.setEdge(me);
			ereader.setRouting(EdgeAttributesReader.ROUTING_AUTO);
			Color col = ereader.getLineColor();
			if (col == SPECIFIC_G1_COLOR) comment+= "is specific to g1";
			else if (col == SPECIFIC_G2_COLOR) comment+= "is specific to g2";
			else comment+= "is common to both graphs";
			int edgeCount = ((RegulatoryMultiEdge) me).getEdgeCount();
			for (int j = 0; j < edgeCount; j++) {
				((RegulatoryMultiEdge) me).getGsAnnotation(j).appendToComment(comment);
			}
			log(comment+"\n");
			if (e1 != null && e2 != null) compareEdges((RegulatoryMultiEdge)e1, (RegulatoryMultiEdge)e2);
			if (e1 != null) meMap.put(e1,me);
			else if (e2 != null) meMap.put(e2,me);
		}
		setAllLogicalFunctions();
		meMap = null;
		logicalFunctionPending = null;
	}
	
	protected void setNodesColor() {
		for (Iterator it=verticesIdsSet.iterator() ; it.hasNext() ;) {	//For all the vertices
			RegulatoryNode v, v1, v2;
			String id = (String)it.next();
			v1 = (RegulatoryNode)g1m.getNodeByName(id);
			v2 = (RegulatoryNode)g2m.getNodeByName(id);
			String comment = null;
			
			//Check which graph own the node, set the appropriate color to it and if it is owned by both graph, compare its attributes.
			if (v1 == null) {
				comment = "The node "+id+" is specific to "+g2.getGraphName()+"\n";
				v = g.addNewNode(id, v2.getName(), v2.getMaxValue());
				mergeNodeAttributes(v, v2, null, gm.getNodeAttributeReader(), g2m.getNodeAttributeReader(), null, SPECIFIC_G2_COLOR);
				setLogicalFunction(v, v2, g2);
			} else if (v2 == null) {
				comment = "The node "+id+" is specific to "+g1.getGraphName()+"\n";
				v = g.addNewNode(id, v1.getName(), v1.getMaxValue());
				mergeNodeAttributes(v, v1, null, gm.getNodeAttributeReader(), g1m.getNodeAttributeReader(), null, SPECIFIC_G1_COLOR);
				setLogicalFunction(v, v1, g1);
			} else {
				comment = "The node "+id+" is common to both graphs\n";
				v = g.addNewNode(id, v1.getName(), (byte) Math.max(v1.getMaxValue(), v2.getMaxValue()));
				Color[] color = {COMMON_COLOR};
				comment += compareNodes(v ,v1, v2, color);
				mergeNodeAttributes(v, v1, v2, gm.getNodeAttributeReader(), g1m.getNodeAttributeReader(), g2m.getNodeAttributeReader(), color[0]);
				setLogicalFunction(v, v1, g1);
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
				a.addLink(link, g);
			}
		}
	}
	
	protected void addNodesFromGraph( Graph gm) {
		for (Iterator it=gm.getNodes().iterator() ; it.hasNext() ;) {
			RegulatoryNode vertex = (RegulatoryNode)it.next();
			verticesIdsSet.add(vertex.getId());
		}
	}

	
	protected void addEdgesFromGraph( Graph gm_main, Graph gm_aux, String id, Color vcol, Color pcol, EdgeAttributesReader ereader) {
		RegulatoryNode v = (RegulatoryNode) gm_main.getNodeByName(id);
		if (v == null) {
			return;
		}
		RegulatoryEdge e = null;
		EdgeAttributesReader e1reader = gm_main.getEdgeAttributeReader();
		EdgeAttributesReader e2reader = gm_aux.getEdgeAttributeReader();

		//If v is a node from the studied graph, we look at its edges
		RegulatoryNode source = (RegulatoryNode) gm.getNodeByName(id);
		for (RegulatoryMultiEdge me1: ((RegulatoryGraph)gm_main).getOutgoingEdges(v)) {
			String tid = me1.getTarget().getId();
			RegulatoryNode target = (RegulatoryNode) gm.getNodeByName(tid);
			
			if (gm.getEdge(source, target) != null) {
				continue;
			}

			RegulatoryMultiEdge me2 = null;
			if (vcol != SPECIFIC_G1_COLOR && vcol != SPECIFIC_G2_COLOR && isCommonNode(target)) {
				Edge e2 = gm_aux.getEdge(gm_aux.getNodeByName(id), gm_aux.getNodeByName(tid));
				if (e2 != null) {
					me2 = (RegulatoryMultiEdge)e2;
				}
			}
			
			for (int i = 0; i < me1.getEdgeCount(); i++) {
				try{
					e = g.addNewEdge(id, tid, me1.getMin(i) , me1.getSign(i));
					Annotation gsa = e.me.getGsAnnotation(i);
					e.me.getGsAnnotation(i).copyFrom(me1.getGsAnnotation(i));
					if (me2 != null && me2.getEdgeCount() >= i) {
						// merge annotations
						addtoannotation(gsa, me2.getGsAnnotation(i));
					}
				}
				catch( GsException gs_exception){
					Debugger.error( "Unable to create new edge between vertices '" + id + "' and '" + tid + "' : one of the vertex was not found in the graph");
					Debugger.error( gs_exception);
				}
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
		OMDDNode omdd1 = v1.getTreeParameters(g1);
		OMDDNode omdd2 = v2.getTreeParameters(g2);
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
		for (Iterator it = logicalFunctionPending.iterator(); it.hasNext();) {
			RegulatoryNode[] t = (RegulatoryNode[]) it.next();
			LogicalParameterList lpl = t[1].getV_logicalParameters();
			lpl.applyNewGraph(t[0], meMap);			
		}
	}
	

	
	/**
	 * Compare the node order from both g1 and g2 
	 */
	private boolean compareNodeOrder() {
		String[] no1 = nodeOrderListToStringArray(g1.getNodeOrder());
		String[] no2 = nodeOrderListToStringArray(g2.getNodeOrder());

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
	
	private String[] nodeOrderListToStringArray(List nodeOrder) {
		String[] s = new String[nodeOrder.size()];
		int i = 0;
		Iterator it = nodeOrder.iterator();
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
	
	public Graph getDiffGraph() {
		
		return g;
	}
	public Graph getG1() {
		
		return g1;
	}
	public Graph getG2() {
		
		return g2;
	}
}