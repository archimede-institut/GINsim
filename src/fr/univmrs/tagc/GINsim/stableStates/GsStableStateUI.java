package fr.univmrs.tagc.GINsim.stableStates;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.EnhancedJTable;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class GsStableStateUI extends StackDialog implements GenericStableStateUI {
	private static final long serialVersionUID = -3605525202652679586L;
	
	GsRegulatoryGraph graph;
	StableTableModel tableModel;
	ObjectStore mutantstore = new ObjectStore();
	MutantSelectionPanel mutantPanel;
	JPanel buttonPanel;
	
	public GsStableStateUI(GsRegulatoryGraph graph) {
		super(graph.getGraphManager().getMainFrame(), "display.stableStates", 200, 100);
		this.graph = graph;
		setTitle(Translator.getString("STR_stableStates"));
		Container panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		mutantPanel = new MutantSelectionPanel(this, graph, mutantstore);
		panel.add(mutantPanel, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		JScrollPane sp = new JScrollPane();
		tableModel = new StableTableModel(graph.getNodeOrder(), false);

        EnhancedJTable tableResult = new EnhancedJTable(tableModel);
        tableResult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableResult.getTableHeader().setReorderingAllowed(false);
		sp.setViewportView(tableResult);
		panel.add(sp, c);
		
		setMainPanel(panel);
	}
	
	protected void run() {
		setRunning(true);
		new GsSearchStableStates(graph, (GsRegulatoryMutantDef)mutantstore.getObject(0), this).start();
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