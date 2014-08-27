package org.ginsim.gui.graph.regulatorygraph.perturbation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.perturbation.ListOfPerturbations;
import org.ginsim.gui.utils.widgets.RangeSlider;

public class PerturbationCreatePanel extends JPanel implements ActionListener, ChangeListener {

	private final ListOfPerturbations perturbations;
	private final PerturbationPanelCompanion helper;
	private PerturbationType type = null;
	
	private static final boolean SHOWTYPECOMBO = PerturbationType.values().length > 1 ? true : false;
	
	private final JComboBox selectNode;
    private final RegulatorModel regulatorModel;
	private final JComboBox selectRegulator;
	private final JComboBox selectType;
	private NodeInfo selected = null;
	private NodeInfo regulator = null;
	
	// setup value slider
	private JPanel setupPanel = new JPanel(new GridBagLayout());
	private JLabel valueLabel = new JLabel();
	
	private JRadioButton radioKO = new JRadioButton("0 - Knockout");
	private JRadioButton radioEct = new JRadioButton("1 - Ectopic activity");
	private RangeSlider rangeSlider = new RangeSlider(0,1);
	private JSlider valueSlider = new JSlider(0,1);
	
	private final CreateAction acCreate = new CreateAction(this);
	
	public PerturbationCreatePanel(PerturbationPanelCompanion helper, ListOfPerturbations perturbations) {
		super(new GridBagLayout());
		this.perturbations = perturbations;
		this.helper = helper;
		
		GridBagConstraints cst = new GridBagConstraints();

		if (SHOWTYPECOMBO) {
			add(new JLabel("Type of perturbation"), cst);
			
			cst = new GridBagConstraints();
			cst.gridy = 1;
			cst.weightx = 1;
			cst.fill = GridBagConstraints.HORIZONTAL;
			selectType = new JComboBox(PerturbationType.values());
			selectType.addActionListener(this);
			add(selectType, cst);

			cst = new GridBagConstraints();
			cst.gridx = 1;
		} else {
			selectType = null;
			cst.gridy = 1;
		}

		add(new JLabel("Component"), cst);
		
		cst = new GridBagConstraints();
		cst.gridy = 1;
		cst.gridx = 1;
		cst.weightx = 1;
		cst.fill = GridBagConstraints.HORIZONTAL;
		selectNode = new JComboBox(perturbations.getNodes());
		selectNode.addActionListener(this);
		add(selectNode, cst);

		// reserved space for the specialised settings
		cst = new GridBagConstraints();
		cst.gridx = 0;
		cst.gridy = 2;
		cst.gridwidth = 2;
		cst.weightx = 1;
		cst.weighty = 1;
		cst.fill = GridBagConstraints.BOTH;
		setupPanel.setMinimumSize(new Dimension(100, 100));
		add(setupPanel, cst);
		
		// fill the setup panel
		cst = new GridBagConstraints();
        regulatorModel = new RegulatorModel( perturbations.getGraph());
		selectRegulator = new JComboBox( regulatorModel);
        new JComboBox();
		selectRegulator.addActionListener(this);
		setupPanel.add(selectRegulator, cst);
		
		cst = new GridBagConstraints();
		cst.gridy = 1;
		setupPanel.add(valueLabel, cst);
		
		rangeSlider.setMinorTickSpacing(1);
		rangeSlider.setPaintTicks(true);
		rangeSlider.addChangeListener(this);
		ButtonGroup group = new ButtonGroup();
		group.add(radioKO);
		group.add(radioEct);
		radioKO.setSelected(true);
		
		cst = new GridBagConstraints();
		cst.anchor = GridBagConstraints.WEST;
		cst.gridx = 1;
		cst.gridy = 1;
		setupPanel.add(radioKO, cst);
		setupPanel.add(rangeSlider, cst);
		setupPanel.add(valueSlider, cst);
		
		cst = new GridBagConstraints();
		cst.anchor = GridBagConstraints.WEST;
		cst.gridx = 1;
		cst.gridy = 2;
		setupPanel.add(radioEct, cst);

		// create button
		cst = new GridBagConstraints();
		cst.gridx = 1;
		cst.gridy = 4;
		add(new JButton( acCreate ), cst);

		// create button
		cst = new GridBagConstraints();
		cst.gridx = 0;
		cst.gridy = 5;
		cst.gridwidth = 3;
		cst.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel("You can also select multiple perturbations to combine them"), cst);
		
