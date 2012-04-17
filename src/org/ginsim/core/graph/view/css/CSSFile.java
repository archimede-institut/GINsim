package org.ginsim.core.graph.view.css;

import org.ginsim.core.graph.common.Graph;

public class CSSFile {
	protected CascadingStyleSheet css = null;
	protected String name;
	protected boolean saved = false;
	
	public CSSFile(String name) {
		this.name = name;
		this.css = new CascadingStyleSheet();
		
	}
	
	public boolean setName(String name) {
		this.name = name;
		
		return true;
	}
	
	public String getTextToEdit() {
		return css.toString();
	}
	
	public void saveEditedText(String text) throws CSSSyntaxException {
		css.parse(text);
		System.out.println("CSSFile.saveEditedText()"+css.toString());
	}
	
	@Override
	public String toString() {
		return name;
	}

	public void compileAndApply(Graph graph, CascadingStyleSheetManager cs) {
		css.compile();
		css.apply(graph, cs);
	}

	
}
