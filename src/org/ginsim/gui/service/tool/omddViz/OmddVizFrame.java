package org.ginsim.gui.service.tool.omddViz;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.graph.tree.Tree;
import org.ginsim.graph.tree.TreeParser;
import org.ginsim.graph.tree.TreeParserFromManualOmdd;
import org.ginsim.graph.tree.TreeImpl;
import org.ginsim.gui.GUIManager;

import fr.univmrs.tagc.common.gui.dialog.stackdialog.StackDialog;
import fr.univmrs.tagc.common.managerresources.Translator;

public class OmddVizFrame extends StackDialog implements ActionListener {
	private static final long serialVersionUID = -7619253564236142617L;
	private JFrame frame;
	private RegulatoryGraph graph;
	private Container mainPanel;
	
	private JPanel calcPanel, resPanel;
	private JComboBox  leftOperandCB, rightOperandCB, operatorCB;
	private JTextField  resultTextField;
	private JButton displayTreeButton;
	private OMDDNode leftOmdd, rightOmdd, resOmdd;
	
	private String[] operationsOptions = new String[] {"or", "and", "constraint or", "constraint and", "max"};
	
	
	public OmddVizFrame( RegulatoryGraph graph) {
		super(GUIManager.getInstance().getFrame(graph), "STR_omddViz", 475, 260);
		this.frame = GUIManager.getInstance().getFrame(graph);
		this.graph = graph;
        initialize();
    }

	public void initialize() {
		setMainPanel(getMainPanel());
	}
	
	private Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
		
			c.gridx = 0;
			c.gridy = 0;
			mainPanel.add(getCalcPanel(), c);
			c.gridy++;
			mainPanel.add(getResPanel(), c);
			
			updateLogicalFunctionList();
		}
		return mainPanel;
	}


	private Container getCalcPanel() {
		if (calcPanel == null) {
			calcPanel = new javax.swing.JPanel();
			calcPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			calcPanel.setBorder(BorderFactory.createTitledBorder(Translator.getString("STR_omddViz_operation")));
		
			c.gridx = 0;
			c.gridy = 0;
			leftOperandCB = new JComboBox();
			leftOperandCB.addActionListener(this);
			calcPanel.add(leftOperandCB, c);
			c.gridx++;
			operatorCB = new JComboBox(operationsOptions);
			calcPanel.add(operatorCB, c);
			c.gridx++;
			rightOperandCB = new JComboBox();
			rightOperandCB.addActionListener(this);
			calcPanel.add(rightOperandCB, c);
		}
		return calcPanel;
	}

	private Container getResPanel() {
		if (resPanel == null) {
			resPanel = new javax.swing.JPanel();
			resPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			resPanel.setBorder(BorderFactory.createTitledBorder(Translator.getString("STR_omddViz_results")));
		
			c.gridx = 0;
			c.gridy = 0;
			resultTextField = new JTextField("", 25);
			resPanel.add(resultTextField, c);
			c.gridy++;
			displayTreeButton = new JButton(Translator.getString("STR_omddViz_getTree"));
			displayTreeButton.addActionListener(this);
			resPanel.add(displayTreeButton, c);
		}
		return resPanel;
	}

	protected void run() {
		if (leftOmdd != null && rightOmdd != null) {
			resOmdd = leftOmdd.merge(rightOmdd, operatorCB.getSelectedIndex());
			resultTextField.setText(resOmdd.write().toString());			
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == leftOperandCB) {
			RegulatoryVertex item = (RegulatoryVertex) leftOperandCB.getSelectedItem();
			if (item != null) {
				leftOmdd = item.getTreeParameters((RegulatoryGraph) graph);
			}
		} else if (e.getSource() == rightOperandCB) {
			RegulatoryVertex item = (RegulatoryVertex) rightOperandCB.getSelectedItem();
			if (item != null) {
				rightOmdd = item.getTreeParameters((RegulatoryGraph) graph);
			}
		} else if (e.getSource() == displayTreeButton) {
			run();
			TreeParser parser = new TreeParserFromManualOmdd();
			Tree tree = new TreeImpl(parser);
				
			parser.setParameter(TreeParserFromManualOmdd.PARAM_MANUALOMDD, resOmdd);
			parser.setParameter(TreeParserFromManualOmdd.PARAM_NODEORDER, graph.getNodeOrder());
			parser.run(TreeImpl.MODE_DIAGRAM_WITH_MULTIPLE_LEAFS);
			GUIManager.getInstance().newFrame(tree);

		}
	}

	private void updateLogicalFunctionList() {
		List nodeOrder = graph.getNodeOrder();
		for (Iterator it = nodeOrder.iterator(); it.hasNext();) {
			Object node = (Object) it.next();
			leftOperandCB.addItem(node);
			rightOperandCB.addItem(node);
		}
	}
	
	public JFrame getFrame() {
		return frame;
	}

	
}
