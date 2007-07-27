package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.ibdm.GINsim.util.widget.*;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Point;

public class GsManualParamPanel extends GsBooleanFunctionTreePanel implements ActionListener, MouseListener, MouseMotionListener {
  private JButton /*removeButton,*/ editButton;
  private JLabel paramLabel;

  public GsManualParamPanel(GsTreeElement value, JTree tree, boolean sel, int width) {
    super(value, tree, sel, width);
    paramLabel = new JLabel(value.toString());
    paramLabel.setFont(defaultFont);

    if (sel) {
      paramLabel.setBackground(Color.yellow);
      setBackground(Color.yellow);
    }
    else if (value.toString().equals("")) {
      if (((GsTreeInteractionsModel)tree.getModel()).getVertex().getBaseValue() == ((GsTreeValue)value.getParent().getParent()).getValue()) {
        paramLabel.setBackground(Color.cyan);
        setBackground(Color.cyan);
      }
      else {
        paramLabel.setBackground(Color.red);
        setBackground(Color.red);
      }
    }
    else {
      paramLabel.setBackground(Color.white);
      setBackground(Color.white);
    }
    paramLabel.setForeground(value.getForeground());
    //removeButton = new GsJButton("remove.png");
    //removeButton.addActionListener(this);
    editButton = new GsJButton("edit.png");
    editButton.addActionListener(this);
    //add(removeButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
    //                                         GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    add(editButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                             GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    add(paramLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                           GridBagConstraints.NONE, new Insets(2, 5, 2, 2), 0, 0));
    paramLabel.setPreferredSize(new Dimension(width - editButton.getPreferredSize().width, charHeight));
  }
  public void itemStateChanged(ItemEvent e) {

  }
  public void actionPerformed(ActionEvent e) {
    GsTreeInteractionsModel model = (GsTreeInteractionsModel)tree.getModel();
    /*if (e.getSource() == removeButton) {
      treeElement.remove(false);
      if (tree != null) {
        Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
        model.refreshVertex();
        model.setRootInfos();
        tree.stopEditing();
        model.fireTreeStructureChanged(treeElement.getParent());
        while (enu.hasMoreElements())tree.expandPath((TreePath)enu.nextElement());
      }
    }
    else */if (e.getSource() == editButton) {
      GsParameterChoiceWindow choiceWindow;
      TreePath treePath = tree.getSelectionPath();

      choiceWindow = new GsParameterChoiceWindow(tree);
      choiceWindow.init(model.getGraph().getGraphManager().getIncomingEdges(model.getVertex()), defaultFont,
                        treeElement);
      Point p = tree.getPathBounds(treePath).getLocation();
      p.translate(0, - choiceWindow.getPreferredSize().height - 2);
      SwingUtilities.convertPointToScreen(p, tree);
      choiceWindow.setLocation(p);
      choiceWindow.setVisible(true);
    }
  }
  public void mouseEntered(MouseEvent e) {

  }
  public void mouseExited(MouseEvent e) {

  }
  public void mouseClicked(MouseEvent e) {

  }
  public void mousePressed(MouseEvent e) {
    e.setSource(this);
    getMouseListener().mousePressed(e);
  }
  public void mouseReleased(MouseEvent e) {
    e.setSource(this);
    getMouseListener().mouseReleased(e);
  }
  public void mouseMoved(MouseEvent e) {
    e.setSource(this);
    getMouseMotionListener().mouseMoved(e);
  }
  public void mouseDragged(MouseEvent e) {
    e.setSource(this);
    getMouseMotionListener().mouseDragged(e);
  }
}
