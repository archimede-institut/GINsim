package org.ginsim.servicegui.format.sbml;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.shell.actions.ExportAction;
import org.ginsim.gui.utils.dialog.stackdialog.AbstractStackDialogHandler;
import org.ginsim.service.format.sbml.SBMLQualConfig;

public class SBMLQualExportConfigPanel extends AbstractStackDialogHandler {
	
	private static final long serialVersionUID = 9043565812912568136L;
	
	private final SBMLQualConfig config;
	private final ExportAction<RegulatoryGraph> action;
	
	public SBMLQualExportConfigPanel( SBMLQualConfig config, ExportAction<RegulatoryGraph> action) {
		
		super();
		setLayout(new GridBagLayout());
		this.config = config;
		this.action = action;
	}
	
	@Override
	protected void init() {
		
		InitialStatePanel initPanel = new InitialStatePanel( config.getGraph(), false);
		initPanel.setParam(config);
		
		GridBagConstraints c = new GridBagConstraints();
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
