package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTree;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;

public abstract class GsBooleanFunctionTreePanel extends JPanel {
  private static final long serialVersionUID = -1420693226899522868L;

  protected static Font defaultFont = new Font("monospaced", Font.BOLD, 12);
  protected GsTreeElement treeElement;
  protected JTree tree = null;
  protected static int charWidth = 7, charHeight = 12;
  protected int width = 0;
  protected boolean selected;
  protected String text;
  protected MouseListener mouseListener;
  protected MouseMotionListener mouseMotionListener;
  protected JPanel buttonPanel;

  public GsBooleanFunctionTreePanel(Object value, JTree tree, boolean sel, int w) {
    super();
    setBackground(Color.white);
    treeElement = (GsTreeElement)value;
    treeElement.setEditable(false);
    this.tree = tree;
    selected = sel;
    width = w;
    text = treeElement.toString();
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    buttonPanel.setBackground(Color.white);
    buttonPanel.setOpaque(false);
    setLayout(new BorderLayout(2, 2));
  }
  public void paint(Graphics g) {
    charWidth = g.getFontMetrics(defaultFont).charWidth('A');
    charHeight = g.getFontMetrics(defaultFont).getHeight();
    super.paint(g);
    if (selected) {
      g.setColor(Color.blue);
      g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
    if (treeElement.isDropable()) {
      g.setColor(Color.red);
      g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
  }
  public void setMouseListener(MouseListener ml) {
    mouseListener = ml;
  }
  public void setMouseMotionListener(MouseMotionListener mml) {
    mouseMotionListener = mml;
  }
  public MouseListener getMouseListener() {
    return mouseListener;
  }
  public MouseMotionListener getMouseMotionListener() {
    return mouseMotionListener;
  }
  public Insets getInsets() {
    return new Insets(2, 2, 2, 2);
  }
  public void updateSize() {

  }
}
