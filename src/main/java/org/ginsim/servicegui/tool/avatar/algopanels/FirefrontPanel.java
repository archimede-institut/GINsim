package org.ginsim.servicegui.tool.avatar.algopanels;

import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.colomoto.biolqm.StatefulLogicalModel;
import org.ginsim.service.tool.avatar.domain.State;
import org.ginsim.service.tool.avatar.params.AvatarParameters;
import org.ginsim.service.tool.avatar.simulation.FirefrontSimulation;
import org.ginsim.service.tool.avatar.simulation.Simulation;
import org.ginsim.service.tool.avatar.simulation.others.FirefrontMDDSimulation;

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
	private String runsffVar =open+"Maximum number of iterations performed by the program (should be less than the square size of the state space) [default: 1E3]"+end;
	private String alphaVar =open+"Minimum probability required for a state to be explored (be included the firefront set) [default: 10E-5]"+end;
	private String betaVar =open+"Maximum residual probability in the firefront set to stop the algorithm [default: 10E5]"+end;
	private String maxExpandVar =open+"Maximum number of states to be analyzed in the firefront set (probable states) [default: 1E3]. States with lower probability are put in the neglected set."+end;
	private String depthffVar =open+"Maximum depth (number of visited states) [default: 1E4]"+end;

	/**
	 * Instantiates the context of a simulation panel
	 * @param img icon for the help tooltip
	 * @param flex true for a flexible gridbaglayout (default: true)
	 */
	public FirefrontPanel(Icon img, boolean flex){
		super(img,flex);
		JLabel alphaL = new JLabel("Alpha"), betaL = new JLabel("Beta");
		JLabel maxExpandffL = new JLabel("Max expansion  "), depthffL = new JLabel("Max depth");
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
		depthffB.setText("1E4");
		alphaB.setText("1E-5");
		betaB.setText("1E-5");
		maxExpandffB.setText("1E3");
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
		int depth = (int)Double.parseDouble(depthffB.getText());
		double alpha = Double.parseDouble(alphaB.getText());
		double beta = Double.parseDouble(betaB.getText());
		FirefrontSimulation sim = new FirefrontSimulation();
		sim.addModel(model);
		sim.isGUI = true;
		//sim.maxRuns=Integer.valueOf(runsffB.getText());
		if(alpha>0) sim.alpha=alpha; //optional
		if(beta>0) sim.beta=beta; //optional
		if(depth>0) sim.maxDepth=depth; //optional
		sim.maxExpand = (int)Double.parseDouble(maxExpandffB.getText());
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
