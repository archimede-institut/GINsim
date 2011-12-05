package org.ginsim.service.tool.interactionanalysis;

import org.ginsim.core.graph.view.css.Colorizer;

/**
 * This class is a simple container for the results of the algorithm :
 *		* The InteractionAnalysisColorizer
 *		* The InteractionAnalysisReport
 */
public class InteractionAnalysisAlgoResult {
	private Colorizer colorizer;
	private InteractionAnalysisReport report;

	protected InteractionAnalysisAlgoResult(Colorizer colorizer, InteractionAnalysisReport report) {
		this.colorizer = colorizer;
		this.report = report;
	}
	
	public Colorizer getColorizer() {
		return colorizer;
	}

	public InteractionAnalysisReport getReport() {
		return report;
	}

}
