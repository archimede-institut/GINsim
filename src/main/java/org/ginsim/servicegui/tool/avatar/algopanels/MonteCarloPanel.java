package org.ginsim.servicegui.tool.avatar.algopanels;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.colomoto.biolqm.StatefulLogicalModel;
import org.ginsim.service.tool.avatar.params.AvatarParameters;
import org.ginsim.service.tool.avatar.service.EnumAlgorithm;
import org.ginsim.service.tool.avatar.simulation.MonteCarloSimulation;
import org.ginsim.service.tool.avatar.simulation.Simulation;

/**
 * Class for managing the panel for Monte Carlo simulations
 * 
 * @author Rui Henriques
 * @version 1.0
 */
public class MonteCarloPanel extends SimulationPanel {

	private static final long serialVersionUID = 1L;

	private JTextField runs = new JTextField(), depth = new JTextField();

	private String open = "<html><div style=\"width:265px;\">", end = "</div></html>";
	private String runsVar = open + "Maximum number of iterations [default: 1000]" + end;
	private String depthVar = open + "Maximum depth for each simulation [default: 1000]" + end;

	/**
	 * Instantiates the context of a simulation panel
	 *
	 * icon for the help tooltip
	 */
	public MonteCarloPanel() {
		this.setBorder(BorderFactory.createTitledBorder(EnumAlgorithm.MONTE_CARLO + " Parameters"));

		JLabel runsL = new JLabel("#Runs");
		JLabel depthL = new JLabel("Max depth    ");
		runs.setText("1E3");
		depth.setText("1E3");

		setLayout(new GridLayout(2, 2));
		runsL.setToolTipText(runsVar);
		depthL.setToolTipText(depthVar);
		add(runsL);
		add(runs);
		add(depthL);
		add(depth);
	}

	@Override
	public Simulation getSimulation(StatefulLogicalModel model, boolean quiet) throws Exception {
		MonteCarloSimulation sim = new MonteCarloSimulation();
		sim.addModel(model);
		sim.isGUI = true;
		sim.runs = (int) safeParseDouble(runs.getText());
		sim.maxSteps = (int) Double.parseDouble(depth.getText()); // optional
		sim.quiet = quiet;

		// System.out.println("MonteCarlo\n"+"Model: "+model.getName());
		// System.out.println("Initial states:");
		// for(byte[] state : model.getInitialStates()) System.out.println(" "+new
		// State(state));
		return sim;
	}

	@Override
	public void load(AvatarParameters p) {
		p.mcDepth = depth.getText();
		p.mcRuns = runs.getText();
	}

	private static double safeParseDouble(String str) {
		if (str.matches(".*E$")) {
			str += "0";
		}
		return Double.parseDouble(str);
	}

	@Override
	public void unload(AvatarParameters param) {
		depth.setText(param.mcDepth);
		runs.setText(param.mcRuns);
	}

}
