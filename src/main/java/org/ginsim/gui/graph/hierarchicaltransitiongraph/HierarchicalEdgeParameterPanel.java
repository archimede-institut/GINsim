package org.ginsim.gui.graph.hierarchicaltransitiongraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.hierarchicaltransitiongraph.DecisionOnEdge;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;


public class HierarchicalEdgeParameterPanel  extends AbstractParameterPanel<DecisionOnEdge> {
	private static final long serialVersionUID = 3342245591953494375L;


	private JLabel typeLabel;
	
	public HierarchicalEdgeParameterPanel( Graph g) {
		super(g);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(getTypeLabel(), c);
	}
	
	/*
	 * @see fr.univmrs.tagc.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
	 */
	@Override
	public void setEditedItem(DecisionOnEdge obj) {
		if (obj != null && obj instanceof DecisionOnEdge) {
			DecisionOnEdge decisions = (DecisionOnEdge)obj;
			typeLabel.setText(decisions.toString());
		} else {
			typeLabel.setText("");
		}
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JLabel getTypeLabel() {
		if(typeLabel == null) {
			typeLabel = new JLabel();
		}
		return typeLabel;
	}
}
