package org.ginsim.servicegui.export.cadp;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.ginsim.servicegui.tool.composition.CompositionSpecificationDialog;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;

/**
 * Widget to specify the initial states of each module
 * 
 * @author Nuno D. Mendes
 */
public class InitialStatesWidget extends JPanel {


	private CompositionSpecificationDialog dialog = null;


	public InitialStatesWidget(final CompositionSpecificationDialog dialog) {
		super();
		this.dialog = dialog;
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();


		// TODO: replace with STR_s
		setBorder(BorderFactory
				.createTitledBorder("Specify Initial States"));
		
		InitialStatePanel panel = new InitialStatePanel(dialog.getGraph(),true);
		panel.setSize(new Dimension(200,200));
		
		add(panel,constraints);
		setSize(getPreferredSize());

	}
	
	public List<byte[]> getInitialStates(){
		List<byte[]> initialStates = new ArrayList<byte[]>();
		
		return initialStates;
	}
	
}
