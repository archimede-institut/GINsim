package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import fr.univmrs.ibdm.GINsim.util.widget.GsJButton;
import fr.univmrs.ibdm.GINsim.util.widget.GsJSpinner;

public class GsValuePanel extends GsBooleanFunctionTreePanel implements ActionListener, ChangeListener, MouseListener, MouseMotionListener {
  private static final long serialVersionUID = 207002545507075699L;
  private JButton addButton;
  private JSpinner spinner;

  public GsValuePanel(GsTreeElement value, JTree tree, boolean sel, int width, boolean edit) {
    super(value, tree, sel, width);
    addButton = new GsJButton("add.png");
    addButton.addActionListener(this);
    if (treeElement.getProperty("null function") == null)
      treeElement.setProperty("null function", new Boolean(false));
    else if (((Boolean)treeElement.getProperty("null function")).booleanValue())
      addButton.setEnabled(false);

    SpinnerNumberModel snm = new SpinnerNumberModel();
    snm.setValue(Integer.valueOf(treeElement.toString()));
    snm.setMinimum(new Integer(((GsTreeInteractionsModel)tree.getModel()).getVertex().getBaseValue()));
    snm.setMaximum(new Integer(((GsTreeInteractionsModel)tree.getModel()).getVertex().getMaxValue()));
    snm.setStepSize(new Integer(1));
    treeElement.setProperty("value", Integer.valueOf(treeElement.toString()));
    spinner = new GsJSpinner(snm);
    ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setEditable(false);
    ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setBackground(Color.white);
    spinner.setFont(defaultFont);
    spinner.addChangeListener(this);
    spinner.setBorder(null);
    add(addButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                          GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    add(spinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                        GridBagConstraints.NONE, new Insets(2, 5, 2, 2), 0, 0));
    if (edit) {
      setBackground(Color.cyan);
      spinner.setBackground(Color.cyan);
    }
    else if (sel) {
      setBackground(Color.yellow);
      spinner.setBackground(Color.yellow);
    }
    else {
      setBackground(Color.white);
      spinner.setBackground(Color.white);
    }
  }
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == addButton) {
      addButton.setEnabled(false);
      Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      addButtonAction();
      while (enu.hasMoreElements()) tree.expandPath((TreePath)enu.nextElement());
    }
  }
  public void addButtonAction() {
    try {
      ((GsTreeInteractionsModel)tree.getModel()).addEmptyExpression((short)((GsTreeValue)treeElement).getValue(),
        ((GsTreeInteractionsModel)tree.getModel()).getVertex());
      ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
      treeElement.setProperty("null function", new Boolean(true));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  public void stateChanged(ChangeEvent e) {
    GsTreeInteractionsModel model = (GsTreeInteractionsModel)tree.getModel();
    GsTreeValue value;
    boolean ok = true;
    int val, oldValue;
    Integer newValue;

    oldValue = ((Integer)treeElement.getProperty("value")).intValue();
    val = ((Integer)spinner.getValue()).intValue();
    for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
      value = (GsTreeValue)model.getChild(model.getRoot(), i);
      if (value.getValue() == val) {
        ok = false;
        break;
      }
    }
    if (!ok)
      if (oldValue < val) {
        newValue = new Integer(val + 1);
        if (newValue.compareTo(((SpinnerNumberModel)spinner.getModel()).getMaximum()) <= 0)
          spinner.setValue(newValue);
        else
          spinner.setValue(new Integer(oldValue));
      }
      else if (oldValue > val){
        newValue = new Integer(val - 1);
        if (newValue.compareTo(((SpinnerNumberModel)spinner.getModel()).getMinimum()) >= 0)
          spinner.setValue(newValue);
        else
          spinner.setValue(new Integer(oldValue));
      }
      else
        model.refreshVertex();
    else
      model.updateValue((short)val, (short)oldValue);
  }
  public void mouseClicked(MouseEvent e) {
  }
  public void mouseEntered(MouseEvent e) {
  }
  public void mouseExited(MouseEvent e) {
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
