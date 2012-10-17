package org.ginsim.servicegui.export.cadp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.utils.dialog.stackdialog.AbstractStackDialogHandler;
import org.ginsim.service.export.cadp.CADPExportConfig;
import org.ginsim.servicegui.tool.composition.AdjacencyMatrixWidget;
import org.ginsim.servicegui.tool.composition.CompositionSpecificationDialog;
import org.ginsim.servicegui.tool.composition.InstanceSelectorWidget;
import org.ginsim.servicegui.tool.composition.IntegrationFunctionWidget;

public class CADPExportConfigPanel extends AbstractStackDialogHandler implements CompositionSpecificationDialog {

	private static final long serialVersionUID = 7274577689017747224L;
	
	private final CADPExportConfig config;
	private final CADPExportAction action;
	
	private InstanceSelectorWidget instanceSelectorPanel = null;
	private AdjacencyMatrixWidget adjacencyMatrixPanel = null;
	private IntegrationFunctionWidget integrationPanel = null;
	
	private int instances = 2;

	public CADPExportConfigPanel(CADPExportConfig config, CADPExportAction action) {
		this.config = config;
		this.action = action;
	}


	@Override
	public boolean run() {
		// TODO set initial state
		try {
			config.setTopology(adjacencyMatrixPanel.getTopology());
		} catch (GsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			config.setMapping(integrationPanel.getMapping());
		} catch (GsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO set list visible

		action.selectFile();
		return true;
	}


	@Override
	protected void init() {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.weighty = 0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(getInstanceSelectorPanel(), constraints);

		constraints.gridwidth = 5;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 2;
		add(getAdjacencyMatrixPanel(), constraints);

		constraints.weightx = 2;
		constraints.gridx = 5;
		constraints.gridy = 2;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		add(getIntegrationPanel(), constraints);

		// add panel for initial state
		// add panel to select visible components
		
		
		setSize(getPreferredSize());
		
	}

	private JPanel getInstanceSelectorPanel() {
		if (instanceSelectorPanel == null)
			instanceSelectorPanel = new InstanceSelectorWidget(this);
		return instanceSelectorPanel;

	}

	private JPanel getAdjacencyMatrixPanel() {
		if (adjacencyMatrixPanel == null)
			adjacencyMatrixPanel = new AdjacencyMatrixWidget(this);

		return adjacencyMatrixPanel;
	}

	private JPanel getIntegrationPanel() {
		if (integrationPanel == null) 
			integrationPanel = new IntegrationFunctionWidget(this);
		return integrationPanel;
	}

	@Override
	public int getNumberInstances() {
		return instances;
	}

	@Override
	public void updateNumberInstances(int instances) {
		this.instances = instances;	
		adjacencyMatrixPanel = null;
		this.removeAll();
		init();
		this.revalidate();
	}

	@Override
	public RegulatoryGraph getGraph() {
		return config.getGraph();

	}
}
