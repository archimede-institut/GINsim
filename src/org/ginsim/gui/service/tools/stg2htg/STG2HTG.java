package org.ginsim.gui.service.tools.stg2htg;

import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.dynamicgraph.GsDynamicNode;
import org.ginsim.graph.reducedgraph.GsNodeReducedData;
import org.ginsim.graph.reducedgraph.GsReducedGraph;
import org.ginsim.graph.reducedgraph.ReducedGraphImpl;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.tools.connectivity.AlgoConnectivity;

import fr.univmrs.tagc.common.ColorPalette;

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
			GsNodeReducedData scc = (GsNodeReducedData) it_sccs.next();
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
			GsNodeReducedData b = (GsNodeReducedData) it_b.next();
			sigma(b);
		}
		System.out.println(sigma);
		colorize();
		components = new ArrayList(sigma.size()+A.size());
		createReducedGraph();
		try {
			createSCCGraphByOutgoingEdges(components.size(), components, reducedGraph, reducedGraph.getVertexAttributeReader());
		} catch (InterruptedException e) {
		}
		return reducedGraph;
	}

	private void sigma(GsNodeReducedData b) {
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
				GsNodeReducedData scc = (GsNodeReducedData) tilde_S.get(target);
				if (scc == null) dfs(target, image, visited);
				else {
					if (!image.contains(scc))	image.add(scc);
				}
			}
		}		
	}	
	
	
	private void colorize() {
		colors = ColorPalette.createColorPaletteByRange(sigma.size()+A.size());
		VertexAttributesReader vreader = graph.getVertexAttributeReader();
		int color = 0;
		for (Iterator it_sigma = sigma.keySet().iterator(); it_sigma.hasNext();) {
			HashSet key = (HashSet) it_sigma.next();
			for (Iterator it_key = ((List)sigma.get(key)).iterator(); it_key.hasNext();) {
				GsNodeReducedData scc = (GsNodeReducedData) it_key.next();
				for (Iterator it_scc = scc.getContent().iterator(); it_scc.hasNext();) {
					Object vertex = (Object) it_scc.next();
					vreader.setVertex(vertex);
					vreader.setBackgroundColor(colors[color]);
					vreader.refresh();
				}
			}
			color++;
		}
		for (Iterator it_sigma = A.iterator(); it_sigma.hasNext();) {
			GsNodeReducedData scc = (GsNodeReducedData) it_sigma.next();
			for (Iterator it_scc = scc.getContent().iterator(); it_scc.hasNext();) {
				Object vertex = (Object) it_scc.next();
				vreader.setVertex(vertex);
				vreader.setBackgroundColor(colors[color]);
				vreader.setForegroundColor(Color.BLACK);
				vreader.setBorder(2);
				vreader.refresh();
			}
			color++;
		}
	}
	
	private void createReducedGraph() {
        reducedGraph = new ReducedGraphImpl( graph);
        VertexAttributesReader vreader = reducedGraph.getVertexAttributeReader();
        int i = 0;
		for (Iterator it_sigma = sigma.keySet().iterator(); it_sigma.hasNext();) {
			HashSet key = (HashSet) it_sigma.next();
			Vector content = new Vector();
			for (Iterator it_key = ((List)sigma.get(key)).iterator(); it_key.hasNext();) {
				GsNodeReducedData scc = (GsNodeReducedData) it_key.next();
				content.addAll(scc.getContent());
			}
			GsDynamicNode s = (GsDynamicNode) ((GsNodeReducedData)content.get(0)).getContent().get(0);
			GsComponentVertex comp = new GsComponentVertex("TT-"+s.getPatternString(this.graph), content, key);
			System.out.println("TT-"+s.getPatternString(this.graph));
			for (Iterator it = ((GsNodeReducedData)content.get(0)).getContent().iterator(); it.hasNext();) {
				s = (GsDynamicNode) it.next();
				System.out.println("\t"+s.getPatternString(this.graph)+"\n\t"+s.toString());
			}
			reducedGraph.addVertex(comp);
			components.add(comp);
			vreader.setVertex(comp);
			vreader.setBackgroundColor(colors[i]);
			vreader.refresh();
			i++;
		}
		for (Iterator it_sigma = A.iterator(); it_sigma.hasNext();) {
			GsNodeReducedData scc = (GsNodeReducedData) it_sigma.next();
			GsDynamicNode s = (GsDynamicNode) scc.getContent().get(0);
			GsComponentVertex comp;
			if ( scc.isTrivial() ) comp = new GsComponentVertex(scc, null);
			else {
				HashSet hs = new HashSet();
				hs.add(scc);
				comp = new GsComponentVertex(scc, hs, "CC-"+s.getPatternString(this.graph));
				System.out.println("CC-"+s.getPatternString(this.graph));
				for (Iterator it = scc.getContent().iterator(); it.hasNext();) {
					s = (GsDynamicNode) it.next();
					System.out.println("\t"+s.getPatternString(this.graph)+"\n\t"+s.toString());
				}
			}
			reducedGraph.addVertex(comp);
			components.add(comp);
			vreader.setVertex(comp);
			vreader.setBackgroundColor(colors[i]);
			vreader.refresh();
			i++;
		}
      
        
	}

}
