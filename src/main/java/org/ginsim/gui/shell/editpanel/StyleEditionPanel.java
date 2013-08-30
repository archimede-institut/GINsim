package org.ginsim.gui.shell.editpanel;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.core.graph.view.style.Style;

public class StyleEditionPanel extends JPanel {

	JLabel label = new JLabel();
	
	public StyleEditionPanel() {
		add(label);
		setStyle(null);
	}
	
	public void setStyle(Style style) {
		if (style == null) {
			label.setText("no style to edit");
			return;
		}
		
		label.setText("TODO: edit properties of "+style);
	}
}
