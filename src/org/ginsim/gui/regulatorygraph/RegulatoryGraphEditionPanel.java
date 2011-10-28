package org.ginsim.gui.regulatorygraph;

import java.awt.Component;

import javax.swing.JLabel;

import org.ginsim.graph.regulatoryGraph.RegulatoryGraph;
import org.ginsim.gui.graph.GUIEditor;

@SuppressWarnings("serial")
public class RegulatoryGraphEditionPanel extends JLabel implements GUIEditor<RegulatoryGraph> {

	@Override
	public void setEditedItem(RegulatoryGraph item) {
		setText("Graph panel");
	}

	@Override
	public Component getComponent() {
		return this;
	}

}
