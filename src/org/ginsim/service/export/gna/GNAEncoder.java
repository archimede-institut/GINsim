package org.ginsim.service.export.gna;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
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
				out.write("t" + i + "_" + id);
				if (i < thresholdLevels) {
					out.write(", ");
				}
			}
			out.write("\n");

			OMDDNode mdd = node.getTreeParameters(graph).reduce();
			if (!node.isInput()) {
				out.write("  synthesis-parameters: ");
				if (mdd.next == null && mdd.value == 0) {
					out.write("k_" + id + ", ");
				}
				for (int i = 1; i <= thresholdLevels; i++) {
					out.write("k" + i + "_" + id);
					if (i < thresholdLevels) {
						out.write(", ");
					}
				}
				// Note that, in GNA, there it is possible to also regulate the
				// degradation parameters
				out.write("\n  degradation-parameters: g_" + id + "\n");
				out.write("  state-equation:\n    d/dt " + id + " = ");
				if (mdd.next == null && mdd.value == 0) {
					out.write("k_" + id);
					out.write(" - g_" + id + " * " + id + "\n");
				} else {
					f_browser.browse(mdd, node.getId());
					out.write("\n        - g_" + id + " * " + id + "\n");
				}

			} // end !input

			out.write("  parameter-inequalities:\n    zero_");
			if (!node.isInput() && mdd.next == null && mdd.value == 0) {
				out.write(id + " < k_" + id + " / g_" + id + " < ");
			} else {
				out.write(id + " < ");
			}
			// TODO: Bug: missing Sum combinations of all Ks
			for (int i = 1; i <= thresholdLevels; i++) {
				out.write("t" + i + "_" + id + " < ");
				if (node.isInput())
					continue;
				// out.write("k" + i + "_" + id + " / g_" + id + " < ");
				// if (i == 1)
				// continue;
				ArrayList<String> al = kappaComb(id, i);
				for (String term : al) {
					out.write("(" + term + ") / g_" + id + " < ");
				}
			}
			out.write("max_" + id + "\n\n");

		} // end for each node
	}

	private ArrayList<String> kappaComb(String id, int n) {
		ArrayList<String> alRes = new ArrayList<String>();
		alRes.add("k" + n + "_" + id);
		if (n > 1) {
			for (int i = 1; i < n; i++) {
				ArrayList<String> alFn = kappaComb(id, i);
				for (String term : alFn) {
					alRes.add(term + " + k" + n + "_" + id);
				}
			}
		}
		return alRes;
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
				out.write("\n        + ");
			}
			out.write("k" + leaf.value + "_" + nodeID);
			for (int i = 0; i < path.length; i++) {
				if (path[i][0] != -1) {
					String nodeName = ((RegulatoryNode) nodeOrder.get(i))
							.getId();
					int begin = path[i][0];
					int end = path[i][1] + 1;
					if (begin > 0) {
						out.write(" * s+(" + nodeName + ",t" + begin + "_"
								+ nodeName + ")");
					}
					if (end != -1 && end <= path[i][2]) {
						out.write(" * s-(" + nodeName + ",t" + end + "_"
								+ nodeName + ")");
					}
				}
			}
		} catch (IOException e) {
			// TODO: error!
		}
	}
}