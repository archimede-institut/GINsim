package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class GsValuePanel extends GsBooleanFunctionTreePanel implements ActionListener, ChangeListener {
private static final long serialVersionUID = 207002545507075699L;
private JButton addButton, delButton;
  private JSpinner spinner;

  public GsValuePanel(GsTreeElement value, JTree tree, boolean sel, int width) {
    super(value, tree, sel, width);
    addButton = new GsJButton("add.png");
    addButton.addActionListener(this);
    delButton = new GsJButton("close.png");
    delButton.addActionListener(this);
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
    spinner.setBackground(Color.white);
    add(addButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                          GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    add(delButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                          GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    add(spinner, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                          GridBagConstraints.NONE, new Insets(2, 5, 2, 2), 0, 0));
  }
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == delButton) {
      tree.stopEditing();
      treeElement.remove();
      ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
      ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
    }
    else if (e.getSource() == addButton) {
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
}
