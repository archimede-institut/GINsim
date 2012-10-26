package org.ginsim.service.export.cadp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.composition.IntegrationFunction;

public class CADPIntegrationWriter {
	private CADPExportConfig config = null;

	public CADPIntegrationWriter(CADPExportConfig config) {
		this.config = config;
	}

	public String integrationLotosNTFile() throws GsException {
		String out = "";

		int numberInstances = config.getTopology().getNumberInstances();
		Collection<RegulatoryNode> mappedInputs = config.getMapping()
				.getMappedInputs();
		List<byte[]> initialStates = config.getInitialStates();

		Collection<String> integrationProcessSignature = new HashSet<String>();
		Collection<String> integrationFunctionSignature = new HashSet<String>();

		for (RegulatoryNode input : mappedInputs) {
			Collection<RegulatoryNode> properComponents = config.getMapping()
					.getProperComponentsForInput(input);
			IntegrationFunction integrationFunction = config.getMapping()
					.getIntegrationFunctionForInput(input);

			for (int instance = 0; instance < numberInstances; instance++) {
				List<RegulatoryNode> externalComponents = new ArrayList<RegulatoryNode>();
				List<Integer> externalComponentIndices = new ArrayList<Integer>();

				for (int neighbour = 0; neighbour < numberInstances; neighbour++) {

					if (!config.getTopology()
							.areNeighbours(instance, neighbour))
						continue;

					for (RegulatoryNode proper : properComponents) {

						externalComponents.add(proper);
						externalComponentIndices.add(new Integer(neighbour));

					}
				}
				int[] externalModuleIndices = new int[externalComponentIndices
						.size()];
				int i = 0;
				for (Integer index : externalComponentIndices)
					externalModuleIndices[i++] = index.intValue();

				IntegrationProcessWriter integrationProcessWriter = new IntegrationProcessWriter(
						input, (RegulatoryNode[]) externalComponents.toArray(),
						instance, externalModuleIndices, initialStates,
						integrationFunction);

				out += integrationProcessWriter.concreteIntegrationProcess();
				String formalIntegrationProcess = integrationProcessWriter.formalIntegrationProcess();
				String formalIntegrationFunction = integrationProcessWriter.formalIntegrationFunction();
				
				if (!integrationProcessSignature.contains(formalIntegrationProcess)){
					integrationProcessSignature.add(formalIntegrationProcess);
					out += formalIntegrationProcess;
				}
				
				if (!integrationFunctionSignature.contains(formalIntegrationFunction)){
					integrationFunctionSignature.add(formalIntegrationFunction);
					out += formalIntegrationFunction;
				}

			}

		}

		return out;
	}

	public class IntegrationProcessWriter {
		private RegulatoryNode input = null;
		private RegulatoryNode[] properComponents = null;
		private int inputModuleIndex;
		private int[] externalModuleIndices;
		private List<byte[]> initialStates = null;
		private IntegrationFunction integrationFunction;
		private int localMaxValue = 0; // simplifies, assuming that all Multi
										// variables have the same maxValue
										// this may not be true, giving rise to
										// transitions that never take place
										// in the actual Module, making the
										// Integration process excessively
										// complicated
		private int numberArguments = 0;
		private String gateType = "";
		private String concreteProcessName = "";
		private String formalProcessName = "";
		private String formalFunctionName = "";
		private String formalParametersType = "";
		private String formalStateVarModifier = "";
		private List<String> formalGates = new ArrayList<String>();
		private List<String> formalStateVars = new ArrayList<String>();
		private List<String> formalUpdateVars = new ArrayList<String>();
		private String formalParameterBaseName = "g";

