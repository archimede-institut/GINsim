package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.action.decisionanalysis.GsDecisionOnEdge;

import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;

public class GsHierarchicalEdgeParameterPanel  extends GsParameterPanel {
	private static final long serialVersionUID = 3342245591953494375L;


	private JLabel typeLabel;
	
	/**
	 */
	public GsHierarchicalEdgeParameterPanel( Graph g) {
		super();
		this.graph = g;
		initialize();
	}
	
	/*
	 * @see fr.univmrs.tagc.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
	 */
	public void setEditedObject(Object obj) {
		if (obj != null && obj instanceof GsDecisionOnEdge) {
			GsDecisionOnEdge decisions = (GsDecisionOnEdge)obj;
			typeLabel.setText(decisions.toString());
		} else {
			typeLabel.setText("");
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
