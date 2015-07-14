package org.ginsim.service.tool.modelreversion;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.reverse.ModelReverser;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

/**
 * Model reversion service.
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("reversion")
@ServiceStatus(EStatus.DEVELOPMENT)
public class ModelReversionService implements Service {
	
	private final int DX = 50;
	private final int DY = 50;

	public ModelReverser getModelReverser(RegulatoryGraph graph) {
		return getModelReverser(graph.getModel());
	}

	public ModelReverser getModelReverser(LogicalModel model) {
		return new ModelReverser(model);
	}

	public void copyNodeStyles(RegulatoryGraph src, RegulatoryGraph dest) {

		NodeAttributesReader sNReader = src.getNodeAttributeReader();
		NodeAttributesReader dNReader = dest.getNodeAttributeReader();

		for (RegulatoryNode destNode : dest.getNodes()) {
			dNReader.setNode(destNode);
			RegulatoryNode srcNode = this.getEquivalentNode(src, destNode);
			sNReader.setNode(srcNode);
			dNReader.copyFrom(sNReader);
			// If it's a multivalued node, then performs a displacement
			if (destNode.getId().contains("_b")) {
				int i = Integer.parseInt(destNode.getId().substring(destNode.getId().length() - 1)) - 1;
				dNReader.move(this.DX * i, this.DY * i);
			}
		}
		dNReader.refresh();
	}

	private RegulatoryNode getEquivalentNode(RegulatoryGraph graph, RegulatoryNode node) {
		for (RegulatoryNode gNode : graph.getNodes()) {
			if (node.getId().startsWith(gNode.getId())) {
				return gNode;
			}
		}
		return null;
	}
}
