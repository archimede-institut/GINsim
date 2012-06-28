package org.ginsim.service.export.gna;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.PathSearcher;

/**
 * Exports a Logical Model into an old (non xml) GNA model description.
 * 
 * @author Pedro T. Monteiro
 */
public class LogicalModel2GNA {

	private final LogicalModel model;
	private final Writer out;
	private final PathSearcher searcher;


	public LogicalModel2GNA(LogicalModel model, Writer out) {
		this.model = model;
		this.out = out;
		this.searcher = new PathSearcher(model.getMDDManager());
	}

	
	public void write() throws IOException {

		MDDManager ddmanager = model.getMDDManager();
		List<NodeInfo> coreNodes = model.getNodeOrder();
		PathSearcher searcher = new PathSearcher(ddmanager);
		int[] functions = model.getLogicalFunctions();
		MDDVariable[] variables = ddmanager.getAllVariables();
		for (int p=0 ; p<functions.length ; p++) {
			MDDVariable var = variables[p];
			int mdd = functions[p];
			String id = var.toString();
			
			boolean input = coreNodes.get(p).isInput();
			out.write((input ? "input" : "state") + "-variable: " + id
					+ "\n" + "  zero-parameter: zero_" + id + "\n"
					+ "  box-parameter: max_" + id
					+ "\n  threshold-parameters: ");

			// add parameters for its threshold levels
			int thresholdLevels = var.nbval; // FIXME: check if it should be nbval-1
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
					out.write(" - g_" + id + " * " + id + "\n");
				} else {
					// iterate over paths
					browse(mdd, variables, id);
					out.write("\n        - g_" + id + " * " + id + "\n");
				}

			} // end !input

			
			out.write("  parameter-inequalities:\n    zero_");
			if (!input && mdd == 0) {
				out.write(id + " < k_" + id + " / g_" + id + " < ");
			} else {
				out.write(id + " < ");
			}
			// TODO: Bug: missing Sum combinations of all Ks
			for (int i = 1; i <= thresholdLevels; i++) {
				out.write("t" + i + "_" + id + " < ");
				if (input) {
					continue;
				}
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
	
	
	private void browse(int mdd, MDDVariable[] variables, String nodeID) {
		int[] path = searcher.setNode(mdd);
		boolean first = true;
		for (int leaf: searcher) {
			if (leaf == 0) {
				continue;
			}
			
			try {
				if (first) {
					first = false;
				} else {
					out.write("\n        + ");
				}
				out.write("k" + leaf + "_" + nodeID);
				for (int i = 0; i < path.length; i++) {
					int value = path[i];
					if (value == -1) {
						continue;
					}
					int end = value+1; // TODO: let PathSearcher merge adjacent values
					String nodeName = variables[i].toString();
					if (value > 0) {
						out.write(" * s+(" + nodeName + ",t" + value + "_"
								+ nodeName + ")");
					}
					if (end < variables[i].nbval) {
						out.write(" * s-(" + nodeName + ",t" + end + "_"
								+ nodeName + ")");
					}
				}
			} catch (IOException e) {
				// TODO: error!
			}			
		}
	}
}
