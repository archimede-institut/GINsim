package org.ginsim.service.export.cadp;

import java.util.ArrayList;
import java.util.Collection;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.composition.IntegrationFunctionMapping;
import org.ginsim.service.tool.composition.Topology;

public class CADPExportConfig {
	private Topology topology = null;
	private IntegrationFunctionMapping mapping = null;
	private byte[] initialState = null;
	private Collection<RegulatoryNode> listVisible = null;
	private RegulatoryGraph graph = null;

	public CADPExportConfig(RegulatoryGraph graph) {
		this.graph = graph;
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

	public byte[] getInitialState() {
		return initialState;
	}

	public void setInitialState(byte[] initialState) {
		this.initialState = initialState;
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}

}
