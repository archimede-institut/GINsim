package org.ginsim.servicegui.export.cadp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

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
import org.ginsim.service.tool.composition.IntegrationFunctionMapping;
import org.ginsim.service.tool.composition.Topology;

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
	private InitialStatesWidget initialStatesPanel = null;

	private int instances = 2;
	private Topology topology = new Topology(this.instances);
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

		config.setTopology(this.topology);
		config.setMapping(integrationPanel.getMapping());
		config.setListVisible(visibleComponentsPanel.getSelectedNodes());
		config.setInitialStates(initialStatesPanel.getInitialStates());

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
		add(getVisibleComponentsPanel(), constraints);

		constraints.gridx = 0;
		constraints.gridy = 9;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		add(getInitialStatesPanel(), constraints);

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

	private JPanel getVisibleComponentsPanel() {
		if (visibleComponentsPanel == null)
			visibleComponentsPanel = new VisibleComponentsWidget(this);
		return visibleComponentsPanel;
	}

	private JPanel getInitialStatesPanel() {
		if (initialStatesPanel == null)
			initialStatesPanel = new InitialStatesWidget(this);
		return initialStatesPanel;
	}

	@Override
	public int getNumberInstances() {
		return instances;
	}

	@Override
	public void updateNumberInstances(int instances) {
		this.instances = instances;
		this.topology = new Topology(instances);
		adjacencyMatrixPanel = null;
		initialStatesPanel = null;
		this.removeAll();
		init();
		this.revalidate();
	}

	public void setAsMapped(RegulatoryNode node) {
		this.mappedNodes.add(node);
		visibleComponentsPanel = null;
		this.removeAll();
		init();
		this.revalidate();
	}

	public void unsetAsMapped(RegulatoryNode node) {
		this.mappedNodes.remove(node);
		visibleComponentsPanel = null;
		this.removeAll();
		init();
		this.revalidate();
	}

	public List<RegulatoryNode> getMappedNodes() {
		return this.mappedNodes;
	}

	@Override
	public RegulatoryGraph getGraph() {
		return config.getGraph();

	}

	@Override
	public void addNeighbour(int m, int n) {
		this.topology.addNeighbour(m, n);

	}

	@Override
	public void removeNeighbour(int m, int n) {
		this.topology.removeNeighbour(m, n);
	}

	@Override
	public boolean hasNeihgbours(int m) {
		return this.topology.hasNeighbours(m);
	}

	@Override
	public IntegrationFunctionMapping getMapping()  {
		return this.integrationPanel.getMapping();
	}

	@Override
	public boolean isTrulyMapped(RegulatoryNode node, int m)  {
		return (this.integrationPanel.getMapping().isMapped(node) && this.topology
				.hasNeighbours(m));

	}

	@Override
	public Collection<Entry<RegulatoryNode, Integer>> getInfluencedModuleInputs(
			RegulatoryNode node) {
		// TODO Auto-generated method stub
		return null;
	}
}
