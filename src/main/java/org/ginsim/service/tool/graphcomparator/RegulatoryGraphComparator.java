package org.ginsim.service.tool.graphcomparator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.colomoto.mddlib.MDDComparator;
import org.colomoto.mddlib.MDDComparatorFactory;
import org.colomoto.mddlib.MDDManager;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameterList;


/**
 * Compare 2 RegulatoryGraph
 *
 * @author Duncan Berenguier
 * @since January 2009
 */
public class RegulatoryGraphComparator extends GraphComparator<RegulatoryNode, RegulatoryMultiEdge, RegulatoryGraph> {

	/**
	 * indicates if the node order of both graph is the same.
	 */
	private boolean sameNodeOrder;
	private List<RegulatoryNode[]> logicalFunctionPending;
	private Map<RegulatoryMultiEdge, RegulatoryMultiEdge> meMap;

    private MDDManager ddmanager1, ddmanager2;
    private MDDComparator ddcomparator;


    public RegulatoryGraphComparator(RegulatoryGraph g1, RegulatoryGraph g2) {
        super(g1, g2, GSGraphManager.getInstance().getNewGraph());

        this.ddmanager1 = g1.getMDDFactory();
        this.ddmanager2 = g2.getMDDFactory();
        ddcomparator = MDDComparatorFactory.getComparator(ddmanager1, ddmanager2);

		logicalFunctionPending = new ArrayList<RegulatoryNode[]>();
		
		sameNodeOrder = compareNodeOrder();
		if (!sameNodeOrder) {
			String comment = "diff: The node order is the same for both graph";
			log(comment+"\n");
			graph_new.getAnnotation().appendToComment(comment);
			log(g1.getNodeOrder()+"\n");
			log(g2.getNodeOrder()+"\n");
		}
	}


    @Override
	protected RegulatoryNode copyNode(RegulatoryGraph g, RegulatoryNode v1) {
        String id = v1.getId();
        RegulatoryNode v = graph_new.addNewNode(id, v1.getName(), v1.getMaxValue());
		v.setInput(v.isInput(), graph_new);
        setLogicalFunction(v, v1, graph_1);
		// FIXME: transfer annotations
        return v;
	}

    @Override
    protected RegulatoryMultiEdge copyEdge(RegulatoryGraph srcGraph, RegulatoryMultiEdge e) {
        RegulatoryNode src = graph_new.getNodeByName(e.getSource().getId());
        RegulatoryNode tgt = graph_new.getNodeByName(e.getTarget().getId());

        if (src == null || tgt == null) {
            throw new RuntimeException("Unable to find matching nodes for this edge: "+e);
        }

        RegulatoryMultiEdge newEdge = new RegulatoryMultiEdge(graph_new, src, tgt, e.getSign(0));
		// FIXME: transfer annotations
        graph_new.addEdge(newEdge);
        return newEdge;
    }

    @Override
    protected void doSpecialisedComparison() {

        // TODO: also compare annotations

        for (ComparedItemInfo<RegulatoryNode> info: result.comparedNodes.values()) {
            if (info.first == null || info.second == null) {
                continue;
            }

            if (info.first.getMaxValue() != info.second.getMaxValue()) {
                info.changed = true;
            } else {
                info.changed = compareLogicalFunction(info.first, info.second);
            }
        }

        for (ComparedItemInfo<RegulatoryMultiEdge> info: result.comparedEdges.values()) {
            if (info.first == null || info.second == null) {
                continue;
            }

            if (info.first.getEdgeCount() != info.second.getEdgeCount()) {
                info.changed = true;
            } else {
                for (int i=0 ; i<info.first.getEdgeCount() ; i++) {
                    if ( (info.first.getMax(i) != info.second.getMax(i)) ||
                         (info.first.getSign(i) != info.second.getSign(i))) {
                        info.changed = true;
                        break;
                    }
                }
            }
        }

		setAllLogicalFunctions();
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

/*
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
*/

/*
	private String compareNodes(RegulatoryNode v, RegulatoryNode v1, RegulatoryNode v2, Color[] color) {
		String comment = "";
		if (!v1.getName().equals(v2.getName())) {
			String n1 = v1.getName();
			String n2 = v2.getName();
			if (n1.equals("")) {
				v.setName(n2);
				n1 = "no name";
			}
			if (n2.equals("")) {
                n2 = "no name";
            }
			comment += "   names are different : "+n1+" and "+n2+"\n";
		}

		if (v1.getMaxValue() != v2.getMaxValue()) {
			byte mv1 = v1.getMaxValue();
			byte mv2 = v2.getMaxValue();
			comment += "   max values are different : "+mv1+" and "+mv2+"\n";
			color[0] = COMMON_COLOR_DIFF_MAXVALUES;
		} else if (sameNodeOrder) {
            comment += compareLogicalFunction(v1, v2); //Compare logical function only if they have the same maxValue.
        }
		return comment;
	}
*/
	
	/**
	 * Compare the logical function of node 'v1' and 'v2'.
	 * @param v1 
	 * @param v2
	 */
	private boolean compareLogicalFunction(RegulatoryNode v1, RegulatoryNode v2) {
        int mdd1 = v1.getMDD(graph_1, ddmanager1);
        int mdd2 = v2.getMDD(graph_2, ddmanager2);

		return !ddcomparator.similar(mdd1, mdd2);
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
			try {
				lpl.applyNewGraph(t[0], meMap);
			} catch (Exception e) {
				// TODO: why does copy of logical parameters fail?
			}
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
