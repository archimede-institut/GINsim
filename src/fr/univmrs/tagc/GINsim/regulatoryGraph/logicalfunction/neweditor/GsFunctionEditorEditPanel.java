package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.DTreeNodeBuilder;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.DTreeTableBuilder;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.AbstractDTreeElement;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DTreeElement;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DTreeElementSelectable.JCB;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DTreeElementToggleButton;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DTreeModel;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DecoTree;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.table.DecoTreeTable;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.common.widgets.GsButton;
import fr.univmrs.tagc.common.widgets.GsPanel;

public class GsFunctionEditorEditPanel extends GsPanel implements ItemListener, ActionListener {
	private static final long serialVersionUID = 7097954459263383619L;
	class EditorTableRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 353936508495109143L;
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column != 0) {
				setHorizontalAlignment(JLabel.CENTER);
				setForeground(Color.black);
				setFont(new Font("arial", Font.PLAIN, 12));
			}
			else {
				setHorizontalAlignment(JLabel.LEFT);
				setForeground(Color.green.darker().darker());
				setFont(new Font("courier", Font.BOLD, 11));
			}
			return this;
		}
	}
	class ToggleButtonListener implements ActionListener {
		private AbstractDTreeElement node;
		private GsFunctionEditorEditPanel panel;

		public ToggleButtonListener(GsFunctionEditorEditPanel p) {
			panel = p;
		}
		public void setNode(AbstractDTreeElement n) {
			node = n;
			((GsListInteraction)node.getUserObject()).setNode((DTreeElementToggleButton)n);
		}
		public void actionPerformed(ActionEvent e) {
			boolean b = ((JToggleButton)e.getSource()).isSelected();
			setNodeState(b);
			//if (node.isSelected())
				panel.notClicked((GsListInteraction)node.getUserObject());
		}
		public void setNodeState(boolean b) {
			((GsListInteraction)node.getUserObject()).setNot(b);
		}
	}
	private DecoTreeTable logicalParametersTable;

	private GsButton previousButton, nextButton, deleteButton, andButton, orButton, clearButton, cancelButton, okButton;
	private JRadioButton andRadioButton, orRadioButton;
	private JCheckBox parCheckBox, notCheckBox;
	private GsFunctionEditorModel editorModel;
	private Color darkGreen = new Color(13, 87, 37);
	private Color lightGreen = new Color(12, 122, 52);
	private ImageIcon ic_on, ic_off;
	private GsFunctionEditorControler controler;

  public GsFunctionEditorEditPanel() {
  	super();
  	try {
			URL url = JFrame.class.getResource("/fr/univmrs/tagc/GINsim/resources/icons/notred.png");
			ic_on = new ImageIcon(url);
			url = JFrame.class.getResource("/fr/univmrs/tagc/GINsim/resources/icons/notgrey.png");
			ic_off = new ImageIcon(url);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
    UIManager.put("ScrollBar.width", new Integer(16));
    initGraphic();
    initListeners();
  }
	public void setControler(GsFunctionEditorControler c) {
		controler = c;
	}
  public void init(GsFunctionEditorModel m) {
  	editorModel = m;
    Point p = editorModel.getSelectedArea();
    if (p.x == p.y) {
    	controler.exec(GsFunctionEditorControler.ADD_EMPTY_TERM, editorModel.getSelectedArea());
    	parCheckBox.setSelected(false);
    }
		editorModel.getFunctionPanel().getTreeExpression().setSelection(p, (editorModel.getInteractionList() != null));
    updateTable();
  	updateSelectionTable();
		updateControls();
  }
	public void updateControls() {
		if (editorModel.getInteractionList() != null) {
			logicalParametersTable.setEnabled(true);
			String s = (String)controler.exec(GsFunctionEditorControler.GET_SELECTED_STRING, editorModel.getSelectedArea());
			s = s.replaceAll(" ", "");
			notCheckBox.removeItemListener(this);
			parCheckBox.removeItemListener(this);
			notCheckBox.setEnabled(false);
			parCheckBox.setEnabled(false);
			if (s.startsWith("!"))
				parCheckBox.setSelected((s.charAt(1) == '(') && s.endsWith(")"));
			else
				parCheckBox.setSelected(s.startsWith("(") && s.endsWith(")"));
			notCheckBox.setSelected(s.startsWith("!") & parCheckBox.isSelected());
			notCheckBox.setEnabled(parCheckBox.isSelected());
			parCheckBox.setEnabled(!notCheckBox.isSelected());
			notCheckBox.addItemListener(this);
			parCheckBox.addItemListener(this);
			orRadioButton.setEnabled(true);
			andRadioButton.setEnabled(true);
			if (s.indexOf('|') >= 0) {
				orRadioButton.setSelected(true);
				andRadioButton.setSelected(false);
			}
			else {
				andRadioButton.setSelected(true);
				orRadioButton.setSelected(false);
			}
		}
		else {
			logicalParametersTable.setEnabled(false);
			notCheckBox.setEnabled(false);
			parCheckBox.setEnabled(false);
			orRadioButton.setEnabled(false);
			andRadioButton.setEnabled(false);
		}
	}
	private void updateTable() {
		DTreeNodeBuilder nb = new DTreeNodeBuilder(true);
		DTreeTableBuilder tb = new DTreeTableBuilder(nb);
		AbstractDTreeElement node;
		ToggleButtonListener al;
		tb.clearTree(logicalParametersTable);

		for (int i = 0; i < editorModel.getInteractions().size(); i++) {
			GsRegulatoryMultiEdge o = (GsRegulatoryMultiEdge)editorModel.getInteractions().elementAt(i);
			tb.newNode("", darkGreen);
			nb.setNode();
			nb.setSelectable(false, this);
			al = new ToggleButtonListener(this);
			nb.addToggleButton(ic_off, ic_on, null, al);
			node = nb.getNode();
			node.setUserObject(new GsListInteraction(o, -1, false));
			al.setNode(node);
			nb.addLabel(o.getSource().toString(), darkGreen);
			node = nb.getNode();
			tb.addNode(node);
      for (int j = 0; j < o.getEdgeCount(); j++) {
      	tb.newNode("", lightGreen);
    		nb.setSelectable(false, this);
    		al = new ToggleButtonListener(this);
       	nb.addToggleButton(ic_off, ic_on, null, al);
				node = nb.getNode();
				node.setUserObject(new GsListInteraction(o, j, false));
				al.setNode(node);
				nb.addLabel(o.getEdge(j).getShortInfo(), lightGreen);
    		nb.addValue(GsRegulatoryMultiEdge.SIGN_SHORT[o.getSign(j)], false).addValue(new Integer(o.getMin(j)), false).addValue(o.getEdge(j).getMaxAsString(), false);
    		node = nb.getNode();
    		tb.addNode(node);
      }
      tb.decreaseLevel();
    }
		tb.updateTree();
		tb.expandtree();
		logicalParametersTable.getColumnModel().getColumn(0).setPreferredWidth(logicalParametersTable.getWidth() - 105);
		logicalParametersTable.getColumnModel().getColumn(1).setPreferredWidth(35);
		logicalParametersTable.getColumnModel().getColumn(2).setPreferredWidth(35);
		logicalParametersTable.getColumnModel().getColumn(3).setPreferredWidth(35);
  }
  public void updateSelectionTable() {
  	Vector interactionList = editorModel.getInteractionList();
		DecoTree t = (DecoTree)logicalParametersTable.getTree();
		DTreeModel m = (DTreeModel)t.getModel();
		AbstractDTreeElement root = (AbstractDTreeElement)m.getRoot();
		GsListInteraction l;
		for (int i = 0; i < root.getChildCount(); i++) {
			l = (GsListInteraction)root.getChild(i).getUserObject();
			root.getChild(i).check(false);
			l.setNot(false);
			for (int j = 0; j < root.getChild(i).getChildCount(); j++) {
				l = (GsListInteraction)root.getChild(i).getChild(j).getUserObject();
				root.getChild(i).getChild(j).check(false);
				l.setNot(false);
			}
		}
		if (interactionList != null) {
			Enumeration enu = interactionList.elements();
			GsListInteraction li;
			while (enu.hasMoreElements()) {
				li = (GsListInteraction)enu.nextElement();
				for (int i = 0; i < root.getChildCount(); i++) {
					l = (GsListInteraction)root.getChild(i).getUserObject();
					if (li.equalsIgnoreNot(l)) {
						root.getChild(i).check(true);
						l.setNot(li.getNot());
						break;
					}
					else {
						for (int j = 0; j < root.getChild(i).getChildCount(); j++) {
							l = (GsListInteraction)root.getChild(i).getChild(j).getUserObject();
							if (li.equalsIgnoreNot(l)) {
								root.getChild(i).getChild(j).check(true);
								l.setNot(li.getNot());
								break;
							}
						}
						if (li.equals(l)) break;
					}
				}
			}
		}
		logicalParametersTable.repaint();
  }
	private void initGraphic() {
    GsPanel mainPane = new GsPanel();
    addComponent(mainPane, 0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 0, 0, 0, 0, 0, 0);
    DTreeNodeBuilder nb = new DTreeNodeBuilder(true);
		DTreeTableBuilder tb = new DTreeTableBuilder(nb);

		tb.newTree(18);
		tb.addColumn("").addColumn("Sign").addColumn("Min").addColumn("Max");

		DTreeElement el = tb.newNode("Interactions", Color.black);
		nb.setNode();
		tb.addNode(nb.getNode());

    logicalParametersTable = tb.getTable();
    logicalParametersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		logicalParametersTable.setDefaultRenderer(String.class, new EditorTableRenderer());
    logicalParametersTable.setSelectionBackground(Color.yellow.brighter());
    JScrollPane sp = new JScrollPane(logicalParametersTable);
    sp.getViewport().setPreferredSize(logicalParametersTable.getPreferredSize());
    mainPane.addComponent(sp, 0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 5, 2, 0, 0, 0, 0);

    GsPanel editionPanel = new GsPanel();
    editionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Edition"));
    GsPanel modePanel = new GsPanel();
    //modePanel.setBorder(BorderFactory.createLineBorder(Color.black));
    JLabel modeLabel = new JLabel("Mode :");
    //modePanel.addComponent(modeLabel, 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
    andRadioButton = new JRadioButton("And", true);
    modePanel.addComponent(andRadioButton, 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 0, 0, 0, 0, 0, 0);
    orRadioButton = new JRadioButton("Or");
    modePanel.addComponent(orRadioButton, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 0, 5, 0, 0, 0, 0);
		ButtonGroup bg = new ButtonGroup();
		bg.add(andRadioButton);
		bg.add(orRadioButton);
    GsPanel flagsPanel = new GsPanel();
    //flagsPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    JLabel flagsLabel = new JLabel("Flags :");
    //flagsPanel.addComponent(flagsLabel, 0, 0, 1, 1, 0.0, 0.0, EAST, NONE, 5, 5, 5, 0, 0, 0);
    parCheckBox = new JCheckBox("Par", true);
    flagsPanel.addComponent(parCheckBox, 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 0, 0, 0, 0, 0, 0);
    notCheckBox = new JCheckBox("Not");
    flagsPanel.addComponent(notCheckBox, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 0, 5, 0, 0, 0, 0);

    GsPanel p1 = new GsPanel();
		p1.setBorder(BorderFactory.createLineBorder(Color.black));
		p1.addComponent(modeLabel, 0, 0, 1, 1, 0.0, 0.0, EAST, NONE, 2, 2, 0, 0, 0, 0);
    p1.addComponent(modePanel, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 2, 5, 0, 2, 0, 0);
		p1.addComponent(flagsLabel, 0, 1, 1, 1, 0.0, 0.0, EAST, NONE, 2, 2, 2, 0, 0, 0);
    p1.addComponent(flagsPanel, 1, 1, 1, 1, 0.0, 0.0, WEST, NONE, 2, 5, 2, 2, 0, 0);

    //editionPanel.addComponent(modePanel, 0, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, 2, 2, 0, 2, 0, 0);
		//editionPanel.addComponent(flagsPanel, 0, 1, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, 2, 2, 0, 2, 0, 0);
		editionPanel.addComponent(p1, 0, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, 2, 2, 0, 2, 0, 0);

    GsPanel andOrPanel = new GsPanel();
    andOrPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    andButton = new GsButton("AND");
    andButton.setInsets(4, 4, 2, 2);
    andButton.setForeground(Color.blue);
    andOrPanel.addComponent(andButton, 0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 5, 5, 5, 0, 0, 0);
    orButton = new GsButton("OR");
    orButton.setInsets(4, 4, 2, 2);
    orButton.setForeground(Color.blue);
    andOrPanel.addComponent(orButton, 1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 5, 5, 5, 0, 0, 0);
    clearButton = new GsButton("CLEAR");
    clearButton.setInsets(4, 4, 2, 2);
    clearButton.setForeground(Color.blue);
    andOrPanel.addComponent(clearButton, 2, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 5, 10, 5, 5, 0, 0);
    GsPanel paramsButtonsPanel = new GsPanel();
    paramsButtonsPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    previousButton = new GsButton("previous.png", 4, 4, 2, 2);
    paramsButtonsPanel.addComponent(previousButton, 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
    nextButton = new GsButton("next.png", 4, 4, 2, 2);
    paramsButtonsPanel.addComponent(nextButton, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
    deleteButton = new GsButton("suppr.gif", 4, 4, 2, 2);
    paramsButtonsPanel.addComponent(deleteButton, 2, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);

    //GsPanel p2 = new GsPanel();
    //p2.addComponent(andOrPanel, 0, 0, 1, 1, 0.0, 0.0, NORTH, NONE, 0, 0, 0, 0, 0, 0);
    //p2.addComponent(paramsButtonsPanel, 1, 0, 1, 1, 0.0, 0.0, NORTH, NONE, 0, 5, 0, 0, 0, 0);

    editionPanel.addComponent(andOrPanel, 0, 2, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, 2, 2, 0, 2, 0, 0);
		editionPanel.addComponent(paramsButtonsPanel, 0, 3, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, 2, 2, 0, 2, 0, 0);

    GsPanel okCancelPanel = new GsPanel();
    cancelButton = new GsButton("Cancel");
    cancelButton.setInsets(4, 4, 2, 2);
    okCancelPanel.addComponent(cancelButton, 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
    okButton = new GsButton("OK");
    okButton.setInsets(4, 4, 2, 2);
    okCancelPanel.addComponent(okButton, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
    editionPanel.addComponent(okCancelPanel, 0, 4, 1, 1, 0.0, 0.0, CENTER, NONE, 10, 2, 2, 2, 0, 0);

		mainPane.addComponent(editionPanel, 1, 0, 1, 1, 0.0, 0.0, NORTH, NONE, 5, 5, 2, 2, 0, 0);
	}
	private void initListeners() {
		parCheckBox.addItemListener(this);
		notCheckBox.addItemListener(this);
		andRadioButton.addItemListener(this);
		orRadioButton.addItemListener(this);
		andButton.addActionListener(this);
		orButton.addActionListener(this);
		clearButton.addActionListener(this);
		deleteButton.addActionListener(this);
		nextButton.addActionListener(this);
		previousButton.addActionListener(this);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
	}
	public void setParCheck(boolean b) {
		parCheckBox.setSelected(b);
	}
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == parCheckBox) {
			controler.exec(GsFunctionEditorControler.SET_PARENTHESIS, new Boolean(parCheckBox.isSelected()));
			notCheckBox.setEnabled(parCheckBox.isSelected());
		}
		else if (e.getSource() == notCheckBox) {
			controler.exec(GsFunctionEditorControler.SET_NOT, new Boolean(notCheckBox.isSelected()));
			parCheckBox.setEnabled(!notCheckBox.isSelected());
		}
		else if (e.getSource() == andRadioButton) {
			controler.exec(GsFunctionEditorControler.SET_AND_MODE, new Boolean(andRadioButton.isSelected()));
		}
		else if (e.getSource() == orRadioButton) {
			//controler.exec(GsFunctionEditorControler.SET_AND_MODE, new Boolean(!orRadioButton.isSelected()));
		}
		else {
			JCB cb = (JCB)e.getSource();
			GsListInteraction interaction = (GsListInteraction)cb.getElement().getUserObject();
			Vector v = new Vector();
			v.addElement(interaction);
			v.addElement(andRadioButton.isSelected() ? "&" : "|");
			v.addElement(new Boolean(notCheckBox.isSelected()));
			v.addElement(new Boolean(parCheckBox.isSelected()));
			controler.exec(cb.isSelected() ? GsFunctionEditorControler.MODIF_TERM_ADD : GsFunctionEditorControler.MODIF_TERM_REM, v);
		}
	}
	public void notClicked(GsListInteraction i) {
		Vector v = new Vector();
		v.addElement(i);
		v.addElement(andRadioButton.isSelected() ? "&" : "|");
		v.addElement(new Boolean(notCheckBox.isSelected()));
		v.addElement(new Boolean(parCheckBox.isSelected()));
		controler.exec(GsFunctionEditorControler.MODIF_TERM_NOT, v);
	}

	public void actionPerformed(ActionEvent e) {
		Vector v;
		if ((e.getSource() == andButton)  || (e.getSource() == orButton)) {
			v = new Vector();
			Vector i = new Vector();

			v.addElement(i);
			v.addElement(andRadioButton.isSelected() ? "&" : "|");
			v.addElement(new Boolean(notCheckBox.isSelected()));
			v.addElement(new Boolean(parCheckBox.isSelected()));
			if (e.getSource() == andButton)
				controler.exec(GsFunctionEditorControler.AND, v);
			else
				controler.exec(GsFunctionEditorControler.OR, v);
		}
		else if (e.getSource() == clearButton) {
			AbstractDTreeElement root = (AbstractDTreeElement)logicalParametersTable.getTree().getModel().getRoot();
			logicalParametersTable.getTree().stopEditing();

			logicalParametersTable.clearSelection();
			v = new Vector();
			v.addElement(root);
			v.addElement(new Boolean(andRadioButton.isSelected()));
			v.addElement(new Boolean(notCheckBox.isSelected()));
			v.addElement(new Boolean(parCheckBox.isSelected()));
			controler.exec(GsFunctionEditorControler.CLEAR, v);

			//logicalParametersTable.editingStopped(new ChangeEvent(logicalParametersTable));
			logicalParametersTable.getTree().repaint();
			logicalParametersTable.repaint();
		}
		else if (e.getSource() == deleteButton) {
			controler.exec(GsFunctionEditorControler.DELETE, null);
		}
		else if (e.getSource() == nextButton) {
			controler.exec(GsFunctionEditorControler.NEXT, null);
		}
		else if (e.getSource() == previousButton) {
			controler.exec(GsFunctionEditorControler.PREVIOUS, null);
		}
		else if (e.getSource() == okButton) {
			controler.exec(GsFunctionEditorControler.VALIDATE_EDIT, null);
		}
		else if (e.getSource() == cancelButton) {
			controler.exec(GsFunctionEditorControler.CANCEL_EDIT, null);
		}
	}
}
