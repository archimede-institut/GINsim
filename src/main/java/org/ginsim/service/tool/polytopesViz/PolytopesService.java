package org.ginsim.service.tool.polytopesViz;

import java.text.ParseException;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.hierachicaltransitiongraph.StatesSet;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.graph.view.css.Colorizer;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(Service.class)
public class PolytopesService implements Service {

	
	private Colorizer colorizer;
	private DynamicGraph graph;

	public PolytopesService() {
        setColorizer(new Colorizer(new PolytopesSelector()));
	}
	

	public void run(DynamicGraph graph, String logical_formulae) throws ParseException {
		byte[] childCount = getChildCount(graph);
		StatesSet polytope = new StatesSet(OMDDNode.read(logical_formulae, childCount), childCount);
		if (polytope != null) {
			run(graph, polytope);
		}
	}

	
	private byte[] getChildCount(DynamicGraph graph) {
		byte[] childsCount = new byte[graph.getNodeOrder().size()];
		int i = 0;
		for (NodeInfo v: graph.getNodeOrder()) {
			childsCount[i++] = (byte) ( v.getMax()+1 );
			
		}	
		return childsCount;
	}


	public void run(DynamicGraph graph, StatesSet polytope) {
		this.graph = graph;
		((PolytopesSelector)getColorizer().getSelector()).setCache(polytope);
        getColorizer().doColorize(graph);
	}
	
	public void undoColorize() {
		if (getColorizer() != null) getColorizer().undoColorize(graph);
	}

	/**
	 * @return the colorizer
	 */
	public Colorizer getColorizer() {
		return colorizer;
	}

	/**
	 * @param colorizer the colorizer to set
	 */
	public void setColorizer(Colorizer colorizer) {
		this.colorizer = colorizer;
	}
}
