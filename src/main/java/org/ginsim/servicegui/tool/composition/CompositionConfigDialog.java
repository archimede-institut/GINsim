package org.ginsim.servicegui.tool.composition;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.service.ServiceManager;

import org.ginsim.gui.GUIManager;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.composition.CompositionService;
import org.ginsim.service.tool.composition.IntegrationFunction;
import org.ginsim.service.tool.composition.IntegrationFunctionMapping;
import org.ginsim.service.tool.composition.Topology;

/*
 * The composition dialog
 * 
 * @author Nuno D. Mendes
 */

public class CompositionConfigDialog extends StackDialog {

	// TODO: Replace all strings by token in messages.properties

	private static final long serialVersionUID = 8046844091168372569L;
	RegulatoryGraph graph = null;
	CompositionConfigConfigurePanel config = null;
	boolean isRunning = false;

	CompositionConfigDialog(RegulatoryGraph graph) {
		super(graph, "modelComposer", 700, 300);
		this.graph = graph;
		setTitle("Specify Composition parameters");

		// change name of run button
		// brun.setName("Compose modules");

		CompositionConfigConfigurePanel panel = new CompositionConfigConfigurePanel(
				graph);
		config = panel;
		brun.setText("Compose instances");
		brun.setToolTipText("Compose");
		setMainPanel(panel.getMainPanel());
		setVisible(true);
		setSize(getPreferredSize());

	}

	protected void run() throws GsException {
		setRunning(true);
		brun.setEnabled(false);

		CompositionService service = ServiceManager.getManager().getService(
				CompositionService.class);

		// TODO: Deal here with invalid integration functions w.r.t. to given
		// input
		// and proper components using NotificationManager
		RegulatoryGraph composedGraph = service.run(graph,
				config.getTopology(), config.getIntegrationFunctionMapping());
		GUIManager.getInstance().whatToDoWithGraph(composedGraph, true);

		cancel();
	}

	class CompositionConfigConfigurePanel {

		private RegulatoryGraph graph = null;
		private Topology topology = null;
		private IntegrationFunctionMapping mapping = null;
		private JPanel mainPanel = null;
		private JPanel instanceSelectorPanel = null;
		private JPanel adjacencyMatrixPanel = null;
		private JPanel integrationPanel = null;
		private List<RegulatoryNode> inputNodes = null;
		private List<RegulatoryNode> properNodes = null;
		private Map<RegulatoryNode, JCheckBox> mappedInputSelection = new HashMap<RegulatoryNode, JCheckBox>();
		private Map<RegulatoryNode, JComboBox> mappedFunctionSelection = new HashMap<RegulatoryNode, JComboBox>();
		private Map<RegulatoryNode, JList> mappedProperSelection = new HashMap<RegulatoryNode, JList>();
		private Map<RegulatoryNode, JScrollPane> mappedPane = new HashMap<RegulatoryNode, JScrollPane>();

		private int instances = 2;

		private JSpinner numberInstances = null;
		private JCheckBox matrix[][] = new JCheckBox[instances][instances];

		CompositionConfigConfigurePanel(RegulatoryGraph graph) {
			this.graph = graph;
			init();
		}

		public Topology getTopology() throws GsException {
			topology = new Topology(instances);
			for (int x = 0; x < matrix.length; x++) {
				for (int y = 0; y < matrix.length; y++) {
					if (topology == null) {
						topology = new Topology(instances);
					}
					if (matrix[x][y].isSelected()) {

						topology.addNeighbour(x, y);

					}
				}
			}
			return topology;
		}

		public IntegrationFunctionMapping getIntegrationFunctionMapping()
				throws GsException {

			updateIntegrationFunction();
			return mapping;
		}

		private void init() {
			setMainPanel(getMainPanel());

		}

		private void setupMainPanel(JPanel panel) {

			panel.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();

			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.weightx = 0.4;

			// TODO: Add all subPaness
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			panel.add(getInstanceSelectorPanel(), constraints);
			constraints.gridwidth = 5;
			constraints.weightx = 1.0;
			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.gridheight = GridBagConstraints.REMAINDER;
			panel.add(getAdjacencyMatrixPanel(), constraints);

			constraints.gridx = 5;
			constraints.gridy = 2;
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			panel.add(getIntegrationPanel(), constraints);

		}

