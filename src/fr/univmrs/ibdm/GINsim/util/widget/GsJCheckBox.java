package fr.univmrs.ibdm.GINsim.util.widget;

import java.awt.Insets;

import javax.swing.JCheckBox;

public class GsJCheckBox extends JCheckBox {
	private static final long serialVersionUID = 252777606612289644L;
	
	public GsJCheckBox() {
	}
	
	public GsJCheckBox(String text, boolean selected) {
		super(text, selected);
	}
	
	public Insets getInsets() {
		return new Insets(2, 2, 2, 2);
	}
}
