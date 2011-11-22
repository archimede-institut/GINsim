package org.ginsim.gui.service.tool.regulatorytreefunction;

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

import org.ginsim.graph.tree.Tree;
import org.ginsim.graph.tree.TreeParser;
import org.ginsim.graph.tree.TreeParserFromCircuit;
import org.ginsim.graph.tree.TreeParserFromRegulatoryGraph;
import org.ginsim.gui.service.tool.circuit.FunctionalityContext;

import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * This action panel allow the user to select the
 *     *  omdd representation method
 *     *  the gene associated with the logical function to display (in the case of regulatoryGraph)
 *
 */
public class TreeActionPanel extends JPanel {
	private static final long serialVersionUID = 3342245591953494375L;

	private JComboBox sourceList;

	private JComboBox treeModeList;
	private Tree tree;


	private static Vector TREEMODES;
	static {
		TREEMODES = new Vector(3);
		TREEMODES.add(Translator.getString("STR_treeviewer_diagram_with_all_leafs"));
		TREEMODES.add(Translator.getString("STR_treeviewer_diagram"));
		TREEMODES.add(Translator.getString("STR_treeviewer_tree"));
	}
	
	/**
	 */
	public TreeActionPanel(Tree g, Object source) {
		super();
		this.tree = g;
		initialize(0);
	}

	public TreeActionPanel(Tree tree, Object source, int selectedIndex) {
		super();
		this.tree = tree;
		initialize(selectedIndex);
	}

	/**
	 * This method initializes this
	 */
	private void initialize(int selectedIndex) {
        this.setLayout(new GridBagLayout());
        getContentPanel();
        this.setMinimumSize(new Dimension(20,20));
		selectionChange();
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

		TreeParser parser = tree.getParser();
		if (parser instanceof TreeParserFromRegulatoryGraph) {
			c.gridx = 0;
			c.gridy++;
			add(new JLabel(Translator.getString("STR_treeviewer_tree_choose_gene")), c);
			c.gridx++;
			sourceList = new JComboBox(new Vector((List) parser.getParameter(TreeParser.PARAM_NODEORDER)));
			sourceList.setSelectedIndex(((Integer)parser.getParameter(TreeParserFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX)).intValue());
			sourceList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectionChange();
				}
			});
			add(sourceList, c);
		} else if (parser instanceof TreeParserFromCircuit) {
			c.gridx = 0;
			c.gridy++;
			add(new JLabel(Translator.getString("STR_treeviewer_tree_choose_circuit")), c);
			c.gridx++;
			sourceList = new JComboBox((Vector)parser.getParameter(TreeParserFromCircuit.PARAM_ALLCONTEXTS));
			sourceList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectionChange();
				}
			});
			add(sourceList, c);		
		} //TODO: else { //Add other parser support here
	}

	protected void selectionChange() {
		int treeMode = treeModeList.getSelectedIndex();
		if (treeMode < 0 && treeMode > 2) treeMode = 0;
		
		if (tree.getParser() instanceof TreeParserFromRegulatoryGraph) {
			Integer geneIndex = new Integer(sourceList.getSelectedIndex());
			tree.getParser().setParameter(TreeParserFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX, geneIndex);
		} else if (tree.getParser() instanceof TreeParserFromCircuit) {
			int contextIndex = sourceList.getSelectedIndex();
			FunctionalityContext fcontext = (FunctionalityContext) ((List)tree.getParser().getParameter(TreeParserFromCircuit.PARAM_ALLCONTEXTS)).get(contextIndex);
			tree.getParser().setParameter(TreeParserFromCircuit.PARAM_INITIALCIRCUITDESC, fcontext.getContext());
		}
		tree.getParser().run(treeMode);
	}
}
