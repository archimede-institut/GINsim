package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import java.awt.*;

import javax.swing.JPanel;
import javax.swing.UIManager;

public class DTreePanel extends JPanel {
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

  public DTreePanel() {
    super();
    title = "";
    setLayout(new GridBagLayout());
  }
  public DTreePanel(String tit) {
    this();
    title = tit;
  }
	public Insets getInsets() {
		return new Insets(1, 0, 1, 0);
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
