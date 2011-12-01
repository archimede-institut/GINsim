package org.ginsim.service.export.gna;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.regulatorygraph.logicalfunction.LogicalFunctionBrowser;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;

/**
 * Exports a GINsim Regulatory graph into an old (non xml) GNA model
 * description.
 * 
 * @author Pedro T. Monteiro
 */
public class GNAEncoder {

	public void write(RegulatoryGraph graph, Writer out) throws IOException {

		GNAFunctionBrowser f_browser = new GNAFunctionBrowser(
				graph.getNodeOrder(), out);

		for (RegulatoryNode node : graph.getNodeOrder()) {
			int thresholdLevels = node.getMaxValue();
			String id = node.getId();

			out.write((node.isInput() ? "input" : "state") + "-variable: " + id
					+ "\n" + "  zero-parameter: zero_" + id + "\n"
					+ "  box-parameter: max_" + id
					+ "\n  threshold-parameters: ");
			for (int i = 1; i <= thresholdLevels; i++) {
				out.write("t_" + id + i);
				if (i < thresholdLevels) {
					out.write(", ");
				}
			}
			out.write("\n");

			OMDDNode mdd = node.getTreeParameters(graph).reduce();
			if (!node.isInput()) {
				out.write("  synthesis-parameters: ");
				if (mdd.next == null && mdd.value == 0) {
					out.write("k_" + id + "0, ");
				}
				for (int i = 1; i <= thresholdLevels; i++) {
					out.write("k_" + id + i);
					if (i < thresholdLevels) {
						out.write(", ");
					}
				}
				// Note that, in GNA, there it is possible to also regulate the
				// degradation parameters
				out.write("\n  degradation-parameters: g_" + id + "\n");
				out.write("  state-equation:\n    d/dt " + id + " = ");
				if (mdd.next == null && mdd.value == 0) {
					out.write("k_" + id + "0");
					out.write(" - g_" + id + " * " + id + "\n");
				} else {
					f_browser.browse(mdd, node.getId());
					out.write(" - g_" + id + " * " + id + "\n");
				}

			} // end !input

			if (mdd.next == null && mdd.value == 0) {
				out.write("  parameter-inequalities: zero_" + id + " < k_" + id
						+ "0 / g_" + id + " < ");
			} else {
				out.write("  parameter-inequalities: zero_" + id + " < ");
			}

			for (int i = 1; i <= thresholdLevels; i++) {
				out.write("t_" + id + i + " < ");
				out.write("k_" + id + i + " / g_" + id + " < ");
			}
			out.write("max_" + id + "\n\n");

		} // end for each node
	}
}

class GNAFunctionBrowser extends LogicalFunctionBrowser {
	Writer out;
	boolean first = true;
	String nodeID;

	public GNAFunctionBrowser(List<RegulatoryNode> nodeOrder, Writer out) {
		super(nodeOrder);
		this.out = out;
	}

	public void browse(OMDDNode node, String name) {
		this.nodeID = name;
		first = true;
		browse(node);
	}

	protected void leafReached(OMDDNode leaf) {
		if (leaf.value == 0) {
			return;
		}
		try {
			if (first) {
				first = false;
			} else {
				out.write(" + ");
			}
			out.write("k_" + nodeID + leaf.value);
			for (int i = 0; i < path.length; i++) {
				if (path[i][0] != -1) {
					String nodeName = ((RegulatoryNode) nodeOrder.get(i))
							.getId();
					int begin = path[i][0];
					int end = path[i][1] + 1;
					if (begin > 0) {
						out.write(" * s+(" + nodeName + ",t_" + nodeName
								+ begin + ")");
					}
					if (end != -1 && end <= path[i][2]) {
						out.write(" * s-(" + nodeName + ",t_" + nodeName + end
								+ ")");
					}
				}
			}
		} catch (IOException e) {
			// TODO: error!
		}
	}
}