		public IntegrationProcessWriter(RegulatoryNode input,
				RegulatoryNode[] properComponents, int inputModuleIndex,
				int[] externalModuleIndices, List<byte[]> initialStates,
				IntegrationFunction integrationFunction) throws GsException {
			this.input = input;
			this.properComponents = properComponents;
			this.inputModuleIndex = inputModuleIndex;
			this.externalModuleIndices = externalModuleIndices;
			this.initialStates = initialStates;
			this.integrationFunction = integrationFunction;
			this.localMaxValue = 0;

			for (int i = 0; i < properComponents.length; i++)
				if (properComponents[i].getMaxValue() > this.localMaxValue)
					this.localMaxValue = properComponents[i].getMaxValue();

			this.numberArguments = properComponents.length;
			this.gateType = input.getMaxValue() > 1 ? "MultiIntegration"
					: "BinaryIntegration";

			this.concreteProcessName = "Integration"
					+ input.getNodeInfo().getNodeID() + "_" + inputModuleIndex;

			this.formalProcessName = "Integration"
					+ (input.getMaxValue() > 1 ? "M" : "B") + numberArguments
					+ integrationFunction.name();

			this.formalFunctionName = "lif"
					+ (input.getMaxValue() > 1 ? "M" : "B") + numberArguments
					+ integrationFunction.name();

			boolean consistent = true;
			for (int i = 0; i < properComponents.length; i++) {
				String componentType = properComponents[i].getMaxValue() > 1 ? "Multi"
						: "Binary";
				if (this.formalParametersType.isEmpty())
					this.formalParametersType = componentType;
				else if (this.formalParametersType != componentType)
					consistent = false;
			}
			if (!consistent)
				throw new GsException(GsException.GRAVITY_ERROR,
						"Formal parameters cannot be of different types");

			this.formalStateVarModifier = this.formalParametersType
					.equals("Multi") ? "M" : "B";

			for (int i = 0; i < this.numberArguments; i++) {
				this.formalGates.add(this.formalParameterBaseName.toUpperCase()
						+ i);
				this.formalStateVars.add(this.formalParameterBaseName
						.toLowerCase() + i);
				this.formalUpdateVars.add("new_"
						+ this.formalParameterBaseName.toLowerCase() + i);
			}

		}

		public String concreteIntegrationProcess() {
			List<String> concreteGateList = new ArrayList<String>();
			for (int i = 0; i < externalModuleIndices.length; i++)
				concreteGateList
						.add("I_"
								+ input.getNodeInfo().getNodeID().toUpperCase()
								+ "_"
								+ inputModuleIndex
								+ "_"
								+ properComponents[i].getNodeInfo().getNodeID()
										.toUpperCase() + "_"
								+ externalModuleIndices[i]);

			String concreteGateCommaList = makeCommaList(concreteGateList);
			String concreteGateSignature = concreteGateCommaList + " : "
					+ gateType;

			List<String> initialStateExternalComponents = new ArrayList<String>();
			for (int i = 0; i < properComponents.length; i++) {
				int moduleIndex = externalModuleIndices[i];
				byte initialComponentValue = initialStates.get(moduleIndex)[config
						.getGraph().getNodeOrder().indexOf(properComponents[i])];
				initialStateExternalComponents.add(initialComponentValue
						+ (properComponents[i].getMaxValue() > 1 ? "M" : "B"));
			}

			String initialExternalState = makeCommaList(initialStateExternalComponents);

			String out = "";
			out += "process " + concreteProcessName + "["
					+ concreteGateSignature + "] is\n";
			out += "\t" + formalProcessName + "[" + concreteGateCommaList
					+ "](" + initialExternalState + ")\n";
			out += "end process\n\n";

			return out;
		}

		public String formalIntegrationProcess() {

			String formalGateCommaList = makeCommaList(this.formalGates);
			String formalGateSignature = formalGateCommaList + " : "
					+ this.gateType;
			String formalStateVarsCommaList = makeCommaList(this.formalStateVars);
			String formalStateVarsSignature = formalStateVarsCommaList + " : "
					+ this.formalParametersType;
			String formalUpdateVarsCommaList = makeCommaList(this.formalUpdateVars);
			String formalUpdateVarsSignature = formalUpdateVarsCommaList
					+ " : " + this.formalParametersType;

			String out = "";

			out += "process " + this.formalProcessName + " ["
					+ formalGateSignature + "](" + formalStateVarsSignature
					+ ") is\n";
			out += "\tvar " + formalUpdateVarsSignature + " in\n";
			out += "\t\tloop\n";
			out += "\t\t\tselect\n";
			boolean isFirst = true;
			for (String stateVar : this.formalStateVars) {
				if (isFirst)
					isFirst = false;
				else
					out += "\t\t\t[]\n";

				String maxValue = (this.formalStateVarModifier.equals("B") ? 1
						: this.localMaxValue) + this.formalStateVarModifier;
				String minValue = 0 + this.formalStateVarModifier;

				String updateVar = this.formalUpdateVars
						.get(this.formalStateVars.indexOf(stateVar));

				String gateName = this.formalGates.get(this.formalStateVars
						.indexOf(stateVar));

				out += "\t\t\t\tselect\n";
				out += "\t\t\t\t\tif " + stateVar + " != " + maxValue
						+ " then " + updateVar + " := " + stateVar
						+ " + 1 else stop end if\n";
				out += "\t\t\t\t\t[]\n";
				out += "\t\t\t\t\tif " + stateVar + " != " + minValue
						+ " then " + updateVar + " := " + stateVar
						+ " - 1 else stop end if\n";
				out += "\t\t\t\tend select;\n";
				out += "\t\t\t\tif "
						+ this.formalFunctionName
						+ "("
						+ makeUpdatedCommaList(this.formalStateVars,
								this.formalUpdateVars, stateVar) + ") != "
						+ this.formalFunctionName + "("
						+ makeCommaList(this.formalStateVars) + ") then\n";
				out += "\t\t\t\t\t"
						+ gateName
						+ "("
						+ updateVar
						+ ","
						+ this.formalFunctionName
						+ "("
						+ makeUpdatedCommaList(this.formalStateVars,
								this.formalUpdateVars, stateVar) + "))\n";
				out += "\t\t\t\telse\n";
				out += "\t\t\t\t\t" + gateName + "(" + updateVar + ")\n";
				out += "\t\t\t\tend if;\n";
				out += stateVar + " := " + updateVar + "\n";

			}
			out += "\t\t\tend select\n";
			out += "\t\tend loop\n";
			out += "\tend var\n";
			out += "end process\n\n";

			return out;
		}

