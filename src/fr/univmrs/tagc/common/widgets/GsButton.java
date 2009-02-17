package fr.univmrs.tagc.common.widgets;

import java.awt.Insets;

import javax.swing.JButton;

import fr.univmrs.tagc.common.manageressources.ImageLoader;
import javax.swing.ImageIcon;

public class GsButton extends JButton {
  private static final long serialVersionUID = -6823613455585788403L;
  private Insets insets;

  public GsButton(String text) {
    super(text);
    insets = new Insets(2, 2, 2, 2);
  }
  public void setInsets(int left, int right, int top, int bottom) {
  	insets = new Insets(top, left, bottom, right);
  }
  public GsButton(ImageIcon icon) {
    super(icon);
    insets = new Insets(2, 2, 2, 2);
  }
  public GsButton(String iconName, int left, int right, int top, int bottom) {
    super(ImageLoader.getImageIcon(iconName));
    insets = new Insets(top, left, bottom, right);
  }
  public Insets getInsets() {
    return insets;
  }
}
