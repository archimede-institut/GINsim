package org.ginsim.service.tool.interactionanalysis;

import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.kohsuke.MetaInfServices;

@MetaInfServices( Service.class)
@Alias("interactions-analysis")
@ServiceStatus(EStatus.RELEASED)
public class InteractionAnalysisService implements Service {
	
	/**
	 * Run the Interaction analysis by instantiating and calling a InteractionAnalysisAlgo and
	 * return an InteractionAnalysisAlgoResult containing the report and the colorizer. 
	 * 
	 * @param g the graph where to search the non functional interactions.
	 * @param perturbation the perturbation definition
	 * @param selectedNodes the set of selected nodes to run the analysis on.
	 * @return an InteractionAnalysisAlgoResult containing the report and the colorizer.
	 */
	public InteractionAnalysisAlgoResult run(RegulatoryGraph  g, Perturbation perturbation, List<RegulatoryNode> selectedNodes) {
		InteractionAnalysisAlgo algo = new InteractionAnalysisAlgo();
		InteractionAnalysisAlgoResult algoResult = algo.run(g, perturbation, selectedNodes);
		return algoResult;
	}
}
