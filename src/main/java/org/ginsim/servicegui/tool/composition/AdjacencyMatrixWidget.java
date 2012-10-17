package org.ginsim.servicegui.tool.composition;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.ginsim.common.application.GsException;
import org.ginsim.service.tool.composition.Topology;

public class AdjacencyMatrixWidget extends JPanel {

	private static final long serialVersionUID = -7735335091138597285L;
	private boolean symmetricTopology = false; // default is asymmetric
	private JCheckBox[][] matrix = null;
	private CompositionSpecificationDialog dialog = null;

	public AdjacencyMatrixWidget(final CompositionSpecificationDialog dialog) {
		super();
		this.dialog = dialog;
		int instances = dialog.getNumberInstances();
		matrix = new JCheckBox[instances][instances];
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		GridBagConstraints topConstraints = new GridBagConstraints();
		GridBagConstraints bottomConstraints = new GridBagConstraints();

		// TODO: replace with STR_s
		setBorder(BorderFactory
				.createTitledBorder("Specify Neighbouring modules"));

		constraints.fill = GridBagConstraints.NONE;
		topConstraints.fill = GridBagConstraints.NONE;
		bottomConstraints.fill = GridBagConstraints.NONE;

		if (instances > 1) {

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
					top.add(new JLabel("M" + x), topConstraints);
				if (x == 0 && y > 0) {
					top.add(new JLabel("M" + y), topConstraints);
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
							boolean selection = ((JCheckBox) e.getSource())
									.isSelected();
							if (symmetricTopology)
								forceSymmetry(selection);
						}

					});
					matrix[x - 1][y - 1] = checkBox;
					top.add(checkBox, topConstraints);

				}

				if (x == instances) {
					topConstraints.gridx = x + 1;
					topConstraints.gridy = y;
					topConstraints.weightx = 1;
					top.add(new JLabel(), topConstraints);
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
					forceSymmetry(true); // not sure we should do this
				}

			});
			JRadioButton buttonAsy = new JRadioButton("Non-symmetric topology");
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
			add(new JLabel(), constraints);

			top.setSize(top.getPreferredSize());
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			add(top, constraints);

			JPanel bottom = new JPanel();
			bottom.setLayout(new GridBagLayout());

			bottomConstraints.gridx = 0;
			bottomConstraints.gridy = 0;
			bottomConstraints.weighty = 0;
			bottomConstraints.weightx = 0;
			bottomConstraints.gridwidth = 1;
			bottom.add(new JLabel(), bottomConstraints);

			bottomConstraints.gridx = 1;
			bottomConstraints.weightx = 0;
			bottom.add(buttonSym, bottomConstraints);

			bottomConstraints.gridx = GridBagConstraints.RELATIVE;
			bottomConstraints.gridwidth = GridBagConstraints.REMAINDER;
			bottomConstraints.weightx = 1;
			bottom.add(new JLabel(), bottomConstraints);

			bottomConstraints.gridx = 0;
			bottomConstraints.gridy = 1;
			bottomConstraints.weightx = 0;
			bottomConstraints.weighty = 0;
			bottomConstraints.gridwidth = 1;
			bottom.add(new JLabel(), bottomConstraints);

			bottomConstraints.gridx = 1;
			bottomConstraints.weightx = 0;
			bottom.add(buttonAsy, bottomConstraints);

			bottomConstraints.gridx = GridBagConstraints.RELATIVE;
			bottomConstraints.gridwidth = GridBagConstraints.REMAINDER;
			bottomConstraints.weightx = 1;
			bottom.add(new JLabel(), bottomConstraints);

			bottomConstraints.gridy = 2;
			bottomConstraints.weighty = 1;
			bottomConstraints.weightx = 1;
			bottomConstraints.gridheight = GridBagConstraints.REMAINDER;
			bottomConstraints.gridwidth = GridBagConstraints.REMAINDER;
			bottom.add(new JLabel(), bottomConstraints);

			bottom.setSize(bottom.getPreferredSize());
			constraints.gridx = 0;
			constraints.gridy = GridBagConstraints.RELATIVE;
			constraints.gridheight = GridBagConstraints.REMAINDER;
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			add(bottom, constraints);

		}
		setSize(getPreferredSize());

	}
	
	private void forceSymmetry(boolean selection) {
		for (int x = 0; x < matrix.length; x++)
			for (int y = 0; y < matrix.length; y++)
				if (selection) {
					if (matrix[x][y].isSelected())
						matrix[y][x].setSelected(true);
				} else {
					if (!matrix[x][y].isSelected())
						matrix[y][x].setSelected(false);
				}

	}

	public boolean isSelected(int x, int y){
		if (x < matrix.length && y < matrix.length)
			return matrix[x][y].isSelected();
		return false;
	}
	
	public Topology getTopology() throws GsException{
		int instances = dialog.getNumberInstances();
		Topology topology = new Topology(instances);

		for (int x = 0; x < instances; x++) {
			for (int y = 0; y < instances; y++) {
				if (isSelected(x, y))
					topology.addNeighbour(x, y);
			}
		}
		return topology;
	}
	
}
