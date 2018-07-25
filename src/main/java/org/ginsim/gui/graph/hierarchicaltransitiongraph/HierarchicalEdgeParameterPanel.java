package org.ginsim.gui.graph.hierarchicaltransitiongraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.hierarchicaltransitiongraph.DecisionOnEdge;
import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;


public class HierarchicalEdgeParameterPanel  extends AbstractParameterPanel<HierarchicalTransitionGraph, DecisionOnEdge> {
	private static final long serialVersionUID = 3342245591953494375L;


	private JLabel typeLabel;
	
	public HierarchicalEdgeParameterPanel( HierarchicalTransitionGraph g) {
		super(g);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        typeLabel = new JLabel();
        this.add(typeLabel, c);
	}
	
	@Override
	public void setEditedItem(DecisionOnEdge decisions) {
		if (decisions != null) {
			typeLabel.setText(decisions.toString());
		} else {
			typeLabel.setText("");
		}
	}

}
