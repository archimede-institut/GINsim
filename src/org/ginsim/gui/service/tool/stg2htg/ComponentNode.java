package org.ginsim.gui.service.tool.stg2htg;

import java.util.HashSet;
import java.util.Vector;

import org.ginsim.graph.reducedgraph.NodeReducedData;


public class ComponentNode extends NodeReducedData {

	private HashSet sigma;

	public ComponentNode(String id, Vector content, Object sigma) {
		super(id, content);
		this.sigma = (HashSet) sigma;
	}

	public ComponentNode(NodeReducedData scc, Object sigma) {
		super(scc.getId(), scc.getContent());
		this.sigma = (HashSet)sigma;
	}
	
	public ComponentNode(NodeReducedData scc, Object sigma, String id) {
		super(id, scc.getContent());
		this.sigma = (HashSet)sigma;
	}
	
	public HashSet getSigma() {
		return sigma;
	}

}
