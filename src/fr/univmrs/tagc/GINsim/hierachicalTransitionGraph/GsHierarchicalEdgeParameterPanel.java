package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.DecisionAnalysis.GsDecisionOnEdge;

public class GsHierarchicalEdgeParameterPanel  extends GsParameterPanel {
	private static final long serialVersionUID = 3342245591953494375L;


	private JLabel typeLabel;
	
	/**
	 */
	public GsHierarchicalEdgeParameterPanel(GsGraph g) {
		super();
		this.graph = g;
		initialize();
	}
	
	/*
	 * @see fr.univmrs.tagc.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
	 */
	public void setEditedObject(Object obj) {
		if (obj instanceof GsDirectedEdge) {
			GsDirectedEdge edge = (GsDirectedEdge)obj;
			GsDecisionOnEdge decisions = (GsDecisionOnEdge) edge.getUserObject();
			if (decisions != null) {
				typeLabel.setText(decisions.toString());
			} else {
				typeLabel.setText("");
			}
		}
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(getTypeLabel(), c);
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
