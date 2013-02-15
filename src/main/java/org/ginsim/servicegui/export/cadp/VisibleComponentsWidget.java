package org.ginsim.servicegui.export.cadp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.servicegui.tool.composition.CompositionSpecificationDialog;

/**
 * Widget to specify the visible components
 * 
 * @author Nuno D. Mendes
 */
public class VisibleComponentsWidget extends JPanel {

	private static final long serialVersionUID = 6900573600550522830L;
	private CompositionSpecificationDialog dialog = null;
	private List<RegulatoryNode> listNodes = null;
	private List<RegulatoryNode> eligible = new ArrayList<RegulatoryNode>();
	private JList nodeList = null;
	private JLabel message = null;

	public VisibleComponentsWidget(final CompositionSpecificationDialog dialog) {
		super();
		this.dialog = dialog;
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// TODO: replace with STR_s
		setBorder(BorderFactory
				.createTitledBorder("Specify Visible Components"));

		message = new JLabel();
		message.setText("");
		message.setForeground(Color.RED);

		listNodes = this.dialog.getGraph().getNodeOrder();

		for (RegulatoryNode node : listNodes) {
			if (!this.dialog.getMappedNodes().contains(node))
				eligible.add(node);
		}

		nodeList = new JList(eligible.toArray());
		nodeList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		nodeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		nodeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				updateMessage();
			}

		});

		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(message, constraints);

		JScrollPane nodeScroll = new JScrollPane(nodeList);
		nodeScroll.setPreferredSize(new Dimension(50, 60));
		nodeScroll.setEnabled(true);

		constraints.gridheight = GridBagConstraints.REMAINDER;

		add(nodeScroll, constraints);
		setSize(getPreferredSize());

	}

	public List<RegulatoryNode> getSelectedNodes() {
		List<RegulatoryNode> selectedList = new ArrayList<RegulatoryNode>();
		int selected[] = nodeList.getSelectedIndices();
		for (int i = 0; i < selected.length; i++)
			selectedList.add(eligible.get(selected[i]));

		return selectedList;
	}

	private void updateMessage() {
		boolean isDiscernible = ((CADPExportConfigPanel) dialog)
				.areCompatibleStableStatesDiscernible();

		if (message != null) {
			if (isDiscernible) {
				message.setText("");
			} else {
				message.setText("Selected components are insufficient to distinguish between potential stable states");
			}
		}
	}

}
