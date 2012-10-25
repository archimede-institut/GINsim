package org.ginsim.service.export.cadp;

import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

public class CADPModuleWriter {

	private CADPExportConfig config;
	String index = "";

	public CADPModuleWriter(CADPExportConfig config, String index) {
		this.config = config;
		this.index = index;
	}

	public CADPModuleWriter(CADPExportConfig config) {
		this.config = config;
	}

	public String toString() {

		int numberInstances = config.getTopology().getNumberInstances();
		String modelName = config.getGraph().getGraphName();
		InitialStateWriter initialStateWriter = new InitialStateWriter(
				config.getInitialStates(), config.getGraph().getNodeOrder());
		GateWriter gateWriter = new GateWriter(config.getGraph().getNodeOrder());
		StateVarWriter stateVarWriter = new StateVarWriter(config.getGraph()
				.getNodeOrder());
		RuleWriter ruleWriter = new RuleWriter(
				config.getGraph().getNodeOrder(), stateVarWriter);

		String output = "";

		// TODO modelName cannot contain "." make sure this does not happen
		output += "module " + modelName + "(common) is\n\n";

		for (int moduleId = 1; moduleId <= numberInstances; moduleId++) {
			String processName = "openLRG_"
					+ initialStateWriter.typedStateConcat(moduleId);

			output += "process " + processName + "[" + gateWriter.typedList()
					+ "] is\n";
			output += "\tLogicModule" + index + "[" + gateWriter.simpleList()
					+ "](" + initialStateWriter.simpleList(moduleId) + ")\n";
			output += "end process\n\n";

		}

		output += "process LogicModule" + index + "[" + gateWriter.typedList()
				+ "](" + stateVarWriter.typedList() + ") is\n";
		output += "\tloop\n";
		output += "\t\tselect\n";
		output += "\t\t\t" + ruleWriter + "\n";
		output += "\t\tend select\n";
		output += "\tend loop\n";
		output += "end process\n\n";

		return output;
	}

	public class InitialStateWriter {
		// TODO: compute correct initial state for mapped input variables
		List<byte[]> initialStates = null;
		List<RegulatoryNode> listNodes = null;

		public InitialStateWriter(List<byte[]> initialStates,
				List<RegulatoryNode> listNodes) {
			this.initialStates = initialStates;
			this.listNodes = listNodes;
		}

		public String simpleList(int moduleId) {
			byte[] initialState = initialStates.get(moduleId);
			String out = "";
			for (int i = 0; i < initialState.length; i++) {
				if (i > 0)
					out += ",";

				out += initialState[i];
			}

			return out;
		}

		public String typedStateConcat(int moduleId) {
			byte[] initialState = initialStates.get(moduleId);
			String out = "";
			for (int i = 0; i < initialState.length; i++) {
				RegulatoryNode node = listNodes.get(i);
				String modifier = node.getMaxValue() > 1 ? "M" : "B";
				out += initialState[i] + modifier;
			}

			return out;

		}
	}

	public class GateWriter {
		List<RegulatoryNode> listNodes = null;

		public GateWriter(List<RegulatoryNode> listNodes) {
			this.listNodes = listNodes;

		}

		public String simpleList() {
			String out = "STABLE";
			for (RegulatoryNode node : listNodes) {
				out += "," + node.getNodeInfo().getNodeID().toUpperCase();
			}

			return out;
		}

		public String typedList() {
			String out = "STABLE:None";
			for (RegulatoryNode node : listNodes) {
				String type = node.getMaxValue() > 1 ? "Multi" : "Binary";
				out += "," + node.getNodeInfo().getNodeID().toUpperCase() + ":"
						+ type;

			}

			return out;

		}
	}

	public class StateVarWriter {
		List<RegulatoryNode> listNodes = null;

		public StateVarWriter(List<RegulatoryNode> listNodes) {
			this.listNodes = listNodes;
		}

		public String simpleList() {
			String out = "";
			for (RegulatoryNode node : listNodes) {

				if (!out.isEmpty())
					out += ",";

				out += node.getNodeInfo().getNodeID().toLowerCase();
			}

			return out;
		}

		public String typedList() {
			String out = "";
			for (RegulatoryNode node : listNodes) {

				if (!out.isEmpty())
					out += ",";

				out += node.getNodeInfo().getNodeID().toLowerCase() + ":"
						+ (node.getMaxValue() > 1 ? "Multi" : "Binary");

			}

			return out;
		}

	}

	public class RuleWriter {

		// TODO: create all the rules
		List<RegulatoryNode> listNodes = null;
		StateVarWriter stateVarWriter = null;

		public RuleWriter(List<RegulatoryNode> listNodes,
				StateVarWriter stateVarWriter) {
			this.listNodes = listNodes;
			this.stateVarWriter = stateVarWriter;

		}

		public String toString() {
			String out = "";

			out += "\t\t\t" + "Stable_State[STABLE](is_Stable("
					+ stateVarWriter.simpleList() + "))\n";

			for (RegulatoryNode node : listNodes) {
				String modifier = node.getMaxValue() > 1 ? "M" : "B";
				String gate = node.getNodeInfo().getNodeID().toUpperCase();
				String stateVar = node.getNodeInfo().getNodeID().toLowerCase();

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

			LogicalModel model = config.getGraph().getModel();

			String stableCondition = "";
			int nodeIndex = 0;
			for (RegulatoryNode node : listNodes) {
				PathSearcher searcher = new PathSearcher(model.getMDDManager(),
						1, node.getMaxValue());
				int path[] = searcher.getPath();
				int kMDDs[] = model.getLogicalFunctions();
				searcher.setNode(kMDDs[nodeIndex]);

				String modifier = node.getMaxValue() > 1 ? "M" : "B";

				// TODO: complete when NuSMVEncoder is finally working
				if (!node.isInput()) {
					out += "function focal_"
							+ node.getNodeInfo().getNodeID().toUpperCase()
							+ "(" + stateVarWriter.typedList() + ") :"
							+ (node.getMaxValue() > 1 ? "Multi" : "Binary")
							+ " is\n";

					int returnValue = 0;

					// There are some paths
					String[] conditionArray = new String[node.getMaxValue()];

					for (int value : searcher) {

						String conjunctiveTerm = "";
						for (int i = 0; i < path.length; i++)
							if (path[i] != -1) {
								if (!conjunctiveTerm.isEmpty())
									conjunctiveTerm += " and ";
								conjunctiveTerm += "("
										+ listNodes.get(i).getNodeInfo()
												.getNodeID().toLowerCase()
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
					for (int v = 1; v < conditionArray.length; v++){
						if (conditionArray[v].isEmpty()){
							out += "\t return (" + v + modifier +")\n";
							failSafe = false;
							break;
						} else {
							String construct = "if";
							if (v > 1)
								construct = "elsif";
							out += "\t" + construct + "(" + conditionArray[v] + ") then\n";
							out += "\t\treturn (" + v + modifier + ")\n";
						}
					
					}
					
					if (failSafe){
						out += "\telse\n";
						out += "\t\t return (0" + modifier +")\n";
					}
					
					out += "\tend if\n";
					out += "end function\n\n";
					
					
					// Builds stable condition for is_Stable
					if (!stableCondition.isEmpty())
						stableCondition += " and ";

					stableCondition += "(focal_"
							+ node.getNodeInfo().getNodeID().toUpperCase()
							+ "(" + stateVarWriter.simpleList() + ") == "
							+ node.getNodeInfo().getNodeID().toLowerCase()
							+ ")";
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
