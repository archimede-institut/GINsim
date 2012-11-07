package org.ginsim.servicegui.export.cadp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.utils.dialog.stackdialog.AbstractStackDialogHandler;
import org.ginsim.service.export.cadp.CADPExportConfig;
import org.ginsim.servicegui.tool.composition.AdjacencyMatrixWidget;
import org.ginsim.servicegui.tool.composition.CompositionSpecificationDialog;
import org.ginsim.servicegui.tool.composition.InstanceSelectorWidget;
import org.ginsim.servicegui.tool.composition.IntegrationFunctionWidget;

/**
 * Main dialog for CADP export
 * 
 * @author Nuno D. Mendes
 */

public class CADPExportConfigPanel extends AbstractStackDialogHandler implements
		CompositionSpecificationDialog {

	private static final long serialVersionUID = 7274577689017747224L;

	private final CADPExportConfig config;
	private final CADPExportAction action;

	private InstanceSelectorWidget instanceSelectorPanel = null;
	private AdjacencyMatrixWidget adjacencyMatrixPanel = null;
	private IntegrationFunctionWidget integrationPanel = null;
	private VisibleComponentsWidget visibleComponentsPanel = null;

	private int instances = 2;
	private List<RegulatoryNode> mappedNodes = new ArrayList<RegulatoryNode>();

	public CADPExportConfigPanel(CADPExportConfig config,
			CADPExportAction action) {
		this.config = config;
		this.action = action;
	}

	@Override
	public boolean run() {
		
		
		// This should not be done here
		// The config setup should be done elsewhere
		// TODO set initial state
		config.setTopology(adjacencyMatrixPanel.getTopology());
	    try {
			config.setMapping(integrationPanel.getMapping());
		} catch (GsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// THIS CANNOT HAPPEN
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
		add(getIntegrationPanel(), constraints);

		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		add(getVisibleComponentsPanel(), constraints);

		// add panel for initial state
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

	private JPanel getVisibleComponentsPanel(){
		if (visibleComponentsPanel == null)
			visibleComponentsPanel = new VisibleComponentsWidget(this);
		return visibleComponentsPanel;
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

	public void setAsMapped(RegulatoryNode node){
		this.mappedNodes.add(node);
		visibleComponentsPanel = null;
		this.removeAll();
		init();
		this.revalidate();
	}
	
	public void unsetAsMapped(RegulatoryNode node){
		this.mappedNodes.remove(node);
		visibleComponentsPanel = null;
		this.removeAll();
		init();
		this.revalidate();
	}
	
	public List<RegulatoryNode> getMappedNodes(){
		return this.mappedNodes;
	}
	
	@Override
	public RegulatoryGraph getGraph() {
		return config.getGraph();

	}
}
