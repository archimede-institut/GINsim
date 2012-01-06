package org.ginsim.gui.graph.tree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.common.utils.Translator;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.tree.Tree;
import org.ginsim.core.graph.tree.TreeBuilder;
import org.ginsim.core.graph.tree.TreeBuilderFromCircuit;
import org.ginsim.core.graph.tree.TreeBuilderFromRegulatoryGraph;
import org.ginsim.core.mdd.MDDContext;
import org.ginsim.gui.graph.GUIEditor;


/**
 * This action panel allow the user to select the
 *     *  omdd representation method
 *     *  the gene associated with the logical function to display (in the case of regulatoryGraph)
 *
 */
public class TreeActionPanel extends JPanel implements GUIEditor<Tree> {
	private static final long serialVersionUID = 3342245591953494375L;

	private JComboBox sourceList;
	private JComboBox treeModeList;
	private Tree tree = null;
	private boolean hasSelectionChanged = false;
	
	private static Vector<String> TREEMODES;

	private JLabel labelChooseComboBox;
	static {
		TREEMODES = new Vector<String>(3);
		TREEMODES.add(Translator.getString("STR_treeviewer_diagram_with_all_leafs"));
		TREEMODES.add(Translator.getString("STR_treeviewer_diagram"));
		TREEMODES.add(Translator.getString("STR_treeviewer_tree"));
	}
	
	
	public TreeActionPanel() {
		initialize();
	}
	
	@Override
	public void setEditedItem(Tree tree) {
		this.tree = tree;
		updateComboBox();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
        this.setLayout(new GridBagLayout());
        getContentPanel();
        this.setMinimumSize(new Dimension(20,20));
	}

	private void getContentPanel() {
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 20;
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel(Translator.getString("STR_treeviewer_tree_choose_mode")), c);
		c.gridx++;
		treeModeList = new JComboBox(TREEMODES);
		treeModeList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectionChange();
			}
		});
		add(treeModeList, c);
		c.gridx = 0;
		c.gridy++;
		labelChooseComboBox = new JLabel("");
		add(labelChooseComboBox, c);
		c.gridx++;
		sourceList = new JComboBox();
		sourceList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectionChange();
			}
		});
		add(sourceList, c);
	}
	
	private void updateComboBox() {
		TreeBuilder parser = tree.getParser();
		sourceList.removeAllItems();
		if (parser instanceof TreeBuilderFromRegulatoryGraph) {
			labelChooseComboBox.setText(Translator.getString("STR_treeviewer_tree_choose_gene"));
			List<RegulatoryNode> nodeOrder = (List<RegulatoryNode>) parser.getParameter(TreeBuilder.PARAM_NODEORDER);
			for (RegulatoryNode node : nodeOrder) {
				sourceList.addItem(node);
			}
			sourceList.setSelectedIndex(((Integer)parser.getParameter(TreeBuilderFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX)).intValue());
		} else if (parser instanceof TreeBuilderFromCircuit) {
			labelChooseComboBox.setText(Translator.getString("STR_treeviewer_tree_choose_circuit"));
			List contexts = (List) parser.getParameter(TreeBuilderFromCircuit.PARAM_ALLCONTEXTS);
			for (Object context : contexts) {
				sourceList.addItem(context);
			}
			sourceList.setSelectedIndex(0);
		}
	}

	protected void selectionChange() {
		if (!hasSelectionChanged) {
			hasSelectionChanged = true;
			return;
		}
		if (tree == null) return;
		int treeMode = treeModeList.getSelectedIndex();
		if (treeMode < 0 && treeMode > 2) treeMode = 0;
		
		if (tree.getParser() instanceof TreeBuilderFromRegulatoryGraph) {
			Integer geneIndex = new Integer(sourceList.getSelectedIndex());
			tree.getParser().setParameter(TreeBuilderFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX, geneIndex);
		} else if (tree.getParser() instanceof TreeBuilderFromCircuit) {
			int contextIndex = sourceList.getSelectedIndex();
			MDDContext fcontext = ((List<MDDContext>)tree.getParser().getParameter(TreeBuilderFromCircuit.PARAM_ALLCONTEXTS)).get(contextIndex);
			tree.getParser().setParameter(TreeBuilderFromCircuit.PARAM_INITIALCIRCUITDESC, fcontext.getContext());
		}
		tree.getParser().run(treeMode);
	}


	@Override
	public Component getComponent() {
		return this;
	}
}
