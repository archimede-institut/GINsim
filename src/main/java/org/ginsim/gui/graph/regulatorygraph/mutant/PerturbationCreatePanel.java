package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.batik.ext.swing.GridBagConstants;
import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.regulatorygraph.perturbation.RegulatoryMutants;
import org.ginsim.gui.utils.widgets.RangeSlider;

public class PerturbationCreatePanel extends JPanel implements ActionListener, ChangeListener {

	private final RegulatoryMutants perturbations;
	private final PerturbationPanelListHelper helper;
	private PerturbationType type = null;
	
	private final JComboBox<NodeInfo> selectNode;
	private NodeInfo selected = null;
	
	// setup value slider
	private JPanel setupPanel = new JPanel(new GridBagLayout());
	private JLabel valueLabel = new JLabel();
	private JSlider fixSlider = new JSlider(0,1);
	private RangeSlider rangeSlider = new RangeSlider(0,1);
	
	private final CreateAction acCreate = new CreateAction(this);
	
	public PerturbationCreatePanel(PerturbationPanelListHelper helper, RegulatoryMutants perturbations) {
		super(new GridBagLayout());
		this.perturbations = perturbations;
		this.helper = helper;
		
		GridBagConstraints cst = new GridBagConstraints();
		add(new JLabel("Component"), cst);
		
		cst = new GridBagConstraints();
		cst.gridx = 1;
		cst.weightx = 1;
		cst.fill = GridBagConstants.HORIZONTAL;
		selectNode = new JComboBox<NodeInfo>(perturbations.getNodes());
		selectNode.addActionListener(this);
		add(selectNode, cst);

		// reserved space for the specialised settings
		cst = new GridBagConstraints();
		cst.gridx = 0;
		cst.gridy = 1;
		cst.gridwidth = 2;
		cst.fill = GridBagConstants.BOTH;
		setupPanel.setMinimumSize(new Dimension(100, 100));
		add(setupPanel, cst);
		
		// fill the setup panel
		cst = new GridBagConstraints();
		setupPanel.add(valueLabel, cst);
		
		fixSlider.setMinorTickSpacing(1);
		fixSlider.setPaintTicks(true);
		fixSlider.addChangeListener(this);
		rangeSlider.setMinorTickSpacing(1);
		rangeSlider.setPaintTicks(true);
		rangeSlider.addChangeListener(this);
		cst = new GridBagConstraints();
		cst.gridx = 1;
		setupPanel.add(fixSlider, cst);
		setupPanel.add(rangeSlider, cst);

		// create button
		cst = new GridBagConstraints();
		cst.gridx = 0;
		cst.gridy = 2;
		add(new JButton( acCreate ), cst);
	}
	
	public void setType(PerturbationType type) {
		this.type = type;
		fixSlider.setVisible(false);
		rangeSlider.setVisible(false);
		valueLabel.setText("");

		if (type == null) {
			selectNode.setEnabled(false);
			return;
		}
		
		selectNode.setSelectedItem(null);
	}

	protected void create() {
		if (type == null) {
			LogManager.error("No perturbation type defined");
			return;
		}
		
		if (selected == null) {
			LogManager.error("No node selected");
			return;
		}
		
		switch (type) {
		case FIXED:
			int fixedValue = fixSlider.getValue();
			if (fixedValue < 0) {
				fixedValue = 0;
			} else if (fixedValue > selected.getMax()) {
				fixedValue = selected.getMax();
			}
			perturbations.addFixedPerturbation(selected, fixedValue);
			helper.refresh();
			break;
			
		case RANGE:
			int min = rangeSlider.getValue();
			int max = rangeSlider.getUpperValue();
			if (min < 0) {
				min = 0;
			}
			if (max > selected.getMax()) {
				max = selected.getMax();
			}
			
			if (min > max) {
				min = max;
			}
			perturbations.addRangePerturbation(selected, min, max);
			helper.refresh();
			break;

		default:
			LogManager.debug("Unknown perturbation type: "+type);
			return;
		}
		
		setType(type);
	}
	
	protected void cancel() {
		helper.selectionChanged(null);
	}

    public void stateChanged(ChangeEvent e) {
    	switch (type) {
		case FIXED:
			valueLabel.setText(""+fixSlider.getValue());
			break;
		case RANGE:
			valueLabel.setText("["+rangeSlider.getValue() + ","+rangeSlider.getUpperValue()+"]");
			break;
		}
    }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (type == null) {
			acCreate.setEnabled(false);
			setupPanel.setBorder(null);
			return;
		}

		// called when the selected node changed: update GUI accordingly
		Object sel = selectNode.getSelectedItem();
		if (sel == null || !(sel instanceof NodeInfo )) {
			acCreate.setEnabled(false);
			selected = null;
			setupPanel.setBorder(null);
			return;
		}
		selected = (NodeInfo)sel;
		
		switch (type) {
		case FIXED:
			setupPanel.setBorder(BorderFactory.createTitledBorder("Fix component value"));
			fixSlider.setMaximum(selected.getMax());
			fixSlider.setValue(0);
			fixSlider.setVisible(true);
			break;

		case RANGE:
			setupPanel.setBorder(BorderFactory.createTitledBorder("Lock component value in range"));
			rangeSlider.setMaximum(selected.getMax());
			rangeSlider.setValue(0);
			rangeSlider.setUpperValue(selected.getMax());
			rangeSlider.setVisible(true);
			break;
			
		default:
			setupPanel.setBorder(BorderFactory.createTitledBorder("???"));
			return;
		}
		
		stateChanged(null);
		acCreate.setEnabled(true);
	}

}

class CreateAction extends AbstractAction {
	private final PerturbationCreatePanel panel;

	public CreateAction(PerturbationCreatePanel panel) {
		super("Create");
		this.panel = panel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		panel.create();
	}
}
