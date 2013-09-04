package org.ginsim.gui.shell.editpanel;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.core.graph.view.style.StyleManager;

public class StyleManagerPanel extends JPanel {

	private final StyleManager manager;
	
	public StyleManagerPanel(StyleManager manager) {
		this.manager = manager;
		
		add(new JLabel("TODO: style manager"));
	}
}
