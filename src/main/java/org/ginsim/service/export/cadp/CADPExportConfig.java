package org.ginsim.service.export.cadp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.colomoto.logicalmodel.tool.stablestate.StableStateSearcher;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.service.tool.composition.IntegrationFunction;
import org.ginsim.service.tool.composition.IntegrationFunctionMapping;
import org.ginsim.service.tool.composition.Topology;
import org.ginsim.service.tool.stablestates.StableStatesService;

/**
 * Class containing all the information required for the specification of the
 * composition to be provided via LTS abstraction and minimisation using CADP
 * 
 * @author Nuno D. Mendes
 * 
 */
public class CADPExportConfig {
	private Topology topology = null;
	private IntegrationFunctionMapping mapping = null;
	private List<byte[]> initialStates = null;
	private List<List<byte[]>> compatibleStableStates = null;
	private List<RegulatoryNode> listVisible = null;
	private RegulatoryGraph graph = null;
	private String modelName = "";

	public CADPExportConfig(RegulatoryGraph graph) {
		this.graph = graph;
		this.modelName = graph.getGraphName();
		if (this.modelName.contains("."))
			this.modelName.replace(".", "_");
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
		if (this.modelName.contains("."))
			this.modelName.replace(".", "_");
	}

	public Topology getTopology() {
		return topology;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
	}

	public IntegrationFunctionMapping getMapping() {
		return mapping;
	}

	public void setMapping(IntegrationFunctionMapping mapping) {
		this.mapping = mapping;
	}

	public List<RegulatoryNode> getListVisible() {
		return listVisible;
	}

	public void setListVisible(List<RegulatoryNode> listVisible) {
		this.listVisible = listVisible;
	}

	public void addVisible(RegulatoryNode node) {
		if (listVisible == null)
			listVisible = new ArrayList<RegulatoryNode>();
		listVisible.add(node);
	}

	public List<byte[]> getInitialStates() {
		return initialStates;
	}

	public void setInitialStates(List<byte[]> initialStates) {
		this.initialStates = initialStates;
	}

	public void addInitialState(int index, byte[] initialState) {
		if (this.initialStates == null)
			this.initialStates = new ArrayList<byte[]>();

		this.initialStates.add(index, initialState);
	}

	public void addInitialState(byte[] initialState) {
		if (this.initialStates == null)
			this.initialStates = new ArrayList<byte[]>();

		this.initialStates.add(initialState);
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}

	public String getLNTModelFilename() {
		return this.getModelName() + ".lnt";
	}

	public String getBCGModelFilename(int moduleId) {
		return this.getModelName() + "_" + moduleId + ".bcg";
	}

	public String getLNTIntegrationFilename() {
		return "integration_" + this.getModelName() + "_"
				+ this.getTopology().getNumberInstances() + ".lnt";
	}

	public String getBCGIntegrationFilename(RegulatoryNode node, int moduleId) {
		return "integration_" + node.getNodeInfo().getNodeID().toUpperCase()
				+ "_" + moduleId + ".bcg";
	}

	public String getExpFilename() {
		return "composition_" + this.getModelName() + "_"
				+ this.getTopology().getNumberInstances() + ".exp";
	}

	public void setCompatibleStableStates(List<List<byte[]>> compatibleStableStates){
		this.compatibleStableStates = compatibleStableStates;
	}
	
	public List<List<byte[]>> getCompatibleStableStates(){
		return this.compatibleStableStates;
	}


}
