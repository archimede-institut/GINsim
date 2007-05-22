package fr.univmrs.ibdm.GINsim.util.widget;

import java.awt.Insets;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

public class GsJSpinner extends JSpinner {
	private static final long serialVersionUID = -5547290338565107673L;
	
	public GsJSpinner(SpinnerModel model) {
		super(model);
	}
	
	public Insets getInsets() {
		return new Insets(0, 2, 0, 0);
	}
}
