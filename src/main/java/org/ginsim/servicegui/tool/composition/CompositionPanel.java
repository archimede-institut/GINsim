package org.ginsim.servicegui.tool.composition;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.composition.CompositionConfig;

/**
 * Main panel for composition dialog
 * 
 * @author Nuno D. Mendes
 */

public class CompositionPanel extends JPanel implements
		CompositionSpecificationDialog {

	private static final long serialVersionUID = 1139543816020490397L;

	private RegulatoryGraph graph = null;
	private List<RegulatoryNode> mappedNodes = new ArrayList<RegulatoryNode>();
	

	private JPanel mainPanel = null;
	private InstanceSelectorWidget instanceSelectorPanel = null;
	private AdjacencyMatrixWidget adjacencyMatrixPanel = null;
	private IntegrationFunctionWidget integrationPanel = null;

	private JPanel reducePanel = null;
	private JCheckBox toReduce = null;

	private int instances = 2;

	CompositionPanel(RegulatoryGraph graph) {
		this.graph = graph;
	}

	public CompositionConfig getConfig() throws GsException {
		CompositionConfig config = new CompositionConfig();
		config.setTopology(adjacencyMatrixPanel.getTopology());
		config.setMapping(integrationPanel.getMapping());
		config.setReduce(toReduce.isSelected());
		return config;

	}

	private void setupMainPanel(JPanel panel) {

		panel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.weighty = 0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(getInstanceSelectorPanel(), constraints);

		constraints.gridwidth = 5;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 2;
		panel.add(getAdjacencyMatrixPanel(), constraints);

		constraints.weightx = 2;
		constraints.gridx = 5;
		constraints.gridy = 2;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(getIntegrationPanel(), constraints);

		constraints.weighty = 0;
		constraints.gridx = 0;
		constraints.gridy = 10;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		panel.add(getReducePanel(), constraints);

		panel.setSize(panel.getPreferredSize());

	}

	private void refreshMainPanel() {

		// TODO: Make toplevel CardLayout redraw itself (otherwise resizing
		// may go wrong and keep old panels)

		if (mainPanel == null)
			return;
		mainPanel.removeAll();
		// redraw both Panels
		instanceSelectorPanel.removeAll();
		instanceSelectorPanel = null;
		adjacencyMatrixPanel = null;
		reducePanel = null;

		setupMainPanel(mainPanel);
		mainPanel.revalidate();
		mainPanel.setSize(getPreferredSize());

	}

	public JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			setupMainPanel(mainPanel);
		}
		return mainPanel;

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

	private JPanel getReducePanel() {
		if (reducePanel == null) {
			reducePanel = new JPanel();
			reducePanel.setLayout(new GridBagLayout());
			reducePanel.setBorder(BorderFactory.createTitledBorder(""));
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.weightx = 0;
			toReduce = new JCheckBox();
			toReduce.setEnabled(true);
			toReduce.setSelected(true);
			reducePanel.add(toReduce, constraints);
			constraints.gridx = GridBagConstraints.RELATIVE;
			constraints.weightx = 0;
			reducePanel.add(new JLabel("Reduce mapped input components"),
					constraints);
			constraints.weightx = 2;
			reducePanel.add(new JLabel(""), constraints);
			reducePanel.setSize(reducePanel.getPreferredSize());
		}
		return reducePanel;
	}

	public int getNumberInstances() {
		return instances;
	}

	public void updateNumberInstances(int instances) {
		this.instances = instances;
		refreshMainPanel();
	}
	
	public void setAsMapped(RegulatoryNode node){
		this.mappedNodes.add(node);
		//refreshMainPanel();
	}
	
	public void unsetAsMapped(RegulatoryNode node){
		this.mappedNodes.remove(node);
		//refreshMainPanel();
	}
	
	public List<RegulatoryNode> getMappedNodes(){
		return this.mappedNodes;
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}
}