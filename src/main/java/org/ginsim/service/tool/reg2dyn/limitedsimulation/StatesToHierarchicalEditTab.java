package org.ginsim.service.tool.reg2dyn.limitedsimulation;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.graph.hierarchicaltransitiongraph.HierarchicalCellRenderer;
import org.ginsim.gui.graph.hierarchicaltransitiongraph.HierarchicalTableModel;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;
import org.ginsim.gui.shell.editpanel.EditTab;

/**
 * StatesToHierarchicalEditTab provides a ParameterPanel for the mainframe presenting information 
 * on the hierarchicalNode corresponding to the selected DynamicNode.
 * @author Duncan Berenguier
 *
 */
public class StatesToHierarchicalEditTab extends AbstractParameterPanel<DynamicNode>  implements EditTab {

	private static final long serialVersionUID = -4906477197046628561L;
	public static final String title = "HierarchicalNode";
	
	private final HashMap<DynamicNode, HierarchicalNode> states2hnode;
	private HierarchicalTransitionGraph htg;
	
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JLabel typeLabel = null;
	private JLabel nameLabel;
	private JButton showInHTGButton = null;
	private HierarchicalNode node;
	
	
	public StatesToHierarchicalEditTab(DynamicGraph graph, HierarchicalTransitionGraph htg) {
		super(graph);
		this.states2hnode = LimitedSimulationService.getStatesToHierarchicalNodes(graph);
		this.htg = htg;
		initGUI();
	}

	
	private void initGUI() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(getNameLabel(), c);
        
        c.gridy++;
        this.add(getTypeLabel(), c);
        
        
        c.gridy++;
        c.weighty = 1;
        this.add(getJScrollPane(), c);
        this.setMinimumSize(new Dimension(20,20));
        
        c.gridy++;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        this.add(getShowInHTGButton(), c);
	}

	private JButton getShowInHTGButton() {
		if (showInHTGButton == null) {
			showInHTGButton = new JButton(Translator.getString("STR_limitedSimulation_showInHTGButton"));
			showInHTGButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					showInHTGButton();
				}
			});
		}
		return showInHTGButton;
	}
	
	protected void showInHTGButton() {
		if (node != null) {
			GraphSelection<HierarchicalNode, ?> selection = GUIManager.getInstance().getGraphGUI(htg).getSelection();
			Collection<HierarchicalNode> v = new Vector<HierarchicalNode>();
			v.add(node);
			selection.setSelectedNodes(v);
			GUIManager.getInstance().getFrame(htg).toFront();
		}
	}


	private Component getNameLabel() {
		if(nameLabel == null) {
			nameLabel = new JLabel();
		}
		return nameLabel;
	}


	private JLabel getTypeLabel() {
		if(typeLabel == null) {
			typeLabel = new JLabel();
		}
		return typeLabel;
	}
	private JTable getJTable() {
		if(jTable == null) {
			jTable = new JTable();
			jTable.setDefaultRenderer(Object.class, new HierarchicalCellRenderer());
			jTable.setModel( new HierarchicalTableModel(htg));
            jTable.getTableHeader().setReorderingAllowed(false);
		}
		return jTable;
	}
	private JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
			jScrollPane.setSize(88, 104);
			jScrollPane.setLocation(81, 5);
		}
		return jScrollPane;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isActive(GraphSelection<?, ?> selection) {
		List<?> selectedNodes = selection.getSelectedNodes();
		if (selectedNodes != null && selectedNodes.size() == 1) {
			setEditedItem((DynamicNode) selectedNodes.get(0));
			return true;
		}
		return false;
	}

	@Override
	public void setEditedItem(DynamicNode item) {
		this.node = states2hnode.get(item);
		((HierarchicalTableModel)getJTable().getModel()).setContent(node);
        jTable.getColumnModel().getColumn(0).setMinWidth(10);
        jTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        nameLabel.setText("Name: "+node.toString()+", ID:"+node.getUniqueId());
        typeLabel.setText("Type: "+Translator.getString("STR_"+node.typeToString()));
	}

}
