package org.ginsim.gui.regulatorygraph;

import java.awt.Component;

import javax.swing.JLabel;

import org.ginsim.graph.regulatoryGraph.RegulatoryVertex;
import org.ginsim.gui.graph.GUIEditor;

@SuppressWarnings("serial")
public class RegulatoryVertexEditionPanel extends JLabel implements GUIEditor<RegulatoryVertex> {

	@Override
	public void setEditedItem(RegulatoryVertex item) {
		setText("Vertex panel");
	}

	@Override
	public Component getComponent() {
		return this;
	}

}
