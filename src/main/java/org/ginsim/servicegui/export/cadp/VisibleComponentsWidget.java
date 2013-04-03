package org.ginsim.servicegui.export.cadp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
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
	private JLabel messageLabel = null;

	public VisibleComponentsWidget(final CompositionSpecificationDialog dialog) {
		super();
		this.dialog = dialog;
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);

		// TODO: replace with STR_s
		setBorder(BorderFactory
				.createTitledBorder("Specify Visible Components"));

		messageLabel = new JLabel();
		messageLabel.setText("");
		messageLabel.setForeground(Color.RED);

		listNodes = this.dialog.getGraph().getNodeOrder();

		for (RegulatoryNode node : listNodes) {
			if (!this.dialog.getMappedNodes().contains(node))
				eligible.add(node);
		}

		nodeList = new JList(eligible.toArray());
		nodeList.setVisibleRowCount(0);
		nodeList.setLayoutOrientation(JList.VERTICAL_WRAP);
		nodeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		nodeList.setSize(nodeList.getPreferredSize());

		nodeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
					update();

			}

		});

		nodeList.setSize(nodeList.getPreferredSize());

		JScrollPane nodeScroll = new JScrollPane(nodeList);
		nodeScroll.setSize(nodeScroll.getPreferredSize());
		nodeScroll.setEnabled(true);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.ParallelGroup hGroup = layout
				.createParallelGroup(Alignment.CENTER)
				.addComponent(messageLabel).addComponent(nodeScroll);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup()
				.addComponent(messageLabel).addComponent(nodeScroll);

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);

		setSize(getPreferredSize());

	}

	public List<RegulatoryNode> getSelectedNodes() {
		List<RegulatoryNode> selectedList = new ArrayList<RegulatoryNode>();
		int selected[] = nodeList.getSelectedIndices();

		for (int i = 0; i < selected.length; i++)
			selectedList.add(eligible.get(selected[i]));

		return selectedList;
	}

	public void setSelectedNodes(List<RegulatoryNode> selected) {
		for (RegulatoryNode node : selected)
			if (eligible.contains(node))
				nodeList.setSelectedIndex(eligible.indexOf(node));
	}

	private void update() {
		boolean isDiscernible = ((CADPExportConfigPanel) dialog)
				.areCompatibleStableStatesDiscernible();

		if (messageLabel != null) {
			if (isDiscernible) {
				messageLabel.setText("");
			} else {
				messageLabel
						.setText("Selected components are insufficient to distinguish between potential stable states");
			}

			this.repaint();
		}
	}

	public VisibleComponentsWidget reBuild() {
		VisibleComponentsWidget widget = new VisibleComponentsWidget(dialog);
		widget.setSelectedNodes(getSelectedNodes());
		widget.update();

		return widget;
	}

}
