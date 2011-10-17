package org.ginsim.graph.regulatoryGraph;

import org.ginsim.graph.AbstractGraphFrontend;

/**
 * Simple LRG, mostly placeholder for now.
 * 
 * @author Aurelien Naldi
 */
public class RegulatoryGraphImpl extends AbstractGraphFrontend<RegulatoryVertex, RegulatoryEdge> implements RegulatoryGraph {

	@Override
	protected RegulatoryVertex createVertex(int mode) {
		return new RegulatoryVertex();
	}

	@Override
	protected RegulatoryEdge createEdge(RegulatoryVertex source, RegulatoryVertex target, int mode) {
		return new RegulatoryEdge(source, target);
	}

	
	@Override
	public void specificMethod() {
		// TODO: stuff... 
	}
}
