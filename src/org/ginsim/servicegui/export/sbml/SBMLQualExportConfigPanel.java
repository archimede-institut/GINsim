package org.ginsim.servicegui.export.sbml;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.utils.dialog.stackdialog.AbstractStackDialogHandler;
import org.ginsim.service.export.sbml.SBMLQualConfig;

public class SBMLQualExportConfigPanel extends AbstractStackDialogHandler {
	
	private static final long serialVersionUID = 9043565812912568136L;
	
	private final SBMLQualConfig config;
	private final SBMLQualExportAction action;
	
	protected SBMLQualExportConfigPanel( SBMLQualConfig config, SBMLQualExportAction action) {
		
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
