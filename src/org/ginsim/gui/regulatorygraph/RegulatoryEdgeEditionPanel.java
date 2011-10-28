package org.ginsim.gui.regulatorygraph;

import java.awt.Component;

import javax.swing.JLabel;

import org.ginsim.graph.regulatoryGraph.RegulatoryEdge;
import org.ginsim.gui.graph.GUIEditor;

@SuppressWarnings("serial")
public class RegulatoryEdgeEditionPanel extends JLabel implements GUIEditor<RegulatoryEdge> {

	@Override
	public void setEditedItem(RegulatoryEdge item) {
		setText("Edge panel");
	}

	@Override
	public Component getComponent() {
		return this;
	}

}
