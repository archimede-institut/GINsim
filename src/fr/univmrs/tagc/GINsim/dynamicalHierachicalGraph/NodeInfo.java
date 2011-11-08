package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

public class NodeInfo {
	public final String name;
	public final byte max;

	public NodeInfo(String name, byte max) {
		super();
		this.name = name;
		this.max = max;
	}

	public NodeInfo(GsRegulatoryVertex vertex) {
		this(vertex.getId(), vertex.getMaxValue());
	}
}
