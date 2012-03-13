package org.ginsim.servicegui.export.petrinet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.utils.dialog.stackdialog.AbstractStackDialogHandler;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialogHandler;
import org.ginsim.service.export.petrinet.PNConfig;
import org.ginsim.service.export.petrinet.PetrinetExportService;
import org.ginsim.service.tool.reg2dyn.SimulationParameterList;
import org.ginsim.service.tool.reg2dyn.SimulationParametersManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassManager;
import org.ginsim.servicegui.tool.reg2dyn.PrioritySelectionPanel;

public class PetriNetExportAction extends ExportAction<RegulatoryGraph> {

	static final String PNFORMAT = "export.petriNet.defaultFormat";

	private final PetrinetExportService service;

	PNConfig config;
	
	public PetriNetExportAction(RegulatoryGraph graph, PetrinetExportService service) {
		super(graph, "STR_PetriNet", "STR_PetriNet_descr", null);
		this.service = service;
	}

	protected void doExport( String filename) {
		// call the selected export method to do the job
		try {
			config.format.export(config, filename);
		} catch (IOException e) {
			LogManager.error(e);
		}
	}

	@Override
	public StackDialogHandler getConfigPanel() {
		config = new PNConfig(graph);
		return new PNExportConfigPanel(config, this);
	}

	@Override
	protected FileFormatDescription getFileFilter() {
		// FIXME return the filter associated to the selected format
		// return config.format.ffilter
		
		return null;
	}

}

class PNExportConfigPanel extends AbstractStackDialogHandler {
    private static final long serialVersionUID = 9043565812912568136L;

    private final PNConfig config;
	private PrioritySelectionPanel priorityPanel = null;
	private final PetriNetExportAction action;

	protected PNExportConfigPanel ( PNConfig config, PetriNetExportAction action) {
    	this.config = config;
    	this.action = action;
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

		SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager.getInstance().getObject(graph, SimulationParametersManager.KEY, true);
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
	public boolean run() {
		action.selectFile();
		return true;
	}
}
