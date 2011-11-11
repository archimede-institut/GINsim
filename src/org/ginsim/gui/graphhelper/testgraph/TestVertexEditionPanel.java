package org.ginsim.gui.graphhelper.testgraph;

import java.awt.Component;

import javax.swing.JLabel;

import org.ginsim.graph.testGraph.TestVertex;
import org.ginsim.gui.graph.GUIEditor;

@SuppressWarnings("serial")
public class TestVertexEditionPanel extends JLabel implements GUIEditor<TestVertex> {

	@Override
	public void setEditedItem(TestVertex item) {
		setText("Vertex panel");
	}

	@Override
	public Component getComponent() {
		return this;
	}

}
