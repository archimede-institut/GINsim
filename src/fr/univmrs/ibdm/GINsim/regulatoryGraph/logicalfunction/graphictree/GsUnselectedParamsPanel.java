package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Vector;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.*;
import javax.swing.plaf.metal.MetalCheckBoxUI;
import sun.swing.SwingUtilities2;
import javax.swing.text.View;
import javax.swing.plaf.basic.BasicHTML;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Enumeration;
import javax.swing.tree.TreePath;
import java.util.Hashtable;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;

public class GsUnselectedParamsPanel extends JPanel implements ItemListener {
  class GsCheckBoxUI extends MetalCheckBoxUI {
    public synchronized void paint(Graphics g, JComponent c) {
      AbstractButton b = (AbstractButton) c;
      ButtonModel model = b.getModel();

      Dimension size = c.getSize();

      Font f = c.getFont();
      g.setFont(f);
      FontMetrics fm = SwingUtilities2.getFontMetrics(c, g, f);

      Rectangle viewRect = new Rectangle(size);
      Rectangle iconRect = new Rectangle();
      Rectangle textRect = new Rectangle();

      Insets i = c.getInsets();
      viewRect.x += i.left;
      viewRect.y += i.top;
      viewRect.width -= (i.right + viewRect.x);
      viewRect.height -= (i.bottom + viewRect.y);

      String text = SwingUtilities.layoutCompoundLabel(
        c, fm, b.getText(), getDefaultIcon(),
        b.getVerticalAlignment(), b.getHorizontalAlignment(),
        b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
        viewRect, iconRect, textRect, b.getIconTextGap());

      // fill background
      if (c.isOpaque()) {
        g.setColor(b.getBackground());
        g.fillRect(0, 0, size.width, size.height);
      }

      // Draw the Text
      if (text != null) {
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null)
          v.paint(g, textRect);
        else {
          int mnemIndex = b.getDisplayedMnemonicIndex();
          if(model.isEnabled())
            // *** paint the text normally
            g.setColor(b.getForeground());
          else
            // *** paint the text disabled
            g.setColor(getDisabledTextColor());
           SwingUtilities2.drawStringUnderlineCharAt(c,g,text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
        }
      }

      // Paint the radio button
      ((Graphics2D)g).scale(0.7, 0.7);
      getDefaultIcon().paintIcon(c, g, iconRect.x + 1 + (int)((float)iconRect.width * 0.15),
                                 iconRect.y + 1 + (int)((float)iconRect.height * 0.15));
      ((Graphics2D)g).scale(1, 1);
    }
  }
  private static Font defaultFont = new Font("monospaced", Font.PLAIN, 10);
  private Hashtable checkBoxes;
  private JTree tree;
  private GsFunctionPanel functionPanel;

  public GsUnselectedParamsPanel(Vector v, boolean sel, JTree tree, GsFunctionPanel functionPanel) {
    super();
    this.tree = tree;
    this.functionPanel = functionPanel;
    JCheckBox cb;
    setLayout(new GridBagLayout());
    setBorder(null);
    checkBoxes = new Hashtable();
    if (v != null) {
      for (int k = 0; k < v.size(); k++) {
        cb = new JCheckBox(v.elementAt(k).toString(), true) {
          public Insets getInsets() {
            return new Insets(1, 1, 0, 0);
          }
        };
        checkBoxes.put(cb, v.elementAt(k));
        cb.setUI(new GsCheckBoxUI());
        cb.setFont(defaultFont);
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 13));
        cb.setOpaque(true);
        if (sel)
          cb.setBackground(Color.orange);
        else
          cb.setBackground(Color.white);
        add(cb, new GridBagConstraints(0, k, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                       GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 0, 0));
        cb.addItemListener(this);
      }
      if (sel)
        setBackground(Color.orange);
      else
        setBackground(Color.white);
    }
  }
  public void itemStateChanged(ItemEvent e) {
    JCheckBox cb = (JCheckBox)e.getItem();
    GsTreeElement treeElement = (GsTreeElement)checkBoxes.get(cb);
    cb.removeItemListener(this);
    treeElement.setChecked(true);
    if (checkBoxes.size() == 1) functionPanel.hideButtonPressed();
    if (tree != null) {
      Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      TreePath tp = tree.getSelectionPath();
      ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
      tree.stopEditing();
      ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged(treeElement.getParent());
      while (enu.hasMoreElements()) tree.expandPath((TreePath)enu.nextElement());
      tree.setSelectionPath(tp);
      treeElement.getParent().setChecked(true);
    }
    cb.addItemListener(this);
  }
}
