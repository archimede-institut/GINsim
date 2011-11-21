package org.ginsim.gui.service.tools.stg2htg;

import java.util.HashSet;
import java.util.Vector;

import org.ginsim.graph.reducedgraph.NodeReducedData;


public class ComponentVertex extends NodeReducedData {

	private HashSet sigma;

	public ComponentVertex(String id, Vector content, Object sigma) {
		super(id, content);
		this.sigma = (HashSet) sigma;
	}

	public ComponentVertex(NodeReducedData scc, Object sigma) {
		super(scc.getId(), scc.getContent());
		this.sigma = (HashSet)sigma;
	}
	
	public ComponentVertex(NodeReducedData scc, Object sigma, String id) {
		super(id, scc.getContent());
		this.sigma = (HashSet)sigma;
	}
	
	public HashSet getSigma() {
		return sigma;
	}

}
