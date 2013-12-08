package org.ginsim.service.tool.interactionanalysis;

import org.ginsim.core.graph.view.style.StyleProvider;

/**
 * This class is a simple container for the results of the algorithm :
 *		* The Style Provider
 *		* The InteractionAnalysisReport
 */
public class InteractionAnalysisAlgoResult {
	private StyleProvider style;
	private InteractionAnalysisReport report;

	protected InteractionAnalysisAlgoResult(StyleProvider style, InteractionAnalysisReport report) {
		this.style = style;
		this.report = report;
	}
	
	public StyleProvider getStyle() {
		return style    ;
	}

	public InteractionAnalysisReport getReport() {
		return report;
	}

}
