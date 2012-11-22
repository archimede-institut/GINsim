package org.ginsim.service.export.cadp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/**
 * Class that produces the LOTOS NT specification of a single Logical Regulatory
 * Module
 * 
 * @author Nuno D. Mendes
 * 
 */
public class CADPModuleWriter extends CADPWriter {

	private String index = ""; // to be used when different models are mixed
	private Set<String> concreteProcessSignature = new HashSet<String>();
	private int numberInstances = this.getNumberInstances();
	private String modelName = this.getModelName();
	private CADPWriter.InitialStateWriter initialStateWriter = this
			.getInitialStateWriter();
	private CADPWriter.GateWriter gateWriter = this.getGateWriter();
	private CADPWriter.StateVarWriter stateVarWriter = this.getStateVarWriter();

	public CADPModuleWriter(CADPExportConfig config, String index) {
		super(config);
		this.index = index;
	}

	public CADPModuleWriter(CADPExportConfig config) {
		super(config);
	}

	public String toString() {

		RuleWriter ruleWriter = new RuleWriter(this.getAllComponents(),
				stateVarWriter);
		FunctionWriter functionWriter = new FunctionWriter(
				this.getAllComponents(), stateVarWriter);

		String output = "";
		output += "module " + modelName + "(common) is\n\n";

		for (int moduleId = 1; moduleId <= numberInstances; moduleId++) {
			String processName = this.concreteProcessName(moduleId);
			if (concreteProcessSignature.contains(processName))
				continue;
			else
				concreteProcessSignature.add(processName);

			output += "process " + processName + "[" + gateWriter.typedList()
					+ "] is\n";
			output += "\t" + this.formalProcessName(index) + "["
					+ gateWriter.simpleList() + "]("
					+ initialStateWriter.typedSimpleList(moduleId) + ")\n";
			output += "end process\n\n";

		}

		output += "process " + this.formalProcessName(index) + "["
				+ gateWriter.typedList() + "](" + stateVarWriter.typedList()
				+ ") is\n";
		output += "\tloop\n";
		output += "\t\tselect\n";
		output += "\t\t\t" + ruleWriter + "\n";
		output += "\t\tend select\n";
		output += "\tend loop\n";
		output += "end process\n\n";

		output += functionWriter;

		return output;
	}

	public class RuleWriter {

		List<RegulatoryNode> listNodes = null;
		StateVarWriter stateVarWriter = null;

		public RuleWriter(List<RegulatoryNode> listNodes,
				StateVarWriter stateVarWriter) {
			this.listNodes = listNodes;
			this.stateVarWriter = stateVarWriter;

		}

		public String toString() {
			String out = "";

			out += "\t\t\t" + "Stable_State["
					+ CADPWriter.getStableActionName() + "](is_Stable("
					+ stateVarWriter.simpleList() + "))\n";

			for (RegulatoryNode node : listNodes) {
				String modifier = node.getMaxValue() > 1 ? "M" : "B";
				String gate = CADPWriter.node2Gate(node);
				String stateVar = CADPWriter.node2StateVar(node);

				if (node.isInput()) {
					out += "\t\t[]\n";
					out += "\t\t\tInput_Regulator" + modifier + "[" + gate
							+ "] (!?" + stateVar + ")\n";
				} else {
					for (int v = 0; v <= node.getMaxValue(); v++) {
						out += "\t\t[]\n";
						out += "\t\t\tProperRegulator" + modifier + "[" + gate
								+ "]" + "(focal_" + gate + "("
								+ stateVarWriter.simpleList() + ") ==" + v
								+ modifier + "), !?" + stateVar + "," + v
								+ modifier + ")\n";
					}
				}
			}

			out += "\n";

			return out;
		}
	}

	public class FunctionWriter {

		List<RegulatoryNode> listNodes = null;
		StateVarWriter stateVarWriter = null;

		public FunctionWriter(List<RegulatoryNode> listNodes,
				StateVarWriter stateVarWriter) {
			this.listNodes = listNodes;
			this.stateVarWriter = stateVarWriter;

		}

		public String toString() {

			String out = "";

			LogicalModel model = getModel();

			String stableCondition = "";
			int nodeIndex = 0;
			for (RegulatoryNode node : listNodes) {
				PathSearcher searcher = new PathSearcher(model.getMDDManager(),
						1, node.getMaxValue());
				int path[] = searcher.getPath();
				int kMDDs[] = model.getLogicalFunctions();
				searcher.setNode(kMDDs[nodeIndex]);

				String modifier = node.getMaxValue() > 1 ? "M" : "B";

				if (!node.isInput()) {
					out += "function focal_" + CADPWriter.node2Gate(node) + "("
							+ stateVarWriter.typedList() + ") :"
							+ (node.getMaxValue() > 1 ? "Multi" : "Binary")
							+ " is\n";

					String[] conditionArray = new String[node.getMaxValue() + 1];
					// we make space for the unused v=0 condition to simplify
					// syntax in subsequent code

					for (int value : searcher) {

						String conjunctiveTerm = "";
						for (int i = 0; i < path.length; i++)
							if (path[i] != -1) {
								if (!conjunctiveTerm.isEmpty())
									conjunctiveTerm += " and ";
								conjunctiveTerm += "("
										+ CADPWriter.node2StateVar(listNodes
												.get(i))
										+ " == "
										+ path[i]
										+ (listNodes.get(i).getMaxValue() > 1 ? "M"
												: "B") + ")";

							}
						if (!conditionArray[value].isEmpty())
							conditionArray[value] += " or ";
						conditionArray[value] += "(" + conjunctiveTerm + ")";

					}

					boolean failSafe = true;
					for (int v = 1; v < conditionArray.length; v++) {
						if (conditionArray[v].isEmpty()) {
							out += "\t return (" + v + modifier + ")\n";
							failSafe = false;
							break;
						} else {
							String construct = "if";
							if (v > 1)
								construct = "elsif";
							out += "\t" + construct + "(" + conditionArray[v]
									+ ") then\n";
							out += "\t\treturn (" + v + modifier + ")\n";
						}

					}

					if (failSafe) {
						out += "\telse\n";
						out += "\t\t return (0" + modifier + ")\n";
					}

					out += "\tend if\n";
					out += "end function\n\n";

					// Builds stable condition for is_Stable
					if (!stableCondition.isEmpty())
						stableCondition += " and ";

					stableCondition += "(focal_" + CADPWriter.node2Gate(node)
							+ "(" + stateVarWriter.simpleList() + ") == "
							+ CADPWriter.node2StateVar(node) + ")";
				}
				nodeIndex++;

			}

			out += "function is_Stable(" + stateVarWriter.typedList()
					+ ") : Bool is\n";
			out += "\t if (" + stableCondition + ") then\n";
			out += "\t\treturn (true)\n";
			out += "\telse\n";
			out += "\t\treturn (false)\n";
			out += "\tend if\n";
			out += "end function\n\n";

			return out;
		}

	}

}
