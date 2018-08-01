package org.ginsim.service.tool.modelbooleanizer;

import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.modifier.booleanize.BooleanizeModifier;
import org.colomoto.biolqm.modifier.booleanize.BooleanizeService;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.ViewCopyHelper;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.kohsuke.MetaInfServices;

/**
 * Model Booleanizer service.
 * 
 * @author Pedro T. Monteiro
 */
@MetaInfServices(Service.class)
@Alias("booleanizer")
@ServiceStatus(EStatus.DEVELOPMENT)
public class ModelBooleanizerService implements Service {

	public LogicalModel booleanize(LogicalModel origModel) {
		return new BooleanizeModifier(origModel).getModifiedModel();
	}

	public void copyNodeStyles(RegulatoryGraph src, RegulatoryGraph dest) {
		dest.copyView(src, new BooleanizedCopyHelper(src));
	}

}

class BooleanizedCopyHelper
	implements ViewCopyHelper<Graph<RegulatoryNode,RegulatoryMultiEdge>, RegulatoryNode, RegulatoryMultiEdge> {

	private static final int DX = 50;
	private static final int DY = 50;
	
	private final Pattern p = Pattern.compile(".*_b[1-9]$");
	
	private final RegulatoryGraph graph;
	private final Dimension offset = new Dimension();
	
	public BooleanizedCopyHelper(RegulatoryGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public RegulatoryNode getSourceNode(RegulatoryNode destNode) {
		String nid = destNode.getId();
		for (RegulatoryNode gNode : graph.getNodes()) {
			if (nid.startsWith(gNode.getId())) {
				// If it's a multivalued node, then performs a displacement
				Matcher m = p.matcher(destNode.getId());
				if (m.find()) {
					int i = Integer.parseInt(destNode.getId().substring(
							destNode.getId().length() - 1)) - 1;
					offset.setSize(DX * i, DY * i);
				} else {
					offset.setSize(0,0);
				}
	
				return gNode;
			}
		}
		return null;
	}
	
	@Override
	public RegulatoryMultiEdge getSourceEdge(RegulatoryMultiEdge me) {
	    RegulatoryNode src = getSourceNode(me.getSource());
	    RegulatoryNode tgt = getSourceNode(me.getTarget());
	    if (src != null && tgt != null) {
	    	return graph.getEdge(src, tgt);
	    }
		return null;
	}
	
	@Override
	public Dimension getOffset() {
		return offset;
	}

}
