package org.ginsim.service.export.gna;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;

/**
 * Exports a GINsim Regulatory graph into an old (non xml) GNA model
 * description.
 * 
 * @author Pedro T. Monteiro
 */
public class GNAEncoder {

	public void write(RegulatoryGraph graph, Writer out) throws IOException {

		LogicalModel model = graph.getModel();
		MDDManager ddmanager = model.getMDDManager();
		PathSearcher searcher = new PathSearcher(ddmanager);
		List<NodeInfo> nodeOrder = model.getNodeOrder();
		int[] functions = model.getLogicalFunctions();
		MDDVariable[] variables = ddmanager.getAllVariables();

		for (int p = 0; p < functions.length; p++) {
			MDDVariable var = variables[p];
			int mdd = functions[p];
			String id = var.toString();

			boolean input = nodeOrder.get(p).isInput();
			out.write((input ? "input" : "state") + "-variable: " + id + "\n"
					+ "  zero-parameter: zero_" + id + "\n"
					+ "  box-parameter: max_" + id
					+ "\n  threshold-parameters: ");

			// add parameters for its threshold levels
			int thresholdLevels = var.nbval - 1;
			for (int i = 1; i <= thresholdLevels; i++) {
				out.write("t" + i + "_" + id);
				if (i < thresholdLevels) {
					out.write(", ");
				}
			}
			out.write("\n");

			if (!input) {
				out.write("  synthesis-parameters: ");
				if (mdd == 0) {
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
				if (mdd == 0) {
					out.write("k_" + id);
				} else {
					// iterate over paths
					browse(searcher, mdd, variables, id, out);
				}
				out.write("\n        - g_" + id + " * " + id + "\n");

			} // end !input

			out.write("  parameter-inequalities:\n    zero_");
			if (!input && mdd == 0) {
				out.write(id + " < k_" + id + " / g_" + id + " < ");
			} else {
				out.write(id + " < ");
			}

			for (int i = 1; i <= thresholdLevels; i++) {
				out.write("t" + i + "_" + id + " < ");
				if (input) {
					continue;
				}

				String last = "k" + i + "_" + id;
				for (int j = i; j > 0; j--) {
					if (j == i) {
						out.write(last + " / g_" + id + " < ");
					} else {
						last = "k" + j + "_" + id + " + " + last;
						out.write("( " + last + " ) / g_" + id + " < ");
					}
				}
			}
			out.write("max_" + id + "\n");

		} // end for each node
	}

	private void browse(PathSearcher searcher, int mdd,
			MDDVariable[] variables, String nodeID, Writer out)
			throws IOException {
		int[] path = searcher.setNode(mdd);
		Map<Integer, Set<String>> mapEq = new HashMap<Integer, Set<String>>();

		for (int leaf : searcher) {
			if (leaf == 0) {
				continue;
			}

			String expr = "k" + leaf + "_" + nodeID;
			for (int i = 0; i < path.length; i++) {
				int value = path[i];
				if (value == -1) {
					continue;
				}
				int end = value + 1;
				String nodeName = variables[i].toString();
				if (value > 0) {
					expr += " * s+(" + nodeName + ",t" + value + "_" + nodeName
							+ ")";
				}
				if (end < variables[i].nbval) {
					expr += " * s-(" + nodeName + ",t" + end + "_" + nodeName
							+ ")";
				}
			}
			if (!mapEq.containsKey(leaf)) {
				mapEq.put(leaf, new HashSet<String>());
			}
			mapEq.get(leaf).add(expr);
		}

		// *README*
		// This reordering of expressions within the state-equation is due to
		// a GNA feature in which the synthesis parameters are presented
		// in the inequalities expressions in the order for which they appear
		// in the state equation.
		// If we don't respect that, GNA's parser is lost :(
		boolean first = true;
		for (int leaf : mapEq.keySet()) {
			for (String expr : mapEq.get(leaf)) {
				if (first) {
					first = false;
				} else {
					out.write("\n        + ");
				}
				out.write(expr);
			}
		}
	}
}