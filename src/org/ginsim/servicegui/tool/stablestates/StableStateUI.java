package org.ginsim.servicegui.tool.stablestates;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.utils.dialog.stackdialog.AbstractStackDialogHandler;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.tool.stablestates.StableStateSearcher;
import org.ginsim.service.tool.stablestates.StableStatesService;


public class StableStateUI extends AbstractStackDialogHandler {
	private static final long serialVersionUID = -3605525202652679586L;
	
	RegulatoryGraph graph;
	StableStateSearcher algo;
	StableTableModel tableModel;
	ObjectStore mutantstore = new ObjectStore();
	MutantSelectionPanel mutantPanel;
	JPanel buttonPanel;
	
	public StableStateUI(RegulatoryGraph graph) {
		
		// super(graph, "display.stableStates", 200, 100);
		this.graph = graph;
		this.algo = ServiceManager.get(StableStatesService.class).getSearcher(graph);

	}
	
	@Override
	protected void init() {

		stack.setTitle(Translator.getString("STR_stableStates"));

		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel(Translator.getString("STR_stableStates_title")), c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		mutantPanel = new MutantSelectionPanel( stack, graph, mutantstore);
		add(mutantPanel, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		JScrollPane sp = new JScrollPane();
		tableModel = new StableTableModel(graph.getNodeOrder(), false);

        EnhancedJTable tableResult = new EnhancedJTable(tableModel);
        tableResult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableResult.getTableHeader().setReorderingAllowed(false);
		sp.setViewportView(tableResult);
		add(sp, c);
	}
	
	public void run() {
		algo.setPerturbation((Perturbation)mutantstore.getObject(0));
		setResult(algo.getStables());
	}
	
	public void setResult(OMDDNode stable) {
		tableModel.setResult(stable, graph);
	}
	
}
