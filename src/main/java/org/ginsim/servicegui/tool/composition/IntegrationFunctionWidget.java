package org.ginsim.servicegui.tool.composition;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.service.tool.composition.IntegrationFunction;
import org.ginsim.service.tool.composition.IntegrationFunctionMapping;

/**
 * Widget to specify the Integration Function
 * 
 * @author Nuno D. Mendes
 */

public class IntegrationFunctionWidget extends JPanel {

	private static final long serialVersionUID = 2423386096996654728L;
	private List<RegulatoryNode> inputNodes = null;
	private List<RegulatoryNode> properNodes = null;
	private JLabel messageLabel = new JLabel();
	private Map<RegulatoryNode, JCheckBox> mappedInputSelection = new HashMap<RegulatoryNode, JCheckBox>();
	private Map<RegulatoryNode, JComboBox> mappedFunctionSelection = new HashMap<RegulatoryNode, JComboBox>();
	private Map<RegulatoryNode, JList> mappedProperSelection = new HashMap<RegulatoryNode, JList>();
	private Map<JList, RegulatoryNode> reverseProperSelectionMap = new HashMap<JList, RegulatoryNode>();
	private Map<RegulatoryNode, JScrollPane> mappedPane = new HashMap<RegulatoryNode, JScrollPane>();

	public IntegrationFunctionWidget(final CompositionSpecificationDialog dialog) {
		super();

		// TODO: replace with STR

		RegulatoryGraph graph = dialog.getGraph();
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		setBorder(BorderFactory
				.createTitledBorder("Specify Integration Function for Inputs"));

		List<RegulatoryNode> listNodes = graph.getNodeOrder();
		inputNodes = new ArrayList<RegulatoryNode>();
		properNodes = new ArrayList<RegulatoryNode>();

		for (RegulatoryNode node : listNodes) {
			if (node.isInput())
				inputNodes.add(node);
			else
				properNodes.add(node);
		}

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addComponent(messageLabel);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addComponent(messageLabel);

		GroupLayout.ParallelGroup nodeCheckGroup = layout.createParallelGroup();
		GroupLayout.ParallelGroup nodeLabelGroup = layout.createParallelGroup();
		GroupLayout.ParallelGroup nodeComboGroup = layout.createParallelGroup();
		GroupLayout.ParallelGroup nodeScrollGroup = layout
				.createParallelGroup();

		hGroup.addGroup(nodeCheckGroup);
		hGroup.addGroup(nodeLabelGroup);
		hGroup.addGroup(nodeComboGroup);
		hGroup.addGroup(nodeScrollGroup);

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
							dialog.setAsMapped(node);

						} else {
							comboBox.setSelectedIndex(0);
							comboBox.setEnabled(false);
							scroll.setEnabled(false);
							dialog.unsetAsMapped(node);
						}

					}
				}

			});

			JLabel nodeLabel = new JLabel(node.getId());

			Collection<IntegrationFunction> listIF = null;
			List<RegulatoryNode> properComponents = getMapping()
					.getProperComponentsForInput(node);

			// TODO: this should be updated with actions on the list of
			// arguments
			if (properComponents == null || properComponents.isEmpty())
				listIF = IntegrationFunction.whichCanApply(node);
			else
				listIF = IntegrationFunction.whichCanApply(node,
						properComponents);

			Object[] listChoices = new Object[listIF.size() + 1];
			int i = 0;
			for (IntegrationFunction intFun : listIF)
				listChoices[i++] = intFun;

			JComboBox nodeCombo = new JComboBox(listChoices);
			nodeCombo.setEditable(false);
			nodeCombo.setEnabled(false);

			mappedFunctionSelection.put(node, nodeCombo);

			JList nodeList = new JList(properNodes.toArray());
			nodeList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			nodeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

			nodeList.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent event) {
					int[] selection = ((JList) event.getSource())
							.getSelectedIndices();
					RegulatoryNode input = reverseProperSelectionMap
							.get((JList) event.getSource());
					JComboBox nodeCombo = mappedFunctionSelection.get(input);
					// TODO: nodeCombo.removeAllItems();
					// TODO: nodeCombo.addItem() for each option
					// TODO: keep selected item it if is still admissible and
					// fireXXXchange if value is changed or reset
				}

			});

			// TODO: Add ListSelectionListener (should take into account corresponding
			// TODO: node to select new list of actions that can be applied)

			JScrollPane nodeScroll = new JScrollPane(nodeList);
			nodeScroll.setPreferredSize(new Dimension(50, 60));
			nodeScroll.setEnabled(false);

			reverseProperSelectionMap.put(nodeList, node);
			mappedProperSelection.put(node, nodeList);
			mappedPane.put(node, nodeScroll);

			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(nodeCheck).addComponent(nodeLabel)
					.addComponent(nodeCombo).addComponent(nodeScroll));
			nodeCheckGroup.addComponent(nodeCheck);
			nodeLabelGroup.addComponent(nodeLabel);
			nodeComboGroup.addComponent(nodeCombo);
			nodeScrollGroup.addComponent(nodeScroll);

		}

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
	}

	/**
	 * 
	 * Builds the integration function mapping object from current selections
	 * 
	 * @return IntegrationFunctionMapping
	 */
	public IntegrationFunctionMapping getMapping() {
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

					try {
						mapping.addMapping(node, listProper,
								(IntegrationFunction) selectedFunction);
					} catch (GsException e) {
						NotificationManager.publishException(
								"Composition specification error",
								"Refusing to add unauthorised mapping", e);
					}

				}

			}
		}
		return mapping;
	}

}