		public String formalIntegrationFunction() {
			String out = "";

			out += "function " + this.formalFunctionName + "("
					+ makeCommaList(this.formalStateVars) + " : "
					+ this.formalParametersType + ") : " + this.gateType
					+ " is\n";

			String value = "";
			String connective = "";
			String returnValue = "";
			String elseReturnValue = "";
			String operator = " == ";

			boolean isSpecial = false;
			boolean reverseValues = false;

			switch (this.integrationFunction) {
			case OR:
				value = 0 + this.formalStateVarModifier;
				connective = " and ";
				returnValue = 0 + this.formalStateVarModifier;
				elseReturnValue = 1 + this.formalStateVarModifier;
				break;
			case AND:
				value = 1 + this.formalStateVarModifier;
				connective = " and ";
				returnValue = 1 + this.formalStateVarModifier;
				elseReturnValue = 0 + this.formalStateVarModifier;
				break;
			case THRESHOLD2:
				value = this.localMaxValue + this.formalStateVarModifier;
				connective = " or ";
				returnValue = 1 + this.formalStateVarModifier;
				elseReturnValue = 0 + this.formalStateVarModifier;
				break;
			case MAX:
				isSpecial = true;
				operator = " >= ";
				connective = " and ";
				reverseValues = false;
				break;
			case MIN:
				isSpecial = true;
				operator = " <= ";
				connective = " and ";
				reverseValues = true;
				break;
			case MAX_LEFT:
			case MAX_RIGHT:
				break;

			}

			if (!isSpecial) {
				List<String> terms = new ArrayList<String>();

				for (String stateVar : this.formalStateVars) {
					terms.add(stateVar + operator + value);
				}

				String ifCondition = makeCommaList(terms, connective);
				out += "\tif " + ifCondition + " then\n";
				out += "\t\treturn (" + returnValue + ")\n";
				out += "\telse\n";
				out += "\t\treturn(" + elseReturnValue + ")\n";
				out += "\tend if\n";

			} else {
				List<Integer> valueList = new ArrayList<Integer>();
				for (int v = 0; v <= this.localMaxValue; v++)
					valueList.add(new Integer(v));

				if (reverseValues)
					Collections.reverse(valueList);

				boolean isFirst = true;
				boolean isLast = false;
				String ifStatement = "if ";
				for (Integer curValue : valueList) {
					List<String> terms = new ArrayList<String>();
					for (String stateVar : this.formalStateVars)
						terms.add(stateVar + operator + value);

					isLast = valueList.size() == valueList.indexOf(curValue) + 1;

					if (isLast) {
						out += "\telse\n";
						out += "\t\treturn (" + curValue
								+ this.formalStateVarModifier + ")\n";
						out += "\tend if\n";
					} else {
						if (isFirst)
							isFirst = false;
						else
							ifStatement = "elsif ";

						out += "\t" + ifStatement
								+ makeCommaList(terms, connective) + " then\n";
						out += "\t\treturn (" + curValue
								+ this.formalStateVarModifier + ")\n";

					}

				}

			}

			out += "end function\n\n";

			return out;
		}

		public String EXPspecification() {
			String out = "";

			return out;
		}

		public String synchronizationVectors() {
			String out = "";

			return out;
		}

		private String makeCommaList(List<String> list) {
			return makeCommaList(list, ",");
		}

		private String makeCommaList(List<String> list, String connective) {
			String out = "";
			for (String name : list) {
				if (!out.isEmpty())
					out += connective;
				out += name;
			}
			return out;
		}

		private String makeUpdatedCommaList(List<String> original,
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

	}

}
