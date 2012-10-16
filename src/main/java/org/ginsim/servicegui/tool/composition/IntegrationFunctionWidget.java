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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.composition.IntegrationFunction;
import org.ginsim.service.tool.composition.IntegrationFunctionMapping;

public class IntegrationFunctionWidget extends JPanel {

	private static final long serialVersionUID = 2423386096996654728L;
	List<RegulatoryNode> inputNodes = null;
	List<RegulatoryNode> properNodes = null;
	private Map<RegulatoryNode, JCheckBox> mappedInputSelection = new HashMap<RegulatoryNode, JCheckBox>();
	private Map<RegulatoryNode, JComboBox> mappedFunctionSelection = new HashMap<RegulatoryNode, JComboBox>();
	private Map<RegulatoryNode, JList> mappedProperSelection = new HashMap<RegulatoryNode, JList>();
	private Map<RegulatoryNode, JScrollPane> mappedPane = new HashMap<RegulatoryNode, JScrollPane>();

	public IntegrationFunctionWidget(final CompositionSpecificationDialog dialog) {
		super();

		RegulatoryGraph graph = dialog.getGraph();
		setLayout(new GridBagLayout());
		setBorder(BorderFactory
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
						JCheckBox checkBox = mappedInputSelection.get(node);
						JComboBox comboBox = mappedFunctionSelection.get(node);
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

			add(nodeCheck, constraints);
			constraints.gridx = 1;
			add(nodeLabel, constraints);
			constraints.gridx = 2;
			add(nodeCombo, constraints);
			constraints.gridx = 3;
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			add(nodeScroll, constraints);
			setSize(getPreferredSize());
		}
	}

	public IntegrationFunctionMapping getMapping() throws GsException {
		IntegrationFunctionMapping mapping = new IntegrationFunctionMapping();
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

					if (!IntegrationFunction.whichCanApply(node, listProper)
							.contains(selectedFunction))
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
		return mapping;
	}

}
