package fr.univmrs.ibdm.GINsim.util.widget;

import java.awt.Insets;

import javax.swing.JButton;

import fr.univmrs.ibdm.GINsim.global.GsEnv;

public class GsJButton extends JButton {
	private static final long serialVersionUID = -6823613455585788403L;

	public GsJButton(String iconName) {
		super(GsEnv.getIcon(iconName));
	}
	
	public Insets getInsets() {
        return new Insets(2, 2, 2, 2);
      }
}
