package org.ginsim.servicegui.tool.avatar.algopanels;

import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.colomoto.logicalmodel.StatefulLogicalModel;
import org.ginsim.service.tool.avatar.domain.State;
import org.ginsim.service.tool.avatar.params.AvatarParameters;
import org.ginsim.service.tool.avatar.simulation.FirefrontMDDSimulation;
import org.ginsim.service.tool.avatar.simulation.FirefrontSimulation;
import org.ginsim.service.tool.avatar.simulation.Simulation;

/**
 * Class for managing the panel for Firefront simulations
 * @author Rui Henriques
 * @version 1.0
 */
public class FirefrontPanel extends SimulationPanel {

	private static final long serialVersionUID = 1L;

	private JTextField maxExpandffB = new JTextField(), depthffB = new JTextField();
	private JTextField alphaB = new JTextField(), betaB = new JTextField();
	
	private String open = "<html><div style=\"width:265px;\">", end = "</div></html>";
	private String runsffVar =open+"Specifies the maximum number of iterations performed by the program (should be less than the square size of the state space) [default: 1000]"+end;
	private String alphaVar =open+"Specifies the minimum probability required for a state to be explored (be included the firefront set of probable states) [default: 10^-5]"+end;
	private String betaVar =open+"Specifies the maximum residual probability in the firefront state-set (probable states) in order to stop the algorithm [default: 10^-5]"+end;
	private String maxExpandVar =open+"Specifices the maximum number of states to be analyzed in the firefront state-set (probable states) [default: 10000]. The states with lower probability will transition to the set of neglected states."+end;
	private String depthffVar =open+"Specifies the limit depth (number of visited states) within each simulation [default: 10000]"+end;

	/**
	 * Instantiates the context of a simulation panel
	 * @param img icon for the help tooltip
	 * @param flex true for a flexible gridbaglayout (default: true)
	 */
	public FirefrontPanel(Icon img, boolean flex){
		super(img,flex);
		JLabel alphaL = new JLabel("Alpha"), betaL = new JLabel("Beta");
		JLabel maxExpandffL = new JLabel("Max.Expand   "), depthffL = new JLabel("Max.Depth");
		JLabel[] ffquestions = new JLabel[]{new JLabel(""), new JLabel(""), new JLabel(""), new JLabel("")};
		for(JLabel question : ffquestions) question.setIcon(helpImg);

		if(flexible){
			setLayout(new GridLayout(4,2));
			alphaL.setIcon(helpImg);
			betaL.setIcon(helpImg);
			maxExpandffL.setIcon(helpImg);
			depthffL.setIcon(helpImg);
			alphaL.setToolTipText(alphaVar);
			betaL.setToolTipText(betaVar);
			maxExpandffL.setToolTipText(maxExpandVar);
			depthffL.setToolTipText(depthffVar);
		} else {
			setLayout(null);
			int width0=55, width2=70, height2=19, startX2=10, startY2=20;
			int shift=0;
			//runsffL.setBounds(startX2, startY2+height2*0, width2, height2);
			alphaL.setBounds(startX2, startY2+height2*1, width2, height2);
			betaL.setBounds(startX2, startY2+height2*2, width2, height2);
			maxExpandffL.setBounds(startX2, startY2+height2*3, width2, height2);
			depthffL.setBounds(startX2, startY2+height2*4, width2, height2);
			//runsffB.setBounds(startX2+width2+2, startY2+height2*0, width0, height2);
			alphaB.setBounds(startX2+width2+2, startY2+height2*1, width0, height2);
			betaB.setBounds(startX2+width2+2, startY2+height2*2, width0, height2);
			///maxExpandffB.setBounds(startX2+width2+2, startY2+height2*3, width0, height2);
			depthffB.setBounds(startX2+width2+2, startY2+height2*4, width0, height2);
			ffquestions[0].setToolTipText(runsffVar);
			ffquestions[1].setToolTipText(alphaVar);
			ffquestions[2].setToolTipText(betaVar);
			ffquestions[3].setToolTipText(maxExpandVar);
			ffquestions[4].setToolTipText(depthffVar);
			for(JLabel question : ffquestions) 
				question.setBounds(startX2+width2+width0+5, startY2+height2*(shift++), 15, 15);
			for(JLabel question : ffquestions) add(question);
		}
		depthffB.setText("10000");
		alphaB.setText("0.00001");
		betaB.setText("0.00001");
		maxExpandffB.setText("1000");
		add(depthffL);
		add(depthffB);
		add(alphaL);
		add(alphaB);
		add(betaL);
		add(betaB);
		add(maxExpandffL);
		add(maxExpandffB);
	}

	@Override
	public Simulation getSimulation(StatefulLogicalModel model, boolean plots, boolean quiet) throws Exception{
		int depth = Integer.valueOf(depthffB.getText());
		double alpha = Double.valueOf(alphaB.getText()), beta = Double.valueOf(betaB.getText());
		FirefrontSimulation sim = new FirefrontSimulation(model);
		sim.isGUI = true;
		//sim.maxRuns=Integer.valueOf(runsffB.getText());
		if(alpha>0) sim.alpha=alpha; //optional
		if(beta>0) sim.beta=beta; //optional
		if(depth>0) sim.maxDepth=depth; //optional
		sim.maxExpand = Integer.valueOf(maxExpandffB.getText());
		sim.quiet = quiet;
		sim.plots = plots; 
		
		System.out.println("FIREFRONT\n"+"Model: "+model.getName());
		System.out.println("Initial states:");
		for(byte[] state : model.getInitialStates()) System.out.println("  "+new State(state));
		System.out.println("Alpha threshold:"+ alpha + "\nBeta threshold:"+beta);
		return sim;
	}
	
	@Override
	public void load(AvatarParameters p) {
    	p.ffDepth=depthffB.getText();
    	p.ffMaxExpand=maxExpandffB.getText();
    	p.ffAlpha=alphaB.getText();
    	p.ffBeta=betaB.getText();
	}
	
	@Override
	public void unload(AvatarParameters param) {
    	depthffB.setText(param.ffDepth);
    	maxExpandffB.setText(param.ffMaxExpand);
    	alphaB.setText(param.ffAlpha);
    	betaB.setText(param.ffBeta);
	}
}
