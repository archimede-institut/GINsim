package org.ginsim.servicegui.tool.composition;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
import org.ginsim.service.tool.composition.CompositionConfig;
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
	CompositionConfigConfigurePanel dialog = null;
	boolean isRunning = false;

	CompositionConfigDialog(RegulatoryGraph graph) {
		super(graph, "modelComposer", 700, 300);
		this.graph = graph;
		setTitle("Specify Composition parameters");

		CompositionConfigConfigurePanel panel = new CompositionConfigConfigurePanel(
				graph);
		dialog = panel;
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
		RegulatoryGraph composedGraph = service.run(graph, dialog.getConfig());
		GUIManager.getInstance().whatToDoWithGraph(composedGraph, true);

		cancel();
	}

	class CompositionConfigConfigurePanel {

		private RegulatoryGraph graph = null;
		private Topology topology = null;
		private IntegrationFunctionMapping mapping = null;
		private boolean symmetricTopology = false; // default is asymmetric
		private JPanel mainPanel = null;
		private JPanel instanceSelectorPanel = null;
		private JPanel adjacencyMatrixPanel = null;
		private JPanel integrationPanel = null;
		private JPanel reducePanel = null;
		private List<RegulatoryNode> inputNodes = null;
		private List<RegulatoryNode> properNodes = null;
		private Map<RegulatoryNode, JCheckBox> mappedInputSelection = new HashMap<RegulatoryNode, JCheckBox>();
		private Map<RegulatoryNode, JComboBox> mappedFunctionSelection = new HashMap<RegulatoryNode, JComboBox>();
		private Map<RegulatoryNode, JList> mappedProperSelection = new HashMap<RegulatoryNode, JList>();
		private Map<RegulatoryNode, JScrollPane> mappedPane = new HashMap<RegulatoryNode, JScrollPane>();
		private JCheckBox toReduce = null;

		private int instances = 2;

		private JSpinner numberInstances = null;
		private JCheckBox matrix[][] = new JCheckBox[instances][instances];

		CompositionConfigConfigurePanel(RegulatoryGraph graph) {
			this.graph = graph;
			init();
		}

		public CompositionConfig getConfig() throws GsException {
			CompositionConfig config = new CompositionConfig();
			config.setTopology(getTopology());
			config.setMapping(getMapping());
			config.setReduce(toReduce.isSelected());
			return config;

		}

		private Topology getTopology() throws GsException {
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

		private IntegrationFunctionMapping getMapping() throws GsException {

			updateIntegrationFunction();
			return mapping;
		}

		private void init() {
			setMainPanel(getMainPanel());

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
				instanceSelectorPanel.setSize(instanceSelectorPanel
						.getPreferredSize());
			}
			return instanceSelectorPanel;

		}

		private JPanel getAdjacencyMatrixPanel() {
			if (adjacencyMatrixPanel == null) {
				adjacencyMatrixPanel = new JPanel();
				adjacencyMatrixPanel.setLayout(new GridBagLayout());
				GridBagConstraints constraints = new GridBagConstraints();
				GridBagConstraints topConstraints = new GridBagConstraints();
				GridBagConstraints bottomConstraints = new GridBagConstraints();

				// TODO: replace with STR_comp_nrInstances
				adjacencyMatrixPanel.setBorder(BorderFactory
						.createTitledBorder("Specify Neighbouring modules"));

				constraints.fill = GridBagConstraints.NONE;
				topConstraints.fill = GridBagConstraints.NONE;
				bottomConstraints.fill = GridBagConstraints.NONE;
				
				if (instances <= 1) {
					return adjacencyMatrixPanel;
				}

				JPanel top = new JPanel();
				top.setLayout(new GridBagLayout());
				
				int x = 0;
				int y = 0;
				while (x <= instances && y <= instances) {
					topConstraints.gridx = x;
					topConstraints.gridy = y;
					topConstraints.weighty = 0;
					topConstraints.weightx = 0;

					
					if (x == 0 && y == 0)
						top.add(new JLabel(), topConstraints);
					
					
					if (y == 0 && x > 0)
						top.add(new JLabel("M" + x),
								topConstraints);
					if (x == 0 && y > 0) {
						top.add(new JLabel("M" + y),
								topConstraints);
					}
					if (x > 0 && y > 0) {
						JCheckBox checkBox = new JCheckBox();

						// Modules cannot be their own neighbours
						if (x == y)
							checkBox.setEnabled(false);
						else
							checkBox.setEnabled(true);

						checkBox.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								boolean selection = ((JCheckBox) e.getSource()).isSelected();
								if (symmetricTopology)
									forceSymmetry(selection);
							}

						});
						matrix[x - 1][y - 1] = checkBox;
						top.add(checkBox, topConstraints);
						
					}
					
					if (x == instances){
						topConstraints.gridx = x + 1;
						topConstraints.gridy = y;
						topConstraints.weightx = 1;
						top.add(new JLabel(),topConstraints);
					}

					x++;
					if (x > instances && y <= instances) {
						y++;
						x = 0;
					}
				}


				ButtonGroup symmetry = new ButtonGroup();
				JRadioButton buttonSym = new JRadioButton("Symmetric topology");
				buttonSym.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						symmetricTopology = true;
					}

				});
				JRadioButton buttonAsy = new JRadioButton(
						"Non-symmetric topology");
				buttonAsy.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						symmetricTopology = false;

					}

				});
				
				if (symmetricTopology)
					buttonSym.setSelected(true);
				else
					buttonAsy.setSelected(true);

				symmetry.add(buttonSym);
				symmetry.add(buttonAsy);
			
				constraints.gridx = 0;
				constraints.gridy = GridBagConstraints.RELATIVE;
				constraints.weighty = 1;
				constraints.weightx = 1;
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				adjacencyMatrixPanel.add(new JLabel(),constraints);
				
				top.setSize(top.getPreferredSize());
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				adjacencyMatrixPanel.add(top, constraints);
				
				
				JPanel bottom = new JPanel();
				bottom.setLayout(new GridBagLayout());
				
				bottomConstraints.gridx = 0;
				bottomConstraints.gridy = 0;
				bottomConstraints.weighty = 0;
				bottomConstraints.weightx = 0;
				bottomConstraints.gridwidth = 1;
				bottom.add(new JLabel(),bottomConstraints);
				
				bottomConstraints.gridx = 1;
				bottomConstraints.weightx = 0;
				bottom.add(buttonSym,bottomConstraints);

				bottomConstraints.gridx = GridBagConstraints.RELATIVE;
				bottomConstraints.gridwidth = GridBagConstraints.REMAINDER;
				bottomConstraints.weightx = 1;
				bottom.add(new JLabel(),bottomConstraints);
				
				bottomConstraints.gridx = 0;
				bottomConstraints.gridy = 1;
				bottomConstraints.weightx = 0;
				bottomConstraints.weighty = 0;
				bottomConstraints.gridwidth = 1;
				bottom.add(new JLabel(),bottomConstraints);
				
				
				bottomConstraints.gridx = 1;
				bottomConstraints.weightx = 0;
				bottom.add(buttonAsy,bottomConstraints);
				
				bottomConstraints.gridx = GridBagConstraints.RELATIVE;
				bottomConstraints.gridwidth = GridBagConstraints.REMAINDER;
				bottomConstraints.weightx = 1;
				bottom.add(new JLabel(),bottomConstraints);
				
				bottomConstraints.gridy = 2;
				bottomConstraints.weighty = 1;
				bottomConstraints.weightx = 1;
				bottomConstraints.gridheight = GridBagConstraints.REMAINDER;
				bottomConstraints.gridwidth = GridBagConstraints.REMAINDER;
				bottom.add(new JLabel(),bottomConstraints);
				
				bottom.setSize(bottom.getPreferredSize());
				constraints.gridx = 0;
				constraints.gridy = GridBagConstraints.RELATIVE;
				constraints.gridheight = GridBagConstraints.REMAINDER;
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				adjacencyMatrixPanel.add(bottom,constraints);
				
				adjacencyMatrixPanel.setSize(adjacencyMatrixPanel
						.getPreferredSize());
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
					integrationPanel.setSize(integrationPanel
							.getPreferredSize());
				}
			}
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

		private void forceSymmetry(boolean selection) {
			for (int x = 0; x < matrix.length; x++)
				for (int y = 0; y < matrix.length; y++)
					if (selection){
					if (matrix[x][y].isSelected())
						matrix[y][x].setSelected(true);
					} else {
						if (! matrix[x][y].isSelected())
							matrix[y][x].setSelected(false);
					}
			
		}
	}
}
