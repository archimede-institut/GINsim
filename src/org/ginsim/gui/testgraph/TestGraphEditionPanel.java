package org.ginsim.gui.testgraph;

import java.awt.Component;

import javax.swing.JLabel;

import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.gui.graph.GUIEditor;

@SuppressWarnings("serial")
public class TestGraphEditionPanel extends JLabel implements GUIEditor<TestGraph> {

	@Override
	public void setEditedItem(TestGraph item) {
		setText("Graph panel");
	}

	@Override
	public Component getComponent() {
		return this;
	}

}
