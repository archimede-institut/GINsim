package org.ginsim.servicegui.tool.composition;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.composition.IntegrationFunction;
import org.ginsim.service.tool.composition.IntegrationFunctionMapping;

/**
 * Widget to specify the Integration Function
 * 
 * @author Nuno D. Mendes
 */

public class IntegrationFunctionWidget extends JPanel {

	private static final long serialVersionUID = 2423386096996654728L;
	private CompositionSpecificationDialog dialog = null;
	private List<RegulatoryNode> inputNodes = null;
	private List<RegulatoryNode> properNodes = null;
	private JLabel messageLabel = new JLabel();
	private JLabel outputLabel = new JLabel();
	private Map<RegulatoryNode, JCheckBox> mappedInputSelection = new HashMap<RegulatoryNode, JCheckBox>();
	private Map<RegulatoryNode, JComboBox> mappedFunctionSelection = new HashMap<RegulatoryNode, JComboBox>();
	private Map<RegulatoryNode, JList> mappedProperSelection = new HashMap<RegulatoryNode, JList>();
	private Map<JList, RegulatoryNode> reverseProperSelectionMap = new HashMap<JList, RegulatoryNode>();
	private Map<RegulatoryNode, JScrollPane> mappedPane = new HashMap<RegulatoryNode, JScrollPane>();

