package org.ginsim.servicegui.tool.avatar.algopanels;

import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.colomoto.logicalmodel.StatefulLogicalModel;
import org.ginsim.service.tool.avatar.domain.State;
import org.ginsim.service.tool.avatar.params.AvatarParameters;
import org.ginsim.service.tool.avatar.simulation.MonteCarloSimulation;
import org.ginsim.service.tool.avatar.simulation.Simulation;

/**
 * Class for managing the panel for Monte Carlo simulations
 * @author Rui Henriques
 * @version 1.0
 */
public class MonteCarloPanel extends SimulationPanel {

	private static final long serialVersionUID = 1L;

	private JTextField runs = new JTextField(), depth = new JTextField();

	private String open = "<html><div style=\"width:265px;\">", end = "</div></html>";
	private String runsVar =open+"Specifies the maximum number of iterations performed by the program (should be less than the square size of the state space) [default: 1000]"+end;
	private String depthVar =open+"Specifies the limit depth (number of visited states) within each simulation [default: 1000]"+end;

	/**
	 * Instantiates the context of a simulation panel
	 * @param img icon for the help tooltip
	 * @param flex true for a flexible gridbaglayout (default: true)
	 */
	public MonteCarloPanel(Icon helpImg, boolean flex){
		super(helpImg,flex);
		flexible = flex;
		JLabel runsL = new JLabel("#Runs"), depthL = new JLabel("Max.Depth     ");
		runs.setText("1000");
		depth.setText("1000");

		if(flexible){
			setLayout(new GridLayout(2,2));
			runsL.setIcon(helpImg);
			depthL.setIcon(helpImg);
			runsL.setToolTipText(runsVar);
			depthL.setToolTipText(depthVar);
			add(runsL);
			add(runs);
			add(depthL);
			add(depth);
		} else {
			setLayout(null);
			JLabel runsQ = new JLabel(""), depthQ = new JLabel("");
			runsQ.setIcon(helpImg);
			depthQ.setIcon(helpImg);
			runsQ.setToolTipText(runsVar);
			depthQ.setToolTipText(depthVar);
			int width0=55, width2=70, height2=19, startX2=10, startY2=20;
			runsL.setBounds(startX2, startY2+height2*0, width2, height2);
			depthL.setBounds(startX2, startY2+height2*1, width2, height2);
			runs.setBounds(startX2+width2+2, startY2+height2*0, width0, height2);
			depth.setBounds(startX2+width2+2, startY2+height2*1, width0, height2);
			runsQ.setBounds(startX2+width2+width0+5, startY2, 15, 15);
			depthQ.setBounds(startX2+width2+width0+5, startY2+height2, 15, 15);
			add(runsQ);
			add(depthQ);
		}
	}

	@Override
	public Simulation getSimulation(StatefulLogicalModel model, boolean plots, boolean quiet) throws Exception{
		MonteCarloSimulation sim = new MonteCarloSimulation(model);
		sim.isGUI = true;
		sim.runs = Integer.valueOf(runs.getText());
		sim.maxSteps=Integer.valueOf(depth.getText()); //optional
		sim.quiet = quiet;
			
		System.out.println("MonteCarlo\n"+"Model: "+model.getName());
		System.out.println("Initial states:");
		for(byte[] state : model.getInitialStates()) System.out.println("  "+new State(state));
		return sim;
	}

	@Override
	public void load(AvatarParameters p) {
    	p.mcDepth=depth.getText();
    	p.mcRuns=runs.getText();
	}
	@Override
	public void unload(AvatarParameters param) {
    	depth.setText(param.mcDepth);
    	runs.setText(param.mcRuns);
	}

}
