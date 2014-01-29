package org.ginsim.service.export.avatar;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;

/**
 * Exports a GINsim Regulatory graph into a AVATAR model description.
 * 
 * @author Pedro T. Monteiro
 */
public class AvatarEncoder {

	private static String[] avatarReserved = { "MODULE", "DEFINE", "MDEFINE",
			"CONSTANTS", "VAR", "IVAR", "FROZENVAR", "INIT", "TRANS", "INVAR",
			"SPEC", "CTLSPEC", "LTLSPEC", "PSLSPEC", "COMPUTE", "NAME",
			"INVARSPEC", "FAIRNESS", "JUSTICE", "COMPASSION", "ISA", "ASSIGN",
			"CONSTRAINT", "SIMPWFF", "CTLWFF", "LTLWFF", "PSLWFF", "COMPWFF",
			"IN", "MIN", "MAX", "MIRROR", "PRED", "PREDICATES", "process",
			"array", "of", "boolean", "integer", "real", "word", "word1",
			"bool", "signed", "unsigned", "extend", "resize", "sizeof",
			"uwconst", "swconst", "EX", "AX", "EF", "AF", "EG", "AG", "E", "F",
			"O", "G", "H", "X", "Y", "Z", "A", "U", "S", "V", "T", "BU", "EBF",
			"ABF", "EBG", "ABG", "case", "esac", "mod", "next", "init",
			"union", "in", "xor", "xnor", "self", "TRUE", "FALSE", "count" };

	private String avoidAvatarNames(String keyword) {
		if (keyword == null) {
			return keyword;
		}
		if (keyword.length() == 1) {
			return "_" + keyword;
		}

		for (String reserved : avatarReserved) {
			if (keyword.compareToIgnoreCase(reserved) == 0) {
				return "_" + keyword;
			}
		}
		return keyword;
	}

