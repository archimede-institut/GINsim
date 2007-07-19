package fr.univmrs.ibdm.GINsim.stableStates;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.gui.GsStackDialog;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;

public class GsStableStateUI extends GsStackDialog implements GenericStableStateUI {
	private static final long serialVersionUID = -3605525202652679586L;
	
	GsRegulatoryGraph graph;
	StableTableModel tableModel;
	MutantSelectionPanel mutantPanel;
	JPanel buttonPanel;
	
	public GsStableStateUI(GsRegulatoryGraph graph) {
		super(graph.getGraphManager().getMainFrame(), "display.stableStates", 200, 100);
		this.graph = graph;
		Container panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		mutantPanel = new MutantSelectionPanel(this, graph, null);
		panel.add(mutantPanel, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		JScrollPane sp = new JScrollPane();
		tableModel = new StableTableModel(graph.getNodeOrder(), false);

        GsJTable tableResult = new GsJTable(tableModel);
        tableResult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableResult.getTableHeader().setReorderingAllowed(false);
		sp.setViewportView(tableResult);
		panel.add(sp, c);
		
		setMainPanel(panel);
	}
	
	protected void run() {
		setRunning(true);
		new GsSearchStableStates(graph, mutantPanel.getMutant(), this).start();
	}
	
	public void setResult(OmddNode stable) {
		tableModel.setResult(stable);
		setRunning(false);
	}
	
	public void doClose() {
		setVisible(false);
		dispose();
	}
}