		private void refreshMainPanel() {

			// TODO: Make toplevel CardLayout redraw itself (otherwise resizing
			// may go wrong and keep old panels)

			if (mainPanel == null)
				return;
			mainPanel.removeAll();
			// redraw both Panels
			instanceSelectorPanel.removeAll();
			// adjacencyMatrixPanel.removeAll();
			instanceSelectorPanel.revalidate();
			adjacencyMatrixPanel.revalidate();
			instanceSelectorPanel = null;
			adjacencyMatrixPanel = null;

			// recreate adjacency matrix with new dimensions

			matrix = new JCheckBox[instances][instances];
			topology = new Topology(instances);

			setupMainPanel(mainPanel);
			mainPanel.revalidate();
			mainPanel.setSize(getPreferredSize());

		}

		private JPanel getMainPanel() {
			if (mainPanel == null) {
				mainPanel = new JPanel();
				setupMainPanel(mainPanel);
			}
			return mainPanel;

		}

		private JPanel getInstanceSelectorPanel() {
			if (instanceSelectorPanel == null) {
				instanceSelectorPanel = new JPanel();
				instanceSelectorPanel.setLayout(new GridBagLayout());

				// TODO: replace with STR_comp_nrInstances
				instanceSelectorPanel.setBorder(BorderFactory
						.createTitledBorder("Number of Instances"));

				JSpinner input = null;
				if (this.numberInstances == null) {
					SpinnerNumberModel model = new SpinnerNumberModel();
					model.setMinimum(2);
					model.setStepSize(1);
					model.setValue(instances);
					input = new JSpinner(model);

				} else {
					input = this.numberInstances;
				}

				input.setEnabled(true);
				this.numberInstances = input;

				JButton update = new JButton("Update");
				update.setEnabled(true);
				update.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						updateNumberInstances();
					}

					private void updateNumberInstances() {
						instances = (Integer) numberInstances.getValue();
						refreshMainPanel();

					}
				});

