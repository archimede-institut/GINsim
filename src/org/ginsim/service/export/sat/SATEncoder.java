package org.ginsim.service.export.sat;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

/**
 * Exports a GINsim Regulatory graph into a SAT model description.
 * 
 * @author Pedro T. Monteiro
 */
public class SATEncoder {
	/**
	 * Export the graph to a SVG file
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
	public void write(SATConfig config, Writer out) throws IOException {

		DateFormat dateformat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG);
		out.write("c " + dateformat.format(new Date()) + "\n");
		out.write("c GINsim implicit representation for SAT\n");
		out.write("c Variables are considered to be multi-valued\n");

		RegulatoryGraph graph = config.getGraph();
		List<RegulatoryNode> nodeOrder = graph.getNodeOrder();
		String[] t_regulators = new String[nodeOrder.size()];
		RegulatoryNode[] t_vertex = new RegulatoryNode[nodeOrder.size()];
		for (int i = 0; i < t_vertex.length; i++) {
			RegulatoryNode node = nodeOrder.get(i);
			t_vertex[i] = node;
			t_regulators[i] = node.getId();
		}
		OMDDNode[] t_tree = graph.getAllTrees(true);

		int clauses = 0, vars = 0;

		StringBuffer sb = new StringBuffer();
		sb.append("\n\nc Declaration of variables and \"at least one\" valuation:");
		for (int i = 0; i < t_vertex.length; i++) {
			sb.append("\nc ").append(t_regulators[i]).append(": [0-");
			sb.append(t_vertex[i].getMaxValue()).append("] -> [");
			sb.append(getSATVariable(t_vertex, i, 0)).append("...");
			sb.append(getSATVariable(t_vertex, i, t_vertex[i].getMaxValue()))
					.append("]\n");
			for (int j = 0; j <= t_vertex[i].getMaxValue(); j++) {
				vars++;
				sb.append(getSATVariable(t_vertex, i, j)).append(" ");
			}
			sb.append("0");
			clauses++;
		}

		sb.append("\n\nc Internal variable domain restrictions: ");
		Iterator<InitialState> it = config.getInitialState().keySet()
				.iterator();
		if (it.hasNext()) {
			Map<RegulatoryNode, List<Integer>> m_states = (it.hasNext()) ? it
					.next().getMap()
					: new HashMap<RegulatoryNode, List<Integer>>();
			clauses += writeInitialState(sb, t_vertex, false, m_states);
		} else {
			sb.append("Not specified!");
		}
		sb.append("\nc Input variable domain restrictions: ");
		it = config.getInputState().keySet().iterator();
		if (it.hasNext()) {
			Map<RegulatoryNode, List<Integer>> m_states = (it.hasNext()) ? it
					.next().getMap()
					: new HashMap<RegulatoryNode, List<Integer>>();
			clauses += writeInitialState(sb, t_vertex, true, m_states);
		} else {
			sb.append("Not specified!");
		}

		sb.append("\n\nc Declaration of the model regulatory rules:");
		int[] t_cst = new int[t_vertex.length];
		for (int i = 0; i < t_vertex.length; i++) {
			sb.append("\nc ").append(t_regulators[i]).append(" rules");
			// TODO: verify if continues...
			if (t_vertex[i].isInput())
				continue;
			for (int j = 0; j < t_cst.length; j++)
				t_cst[j] = -1;
			clauses += node2SAT(t_tree[i], sb, t_vertex, t_cst, i);
		}

		sb.append("\n\nc Export type ");
		boolean bType1 = (config.getExportType() == SATConfig.CFG_FIX_POINT);
		if (bType1) {
			sb.append("1: Search fix point attractors");
			for (int i = 0; i < t_vertex.length; i++) {
				sb.append("\nc ").append(t_regulators[i]);
				sb.append(": exactly one valuation");
				for (int j = 0; j <= t_vertex[i].getMaxValue(); j++) {
					for (int k = j + 1; k <= t_vertex[i].getMaxValue(); k++) {
						sb.append("\n-").append(getSATVariable(t_vertex, i, j));
						sb.append(" -").append(getSATVariable(t_vertex, i, k));
						sb.append(" 0");
						clauses++;
					}
				}
			}
		} else {
			sb.append("2: Search complex attractors");
			for (int i = 0; i < t_vertex.length; i++) {
				sb.append("\nc ").append(t_regulators[i]);
				sb.append(": no restriction on valuations");
			}
		}
		sb.append("\n");

		out.write("\np cnf " + vars + " " + clauses);
		out.write(sb.toString());
	}

	// TODO: not efficient at all!
	private int getSATVariable(RegulatoryNode[] t_nodes, int nodeId, int value) {
		int iRet = 0;
		for (int i = 0; i < t_nodes.length; i++) {
			if (i == nodeId) {
				iRet += value + 1; // In the multi-valued case '0' is a SATvar
				break;
			}
			iRet += t_nodes[i].getMaxValue() + 1;
		}
		return iRet;
	}

	private int node2SAT(OMDDNode node, StringBuffer sb,
			RegulatoryNode[] t_names, int[] t_cst, int nodeID)
			throws IOException {
		int clauses = 0;
		if (node.next == null) {
			String s = "";
			for (int i = 0; i < t_cst.length; i++) {
				if (t_cst[i] != -1) {
					s += "-" + getSATVariable(t_names, i, t_cst[i]) + " ";
				}
			}
			sb.append("\n" + s + getSATVariable(t_names, nodeID, node.value)
					+ " 0");
			return 1;
		}
		for (int i = 0; i < node.next.length; i++) {
			t_cst[node.level] = i;
			clauses += node2SAT(node.next[i], sb, t_names, t_cst, nodeID);
		}
		t_cst[node.level] = -1;
		return clauses;
	}

	private int writeInitialState(StringBuffer sb, RegulatoryNode[] t_vertex, boolean bInput,
			Map<RegulatoryNode, List<Integer>> mInitStates) {
		int clauses = 0;
		for (int i = 0; i < t_vertex.length; i++) {
			if (t_vertex[i].isInput() != bInput)
				continue;
			String s_init = "";
			List<Integer> v = mInitStates.get(t_vertex[i]);
			if (v != null && v.size() > 0) {
				for (int j = 0; j < v.size(); j++) {
					s_init += getSATVariable(t_vertex, i, j) + " ";
				}
			}
			if (!s_init.isEmpty()) {
				sb.append("\n").append(s_init).append("0");
				clauses++;
			}
		}
		return clauses;
	}
}
