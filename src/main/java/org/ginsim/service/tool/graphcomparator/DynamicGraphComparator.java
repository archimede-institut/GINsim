package org.ginsim.service.tool.graphcomparator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.dynamicgraph.DynamicEdge;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;


/**
 * Compare 2 DynamicGraph
 * @author Duncan Berenguier
 * @since January 2009
 *
 */
public class DynamicGraphComparator extends GraphComparator<DynamicNode, DynamicEdge, DynamicGraph> {

    public DynamicGraphComparator( DynamicGraph g1, DynamicGraph g2) {
        super(g1, g2, GraphManager.getInstance().getNewGraph( DynamicGraph.class));
	}

    @Override
    protected DynamicNode copyNode(DynamicGraph g, DynamicNode v1) {
        DynamicNode v = new DynamicNode(v1.state);
        graph_new.addNode(v);
        return v;
    }

    @Override
    protected DynamicEdge copyEdge(DynamicGraph g, DynamicEdge e1) {
        return graph_new.addEdge(e1.getSource(), e1.getTarget(), false);
    }

    @Override
    protected void doSpecialisedComparison() {
        // nothing is really needed for the STG
    }

    /*
	protected void addEdgesFromGraph( DynamicGraph gm_main, DynamicGraph gm_aux, String id, Color vcol, Color pcol, EdgeAttributesReader ereader) {
		DynamicNode v = gm_main.getNodeByName(id);
		Edge<DynamicNode> e = null;
		EdgeAttributesReader e1reader = gm_main.getEdgeAttributeReader();
		EdgeAttributesReader e2reader = gm_aux.getEdgeAttributeReader();

		if (v != null) {
		    // If v is a vertex from the studied graph, we look at its edges
			Collection<DynamicEdge> edges = gm_main.getOutgoingEdges(v);
			for (Edge<DynamicNode> e1: edges) {
				String tid = e1.getTarget().toString();
				DynamicNode source = graph_new.getNodeByName(id);
				DynamicNode target = graph_new.getNodeByName(tid);
				DynamicEdge e2 = graph_new.getEdge(source, target);

				if (e2 == null) {
				    //The edge doesn't not already exists.
					e = graph_new.addEdge(v, e1.getTarget(), false);
                } else {
					continue;
                }

				String comment = "This edge ";
				if (vcol != COMMON_COLOR || !isCommonNode(target)) {
				    //The edge's vertices are specific to one graph therefore the edge is specific, and we add it with the right color.
					comment+= "is specific to "+(pcol == SPECIFIC_G1_COLOR ? "g1":"g2");
					mergeEdgeAttributes(e, e1, null, pcol, ereader, e1reader, null);
				} else {
					e2 = gm_aux.getEdge(gm_aux.getNodeByName(id), gm_aux.getNodeByName(tid));
					if (e2 != null) {
						comment+= "is common to both graphs";
						mergeEdgeAttributes(e, e1, e2, vcol, ereader, e1reader, e2reader);
					} else {
						comment+= "is specific to "+(pcol == SPECIFIC_G1_COLOR ? "g1":"g2");
						mergeEdgeAttributes(e, e1, null, pcol, ereader, e1reader, null);
					}
				}
				log(comment+" ("+e+")\n");
			}
		}
	}

*/
	
	/**
	 * Return a common nodeOrder for g1 and g2.
	 * Return null if there is any kind of incompatibility between both nodeOrder (different sizes, different nodes...) 
	 * 
	 * @param g1 a graph (in any order)
	 * @param g2 another graph (in any order)
	 * @return a node order or null if incompatible.
	 */
	public static List<String> getNodeOrder( DynamicGraph g1, DynamicGraph g2) {
		List<NodeInfo> no1 = g1.getNodeOrder();
		List<NodeInfo> no2 = g2.getNodeOrder();
		List<String> gNodeOrder = new ArrayList<String>();
		
		if (no1.size() == no2.size()) {
			for (Iterator<NodeInfo> it1 = no1.iterator(), it2 = no2.iterator(); it1.hasNext();) {
				String v1 = it1.next().toString();
				String v2 = it2.next().toString();
				gNodeOrder.add(v1);
				
				if (!v1.equals(v2)) {
					gNodeOrder = null;
					break;
				}
			}			
		}
	
		return gNodeOrder;
	}
}
