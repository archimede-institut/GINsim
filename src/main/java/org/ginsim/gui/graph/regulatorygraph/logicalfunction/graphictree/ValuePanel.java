package org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;

import org.ginsim.commongui.utils.ImageLoader;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeInteractionsModel;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeElement;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeValue;
import org.ginsim.gui.utils.widgets.GsButton;


public class ValuePanel extends BooleanFunctionTreePanel implements ActionListener, ChangeListener, MouseListener, MouseMotionListener {
  private static final long serialVersionUID = 207002545507075699L;
  private JButton addButton;
  private JSpinner spinner;

  public ValuePanel(TreeElement value, JTree tree, boolean sel, int width, boolean edit) {
    super(value, tree, sel, width);
    addButton = new GsButton(ImageLoader.getImageIcon("add.png"));
    addButton.addActionListener(this);
    if (treeElement.getProperty("null function") == null) {
		treeElement.setProperty("null function", new Boolean(false));
	} else if (((Boolean)treeElement.getProperty("null function")).booleanValue()) {
		addButton.setEnabled(false);
	}
    buttonPanel.add(Box.createVerticalGlue());
    buttonPanel.add(addButton);
    buttonPanel.add(Box.createVerticalGlue());

    SpinnerNumberModel snm = new SpinnerNumberModel();
    snm.setValue(Integer.valueOf(treeElement.toString()));
    snm.setMinimum(new Integer(0));
    snm.setMaximum(new Integer(((TreeInteractionsModel)tree.getModel()).getNode().getMaxValue()));
    snm.setStepSize(new Integer(1));
    treeElement.setProperty("value", Integer.valueOf(treeElement.toString()));
    spinner = new JSpinner(snm);
    if (System.getProperty("os.name").indexOf("Mac") >= 0) {
		spinner.setFont(new Font("courier", Font.PLAIN, 7));
	}
    spinner.setPreferredSize(new Dimension(5 * charWidth, charHeight + 6));
    ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setEditable(false);
    ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setBackground(Color.white);
    spinner.setFont(defaultFont);
    spinner.addChangeListener(this);
    spinner.setBorder(null);
    JPanel bouche = new JPanel();
    bouche.setPreferredSize(new Dimension(width - 9, charHeight));
    add(buttonPanel, BorderLayout.WEST);

    JPanel spinnerPanel = new JPanel();
    spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));
    spinnerPanel.setBackground(Color.white);
    spinnerPanel.add(spinner);
    spinnerPanel.add(bouche);
    spinnerPanel.setOpaque(false);
    spinner.setOpaque(false);
    add(spinnerPanel, BorderLayout.CENTER);
    bouche.setOpaque(false);
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
      while (enu.hasMoreElements()) {
		tree.expandPath((TreePath)enu.nextElement());
	}
    }
  }
  public void addButtonAction() {
    try {
      ((TreeInteractionsModel)tree.getModel()).addEmptyExpression((byte)((TreeValue)treeElement).getValue(),
        ((TreeInteractionsModel)tree.getModel()).getNode());
      ((TreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((TreeElement)tree.getModel().getRoot());
      treeElement.setProperty("null function", new Boolean(true));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  public void stateChanged(ChangeEvent e) {
    TreeInteractionsModel model = (TreeInteractionsModel)tree.getModel();
    TreeValue value;
    boolean ok = true;
    int val, oldValue;
    Integer newValue;

    oldValue = ((Integer)treeElement.getProperty("value")).intValue();
    val = ((Integer)spinner.getValue()).intValue();
    for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
      value = (TreeValue)model.getChild(model.getRoot(), i);
      if (value.getValue() == val) {
        ok = false;
        break;
      }
    }
    if (!ok) {
		if (oldValue < val) {
		    newValue = new Integer(val + 1);
		    if (newValue.compareTo((Integer)((SpinnerNumberModel)spinner.getModel()).getMaximum()) <= 0) {
				spinner.setValue(newValue);
			} else {
				spinner.setValue(new Integer(oldValue));
			}
		  }
		  else if (oldValue > val){
		    newValue = new Integer(val - 1);
		    if (newValue.compareTo((Integer)((SpinnerNumberModel)spinner.getModel()).getMinimum()) >= 0) {
				spinner.setValue(newValue);
			} else {
				spinner.setValue(new Integer(oldValue));
			}
		  } else {
			model.refreshNode();
		}
	} else {
		model.updateValue((byte)val, (byte)oldValue);
	}
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
