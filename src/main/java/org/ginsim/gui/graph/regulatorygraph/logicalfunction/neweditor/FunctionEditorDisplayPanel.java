package org.ginsim.gui.graph.regulatorygraph.logicalfunction.neweditor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;

import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeExpression;
import org.ginsim.gui.utils.widgets.GsButton;
import org.ginsim.gui.utils.widgets.GsPanel;


public class FunctionEditorDisplayPanel extends GsPanel implements ItemListener, ActionListener {
	private static final long serialVersionUID = -945722071153537623L;
	private JRadioButton userInputRadioButton, compactRadioButton, rawDNFRadioButton, quineRadioButton, quineDNF, quineCNF;
	private JProgressBar displayProgressBar;
	private GsButton cancelButton, okButton, cancelQuineButton;
	private FunctionEditorControler controler;
	private TreeExpression expression;
	private String userInput;

	public FunctionEditorDisplayPanel() {
		super();
		initGraphic();
		initListeners();
	}
	private void initGraphic() {
		GsPanel mainPane = new GsPanel();
    addComponent(mainPane, 0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 0, 0, 0, 0, 0, 0);
		GsPanel displayPanel = new GsPanel();
		displayPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Display"));
		userInputRadioButton = new JRadioButton("User input");
		displayPanel.addComponent(userInputRadioButton, 0, 0, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 5, 0, 5, 0, 0);
		compactRadioButton = new JRadioButton("Compact");
		displayPanel.addComponent(compactRadioButton, 0, 1, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 5, 0, 5, 0, 0);
		rawDNFRadioButton = new JRadioButton("Raw DNF");
		displayPanel.addComponent(rawDNFRadioButton, 0, 2, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 5, 0, 5, 0, 0);
		quineRadioButton = new JRadioButton("Quine-McCluskey");
		displayPanel.addComponent(quineRadioButton, 0, 3, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 5, 0, 5, 0, 0);
		quineDNF = new JRadioButton("DNF");
		displayPanel.addComponent(quineDNF, 0, 4, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 20, 0, 5, 0, 0);
		quineCNF = new JRadioButton("CNF");
		displayPanel.addComponent(quineCNF, 0, 5, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 20, 0, 5, 0, 0);
		GsPanel pbPanel = new GsPanel();
		displayProgressBar = new JProgressBar();
		displayProgressBar.setForeground(Color.green);
		pbPanel.addComponent(displayProgressBar, 0, 0, 1, 1, 1.0, 1.0, GsPanel.WEST, GsPanel.BOTH, 0, 0, 0, 0, 0, 0);
		cancelQuineButton = new GsButton("Abort");
		pbPanel.addComponent(cancelQuineButton, 1, 0, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 5, 0, 0, 0, 0);
		displayPanel.addComponent(pbPanel, 0, 6, 1, 1, 1.0, 0.0, GsPanel.WEST, GsPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
		mainPane.addComponent(displayPanel, 0, 0, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
		ButtonGroup bg = new ButtonGroup();
		bg.add(userInputRadioButton);
		bg.add(compactRadioButton);
		bg.add(rawDNFRadioButton);
		bg.add(quineRadioButton);
		bg = new ButtonGroup();
		bg.add(quineDNF);
		bg.add(quineCNF);
		quineDNF.setEnabled(false);
		quineCNF.setEnabled(false);
		GsPanel okCancelPanel = new GsPanel();
		cancelButton = new GsButton("Cancel");
		cancelButton.setInsets(4, 4, 2, 2);
		okCancelPanel.addComponent(cancelButton, 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		okButton = new GsButton("OK");
		okButton.setInsets(4, 4, 2, 2);
		okCancelPanel.addComponent(okButton, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		mainPane.addComponent(okCancelPanel, 0, 1, 1, 1, 0.0, 0.0, CENTER, NONE, 10, 5, 5, 5, 0, 0);
	}
	private void initListeners() {
		quineRadioButton.addItemListener(this);
		compactRadioButton.addItemListener(this);
		userInputRadioButton.addItemListener(this);
		rawDNFRadioButton.addItemListener(this);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		quineDNF.addItemListener(this);
		quineCNF.addItemListener(this);
	}
	public void setControler(FunctionEditorControler c) {
		controler = c;
	}
	public void init(TreeExpression e) {
		expression = e;
		userInput = e.toString();
		userInputRadioButton.setSelected(true);
		compactRadioButton.setSelected(false);
		rawDNFRadioButton.setSelected(false);
		quineRadioButton.setSelected(false);
		quineDNF.setSelected(false);
		quineCNF.setSelected(false);
		cancelQuineButton.setEnabled(false);
	}
	public void itemStateChanged(ItemEvent e) {
		Vector v;

		if (e.getSource() == quineRadioButton) {
			if (quineRadioButton.isSelected()) {
				quineDNF.setSelected(true);
				quineCNF.setSelected(false);
			}
			else {
				quineDNF.setSelected(false);
				quineCNF.setSelected(false);
			}
			quineDNF.setEnabled(quineRadioButton.isSelected());
			quineCNF.setEnabled(quineRadioButton.isSelected());
		}
		else  if (e.getSource() == userInputRadioButton) {
			if (userInputRadioButton.isSelected()) {
				v = new Vector();
				v.addElement(expression);
				v.addElement(userInput);
				controler.exec(FunctionEditorControler.USER, v);
			}
		}
		else if (e.getSource() == compactRadioButton) {
			if (compactRadioButton.isSelected())
				controler.exec(FunctionEditorControler.COMPACT, expression);
		}
		else if (e.getSource() == rawDNFRadioButton) {
			if (rawDNFRadioButton.isSelected())
				controler.exec(FunctionEditorControler.DNF, expression);
		}
		else if (e.getSource() == quineDNF) {
			if (quineDNF.isSelected()) {
				v = new Vector();
				v.addElement(expression);
				v.addElement(cancelQuineButton);
				v.addElement(displayProgressBar);
				controler.exec(FunctionEditorControler.QUINE_DNF, v);
			}
		}
		else if (e.getSource() == quineCNF) {
			if (quineCNF.isSelected()) {
				v = new Vector();
				v.addElement(expression);
				v.addElement(cancelQuineButton);
				v.addElement(displayProgressBar);
				controler.exec(FunctionEditorControler.QUINE_CNF, v);
			}
		}
		else if (e.getSource() == cancelQuineButton) {
			controler.exec(FunctionEditorControler.QUINE_DNF, null);
		}
	}
	public void cancel() {
		controler.exec(FunctionEditorControler.CANCEL_DISPLAY, expression);
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			controler.exec(FunctionEditorControler.VALIDATE_DISPLAY, expression);
		}
		else if (e.getSource() == cancelButton) {
			cancel();
		}
	}
}
