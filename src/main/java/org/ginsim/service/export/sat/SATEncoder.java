package org.ginsim.service.export.sat;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.tool.booleanize.Booleanizer;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.PathSearcher;
import org.colomoto.mddlib.operators.MDDBaseOperators;
import org.ginsim.common.application.GsException;

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
			// (A <-> B) »» not (~A.~Ka | A.Ka) »» (~A.Ka | A.~Ka)
			// int combinedMDD = MDDBaseOperators.OR.combine(manager,
			// MDDBaseOperators.AND.combine(manager, varFalse, kVtrue),
			// MDDBaseOperators.AND.combine(manager, varTrue, kVfalse));
			// (A -> Ka) »» A & ~Ka
			// int combinedMDD = MDDBaseOperators.OR.combine(manager, varTrue,
			// kVfalse);

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
		out.write("c CNF representation of a logical model exported by GINsim\n");
		// for Intervention exports, add the number of "core" variables
		out.write("p cnf "
				+ (coreNodes.size() + (config.isIntervention() ? iNonInputs : 0))
				+ " " + nSATrules + "\n");
		out.write(sb.toString());
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