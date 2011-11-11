package org.ginsim.gui.graphhelper.testgraph;

import java.awt.Component;

import javax.swing.JLabel;

import org.ginsim.graph.testGraph.TestEdge;
import org.ginsim.gui.graph.GUIEditor;

@SuppressWarnings("serial")
public class TestEdgeEditionPanel extends JLabel implements GUIEditor<TestEdge> {

	@Override
	public void setEditedItem(TestEdge item) {
		setText("Edge panel");
	}

	@Override
	public Component getComponent() {
		return this;
	}

}
