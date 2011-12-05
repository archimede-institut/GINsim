package org.ginsim.servicegui.tool.omddViz;

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

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.graph.tree.Tree;
import org.ginsim.core.graph.tree.TreeImpl;
import org.ginsim.core.graph.tree.TreeBuilder;
import org.ginsim.core.graph.tree.TreeBuilderFromManualOmdd;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;


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
			RegulatoryNode item = (RegulatoryNode) leftOperandCB.getSelectedItem();
			if (item != null) {
				leftOmdd = item.getTreeParameters((RegulatoryGraph) graph);
			}
		} else if (e.getSource() == rightOperandCB) {
			RegulatoryNode item = (RegulatoryNode) rightOperandCB.getSelectedItem();
			if (item != null) {
				rightOmdd = item.getTreeParameters((RegulatoryGraph) graph);
			}
		} else if (e.getSource() == displayTreeButton) {
			run();
			TreeBuilder parser = new TreeBuilderFromManualOmdd();
			Tree tree = GraphManager.getInstance().getNewGraph( Tree.class, parser);
				
			parser.setParameter(TreeBuilderFromManualOmdd.PARAM_MANUALOMDD, resOmdd);
			parser.setParameter(TreeBuilderFromManualOmdd.PARAM_NODEORDER, graph.getNodeOrder());
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