	/**
	 * Export the graph to a AVATAR file
	 * 
	 * @param graph
	 *            the regulatory graph to export
	 * @param nodes
	 *            the list of nodes that must be exported
	 * @param edges
	 *            the list of edges that must be exported
	 * @param out
	 *            the writer receiving the encoded model description
	 */
	public void write(AvatarConfig config, Writer out) throws IOException,
			GsException {

		LogicalModel model = config.getModel();
		List<NodeInfo> coreNodes = model.getNodeOrder();
		List<NodeInfo> outputNodes = model.getExtraComponents();
		if (coreNodes.isEmpty() && outputNodes.isEmpty()) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"AVATAR does not support empty graphs");
		}
		if (!hasCoreNodes(coreNodes)) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"AVATAR needs at least one core (non-input/non-output) node");
		}

		DateFormat dateformat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG);
		out.write("-- " + dateformat.format(new Date()) + "\n");
		out.write("-- GINsim export for Avatar/Firefront\n");
		out.write("-- Inspired on the NuSMV v2.1+ syntax\n");

		NodeInfo[] aNodeOrder = new NodeInfo[coreNodes.size()];
		boolean hasInputVars = false;
		for (int i = 0; i < aNodeOrder.length; i++) {
			NodeInfo node = model.getNodeOrder().get(i);
			aNodeOrder[i] = node;
			if (node.isInput())
				hasInputVars = true;
		}

		if (hasInputVars) {
			out.write("\nIVAR");
			out.write("\n-- Input variables declaration\n");
			for (int i = 0; i < coreNodes.size(); i++) {
				if (coreNodes.get(i).isInput()) {
					String s_levels = "0";
					for (int j = 1; j <= coreNodes.get(i).getMax(); j++)
						s_levels += ", " + j;
					out.write("  "
							+ avoidAvatarNames(coreNodes.get(i).getNodeID())
							+ " : { " + s_levels + "};\n");
				}
			}
		}

		out.write("\nVAR");
		out.write("\n-- State variables declaration\n");
		for (int i = 0; i < coreNodes.size(); i++) {
			if (coreNodes.get(i).isInput())
				continue;
			String s_levels = "0";

			for (int j = 1; j <= coreNodes.get(i).getMax(); j++)
				s_levels += ", " + j;

			out.write("  " + avoidAvatarNames(coreNodes.get(i).getNodeID())
					+ " : {" + s_levels + "};\n");
		}

		out.write("\nDEFINE\n");

		// Nodes actual logical rules
		out.write("-- Variable next level regulation\n");
		int[] kMDDs = model.getLogicalFunctions();
		for (int i = 0; i < coreNodes.size(); i++) {
			NodeInfo node = coreNodes.get(i);
			if (node.isInput())
				continue;
			out.write(avoidAvatarNames(node.getNodeID()) + "_focal :=\n");
			out.write("  case\n");
			nodeRules2Avatar(out, model, kMDDs[i], coreNodes, node);
			out.write("  esac;\n");
		}
		out.write("\n");

		out.write("-- Declaration of output variables\n");
		if (outputNodes.size() > 0) {
			kMDDs = model.getExtraLogicalFunctions();
			for (int i = 0; i < outputNodes.size(); i++) {
				NodeInfo node = outputNodes.get(i);
				out.write(avoidAvatarNames(node.getNodeID()) + " :=\n");
				out.write("  case\n");
				nodeRules2Avatar(out, model, kMDDs[i], coreNodes, node);
				out.write("  esac;\n");
			}
		} else {
			out.write("-- Empty !\n");
		}
		out.write("\n");

		// Initial States definition
		out.write("-- Declaration of core variables restriction list\n");
		out.write(writeStateList(aNodeOrder, config.getInitialState().keySet()
				.iterator(), false));
		out.write("-- Declaration of input variables restriction list\n");
		out.write(writeStateList(aNodeOrder, config.getInputState().keySet()
				.iterator(), true));
	}

	private void nodeRules2Avatar(Writer out, LogicalModel model, int nodeMDD,
			List<NodeInfo> coreNodeOrder, NodeInfo node) throws IOException {
		PathSearcher searcher = new PathSearcher(model.getMDDManager(), 1,
				node.getMax());
		int[] path = searcher.getPath();
		searcher.setNode(nodeMDD);

		int leafValue = 0;
		String s = "";
		for (int l : searcher) {
			boolean bWrite = false;
			for (int i = 0; i < path.length; i++) {
				if (path[i] != -1) {
					if (!bWrite)
						s += "    ";
					if (bWrite)
						s += " & ";
					s += "("
							+ avoidAvatarNames(coreNodeOrder.get(i).getNodeID())
							+ " = " + path[i] + ")";
					bWrite = true;
				}
			}
			if (!s.isEmpty()) {
				s += " : " + l + ";\n";
			} else {
				leafValue = l;
			}
		}
		out.write(s);
		out.write("    TRUE : " + leafValue + ";\n");
	}

	private String writeStateList(NodeInfo[] t_vertex,
			Iterator<NamedState> iter, boolean input) {
		StringBuffer sb = new StringBuffer();
		if (!iter.hasNext())
			sb.append("-- Empty !\n");
		else {
			while (iter.hasNext()) {
				NamedState iState = iter.next();
				Map<NodeInfo, List<Integer>> m_states = iState.getMap();
				String s_init = "";

				for (int i = 0; i < t_vertex.length; i++) {
					if (input != t_vertex[i].isInput()) {
						continue;
					}
					List<Integer> v = m_states.get(t_vertex[i]);
					if (v != null && v.size() > 0) {
						s_init += "INIT ";
						for (int j = 0; j < v.size(); j++) {
							if (j > 0)
								s_init += " | ";
							s_init += avoidAvatarNames(t_vertex[i].getNodeID())
									+ "=" + v.get(j);
						}
						s_init += ";\n";
					} else {
						s_init += "-- INIT ";
						s_init += avoidAvatarNames(t_vertex[i].getNodeID())
								+ "=0";
						s_init += ";\n";
					}
				}
				sb.append(s_init).append("\n");
			}
		}
		return sb.toString();
	}

	private boolean hasCoreNodes(List<NodeInfo> nodes) {
		boolean hasCore = false;
		if (nodes == null)
			return hasCore;
		for (NodeInfo node : nodes) {
			if (!node.isInput()) {
				hasCore = true;
				break;
			}
		}
		return hasCore;
	}
}
