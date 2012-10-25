package org.ginsim.service.export.cadp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.composition.IntegrationFunction;

public class CADPIntegrationWriter {
	private CADPExportConfig config = null;

	public CADPIntegrationWriter(CADPExportConfig config) {
		this.config = config;
	}

	public String toString() {
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

		/* TODO: Create inner classes that, given a set of parameters
		 * known how to create the concrete Integration process
		 * the abstract integration process
		 * and the corresponding integration function in LOTOS NT
	*/
			
			for (int instance = 0; instance < numberInstances; instance++) {
				int numberNeighbours = config.getTopology()
						.getNumberNeighbours(instance);
				
				List<RegulatoryNode> externalComponents = new ArrayList<RegulatoryNode>();
				List<Integer> externalComponentIndices = new ArrayList<Integer>();
				
				
				for (int neighbour = 0; neighbour < numberInstances; neighbour++) {

					if (!config.getTopology()
							.areNeighbours(instance, neighbour))
						break;

						
					for (RegulatoryNode proper : properComponents) {
						
						externalComponents.add(proper);
						externalComponentIndices.add(new Integer(neighbour));
						
					}
					
					int[] externalModuleIndices = new int[externalComponentIndices.size()];
					int i = 0;
					for (Integer index : externalComponentIndices)
						externalModuleIndices[i++] = index.intValue();
					
					IntegrationProcessWriter integrationProcessWriter = new IntegrationProcessWriter(input, (RegulatoryNode[]) externalComponents.toArray(), instance, externalModuleIndices, initialStates, integrationFunction);
					

						
						/*
						if (!gateList.isEmpty())
							gateList += ",";
						gateList += "I_"
								+ input.getNodeInfo().getNodeID().toUpperCase()
								+ "_"
								+ instance
								+ "_"
								+ proper.getNodeInfo().getNodeID()
										.toUpperCase() + "_" + neighbour;

						if (!initProper.isEmpty())
							initProper += ",";

						initProper += initialState[config.getGraph()
								.getNodeOrder().indexOf(proper)]
								+ (proper.getMaxValue() > 1 ? "M" : "B");
						
					}

					
					
					
					String processName = "Integration"
							+ input.getNodeInfo().getNodeID() + "_" + instance;
					String processType = (input.getMaxValue() > 1 ? "M" : "B")
							+ numberArguments
							+ integrationFunction.name() + "_" + localMaxValue;

					out += "process " + processName + "[" + gateList + ":"
							+ gateType + "] is\n";
					out += "\tIntegration" + processType + "[" + gateList
							+ "](" + initProper + ")\n";
					out += "end process\n\n";
					
					
					List<String> abstractGates = new ArrayList<String>();
					List<String> abstractStateVars = new ArrayList<String>();
					List<String> abstractStateVarsNew = new ArrayList<String>();
					String abstractGateList = "";
					String abstractStateVarList = "";
					String abstractStateVarNewList = "";
					String abstractGateBaseName = "G";
					String stateVarType = null;
					String stateVarModifier = null;
					if (properComponents.iterator().hasNext())
						if (properComponents.iterator().next().getMaxValue() > 1){
							stateVarType = "Multi";
							stateVarModifier = "M";
						} else {
							stateVarType = "Binary";
							stateVarModifier = "B";
						}
					
					for (int i=0;i<numberArguments;i++){
						if (!abstractGateList.isEmpty())
							abstractGateList += ",";
						String abstractGateName = abstractGateBaseName + i;
						abstractGateList += abstractGateName;
						abstractGates.add(abstractGateName);
						if (!abstractStateVarList.isEmpty())
							abstractStateVarList += ",";
						String abstractStateVarName = abstractGateBaseName.toLowerCase() + i;
						abstractStateVarList += abstractStateVarName;
						abstractStateVars.add(abstractStateVarName);
						if(!abstractStateVarNewList.isEmpty())
							abstractStateVarNewList += ",";
						String abstractStateVarNewName = "new_" + abstractGateBaseName.toLowerCase() + i;
						abstractStateVarNewList += abstractStateVarNewName;
						abstractStateVarsNew.add(abstractStateVarNewName);
					}
					
					
					if (! integrationProcessSignature.contains(processType)){
						integrationProcessSignature.add(processType);
						
						out += "process Integration" + processType + "[" + abstractGateList + " : " + gateType + "](" + abstractStateVarList + ":" + stateVarType + ") is\n";
						out += "\tvar " + abstractStateVarNewList + " : " + stateVarType + " in\n";
						out += "\t\tloop\n";
						out += "\t\t\tselect\n";
						
						out += "\t\t\tend select\n";
						out += "\t\tend loop\n";
						out += "\tend var\n";
						out += "end process\n\n";
						
					}
*/
				}

			}
		}

		return out;
	}
	
	public class IntegrationProcessWriter {
		private RegulatoryNode input = null;
		private RegulatoryNode[] properComponets = null;
		private int inputModuleIndex;
		private int[] externalModuleIndices;
		private List<byte[]> initialStates = null;
		private IntegrationFunction integrationFunction;
		private int localMaxValue = 0; // simplifies, assuming that all Multi variables have the same maxValue
									   // this may not be true, giving rise to transitions that never take place
									   // in the actual Module, making the Integration process excessively complicated
		private int numberArguments = 0;	
		private String gateType = "";
		
		
		public IntegrationProcessWriter(RegulatoryNode input, RegulatoryNode[] properComponents, int inputModuleIndex, int[] externalModuleIndices, List<byte[]> initialStates, IntegrationFunction integrationFunction){
			this.input = input;
			this.properComponets = properComponents;
			this.inputModuleIndex = inputModuleIndex;
			this.externalModuleIndices = externalModuleIndices;
			this.initialStates = initialStates;
			this.integrationFunction = integrationFunction;
			this.localMaxValue = 0;
			
			for (int i = 0; i < properComponents.length; i++)
				if (properComponents[i].getMaxValue() > this.localMaxValue)
					this.localMaxValue = properComponents[i].getMaxValue();
			
			this.numberArguments = properComponents.length;
			this.gateType = input.getMaxValue() > 1 ? "MultiIntegration" : "BinaryIntegration";
			
		}
		
		public String concreteIntegrationProcess(){
			String concreteProcessName = "Integration";
			
			String out = "";
			
			return out;
		}
		
		public String abstractIntegrationProcess(){
			String out = "";
			
			return out;
		}
		
		public String abstractIntegrationFunction(){
			String out = "";
			
			return out;
		}
		
	}
	
	public class IntegrationFunctionWriter {
		
	}

}