		if (SHOWTYPECOMBO) {
			setType((PerturbationType)selectType.getSelectedItem());
		} else {
			setType(PerturbationType.RANGE);
		}
	}
	
	private void setType(PerturbationType type) {
		this.type = type;
		radioKO.setVisible(false);
		radioEct.setVisible(false);
		rangeSlider.setVisible(false);
		valueSlider.setVisible(false);
		selectRegulator.setVisible(false);
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
			
		case RANGE:
			
			if (selected.getMax() < 2) {
				int fixedValue = 0;
				if (radioEct.isSelected()) {
					fixedValue = 1;
				}
				perturbations.addFixedPerturbation(selected, fixedValue);
				helper.refresh();
				break;
			}
			
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

		case REGULATOR:
			if (regulator == null) {
				LogManager.error("No regulator selected");
				return;
			}
			
			int value = 0;
			if (regulator.getMax() < 2) {
				if (radioEct.isSelected()) {
					value = 1;
				}
			} else {
				value = valueSlider.getValue();
				if (value < 0) {
					value = 0;
				} else if (value > regulator.getMax()) {
					value = regulator.getMax();
				}
			}

			perturbations.addRegulatorPerturbation(regulator, selected, value);
			helper.refresh();
			break;
			
		default:
			LogManager.debug("Unknown perturbation type: "+type);
			return;
		}
		
		setType(type);
	}
	
    public void stateChanged(ChangeEvent e) {
    	switch (type) {
		case RANGE:
			if (selected.getMax() > 1) {
				valueLabel.setText("["+rangeSlider.getValue() + ","+rangeSlider.getUpperValue()+"]");
			}
			break;
		}
    }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object src = arg0.getSource();
		if (src == selectType) {
			setType( (PerturbationType)selectType.getSelectedItem() );
		} else if (src == selectNode) {
			updateGUI();
		} else if (src == selectRegulator) {
			this.regulator = (NodeInfo)selectRegulator.getSelectedItem();
			updateGUI();
		}
	}
	
	private void updateGUI() {
		PerturbationType selectedType = PerturbationType.RANGE;
		if (SHOWTYPECOMBO) {
			selectedType = (PerturbationType)selectType.getSelectedItem();
		}
		if (type != selectedType) {
			setType(selectedType);
		}
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
		case RANGE:
			selectRegulator.setVisible(false);
			if (selected.getMax() < 2) {
				setupPanel.setBorder(BorderFactory.createTitledBorder("Fix component value"));
				rangeSlider.setVisible(false);
				valueLabel.setVisible(false);
				radioKO.setVisible(true);
				radioEct.setVisible(true);
			} else {
				setupPanel.setBorder(BorderFactory.createTitledBorder("Lock component value in range"));
				rangeSlider.setMaximum(selected.getMax());
				rangeSlider.setValue(0);
				rangeSlider.setUpperValue(selected.getMax());
				radioKO.setVisible(false);
				radioEct.setVisible(false);
				rangeSlider.setVisible(true);
				valueLabel.setVisible(true);
			}
			break;
		case REGULATOR:
			// show GUI for regulator perturbation
			setupPanel.setBorder(BorderFactory.createTitledBorder("Fix a regulator for this component"));
            regulatorModel.setNode(selected);
			selectRegulator.setVisible(true);
			rangeSlider.setVisible(false);

			valueLabel.setVisible(false);
			valueSlider.setVisible(false);
			radioKO.setVisible(false);
			radioEct.setVisible(false);

			if (regulator == null) {
			} else if (regulator.getMax() < 2) {
				radioKO.setVisible(true);
				radioEct.setVisible(true);
			} else {
				valueLabel.setVisible(true);
				valueSlider.setValue(0);
				valueSlider.setMaximum(regulator.getMax());
				valueSlider.setVisible(true);
			}
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

class RegulatorModel extends DefaultComboBoxModel implements MutableComboBoxModel {

    private final RegulatoryGraph lrg;
    private NodeInfo node = null;

    public RegulatorModel(RegulatoryGraph lrg) {
        this.lrg = lrg;
    }

    public void setNode( NodeInfo ni) {
        if (ni == this.node) {
            return;
        }

        this.node = ni;
        int n = getSize();
        removeAllElements();
        fireIntervalRemoved(this, 0, n);
        if (ni == null) {
            setSelectedItem(null);
            return;
        }

        RegulatoryNode node = lrg.getNodeByName(ni.getNodeID());
        Collection<RegulatoryMultiEdge> regulators = lrg.getIncomingEdges(node);
        NodeInfo reg = null;
        for (RegulatoryMultiEdge e: regulators) {
            NodeInfo next = e.getSource().getNodeInfo();
            addElement(next);
            if (reg == null) {
                reg = next;
            }
        }

        fireIntervalAdded(this, 0, getSize());
        setSelectedItem(reg);
    }
}
