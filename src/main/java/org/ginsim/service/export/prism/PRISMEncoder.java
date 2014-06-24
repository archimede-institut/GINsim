package org.ginsim.service.export.prism;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;

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

		LogicalModel model = config.getModel();
		List<NodeInfo> coreNodes = model.getNodeOrder();
		// Nodes actual logical rules
		int[] kMDDs = model.getLogicalFunctions();

		out.write("dtmc\n");
		for (int i = 0; i < coreNodes.size(); i++) {
			NodeInfo node = coreNodes.get(i);
			out.write("\nmodule M_" + node.getNodeID());
			if (node.isInput())
				out.write(" // Input variable");
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

		Set<NamedState> sInputState = config.getInputState().keySet();
		Set<NamedState> sInitialState = config.getInitialState().keySet();
		if (sInputState != null && sInputState.iterator().hasNext()
				|| sInitialState != null && sInitialState.iterator().hasNext()) {
			boolean first = true;
			out.write("\n// Initial states\ninit");
			first = this.writeInitialConds(out, sInputState, first);
			first = this.writeInitialConds(out, sInitialState, first);
			out.write("\nendinit\n");
		}
	}

	private boolean writeInitialConds(Writer out, Set<NamedState> sNamedState,
			boolean first) throws IOException {
		if (sNamedState != null && sNamedState.iterator().hasNext()) {
			Map<NodeInfo, List<Integer>> mStateComp = sNamedState.iterator()
					.next().getMap();
			for (NodeInfo node : mStateComp.keySet()) {
				if (!first)
					out.write(" &");
				List<Integer> vList = mStateComp.get(node);
				if (vList != null && vList.size() > 0) {
					out.write("\n  (");
					for (int j = 0; j < vList.size(); j++) {
						if (j > 0)
							out.write(" | ");
						out.write(node.getNodeID() + "=" + vList.get(j));
					}
					out.write(")");
					first = false;
				}
			}
		}
		return first;
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