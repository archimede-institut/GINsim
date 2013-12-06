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

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDOperator;
import org.colomoto.mddlib.operators.MDDBaseOperators;
import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.tree.Tree;
import org.ginsim.core.graph.tree.TreeBuilder;
import org.ginsim.core.graph.tree.TreeBuilderFromManualOmdd;
import org.ginsim.core.graph.tree.TreeImpl;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;


public class OmddVizFrame extends StackDialog implements ActionListener {
	private static final long serialVersionUID = -7619253564236142617L;
	private JFrame frame;
	private Container mainPanel;
	
	private JPanel calcPanel, resPanel;
	private JComboBox  leftOperandCB, rightOperandCB, operatorCB;
	private JTextField  resultTextField;
	private JButton displayTreeButton;

    private final RegulatoryGraph graph;
    private final MDDManager ddmanager;
    private int leftOmdd, rightOmdd, resOmdd;
	
    // TODO: add missing operators, esp "constraint or", "constraint and" and "max"
    MDDOperator[] operators = { MDDBaseOperators.OR, MDDBaseOperators.AND };
	
	
	public OmddVizFrame( RegulatoryGraph graph) {
		super(GUIManager.getInstance().getFrame(graph), "STR_omddViz", 475, 260);
		this.frame = GUIManager.getInstance().getFrame(graph);
		this.graph = graph;
        this.ddmanager = graph.getMDDFactory();
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
			operatorCB = new JComboBox(operators);
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
		if (leftOmdd != -1 && rightOmdd != -1) {
            MDDOperator op = operators[operatorCB.getSelectedIndex()];
            resOmdd = op.combine(ddmanager, leftOmdd, rightOmdd);
            // FIXME: pretty printing of MDD
			resultTextField.setText("[TODO: MDD pretty print] "+resOmdd);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == leftOperandCB) {
			RegulatoryNode item = (RegulatoryNode) leftOperandCB.getSelectedItem();
			if (item != null) {
				leftOmdd = item.getMDD(graph, ddmanager);
			}
		} else if (e.getSource() == rightOperandCB) {
			RegulatoryNode item = (RegulatoryNode) rightOperandCB.getSelectedItem();
			if (item != null) {
				rightOmdd = item.getMDD(graph, ddmanager);
			}
		} else if (e.getSource() == displayTreeButton) {
			run();
			TreeBuilder parser = new TreeBuilderFromManualOmdd();
			Tree tree = GraphManager.getInstance().getNewGraph( Tree.class, parser);
				
			parser.setParameter(TreeBuilderFromManualOmdd.PARAM_MANUALOMDD, resOmdd);
			parser.setParameter(TreeBuilderFromManualOmdd.PARAM_NODEORDER, graph.getNodeOrder());
			parser.run(TreeImpl.MODE_DIAGRAM_WITH_MULTIPLE_LEAFS);
			GUIManager.getInstance().newFrame(tree, false);

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
