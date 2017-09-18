package org.ginsim.service.tool.modelbooleanizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.modifier.booleanize.Booleanizer;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

/**
 * Model Booleanizer service.
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("booleanizer")
@ServiceStatus(EStatus.DEVELOPMENT)
public class ModelBooleanizerService implements Service {

	private final int DX = 50;
	private final int DY = 50;

	public LogicalModel booleanize(LogicalModel origModel) {
		return Booleanizer.booleanize(origModel);
	}

	public void copyNodeStyles(RegulatoryGraph src, RegulatoryGraph dest) {

		NodeAttributesReader sNReader = src.getNodeAttributeReader();
		NodeAttributesReader dNReader = dest.getNodeAttributeReader();

		Pattern p = Pattern.compile(".*_b[1-9]$");
		for (RegulatoryNode destNode : dest.getNodes()) {
			dNReader.setNode(destNode);
			RegulatoryNode srcNode = this.getEquivalentNode(src, destNode);
			sNReader.setNode(srcNode);
			dNReader.copyFrom(sNReader);
			// If it's a multivalued node, then performs a displacement
			Matcher m = p.matcher(destNode.getId());
			if (m.find()) {
				int i = Integer.parseInt(destNode.getId().substring(
						destNode.getId().length() - 1)) - 1;
				dNReader.move(this.DX * i, this.DY * i);
			}
			dNReader.refresh();
		}

		// copy all unaffected edges
		EdgeAttributesReader ereader = src.getEdgeAttributeReader();
		EdgeAttributesReader bereader = dest.getEdgeAttributeReader();
		for (RegulatoryMultiEdge bme: dest.getEdges()) {
			RegulatoryNode bsrc = this.getEquivalentNode(src, bme.getSource());
			RegulatoryNode target = this.getEquivalentNode(src, bme.getTarget());
			if (bsrc != null && target != null) {
				RegulatoryMultiEdge me = src.getEdge(bsrc, target);
                if (me != null) {
					ereader.setEdge(me);
					bereader.setEdge(bme);
					bereader.copyFrom(ereader);
				}
			}
			bereader.refresh();
		}

	}

	private RegulatoryNode getEquivalentNode(RegulatoryGraph graph, RegulatoryNode node) {

		String nid = node.getId();
		for (RegulatoryNode gNode : graph.getNodes()) {
			if (nid.startsWith(gNode.getId())) {
				return gNode;
			}
		}
		return null;
	}
}
