package org.ginsim.servicegui.export.petrinet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.export.petrinet.PNConfig;
import org.ginsim.servicegui.tool.reg2dyn.PrioritySelectionPanel;

public class PetriNetExportAction extends ExportAction<RegulatoryGraph> {

	static final String PNFORMAT = "export.petriNet.defaultFormat";

	private final PNConfig config = new PNConfig();
	LogicalModel model = null;
	
	public PetriNetExportAction(RegulatoryGraph graph) {
		super(graph, "STR_PetriNet", "STR_PetriNet_descr", null);
	}

	protected void doExport( String filename) {
		// call the selected export method to do the job
		try {
			config.format.getWriter( model).export(config, filename);
		} catch (IOException e) {
			LogManager.error(e);
		}
	}

	@Override
	protected FileFormatDescription getFileFilter() {
		return config.format.getFormatDescription();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		PetriNetExportFrame frame = new PetriNetExportFrame(null, graph, this);
	}
	

	public void selectFile(LogicalModel model) {
		this.model = model;
		selectFile();
	}
}

class PetriNetExportFrame extends LogicalModelActionDialog {

	private final PetriNetExportAction action;
	private final PNConfig config;
	private PrioritySelectionPanel priorityPanel = null;

	public PetriNetExportFrame(JFrame f, RegulatoryGraph lrg, PetriNetExportAction action) {
		super(lrg, f, "PNGUI", 600, 400);
		this.action = action;
		this.config = new PNConfig();
		
    	InitialStatePanel initPanel = new InitialStatePanel(lrg, false);
    	initPanel.setParam(config);
    	
    	JPanel mainPanel = new JPanel();
    	mainPanel.setLayout(new GridBagLayout());
    	GridBagConstraints c;

    	// TODO: restore settings (priority and initial states)
/*    	
		SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager.getInstance().getObject(graph, SimulationParametersManager.KEY, true);
        priorityPanel = new PrioritySelectionPanel(stack, paramList.pcmanager);
        priorityPanel.setStore(config.store, 1);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(priorityPanel, c);
		priorityPanel.setFilter(PriorityClassManager.FILTER_NO_SYNCHRONOUS);
		
		c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 2;
    	c.gridwidth = 2;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.fill = GridBagConstraints.BOTH;
    	mainPanel.add(initPanel, c);
*/		

    	setMainPanel(mainPanel);
	}

	@Override
	public void run(LogicalModel model) {
		action.selectFile(model);
		cancel();
	}

}
