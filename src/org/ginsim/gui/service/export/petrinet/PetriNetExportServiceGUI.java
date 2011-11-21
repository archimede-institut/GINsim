package org.ginsim.gui.service.export.petrinet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.gui.service.tools.reg2dyn.SimulationParameterList;
import org.ginsim.gui.service.tools.reg2dyn.SimulationParametersManager;
import org.ginsim.gui.service.tools.reg2dyn.PriorityClassManager;
import org.ginsim.gui.service.tools.reg2dyn.PrioritySelectionPanel;
import org.ginsim.service.export.petrinet.PNConfig;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.AbstractStackDialogHandler;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.StackDialogHandler;

/**
 * GUI Action to export a LRG into Petri net
 */
@ProviderFor(GsServiceGUI.class)
@StandaloneGUI
public class PetriNetExportServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new PetriNetExportAction((RegulatoryGraph)graph));
			return actions;
		}
		return null;
	}
}


class PetriNetExportAction extends ExportAction<RegulatoryGraph> {

	static final String PNFORMAT = "export.petriNet.defaultFormat";

	PNConfig config;
	
	public PetriNetExportAction(RegulatoryGraph graph) {
		super(graph, "STR_PetriNet", "STR_PetriNet_descr");
	}

	protected void doExport( String filename) {
		// FIXME: call the right subformat to do the job
	}

	@Override
	public StackDialogHandler getConfigPanel() {
		config = new PNConfig(graph);
		return new PNExportConfigPanel(config);
	}

	@Override
	protected GsFileFilter getFileFilter() {
		// FIXME return the filter associated to the selected format
		// return config.format.ffilter
		
		return null;
	}

}

class PNExportConfigPanel extends AbstractStackDialogHandler {
    private static final long serialVersionUID = 9043565812912568136L;

    private final PNConfig config;
	private PrioritySelectionPanel priorityPanel = null;

	protected PNExportConfigPanel ( PNConfig config) {
    	this.config = config;
	}
	
	@Override
	public void init() {
		RegulatoryGraph graph = config.graph;
    	MutantSelectionPanel mutantPanel = null;
    	
    	InitialStatePanel initPanel = new InitialStatePanel(stack, graph, false);
    	initPanel.setParam(config);
    	
    	setLayout(new GridBagLayout());
    	mutantPanel = new MutantSelectionPanel(stack, graph, config.store);
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(mutantPanel, c);

		SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager.getInstance().getObject(graph, SimulationParametersManager.key, true);
        priorityPanel = new PrioritySelectionPanel(stack, paramList.pcmanager);
        priorityPanel.setStore(config.store, 1);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(priorityPanel, c);
		priorityPanel.setFilter(PriorityClassManager.FILTER_NO_SYNCHRONOUS);

		c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 2;
    	c.gridwidth = 2;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.fill = GridBagConstraints.BOTH;
    	add(initPanel, c);
    }

	@Override
	public void run() {
		// TODO: run the export
		Debugger.log("TODO: run the export");
	}
}
