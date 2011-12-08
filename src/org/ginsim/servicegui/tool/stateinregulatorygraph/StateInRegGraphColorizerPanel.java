package org.ginsim.servicegui.tool.stateinregulatorygraph;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.css.Colorizer;
import org.ginsim.gui.graph.view.css.ColorizerPanel;

public class StateInRegGraphColorizerPanel extends ColorizerPanel {

	private static final long serialVersionUID = -2932035763155309309L;
	private byte[] state;

	
	public StateInRegGraphColorizerPanel(String storeUserChoicePrefix, Graph<?, ?> graph) {
		super(false, storeUserChoicePrefix, graph);
	}

	@Override
	protected boolean shouldColorizeInitially() {
		return true;
	}
	
	@Override
	protected void doColorize() {
		((StateInRegGraphSelector) colorizer.getSelector()).setState(state);
		super.doColorize();
	}
	
	public void setState(byte[] state) {
		this.state = state;
	}

	public void setColorizer(Colorizer colorizer) {
		this.colorizer = colorizer;
	}
}
