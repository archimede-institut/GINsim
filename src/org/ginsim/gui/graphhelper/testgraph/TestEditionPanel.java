package org.ginsim.gui.graphhelper.testgraph;

import java.awt.Component;

import javax.swing.JLabel;

import org.ginsim.gui.graph.GUIEditor;

@SuppressWarnings("serial")
public class TestEditionPanel<T> extends JLabel implements GUIEditor<T> {

	private final String baseText;
	
	public TestEditionPanel(String baseText) {
		super(baseText);
		this.baseText = baseText + " panel for: ";
	}

	@Override
	public void setEditedItem(T item) {
		setText(baseText + item);
	}

	@Override
	public Component getComponent() {
		return this;
	}
}
