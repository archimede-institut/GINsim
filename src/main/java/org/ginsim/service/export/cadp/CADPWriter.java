package org.ginsim.service.export.cadp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.composition.IntegrationFunction;

public abstract class CADPWriter {

	private CADPExportConfig config = null;

	public CADPWriter(CADPExportConfig config) {
		this.config = config;
	}

	public List<RegulatoryNode> getAllComponents() {
		return this.config.getGraph().getNodeOrder();
	}

	public static String getStableActionName() {
		return "STABLE";
	}

	public static String node2Gate(RegulatoryNode node, int moduleId) {
		return node.getNodeInfo().getNodeID().toUpperCase() + "_" + moduleId;
	}

	public static String node2Gate(RegulatoryNode node) {
		return node.getNodeInfo().getNodeID().toUpperCase();
	}

	public static String node2StateVar(RegulatoryNode node) {
		return node.getNodeInfo().getNodeID().toLowerCase();
	}

	public static String node2SyncAction(RegulatoryNode input, int inputModuleIndex, RegulatoryNode proper, int properModuleIndex){
		return "I_" + node2Gate(input,inputModuleIndex) + "_" + node2Gate(proper,properModuleIndex);
	}
	
	public int getNumberInstances() {
		return this.config.getTopology().getNumberInstances();
	}

	public String getModelName() {
		return this.config.getModelName();
	}

	public Collection<RegulatoryNode> getMappedInputs() {
		return this.config.getMapping().getMappedInputs();
	}

	public Collection<RegulatoryNode> getProperComponentsForInput(
			RegulatoryNode input) {
		return this.config.getMapping().getProperComponentsForInput(input);
	}

	public IntegrationFunction getIntegrationFunctionForInput(
			RegulatoryNode input) {
		return this.config.getMapping().getIntegrationFunctionForInput(input);
	}

	public Collection<RegulatoryNode> getListVisible(){
		return this.config.getListVisible();
	}
	
	public boolean areNeighbours(int i, int j) {
		return this.config.getTopology().areNeighbours(i, j);
	}

	public InitialStateWriter getInitialStateWriter() {
		return new InitialStateWriter(this.config.getInitialStates(),
				this.getAllComponents());
	}

	public InitialStateWriter getIntegrationInitialStateWriter(
			List<Map.Entry<RegulatoryNode, Integer>> externalComponents) {
		return new InitialStateWriter(this.config.getInitialStates(),
				externalComponents, true);
	}

	public GateWriter getGateWriter() {
		return new GateWriter(this.getAllComponents());
	}

	public StateVarWriter getStateVarWriter() {
		return new StateVarWriter(this.getAllComponents());
	}

	public String concreteProcessName(int moduleId) {
		return "openLRG_"
				+ this.getInitialStateWriter().typedStateConcat(moduleId);
	}

	public String formalProcessName(String index) {
		return "LogicModel" + index;
	}

	public String concreteIntegrationProcessName(RegulatoryNode input,
			int inputModuleIndex) {
		return "Integration" + CADPWriter.node2Gate(input) + "_"
				+ inputModuleIndex;
	}

	public String formalIntegrationProcessName(RegulatoryNode input,
			int numberArguments, IntegrationFunction integrationFunction) {
		return "Integration" + (input.getMaxValue() > 1 ? "M" : "B")
				+ numberArguments + integrationFunction.name();
	}

	public String formalIntegrationFunctionName(RegulatoryNode input,
			int numberArguments, IntegrationFunction integrationFunction) {
		return "lif" + (input.getMaxValue() > 1 ? "M" : "B") + numberArguments
				+ integrationFunction.name();
	}

	public LogicalModel getModel() {
		return this.config.getGraph().getModel();
	}
	
	public String getBCGModelFileName(int moduleId){
		return config.getBCGModelFilename(moduleId);
	}
	
	public String getLNTModelFileName(){
		return config.getLNTModelFilename();
	}

	public String getBCGIntegrationFileName(RegulatoryNode input, int moduleIndex){
		return config.getBCGIntegrationFilename(input, moduleIndex);
	}
	
	public String getLNTIntegrationFileName(){
		return config.getLNTIntegrationFilename();
	}
	
	public String getExpFileName(){
		return config.getExpFilename();
	}
	
	protected static String makeCommaList(List<String> list) {
		return makeCommaList(list, ",");
	}

	protected static String makeCommaList(List<String> list, String connective) {
		String out = "";
		for (String name : list) {
			if (!out.isEmpty())
				out += connective;
			out += name;
		}
		return out;
	}

