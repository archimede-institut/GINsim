package org.ginsim.servicegui.tool.localgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.css.CascadingStyle;
import org.ginsim.service.tool.reg2dyn.updater.SimulationUpdater;


/**
 * 
 * Search all the non functional interactions in the graph 'g' and do some actions on them depending on the options (opt_*).
 * 
 */
public class LocalGraph {
	private RegulatoryGraph regGraph;
	private CascadingStyle cs = null;
	private LocalGraphSelector selector = null;
	private List<byte[]> states;
	private HashMap<RegulatoryMultiEdge, String> functionalityMap;
	private HashMap<RegulatoryNode, Integer> node_to_position;
	private SimulationUpdater updater;
	
	
	static final byte FUNC_NON = 1;
	static final byte FUNC_POSITIVE = 2;
	static final byte FUNC_NEGATIVE = 3;

	public LocalGraph(RegulatoryGraph regGraph) throws GsException {
		this.regGraph = regGraph;
		for (RegulatoryNode node : regGraph.getNodes()) {
			if (node.getMaxValue() > 1) {
				throw new GsException(GsException.GRAVITY_ERROR, "The local graph can only be computed on boolean models");
			}
		}
	}
	public LocalGraph(RegulatoryGraph regGraph, List<byte[]> states) throws GsException {
		this(regGraph);
		this.states = states;		
	}
	
	public void setUpdater(SimulationUpdater updater) {
		this.updater = updater;
	}
	
	public void setState(byte[] state) {
		this.states = new ArrayList<byte[]>(1);
		this.states.add(state);
	}

	public void setStates(List<byte[]> states) {
		this.states = states;
	}

	public void run() {
		node_to_position = new HashMap<RegulatoryNode, Integer>((int) (regGraph.getNodeCount()*1.5));					//m.get(vertex) => its position in the nodeOrder as an Integer.
		int i = 0;
		for (Iterator<RegulatoryNode> it = regGraph.getNodeOrder().iterator(); it.hasNext();) {							//Build the map m
			node_to_position.put(it.next(), Integer.valueOf(i++));
		}
				
		functionalityMap = new HashMap<RegulatoryMultiEdge, String>();
		selector = new LocalGraphSelector();
		selector.setCache(functionalityMap);
		
		for (Iterator<byte[]> it_states = states.iterator(); it_states.hasNext();) {
			byte[] state = it_states.next();
			int j;
			for (RegulatoryMultiEdge edge: regGraph.getEdges()) {
				RegulatoryNode source = edge.getSource();
				RegulatoryNode target = edge.getTarget();
				i = node_to_position.get(source);
				j = node_to_position.get(target);
				byte[] fx = f(state);
				byte[] fxbi = f(bar_x(state, i));
				System.out.println(i+"->"+j);
				print(state);
				print(fx);
				print(fxbi);
				System.out.println(fx[j]+"=="+fxbi[j]);
				if (fx[j] != fxbi[j]) {
					String func = functionalityMap.get(edge);
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
	
	public void print(byte[] x) {
		for (int i = 0 ; i < x.length ; i++) System.out.print(x[i]);
		System.out.println();
	}

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
		
		EdgeAttributesReader ereader = regGraph.getEdgeAttributeReader();
		for (RegulatoryMultiEdge me: regGraph.getEdges()) {
			ereader.setEdge(me);
			cs.applyOnEdge(selector, me, ereader);
		}
		
	}
	
	public void undoColorize() {
		cs.restoreAllEdges(regGraph.getEdges(), regGraph.getEdgeAttributeReader());
	}



	
	protected void finalize() {
		if (functionalityMap != null) {
			if (selector != null) {
                selector.flush(); //remove nonFunctionalInteractions from the cache.
            }
		}
	}
	public Map<RegulatoryMultiEdge, String> getFunctionality() {
		return functionalityMap;
	}

}
