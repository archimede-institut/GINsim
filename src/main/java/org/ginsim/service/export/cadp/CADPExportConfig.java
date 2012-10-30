package org.ginsim.service.export.cadp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.composition.IntegrationFunctionMapping;
import org.ginsim.service.tool.composition.Topology;

/**
 * Class containing all the information required for the specification
 * of the composition to be provided via LTS abstraction and minimisation using CADP
 * 
 * @author Nuno D. Mendes
 *
 */
public class CADPExportConfig {
	private Topology topology = null;
	private IntegrationFunctionMapping mapping = null;
	private List<byte[]> initialStates = null;
	private Collection<RegulatoryNode> listVisible = null;
	private RegulatoryGraph graph = null;
	private String modelName = "";

	public CADPExportConfig(RegulatoryGraph graph) {
		this.graph = graph;
		this.modelName = graph.getGraphName();
		if (this.modelName.contains("."))
			this.modelName.replace(".", "_");
	}

	public String getModelName(){
		return modelName;
	}
	
	public void setModelName (String modelName){
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

	public Collection<RegulatoryNode> getListVisible() {
		return listVisible;
	}

	public void setListVisible(Collection<RegulatoryNode> listVisible) {
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
	
	public void addInitialState(int index, byte[] initialState){
		if (this.initialStates == null)
			this.initialStates = new ArrayList<byte[]>();
		
		this.initialStates.add(index, initialState);
	}
	
	public void addInitialState(byte[] initialState){
		if (this.initialStates == null)
			this.initialStates = new ArrayList<byte[]>();
			
		this.initialStates.add(initialState);
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}

}
