package fr.univmrs.tagc.GINsim.regulatoryGraph.localGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.univmrs.tagc.GINsim.css.CascadingStyle;
import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.reg2dyn.SimulationUpdater;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * 
 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
 * 
 */
public class LocalGraph {
	private GsRegulatoryGraph g;
	private GsGraphManager gm;
	private CascadingStyle cs = null;
	private LocalGraphSelector selector = null;
	private List states;
	private HashMap functionalityMap;
	private HashMap node_to_position;
	private SimulationUpdater updater;
	
	
	static final byte FUNC_NON = 1;
	static final byte FUNC_POSITIVE = 2;
	static final byte FUNC_NEGATIVE = 3;

	public LocalGraph(GsRegulatoryGraph  g) {
		this.g = g;
		this.gm = g.getGraphManager();
	}
	public LocalGraph(GsRegulatoryGraph  g, List states) {
		this(g);
		this.states = states;		
	}
	
	public void setUpdater(SimulationUpdater updater) {
		this.updater = updater;
	}
	
	public void setState(byte[] state) {
		this.states = new ArrayList(1);
		this.states.add(state);
	}

	public void setStates(List states) {
		this.states = states;
	}

	public void run() {
		node_to_position = new HashMap((int) (gm.getVertexCount()*1.5));					//m.get(vertex) => its position in the nodeOrder as an Integer.
		int i = 0;
		for (Iterator it = g.getNodeOrder().iterator(); it.hasNext();) {							//Build the map m
			node_to_position.put(it.next(), Integer.valueOf(i++));
		}
				
		functionalityMap = new HashMap();
		selector = new LocalGraphSelector();
		selector.setCache(functionalityMap);
		
		for (Iterator it_states = states.iterator(); it_states.hasNext();) {
			byte[] state = (byte[]) it_states.next();
			int j;
			for (Iterator it_edges = gm.getAllEdges().iterator(); it_edges.hasNext();) {
				GsDirectedEdge edge = (GsDirectedEdge) it_edges.next();
				GsRegulatoryVertex source = (GsRegulatoryVertex) edge.getSource();
				GsRegulatoryVertex target = (GsRegulatoryVertex) edge.getTarget();
				i = ((Integer) node_to_position.get(source)).intValue();
				j = ((Integer) node_to_position.get(target)).intValue();
				byte[] fx = f(state);
				byte[] fxbi = f(bar_x(state, i));
//				System.out.println(i+"->"+j);
//				print(state);
//				print(fx);
//				print(fxbi);
//				System.out.println(fx[j]+"=="+fxbi[j]);
				if (fx[j] != fxbi[j]) {
					String func = (String) functionalityMap.get(edge);
					if (state[i] == fx[j]) {
						if (func == null || func == LocalGraphSelector.CAT_POSITIVE) functionalityMap.put(edge, LocalGraphSelector.CAT_POSITIVE);
						else functionalityMap.put(edge, LocalGraphSelector.CAT_DUAL);
					} else {
						if (func == null || func == LocalGraphSelector.CAT_NEGATIVE) functionalityMap.put(edge, LocalGraphSelector.CAT_NEGATIVE);
						else functionalityMap.put(edge, LocalGraphSelector.CAT_DUAL);
					}
				} else {
					//functionalityMap.put(edge, LocalGraphSelector.CAT_NONFUNCTIONNAL); //NO need, because, non functionnal by default
				}
			}
			
		}

	}
	
//	public void print(byte[] x) {
//		for (int i = 0 ; i < x.length ; i++) System.out.print(x[i]);
//		System.out.println();
//	}

	/**
	 * Compute the next state
	 * @param x
	 * @return
	 */
	private byte[] f(byte[] x) {
		updater.setState(x, 0, null);
		byte[] fx = updater.nextState();
		if (fx != null) return fx;
		return x;
	}
	
	/**
	 * Create and return a new state by changing the value at i
	 * @param x the state
	 * @param i the change
	 * @return
	 */
	private byte[] bar_x(byte[] x, int i) {
		byte[] x_b = new byte[x.length];
		for (int j = 0; j < x_b.length; j++) {
			x_b[j] = x[j];
		}
		x_b[i] = (byte) (1 - x_b[i]);
		return x_b;
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
		for (Iterator iterator = gm.getEdgeIterator(); iterator.hasNext();) {
			GsDirectedEdge me = (GsDirectedEdge) iterator.next();
			ereader.setEdge(me);
			cs.applyOnEdge(selector, me, ereader);
		}
		
	}
	
	public void undoColorize() {
		cs.restoreAllEdges(gm.getAllEdges(), gm.getEdgeAttributesReader());
	}



	
	protected void finalize() {
		if (functionalityMap != null) {
			if (selector != null) {
                selector.flush(); //remove nonFunctionalInteractions from the cache.
            }
		}
	}
	public Map getFunctionality() {
		return functionalityMap;
	}

}
