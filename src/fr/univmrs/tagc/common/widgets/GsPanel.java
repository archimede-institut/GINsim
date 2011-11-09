package fr.univmrs.tagc.common.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.UIManager;

public class GsPanel extends JPanel {
  private static final long serialVersionUID = -2899156169561371789L;
  public static Color shCol = UIManager.getColor("TextField.darkShadow");
  public static final int WEST = GridBagConstraints.WEST;
  public static final int NORTHWEST = GridBagConstraints.NORTHWEST;
  public static final int NORTH = GridBagConstraints.NORTH;
  public static final int CENTER = GridBagConstraints.CENTER;
  public static final int SOUTHEAST = GridBagConstraints.SOUTHEAST;
  public static final int NORTHEAST = GridBagConstraints.NORTHEAST;
  public static final int SOUTHWEST = GridBagConstraints.SOUTHWEST;
  public static final int NONE = GridBagConstraints.NONE;
  public static final int HORIZONTAL = GridBagConstraints.HORIZONTAL;
  public static final int VERTICAL = GridBagConstraints.VERTICAL;
  public static final int BOTH = GridBagConstraints.BOTH;
  public static final int EAST = GridBagConstraints.EAST;

  private String title;

  public GsPanel() {
    super();
    title = "";
    setLayout(new GridBagLayout());
  }
  public GsPanel(String tit) {
    this();
    title = tit;
  }
  public void addComponent(Component comp, int gridx, int gridy, int gridwidth, int gridheight,
                           double weightx, double weighty, int anchor, int fill, int insetstop, int insetsleft,
                           int insetsbottom, int insetsright, int ipadx, int ipady) {
    add(comp, new GridBagConstraints(gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill,
        new Insets(insetstop, insetsleft, insetsbottom, insetsright), ipadx, ipady));
  }
  public String getTitle() {
    return title;
  }
}
