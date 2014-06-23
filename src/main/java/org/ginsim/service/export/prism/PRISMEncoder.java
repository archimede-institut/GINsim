package org.ginsim.service.export.prism;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.common.application.GsException;

/**
 * Exports a Regulatory graph into a PRISM model description.
 * 
 * @author Pedro T. Monteiro
 */
public class PRISMEncoder {

	// http://www.prismmodelchecker.org/manual/ThePRISMLanguage/ModulesAndVariables
	private static String[] prismReserved = { "A", "bool", "clock", "const",
			"ctmc", "C", "double", "dtmc", "E", "endinit", "endinvariant",
			"endmodule", "endrewards", "endsystem", "false", "formula",
			"filter", "func", "F", "global", "G", "init", "invariant", "I",
			"int", "label", "max", "mdp", "min", "module", "X",
			"nondeterministic", "Pmax", "Pmin", "P", "probabilistic", "prob",
			"pta", "rate", "rewards", "Rmax", "Rmin", "R", "S", "stochastic",
			"system", "true", "U", "W" };

	private String avoidPRISMNames(String keyword) {
		if (keyword == null) {
			return keyword;
		}
		if (keyword.length() == 1) {
			return "_" + keyword;
		}

		for (String reserved : prismReserved) {
			if (keyword.compareToIgnoreCase(reserved) == 0) {
				return "_" + keyword;
			}
		}
		return keyword;
	}

	/**
	 * Export the graph to a PRISM model description
	 * 
	 * @param config
	 *            the user defined parameters with the model
	 * @param out
	 *            the writer receiving the encoded model description
	 */
	public void write(PRISMConfig config, Writer out) throws IOException,
			GsException {

		out.write("dtmc\n");

		LogicalModel model = config.getModel();
		List<NodeInfo> coreNodes = model.getNodeOrder();

		// Nodes actual logical rules
		int[] kMDDs = model.getLogicalFunctions();
		for (int i = 0; i < coreNodes.size(); i++) {
			NodeInfo node = coreNodes.get(i);
			out.write("\nmodule M_" + node.getNodeID());
			if (node.isInput()) out.write(" // Input variable");
			out.write("\n");
			out.write("  " + node.getNodeID() + " : [0.." + node.getMax()
					+ "];\n");
			// TODO init x!
			out.write("\n");
			out.write("  [] " + node.getNodeID() + "_focal>" + node.getNodeID()
					+ " & " + node.getNodeID() + "<" + +node.getMax() + " -> ("
					+ node.getNodeID() + "'=" + node.getNodeID() + "+1);\n");
			out.write("  [] " + node.getNodeID() + "_focal<" + node.getNodeID()
					+ " & " + node.getNodeID() + ">0 -> (" + node.getNodeID()
					+ "'=" + node.getNodeID() + "-1);\n");
			out.write("endmodule\n");
			nodeRules2PRISM(out, model, kMDDs[i], coreNodes, node);
		}
	}

	private void nodeRules2PRISM(Writer out, LogicalModel model, int nodeMDD,
			List<NodeInfo> coreNodeOrder, NodeInfo node) throws IOException {
		PathSearcher searcher = new PathSearcher(model.getMDDManager(), 1,
				node.getMax());
		int[] path = searcher.getPath();
		searcher.setNode(nodeMDD);

		int leafValue = 0;
		out.write("formula " + node.getNodeID() + "_focal = \n");
		String s = "";
		for (int l : searcher) {
			boolean bWrite = false;
			for (int i = 0; i < path.length; i++) {
				if (path[i] != -1) {
					if (!bWrite)
						s += "  (";
					if (bWrite)
						s += " & ";
					s += avoidPRISMNames(coreNodeOrder.get(i).getNodeID())
							+ "=" + path[i];
					bWrite = true;
				}
			}
			if (s.isEmpty()) {
				s += "  (true";
				leafValue = l;
			}
			s += ")? " + l + " : \n";
		}
		out.write(s);
		out.write("  " + leafValue + ";\n");
	}
}