	public IntegrationFunctionWidget(final CompositionSpecificationDialog dialog) {
		super();

		// TODO: replace with STR

		this.dialog = dialog;
		RegulatoryGraph graph = dialog.getGraph();
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		setBorder(BorderFactory
				.createTitledBorder("Specify Integration Function for Inputs"));

		List<RegulatoryNode> listNodes = graph.getNodeOrder();
		inputNodes = new ArrayList<RegulatoryNode>();
		properNodes = new ArrayList<RegulatoryNode>();

		messageLabel.setForeground(Color.RED);
		

		for (RegulatoryNode node : listNodes) {
			if (node.isInput())
				inputNodes.add(node);
			else
				properNodes.add(node);
		}

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.ParallelGroup hGroup = layout.createParallelGroup(Alignment.CENTER);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addComponent(messageLabel);

		GroupLayout.ParallelGroup nodeCheckGroup = layout
				.createParallelGroup(Alignment.CENTER);
		GroupLayout.ParallelGroup nodeLabelGroup = layout
				.createParallelGroup(Alignment.CENTER);
		GroupLayout.ParallelGroup nodeComboGroup = layout
				.createParallelGroup(Alignment.CENTER);
		GroupLayout.ParallelGroup nodeScrollGroup = layout
				.createParallelGroup(Alignment.CENTER);


		hGroup.addComponent(messageLabel);
		hGroup.addGroup(layout.createSequentialGroup()
				.addGroup(nodeCheckGroup).addGroup(nodeLabelGroup)
				.addGroup(nodeComboGroup).addGroup(nodeScrollGroup));
		hGroup.addComponent(outputLabel);


		for (RegulatoryNode node : inputNodes) {

			JCheckBox nodeCheck = new JCheckBox();
			mappedInputSelection.put(node, nodeCheck);
			nodeCheck.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					for (RegulatoryNode node : inputNodes) {
						JCheckBox checkBox = mappedInputSelection.get(node);
						JComboBox comboBox = mappedFunctionSelection.get(node);
						JScrollPane scroll = mappedPane.get(node);
						if (checkBox.isSelected()) {
							Dimension size = comboBox.getSize();
							comboBox.setEnabled(true);
							comboBox.setSize(size);
							scroll.setEnabled(true);
							dialog.setAsMapped(node);
						} else {
							comboBox.setEnabled(false);
							scroll.setEnabled(false);
							dialog.unsetAsMapped(node);
						}

					}
				}

			});

			JLabel nodeLabel = new JLabel(node.getId());

			JComboBox nodeCombo = getComboBoxForNode(node);
			mappedFunctionSelection.put(node, nodeCombo);

			JScrollPane nodeScroll = getScrollPaneForNode(node);
			mappedPane.put(node, nodeScroll);

			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(nodeCheck).addComponent(nodeLabel)
					.addComponent(nodeCombo).addComponent(nodeScroll));
			
			

			nodeCheckGroup.addComponent(nodeCheck);
			nodeLabelGroup.addComponent(nodeLabel);
			nodeComboGroup.addComponent(nodeCombo);
			nodeScrollGroup.addComponent(nodeScroll);
			
			

		}

		vGroup.addComponent(outputLabel);
		
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);

		setSize(getPreferredSize());
	}

	/**
	 * 
	 * Builds the integration function mapping object from current selections
	 * 
	 * @return IntegrationFunctionMapping
	 */
	public IntegrationFunctionMapping getMapping() {
		IntegrationFunctionMapping mapping = new IntegrationFunctionMapping();

		int attemptedMappings = 0;
		int successfulMappings = 0;

		for (RegulatoryNode node : inputNodes) {
			JCheckBox checkBox = mappedInputSelection.get(node);
			JComboBox comboBox = mappedFunctionSelection.get(node);

			if (checkBox == null || comboBox == null)
				continue;

			if (checkBox.isSelected()) {
				Object selectedFunction = comboBox.getSelectedItem();
				if (selectedFunction instanceof IntegrationFunction) {
					List<RegulatoryNode> listProper = getSelectedProperComponentsForInput(node);
					
					if (listProper == null)
						continue;

					attemptedMappings++;
					try {
						mapping.addMapping(node, listProper,
								(IntegrationFunction) selectedFunction);

						successfulMappings++;

					} catch (GsException e) {
						/*
						 * NotificationManager.publishException(
						 * "Composition specification error",
						 * "Refusing to add unauthorised mapping", e);
						 */
						String listProperString = "";
						for (RegulatoryNode proper : listProper) {
							if (!listProperString.isEmpty())
								listProperString += ", ";
							listProperString += proper.getNodeInfo()
									.getNodeID();

						}
						messageLabel
								.setText("Refusing to add unauthorised mapping: "
										+ node.getNodeInfo().getNodeID()
										+ " to "
										+ selectedFunction
										+ "("
										+ listProperString + ")");
					}

				}

			}
		}

		if (attemptedMappings == successfulMappings)
			messageLabel.setText("");
		
		String text = "";
		for (RegulatoryNode node : mapping.getMappedInputs()){
			String arguments = "";
			for (RegulatoryNode proper : mapping.getProperComponentsForInput(node)){
				if (!arguments.isEmpty())
					arguments += ", ";
				arguments += proper.getNodeInfo().getNodeID();
			}
			text += node.getNodeInfo().getNodeID() + " : " + mapping.getIntegrationFunctionForInput(node) + "(" + arguments +") "; 
		}
		
		outputLabel.setText(text);

		return mapping;
	}

	private JComboBox getComboBoxForNode(RegulatoryNode node) {


		Collection<IntegrationFunction> listIF = null;
		List<RegulatoryNode> properComponents = getSelectedProperComponentsForInput(node);

		if (properComponents == null || properComponents.isEmpty())
			listIF = IntegrationFunction.whichCanApply(node);
		else {
			System.err.println("Inquiring for "
					+ node.getNodeInfo().getNodeID() + " with "
					+ properComponents.size() + " arguments");
			listIF = IntegrationFunction.whichCanApply(node, properComponents);
		}

		JComboBox nodeCombo = null;

		if (!mappedFunctionSelection.containsKey(node)) {

			MutableComboBoxModel model = new DefaultComboBoxModel();
			for (IntegrationFunction intFun : listIF)
				model.addElement(intFun);

			nodeCombo = new JComboBox();
			nodeCombo.setModel(model);
			nodeCombo.setEditable(false);
			nodeCombo.setEnabled(false);
			nodeCombo.setSize(nodeCombo.getPreferredSize());

			nodeCombo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					getMapping(); // TODO should be called update()
					// TODO updateVisibleComponents
					// TODO updateInitialStates
				}

			});
			
			
		} else {
			// we hope it is never null
			nodeCombo = mappedFunctionSelection.get(node);
			
			IntegrationFunction selectedIF = (IntegrationFunction) nodeCombo.getSelectedItem();
			
			MutableComboBoxModel model = new DefaultComboBoxModel();
			for (IntegrationFunction intFun : listIF) {
				model.addElement(intFun);
				System.err.println("Adding function " + intFun
						+ "to function of node "
						+ node.getNodeInfo().getNodeID());
			}
			if (model.getSize() == 0)
				messageLabel
						.setText("No integration function is possible for the components selected for "
								+ node.getNodeInfo().getNodeID());
			else
				messageLabel.setText("");

			nodeCombo.setModel(model);
			if (listIF.contains(selectedIF))
				nodeCombo.setSelectedItem(selectedIF);
			else
				nodeCombo.setSelectedIndex(0);
			
			nodeCombo.repaint();
		}

		return nodeCombo;
	}

	private JScrollPane getScrollPaneForNode(RegulatoryNode node) {

		if (mappedPane.containsKey(node))
			return mappedPane.get(node);

		JList nodeList = new JList(properNodes.toArray());
		nodeList.setVisibleRowCount(0);
		nodeList.setLayoutOrientation(JList.VERTICAL_WRAP);
		nodeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		nodeList.setSize(nodeList.getPreferredSize());

		reverseProperSelectionMap.put(nodeList, node);
		mappedProperSelection.put(node, nodeList);

		nodeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent event) {
				JList list = (JList) event.getSource();
				fireAdmissibleIntegrationFunctionsChange(reverseProperSelectionMap
						.get(list));
				dialog.fireIntegrationMappingChange();
			}

		});

		JScrollPane nodeScroll = new JScrollPane(nodeList);
		nodeScroll.setSize(nodeScroll.getPreferredSize());
		nodeScroll.setEnabled(false);

		return nodeScroll;

	}

	public List<RegulatoryNode> getSelectedProperComponentsForInput(
			RegulatoryNode input) {
		JList selection = mappedProperSelection.get(input);
		if (selection == null)
			return null;
		int[] indices = selection.getSelectedIndices();
		List<RegulatoryNode> listProper = new ArrayList<RegulatoryNode>();
		for (int i = 0; i < indices.length; i++) {
			listProper.add(properNodes.get(indices[i]));
		}

		return listProper;
	}

	public void fireAdmissibleIntegrationFunctionsChange() {
		for (RegulatoryNode node : mappedFunctionSelection.keySet())
			getComboBoxForNode(node); // updates list of choices
	}

	public void fireAdmissibleIntegrationFunctionsChange(RegulatoryNode node) {
		System.err.println("Changing admissible integration functions for "
				+ node.getNodeInfo().getNodeID());
		if (mappedFunctionSelection.containsKey(node))
			getComboBoxForNode(node); // updates list of choices
	}

}
