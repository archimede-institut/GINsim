package fr.univmrs.ibdm.GINsim.util.widget;

import java.awt.Insets;

import javax.swing.JButton;

import fr.univmrs.ibdm.GINsim.global.GsEnv;

public class GsJButton extends JButton {
  private static final long serialVersionUID = -6823613455585788403L;
  private Insets insets;

  public GsJButton(String iconName) {
    super(GsEnv.getIcon(iconName));
    insets = new Insets(2, 2, 2, 2);
  }
  public GsJButton(String iconName, int left, int right, int top, int bottom) {
    super(GsEnv.getIcon(iconName));
    insets = new Insets(top, left, bottom, right);
  }
  public Insets getInsets() {
    return insets;
  }
}
