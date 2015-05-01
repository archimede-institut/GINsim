package org.ginsim.service.export.sat;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.tool.booleanize.Booleanizer;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.PathSearcher;
import org.colomoto.mddlib.operators.MDDBaseOperators;
import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;

/**
 * Exports a Regulatory graph into a SAT model description.
 * 
 * @author Pedro T. Monteiro
 */
public class SATEncoder {

	/**
	 * Export the graph to a SAT model description
	 * 
	 * @param config
	 *            the user defined parameters with the model
	 * @param out
	 *            the writer receiving the encoded model description
	 */
	public void write(SATConfig config, Writer out) throws IOException,
			GsException {

		LogicalModel multiValueModel = config.getModel();
		LogicalModel model = Booleanizer.booleanize(multiValueModel);

		List<NodeInfo> coreNodes = model.getNodeOrder();
		// Nodes actual logical rules
		int[] kMDDs = model.getLogicalFunctions();
		MDDManager manager = model.getMDDManager();
		MDDVariable[] MDDVars = manager.getAllVariables();

		StringBuffer sb = new StringBuffer();

		int iNonInputs = 0;
		int nSATrules = 0;
		for (int i = 0; i < coreNodes.size(); i++) {
			NodeInfo node = coreNodes.get(i);
			sb.append("c " + (node.isInput() ? "input" : "core") + " var["
					+ (i + 1) + "] " + node.getNodeID() + "\n");
			if (node.isInput())
				continue;
			else
				iNonInputs++;
			
			int varTrue = MDDVars[i].getNode(0, 1);
			int varFalse = MDDVars[i].getNode(1, 0);
			int kVtrue = kMDDs[i];
			int kVfalse = manager.not(kVtrue);

			// (Ka -> A) »» not (~Ka | A) »» ~A.Ka
			int combinedMDD = MDDBaseOperators.AND.combine(manager, varFalse,
					kVtrue);
			nSATrules += nodeRules2SAT(config, sb, model, combinedMDD,
					coreNodes, i + 1, coreNodes.size() + iNonInputs);

			// (~Ka -> ~A) »» not (Ka | ~A) »» A.~Ka
			combinedMDD = MDDBaseOperators.AND.combine(manager, varTrue,
					kVfalse);
			nSATrules += nodeRules2SAT(config, sb, model, combinedMDD,
					coreNodes, i + 1, coreNodes.size() + iNonInputs);
		}

		// Write individual variable restrictions
		if (!config.getInputState().keySet().isEmpty()
				|| !config.getInitialState().isEmpty()) {
			sb.append("c user variable restriction\n");
			nSATrules += varRestr2SAT(model.getNodeOrder(), config
					.getInputState().keySet().iterator(), sb);
			nSATrules += varRestr2SAT(model.getNodeOrder(), config
					.getInitialState().keySet().iterator(), sb);
		}

		out.write("c CNF representation of a logical model exported by GINsim\n");
		// for Intervention exports, add the number of "core" variables
		out.write("p cnf "
				+ (coreNodes.size() + (config.isIntervention() ? iNonInputs : 0))
				+ " " + nSATrules + "\n");
		out.write(sb.toString());
	}

	private int varRestr2SAT(List<NodeInfo> nodeOrder,
			Iterator<NamedState> iter, StringBuffer sb) {
		int nSATrules = 0;
		if (!iter.hasNext())
			return nSATrules;
		// Assumes only ONE selected state
		NamedState iState = iter.next();
		Map<NodeInfo, List<Integer>> m_states = iState.getMap();
		for (int i = 0; i < nodeOrder.size(); i++) {
			List<Integer> v = m_states.get(nodeOrder.get(i));
			if (v != null && v.size() > 0) {
				// Assumes that all models have been Booleanized !
				sb.append((v.get(0) > 0 ? "" : "-") + (i + 1) + " 0\n");
				nSATrules++;
			}
		}
		return nSATrules;
	}

	private int nodeRules2SAT(SATConfig config, StringBuffer sb,
			LogicalModel model, int nodeMDD, List<NodeInfo> coreNodeOrder,
			int satVar, int intervVar) throws IOException {
		PathSearcher searcher = new PathSearcher(model.getMDDManager(), 1, 1);
		int[] path = searcher.getPath();
		searcher.setNode(nodeMDD);

		int nSATrules = 0;
		for (int l : searcher) {
			boolean bWrite = false;
			String s = config.isIntervention() ? intervVar + " " : "";
			for (int i = 0; i < path.length; i++) {
				if (path[i] != -1) {
					if (bWrite)
						s += " ";
					s += ((path[i] == 1) ? "-" : "") + (i + 1);
					bWrite = true;
				}
			}
			nSATrules++;
			sb.append(s + " 0\n");
		}
		// sb.append("\n");
		return nSATrules;
	}
}