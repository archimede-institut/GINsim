package org.ginsim.servicegui.tool.stg2htg;

import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.ginsim.common.ColorPalette;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.utils.log.LogManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.service.tool.connectivity.AlgoConnectivity;


/**
 * A class to find a path in any graph
 */
public class STG2HTG extends AlgoConnectivity {
	private Graph graph;
	private HashMap sigma;
	private HashMap tilde_S;
	private HashSet A;
	private HashSet B;
	private ArrayList components;
	private Color[] colors;
	private Frame frame;

	/**
	 * Create a new thread that will search a path between start and end
	 * The graphManager is used to find the outgoingEdges from a node.
	 * The resultHandler is informed of the progression during the run() and of the results when the run() is finish.
	 * 
	 */
	public STG2HTG(Graph graph) {
		this.graph = graph;
		this.frame = GUIManager.getInstance().getFrame(graph);
	}

	public void run() {
		if (frame != null) {
			compute();
	    	GUIManager.getInstance().whatToDoWithGraph(reducedGraph, null, true);
		} else {
		   compute();
		}
	}
	
	public Object compute() {
		AlgoConnectivity algoSCC = new AlgoConnectivity();
		algoSCC.configure(graph, null, AlgoConnectivity.MODE_COMPO);
		List sccs = (List) algoSCC.compute();

		B = new HashSet(); //set of transients trivials
		A = new HashSet(); //set of the remaining sccs
		tilde_S = new HashMap(); //give the scc of each state

		for (Iterator it_sccs = sccs.iterator(); it_sccs.hasNext();) {
			NodeReducedData scc = (NodeReducedData) it_sccs.next();
			if (scc.isTrivial() && scc.isTransient(graph)) B.add(scc);
			else {
				A.add(scc);
				for (Iterator it_scc = scc.getContent().iterator(); it_scc.hasNext();) {
					Object node = (Object) it_scc.next();
					tilde_S.put(node, scc);
				}
			}
		}

		sigma = new HashMap();
		for (Iterator it_b = B.iterator(); it_b.hasNext();) {
			NodeReducedData b = (NodeReducedData) it_b.next();
			sigma(b);
		}
		LogManager.trace( "sigma = " + sigma);
		colorize();
		components = new ArrayList(sigma.size()+A.size());
		createReducedGraph();
		try {
			createSCCGraphByOutgoingEdges(components.size(), components, reducedGraph, reducedGraph.getNodeAttributeReader());
		} catch (InterruptedException e) {
		}
		return reducedGraph;
	}

	private void sigma(NodeReducedData b) {
		HashSet visited = new HashSet();
		visited.add(b);
		HashSet image = new HashSet();
		dfs(b.getContent().get(0), image, visited);
		Vector component = (Vector) sigma.get(image);
		if (component == null) {
			component = new Vector();
			component.add(b);
			sigma.put(image, component);
		} else {
			component.add(b);
		}
	}

	private void dfs(Object source, HashSet image, HashSet visited) {
		Collection<Edge> outgoingEdges = graph.getOutgoingEdges(source);
		if (outgoingEdges == null) return;
		for (Edge edge: outgoingEdges) {
			Object target = edge.getTarget();
			if (!visited.contains(target)) {
				visited.add(target);
				NodeReducedData scc = (NodeReducedData) tilde_S.get(target);
				if (scc == null) dfs(target, image, visited);
				else {
					if (!image.contains(scc))	image.add(scc);
				}
			}
		}		
	}	
	
	
	private void colorize() {
		colors = ColorPalette.createColorPaletteByRange(sigma.size()+A.size());
		NodeAttributesReader vreader = graph.getNodeAttributeReader();
		int color = 0;
		for (Iterator it_sigma = sigma.keySet().iterator(); it_sigma.hasNext();) {
			HashSet key = (HashSet) it_sigma.next();
			for (Iterator it_key = ((List)sigma.get(key)).iterator(); it_key.hasNext();) {
				NodeReducedData scc = (NodeReducedData) it_key.next();
				for (Iterator it_scc = scc.getContent().iterator(); it_scc.hasNext();) {
					Object vertex = (Object) it_scc.next();
					vreader.setNode(vertex);
					vreader.setBackgroundColor(colors[color]);
					vreader.refresh();
				}
			}
			color++;
		}
		for (Iterator it_sigma = A.iterator(); it_sigma.hasNext();) {
			NodeReducedData scc = (NodeReducedData) it_sigma.next();
			for (Iterator it_scc = scc.getContent().iterator(); it_scc.hasNext();) {
				Object vertex = (Object) it_scc.next();
				vreader.setNode(vertex);
				vreader.setBackgroundColor(colors[color]);
				vreader.setForegroundColor(Color.BLACK);
				vreader.setBorder(2);
				vreader.refresh();
			}
			color++;
		}
	}
	
	private void createReducedGraph() {
		
        reducedGraph = GraphManager.getInstance().getNewGraph( ReducedGraph.class, graph);
        NodeAttributesReader vreader = reducedGraph.getNodeAttributeReader();
        int i = 0;
		for (Iterator it_sigma = sigma.keySet().iterator(); it_sigma.hasNext();) {
			HashSet key = (HashSet) it_sigma.next();
			Vector content = new Vector();
			for (Iterator it_key = ((List)sigma.get(key)).iterator(); it_key.hasNext();) {
				NodeReducedData scc = (NodeReducedData) it_key.next();
				content.addAll(scc.getContent());
			}
			DynamicNode s = (DynamicNode) ((NodeReducedData)content.get(0)).getContent().get(0);
			ComponentNode comp = new ComponentNode("TT-"+s.getPatternString(this.graph), content, key);
			LogManager.trace( "TT-"+s.getPatternString(this.graph));
			for (Iterator it = ((NodeReducedData)content.get(0)).getContent().iterator(); it.hasNext();) {
				s = (DynamicNode) it.next();
				LogManager.trace( "\t"+s.getPatternString(this.graph)+"\n\t"+s.toString());
			}
			reducedGraph.addNode(comp);
			components.add(comp);
			vreader.setNode(comp);
			vreader.setBackgroundColor(colors[i]);
			vreader.refresh();
			i++;
		}
		for (Iterator it_sigma = A.iterator(); it_sigma.hasNext();) {
			NodeReducedData scc = (NodeReducedData) it_sigma.next();
			DynamicNode s = (DynamicNode) scc.getContent().get(0);
			ComponentNode comp;
			if ( scc.isTrivial() ) comp = new ComponentNode(scc, null);
			else {
				HashSet hs = new HashSet();
				hs.add(scc);
				comp = new ComponentNode(scc, hs, "CC-"+s.getPatternString(this.graph));
				LogManager.trace( "CC-"+s.getPatternString(this.graph));
				for (Iterator it = scc.getContent().iterator(); it.hasNext();) {
					s = (DynamicNode) it.next();
					LogManager.trace( "\t"+s.getPatternString(this.graph)+"\n\t"+s.toString());
				}
			}
			reducedGraph.addNode(comp);
			components.add(comp);
			vreader.setNode(comp);
			vreader.setBackgroundColor(colors[i]);
			vreader.refresh();
			i++;
		}
      
        
	}

}
