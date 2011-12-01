package org.ginsim.service.tool.interactionanalysis;

import java.util.List;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.regulatorygraph.mutant.RegulatoryMutantDef;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
public class InteractionAnalysisService implements Service {
	
	/**
	 * Run the Interaction analysis by instantiating and calling a InteractionAnalysisAlgo and
	 * return an InteractionAnalysisAlgoResult containing the report and the colorizer. 
	 * 
	 * @param g the graph where to search the non functional interactions.
	 * @param opt_annotate boolean indicating if the non functional edges should be annotated.
	 * @param mutant the mutant definition
	 * @param selectedNodes the set of selected nodes to run the analysis on.
	 * @return an InteractionAnalysisAlgoResult containing the report and the colorizer.
	 */
	public InteractionAnalysisAlgoResult run(RegulatoryGraph  g, RegulatoryMutantDef mutant, List<RegulatoryNode> selectedNodes) {
		InteractionAnalysisAlgo algo = new InteractionAnalysisAlgo();
		InteractionAnalysisAlgoResult algoResult = algo.run(g, mutant, selectedNodes);
		return algoResult;
	}
}
