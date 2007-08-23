package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;

import javax.swing.*;
//import javax.swing.plaf.basic.BasicHTML;
//import javax.swing.plaf.metal.MetalCheckBoxUI;
//import javax.swing.text.View;
//import javax.swing.tree.TreePath;

//import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
//import fr.univmrs.ibdm.GINsim.util.widget.GsJCheckBox;

public class GsUnselectedParamsPanel extends JPanel /*implements ItemListener*/ {
/*  private static final long serialVersionUID = -3205548998282223157L;
  private static final Color editColor = new Color(164, 215, 164);

  class GsCheckBoxUI extends MetalCheckBoxUI {
    public synchronized void paint(Graphics g, JComponent c) {
      AbstractButton b = (AbstractButton) c;
      ButtonModel model = b.getModel();

      Dimension size = c.getSize();

      Font f = c.getFont();
      g.setFont(f);
      FontMetrics fm = g.getFontMetrics(f);

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
          if(model.isEnabled())
            // *** paint the text normally
            g.setColor(b.getForeground());
          else
            // *** paint the text disabled
            g.setColor(getDisabledTextColor());
          g.drawString(text, textRect.x, textRect.y + fm.getAscent());
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

  public GsUnselectedParamsPanel(Vector v, boolean sel, JTree tree, GsFunctionPanel functionPanel, boolean edit) {
    super();
    this.tree = tree;
    this.functionPanel = functionPanel;
    JCheckBox cb;
    setLayout(new GridBagLayout());
    setBorder(null);
    checkBoxes = new Hashtable();
    if (v != null) {
      for (int k = 0; k < v.size(); k++) {
        cb = new GsJCheckBox(v.elementAt(k).toString(), true);
        checkBoxes.put(cb, v.elementAt(k));
        //cb.setUI(new GsCheckBoxUI());
        cb.setFont(defaultFont);
        cb.setPreferredSize(new Dimension((int)cb.getPreferredSize().getWidth(), 13));
        cb.setOpaque(true);
        if (edit)
          cb.setBackground(editColor);
        else if (sel)
          cb.setBackground(Color.orange);
        else
          cb.setBackground(Color.white);
        add(cb, new GridBagConstraints(0, k, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                       GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 0, 0));
        cb.addItemListener(this);
      }
      if (edit)
        setBackground(editColor);
      else if (sel)
        setBackground(Color.orange);
      else
        setBackground(Color.white);
    }
  }
  public void itemStateChanged(ItemEvent e) {
    JCheckBox cb = (JCheckBox)e.getItem();
    GsTreeElement treeElement = (GsTreeElement)checkBoxes.get(cb);
    cb.removeItemListener(this);
    //treeElement.setChecked(true);
    //if (checkBoxes.size() == 1) functionPanel.hideButtonPressed();
    if (tree != null) {
      Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      TreePath tp = tree.getSelectionPath();
      ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
      ((GsTreeInteractionsModel)tree.getModel()).setRootInfos();
      ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged(treeElement.getParent());
      while (enu.hasMoreElements()) tree.expandPath((TreePath)enu.nextElement());
      tree.setSelectionPath(tp);
      //treeElement.getParent().setChecked(true);
      treeElement.getParent().setEdited(true);
      tree.startEditingAtPath(tp);
    }
    cb.addItemListener(this);
  }*/
}