	protected static String makeUpdatedCommaList(List<String> original,
			List<String> updated, String toUpdate) {
		List<String> modified = new ArrayList<String>();
		for (String name : original) {
			if (!name.equals(toUpdate))
				modified.add(name);
			else
				modified.add(updated.get(original.indexOf(toUpdate)));

		}
		return makeCommaList(modified);
	}

	public class InitialStateWriter {
		// TODO: compute correct initial state for mapped input variables
		private List<byte[]> initialStates = null;
		private List<RegulatoryNode> listNodes = null;
		private List<Map.Entry<RegulatoryNode, Integer>> listExternal = null;
		private boolean isMixed = false;

		public InitialStateWriter(List<byte[]> initialStates,
				List<RegulatoryNode> listNodes) {
			this.initialStates = initialStates;
			this.listNodes = listNodes;
		}

		public InitialStateWriter(List<byte[]> initialStates,
				List<Map.Entry<RegulatoryNode, Integer>> listExternal,
				boolean dummy) {
			this.initialStates = initialStates;
			this.listExternal = listExternal;
			this.isMixed = true;
		}

		public String typedSimpleList(int moduleId) {
			if (this.isMixed == true)
				return "";

			byte[] initialState = initialStates.get(moduleId);
			List<String> listStates = new ArrayList<String>();

			for (int i = 0; i < initialState.length; i++) {
				RegulatoryNode node = listNodes.get(i);
				String modifier = node.getMaxValue() > 1 ? "M" : "B";
				listStates.add(initialState[i] + modifier);
			}

			return CADPWriter.makeCommaList(listStates);
		}

		public String typedStateConcat(int moduleId) {
			if (this.isMixed == true)
				return "";

			byte[] initialState = initialStates.get(moduleId);
			List<String> listStates = new ArrayList<String>();

			for (int i = 0; i < initialState.length; i++) {
				RegulatoryNode node = listNodes.get(i);
				String modifier = node.getMaxValue() > 1 ? "M" : "B";
				listStates.add(initialState[i] + modifier);
			}

			return CADPWriter.makeCommaList(listStates, "");

		}

		public String typedMixedList() {
			if (this.isMixed == false)
				return "";

			List<String> listStates = new ArrayList<String>();

			for (Map.Entry<RegulatoryNode, Integer> entry : listExternal) {
				RegulatoryNode node = entry.getKey();
				int moduleId = entry.getValue().intValue();
				String modifier = node.getMaxValue() > 1 ? "M" : "B";
				listStates.add(initialStates.get(moduleId)[getAllComponents()
						.indexOf(node)] + modifier);
			}

			return CADPWriter.makeCommaList(listStates);

		}

	}

	public static class GateWriter {
		List<RegulatoryNode> listNodes = null;

		public GateWriter(List<RegulatoryNode> listNodes) {
			this.listNodes = listNodes;

		}

		public String simpleList() {
			String out = CADPWriter.getStableActionName();
			for (RegulatoryNode node : listNodes) {
				out += "," + CADPWriter.node2Gate(node);
			}

			return out;
		}

		public String simpleListWithModuleId(int moduleId) {
			String out = CADPWriter.getStableActionName();
			for (RegulatoryNode node : listNodes) {
				out += "," + CADPWriter.node2Gate(node, moduleId);
			}

			return out;
		}

		public String simpleDecoratedList(String decoration) {
			String out = CADPWriter.getStableActionName();
			for (RegulatoryNode node : listNodes) {
				out = "," + CADPWriter.node2Gate(node) + decoration;
			}

			return out;
		}

		public String typedList() {
			String out = CADPWriter.getStableActionName() + ":None";
			for (RegulatoryNode node : listNodes) {
				String type = node.getMaxValue() > 1 ? "Multi" : "Binary";
				out += "," + CADPWriter.node2Gate(node) + ":" + type;

			}

			return out;

		}
	}

	public static class StateVarWriter {
		List<RegulatoryNode> listNodes = null;

		public StateVarWriter(List<RegulatoryNode> listNodes) {
			this.listNodes = listNodes;
		}

		public String simpleList() {
			String out = "";
			for (RegulatoryNode node : listNodes) {

				if (!out.isEmpty())
					out += ",";

				out += CADPWriter.node2StateVar(node);
			}

			return out;
		}

		public String typedList() {
			String out = "";
			for (RegulatoryNode node : listNodes) {

				if (!out.isEmpty())
					out += ",";

				out += CADPWriter.node2StateVar(node) + ":"
						+ (node.getMaxValue() > 1 ? "Multi" : "Binary");

			}

			return out;
		}

	}

}
