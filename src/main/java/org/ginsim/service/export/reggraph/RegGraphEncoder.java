package org.ginsim.service.export.reggraph;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/**
 * Exports a logical model into the Regulatory Graph format.
 * 
 * @author Pedro T. Monteiro
 */
public class RegGraphEncoder {

	private final RegulatoryGraph graph;

	public RegGraphEncoder(RegulatoryGraph graph) {
		this.graph = graph;
	}

	public void write(Writer out) throws IOException {

		for (Iterator<RegulatoryMultiEdge> it = graph.getEdges().iterator(); it
				.hasNext();) {
			RegulatoryMultiEdge edge = it.next();

			String sourceId = ((RegulatoryNode) edge.getSource()).getId();
			String targetId = ((RegulatoryNode) edge.getTarget()).getId();
			String edgeType;
			switch (edge.getSign()) {
			case NEGATIVE:
				edgeType = "-|";
				break;
			case POSITIVE:
				edgeType = "->";
				break;
			case DUAL:
				edgeType = "-|>";
				break;
			default:
				edgeType = "-?";
			}
			out.write(sourceId + " " + edgeType + " " + targetId + "\n");
		}
	}
}
