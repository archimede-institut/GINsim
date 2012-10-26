package org.ginsim.servicegui.tool.composition;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Widget to specify the number of model instances
 * 
 * @author Nuno D. Mendes
 */

public class InstanceSelectorWidget extends JPanel {

	private static final long serialVersionUID = 4722616808096433759L;
	private JSpinner numberInstances = null;

	public InstanceSelectorWidget(final CompositionSpecificationDialog dialog) {
		this(dialog, 2, 1);
	}

	public InstanceSelectorWidget(final CompositionSpecificationDialog dialog,
			int minimum, int step) {
		super();
		setLayout(new GridBagLayout());

		// TODO: replace with STR_comp_nrInstances
		setBorder(BorderFactory.createTitledBorder("Number of Instances"));

		JSpinner input = null;
		if (this.numberInstances == null) {
			SpinnerNumberModel model = new SpinnerNumberModel();
			model.setMinimum(minimum);
			model.setStepSize(step);
			model.setValue(dialog.getNumberInstances());
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
				Integer value = (Integer) numberInstances.getValue();
				dialog.updateNumberInstances(value.intValue());
			}

		});

		add(input);
		add(update);
		setSize(getPreferredSize());
	}

}
