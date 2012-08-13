package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;

public class PerturbationCreatePanel extends JPanel implements ActionListener {

	private final RegulatoryGraph lrg;
	private PerturbationType type = null;
	private NodeInfo nodeinfo = null;
	
	JLabel todolabel = new JLabel("TODO");
	
	public PerturbationCreatePanel(RegulatoryGraph lrg) {
		this.lrg = lrg;
		
		add(todolabel);
	}
	
	public void setType(PerturbationType type) {
		this.type = type;
		
		this.todolabel.setText("TODO: create panel for "+type);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (type == null || nodeinfo == null) {
			System.err.println("Not enough information to create perturbation");
		}
	}
}

