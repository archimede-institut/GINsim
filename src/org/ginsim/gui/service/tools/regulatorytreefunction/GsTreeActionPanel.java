package org.ginsim.gui.service.tools.regulatorytreefunction;

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

import org.ginsim.graph.tree.GsTree;
import org.ginsim.graph.tree.GsTreeParser;
import org.ginsim.graph.tree.GsTreeParserFromCircuit;
import org.ginsim.graph.tree.GsTreeParserFromRegulatoryGraph;
import org.ginsim.gui.service.tools.circuit.GsFunctionalityContext;

import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * This action panel allow the user to select the
 *     *  omdd representation method
 *     *  the gene associated with the logical function to display (in the case of regulatoryGraph)
 *
 */
public class GsTreeActionPanel extends JPanel {
	private static final long serialVersionUID = 3342245591953494375L;

	private JComboBox sourceList;

	private JComboBox treeModeList;
	private GsTree tree;


	private static Vector TREEMODES;
	static {
		TREEMODES = new Vector(3);
		TREEMODES.add(Translator.getString("STR_treeviewer_diagram_with_all_leafs"));
		TREEMODES.add(Translator.getString("STR_treeviewer_diagram"));
		TREEMODES.add(Translator.getString("STR_treeviewer_tree"));
	}
	
	/**
	 */
	public GsTreeActionPanel(GsTree g, Object source) {
		super();
		this.tree = g;
		initialize(0);
	}

	public GsTreeActionPanel(GsTree gsTree, Object source, int selectedIndex) {
		super();
		this.tree = gsTree;
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

		GsTreeParser parser = tree.getParser();
		if (parser instanceof GsTreeParserFromRegulatoryGraph) {
			c.gridx = 0;
			c.gridy++;
			add(new JLabel(Translator.getString("STR_treeviewer_tree_choose_gene")), c);
			c.gridx++;
			sourceList = new JComboBox(new Vector((List) parser.getParameter(GsTreeParser.PARAM_NODEORDER)));
			sourceList.setSelectedIndex(((Integer)parser.getParameter(GsTreeParserFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX)).intValue());
			sourceList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectionChange();
				}
			});
			add(sourceList, c);
		} else if (parser instanceof GsTreeParserFromCircuit) {
			c.gridx = 0;
			c.gridy++;
			add(new JLabel(Translator.getString("STR_treeviewer_tree_choose_circuit")), c);
			c.gridx++;
			sourceList = new JComboBox((Vector)parser.getParameter(GsTreeParserFromCircuit.PARAM_ALLCONTEXTS));
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
		
		if (tree.getParser() instanceof GsTreeParserFromRegulatoryGraph) {
			Integer geneIndex = new Integer(sourceList.getSelectedIndex());
			tree.getParser().setParameter(GsTreeParserFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX, geneIndex);
		} else if (tree.getParser() instanceof GsTreeParserFromCircuit) {
			int contextIndex = sourceList.getSelectedIndex();
			GsFunctionalityContext fcontext = (GsFunctionalityContext) ((List)tree.getParser().getParameter(GsTreeParserFromCircuit.PARAM_ALLCONTEXTS)).get(contextIndex);
			tree.getParser().setParameter(GsTreeParserFromCircuit.PARAM_INITIALCIRCUITDESC, fcontext.getContext());
		}
		tree.getParser().run(treeMode);
	}
}