				instanceSelectorPanel.add(input);
				instanceSelectorPanel.add(update);
			}
			return instanceSelectorPanel;

		}

		private JPanel getAdjacencyMatrixPanel() {
			if (adjacencyMatrixPanel == null) {
				adjacencyMatrixPanel = new JPanel();
				adjacencyMatrixPanel.setLayout(new GridBagLayout());
				GridBagConstraints constraints = new GridBagConstraints();

				// TODO: replace with STR_comp_nrInstances
				adjacencyMatrixPanel.setBorder(BorderFactory
						.createTitledBorder("Specify Neighbouring modules"));

				constraints.fill = GridBagConstraints.NONE;

				if (instances <= 1) {
					return adjacencyMatrixPanel;
				}

				int x = 0;
				int y = 0;
				while (x <= instances && y <= instances) {
					constraints.gridx = x;
					constraints.gridy = y;

					if (x == 0 && y == 0)
						adjacencyMatrixPanel.add(new JLabel(), constraints);
					if (y == 0 && x > 0)
						adjacencyMatrixPanel.add(new JLabel("M" + x),
								constraints);
					if (x == 0 && y > 0) {
						adjacencyMatrixPanel.add(new JLabel("M" + y),
								constraints);
					}
					if (x > 0 && y > 0) {
						JCheckBox checkBox = new JCheckBox();

						// Modules cannot be their own neighgbours
						if (x == y)
							checkBox.setEnabled(false);
						else
							checkBox.setEnabled(true);
						matrix[x - 1][y - 1] = checkBox;
						adjacencyMatrixPanel.add(checkBox, constraints);
					}

					x++;
					if (x > instances && y <= instances) {
						y++;
						x = 0;
					}
				}

			}

			return adjacencyMatrixPanel;
		}

		private JPanel getIntegrationPanel() {
			if (integrationPanel == null) {
				integrationPanel = new JPanel();
				integrationPanel.setLayout(new GridBagLayout());
				integrationPanel
						.setBorder(BorderFactory
								.createTitledBorder("Specify Integration function for inputs"));
				GridBagConstraints constraints = new GridBagConstraints();

				constraints.gridx = 0;
				constraints.gridy = 0;
				constraints.weightx = 1;

				List<RegulatoryNode> listNodes = graph.getNodeOrder();
				inputNodes = new ArrayList<RegulatoryNode>();
				properNodes = new ArrayList<RegulatoryNode>();

				for (RegulatoryNode node : listNodes) {
					if (node.isInput())
						inputNodes.add(node);
					else
						properNodes.add(node);

				}

				for (RegulatoryNode node : inputNodes) {

					JCheckBox nodeCheck = new JCheckBox();
					mappedInputSelection.put(node, nodeCheck);
					nodeCheck.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							for (RegulatoryNode node : inputNodes) {
								JCheckBox checkBox = mappedInputSelection
										.get(node);
								JComboBox comboBox = mappedFunctionSelection
										.get(node);
								JScrollPane scroll = mappedPane.get(node);
								if (checkBox.isSelected()) {
									comboBox.setEnabled(true);
									scroll.setEnabled(true);

								} else {
									comboBox.setSelectedIndex(0);
									comboBox.setEnabled(false);
									scroll.setEnabled(false);
								}

							}
						}

					});

					JLabel nodeLabel = new JLabel(node.getId());

					Collection<IntegrationFunction> listIF = IntegrationFunction
							.whichCanApply(node);

					Object[] listChoices = new Object[listIF.size() + 1];
					int i = 0;
					listChoices[i] = "unmapped";
					for (IntegrationFunction intFun : listIF)
						listChoices[++i] = intFun;

					JComboBox nodeCombo = new JComboBox(listChoices);
					nodeCombo.setEditable(false);
					nodeCombo.setEnabled(false);
					mappedFunctionSelection.put(node, nodeCombo);

					JList nodeList = new JList(properNodes.toArray());
					nodeList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
					nodeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					JScrollPane nodeScroll = new JScrollPane(nodeList);
					nodeScroll.setPreferredSize(new Dimension(50, 60));
					nodeScroll.setEnabled(false);
					mappedProperSelection.put(node, nodeList);
					mappedPane.put(node, nodeScroll);

					integrationPanel.add(nodeCheck, constraints);
					constraints.gridx = 1;
					integrationPanel.add(nodeLabel, constraints);
					constraints.gridx = 2;
					integrationPanel.add(nodeCombo, constraints);
					constraints.gridx = 3;
					constraints.gridwidth = GridBagConstraints.REMAINDER;
					integrationPanel.add(nodeScroll, constraints);
				}
			}
			return integrationPanel;
		}

		private void updateIntegrationFunction() throws GsException {
			mapping = new IntegrationFunctionMapping();
			for (RegulatoryNode node : inputNodes) {
				JCheckBox checkBox = mappedInputSelection.get(node);
				JComboBox comboBox = mappedFunctionSelection.get(node);
				JList selection = mappedProperSelection.get(node);

				if (checkBox.isSelected()) {
					Object selectedFunction = comboBox.getSelectedItem();
					if (selectedFunction instanceof IntegrationFunction) {
						int[] indices = selection.getSelectedIndices();
						List<RegulatoryNode> listProper = new ArrayList<RegulatoryNode>();
						for (int i = 0; i < indices.length; i++) {
							listProper.add(properNodes.get(indices[i]));
						}

						if (!IntegrationFunction
								.whichCanApply(node, listProper).contains(
										selectedFunction))
							throw new GsException(
									GsException.GRAVITY_NORMAL,
									"Cannot apply integration function "
											+ (IntegrationFunction) selectedFunction
											+ " to the given input/proper components");

						mapping.addMapping(node, listProper,
								(IntegrationFunction) selectedFunction);

					}

				}
			}

		}

	}
}
