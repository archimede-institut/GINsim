package org.ginsim.servicegui.tool.avatar.algopanels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.colomoto.logicalmodel.StatefulLogicalModel;
import org.ginsim.service.tool.avatar.params.AvatarParameters;
import org.ginsim.service.tool.avatar.simulation.AvatarMDDSimulation;
import org.ginsim.service.tool.avatar.simulation.AvatarSimulation;
import org.ginsim.service.tool.avatar.simulation.Simulation;
import org.ginsim.service.tool.avatar.simulation.AvatarSimulation.AvatarStrategy;

/**
 * Class for managing the panel for Avatar simulations
 * @author Rui Henriques
 * @version 1.0
 */
public class AvatarPanel extends SimulationPanel {

	private static final long serialVersionUID = 1L;
	private JTextField runsB = new JTextField(), tauB = new JTextField(), depthB = new JTextField(), aproxDepth = new JTextField();
	private JTextField minTranB = new JTextField(), minCycleB = new JTextField(), maxPsizeB = new JTextField(), maxRewiringSizeB = new JTextField();
	private JCheckBox keepTransB = new JCheckBox("Keep Transients");
	private JCheckBox keepOraclesB = new JCheckBox("Keep Oracles");
	private JComboBox strategy = new JComboBox(new DefaultComboBoxModel(new String[] {"Matrix Inversion","Approximate","Uniform Exits"}));

	private String open = "<html><div style=\"width:265px;\">", end = "</div></html>";
	private String runsVar =open+"Specifies the number of simulations to perform (default: 1000)"+end;
	private String tauVar =open+"Indicates the degree of cycle extension: n->2n->4n->8n->16n (default: n=3)"+end;
	private String depthVar =open+"Specifies the maximum number of visited states per simulation"+end;
	private String keepTransVar =open+"Preserves the knowledge regarding the discovered transient cycles for upcoming simulations/runs"+end;
	private String keepAttractorsVar = open+"Preserves the knowledge regarding the discovered terminal cycles (attractors) for upcoming simulations/runs"+end;
	private String strategyVar =open+"Algorithm for rewiring cycles: 1) 'Matrix Inversion' computes the prob of paths in an optimal manner (prone to memory bottlenecks),"+
			"2)'Approximate' strategy computes the path probabilities up to a maximum depth (memory-efficient yet can lead to time overhead), 3) 'Uniform Exists' creates paths within a transient cycle to its exit state with uniform probabilities (heihgtened memory and time efficiency)."+end;
	private String aproxDepthVar = open+"Defines the minimum depth from which to compute exit probabilities within a cycle"+end;
	private String minTransVar =open+"Specifies the mininum size of a transient cycle in order to be kept for subsequent simulation runs (default: 200)"+end;
	private String minCycleVar =open+"Specifies the minimum number of elements in a cycle required to trigger a graph rewiring operation (default: 4)"+end;
	private String maxPSizeVar =open+"Specifies the maximum number of states within a cycle to allow expansion (default: 10000)"+end;
	private String maxRewiringSizeVar =open+"Maximum number of states within a cycle to be rewired"+end;
	private JPanel panelAvatarP1, panelAvatarP2;
	private JPanel p1c = new JPanel(new GridLayout(1,3)); 
	private JLabel[] questions = new JLabel[]{new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), new JLabel("")};

	/**
	 * Instantiates the context of a simulation panel
	 * @param img icon for the help tooltip
	 * @param flex true for a flexible gridbaglayout (default: true)
	 */
	public AvatarPanel(Icon img, boolean flex){
		super(img,flex);
		setBorder(new TitledBorder(new LineBorder(purple,2), "Avatar", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		runsB.setText("1000");
		tauB.setText("3");
		depthB.setText("1000");
		keepTransB.setSelected(true);
		aproxDepth.setText("7");
		minTranB.setText("200");
		minCycleB.setText("4");
		maxPsizeB.setText("10000");
		maxRewiringSizeB.setText("400");
		refresh();

		strategy.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(strategy.getSelectedIndex()==0) keepTransB.setSelected(true);
				else keepTransB.setSelected(false);
				if(strategy.getSelectedIndex()==1) p1c.setVisible(true);
				else p1c.setVisible(false);
				repaint();//refresh();
			}
		});
	}
	
	private void refresh() {
		removeAll();
		int shiftX=6, width=229;
		if(flexible) setLayout(new GridBagLayout());
		else {
			setLayout(null);
			setSize(width+12, 230+getExtraHeight());
		}
		
		panelAvatarP1 = new JPanel();
		panelAvatarP1.setBorder(new TitledBorder(new LineBorder(purple,2), "Main Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		JLabel runsL = new JLabel("#Runs"), tauL = new JLabel("Tau"), depthL = new JLabel("Max.Depth"), strategyL = new JLabel("Rewiring"), aproxDepthL = new JLabel("with depth ", JLabel.RIGHT);
		int width0=40, width1=70, height1=19, startX1=10, startY1=20;
		GridBagConstraints g = new GridBagConstraints();
		if(flexible){
			runsL.setToolTipText(runsVar);
			tauL.setToolTipText(tauVar);
			depthL.setToolTipText(depthVar);
			strategyL.setToolTipText(strategyVar);
			keepTransB.setToolTipText(keepTransVar);
			runsL.setIcon(helpImg);
			tauL.setIcon(helpImg);
			depthL.setIcon(helpImg);
			strategyL.setIcon(helpImg);
			//keepTransB.setIcon(helpImg);
			JPanel p1a = new JPanel(new GridLayout(3,2)); 
			panelAvatarP1.setLayout(new GridBagLayout());
			p1a.add(runsL);
			p1a.add(runsB);
			p1a.add(tauL);
			p1a.add(tauB);
			p1a.add(depthL);
			p1a.add(depthB);
			JPanel p1b = new JPanel(new GridLayout(1,2)); 
			p1b.add(strategyL);
			p1b.add(strategy);
			p1c.add(new JLabel(""));
			p1c.add(aproxDepthL);
			p1c.add(aproxDepth);
			p1c.setVisible(false);
			JPanel p1d = new JPanel(new GridLayout(1,1)); 
			p1d.add(keepTransB);
			GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.gridwidth = 1;
			panelAvatarP1.add(p1a,c);
			c.gridy=1;
			panelAvatarP1.add(p1b,c);
			c.gridy=2;
			panelAvatarP1.add(p1c,c);
			c.gridy=3;
			panelAvatarP1.add(p1d,c);

			g.weightx=1;
			g.gridwidth=1;
			g.fill = GridBagConstraints.HORIZONTAL;
			add(panelAvatarP1,g);
		} else {
			for(JLabel question : questions) question.setIcon(helpImg);
			questions[0].setToolTipText(runsVar);
			questions[1].setToolTipText(tauVar);
			questions[2].setToolTipText(depthVar);
			questions[3].setToolTipText(strategyVar);
			questions[4].setToolTipText(keepTransVar);
			questions[5].setToolTipText(aproxDepthVar);
			//questions[5].setToolTipText(keepAttractorsVar);
			
			panelAvatarP1.setLayout(null);
			panelAvatarP1.setBounds(shiftX,18, width, 123+getExtraHeight());
			runsL.setBounds(startX1, startY1+height1*0, width1, height1);
			tauL.setBounds(startX1, startY1+height1*1, width1, height1);
			depthL.setBounds(startX1, startY1+height1*2, width1, height1);
			runsB.setBounds(startX1+width1, startY1+height1*0, width0, height1);
			tauB.setBounds(startX1+width1, startY1+height1*1, width0, height1);
			depthB.setBounds(startX1+width1, startY1+height1*2, width0, height1);
			strategyL.setBounds(startX1, startY1+height1*3+1, width1, height1);
			strategy.setBounds(startX1+width1, startY1+height1*3+1, width0+83, height1);
			if(strategy.getSelectedIndex()==1){
				aproxDepthL.setBounds(startX1+width1, startY1+height1*4+3, 68, height1);
				aproxDepth.setBounds(startX1+width1+70, startY1+height1*4+3, 54, height1);
				questions[5].setBounds(startX1+width1+126,startY1+height1*4+3,15,15);
				questions[5].setVisible(true);
			} else questions[5].setVisible(false);
			keepTransB.setBounds(startX1, startY1+height1*4+3+getExtraHeight(), width1+width0+10, height1);
			//keepOraclesB.setBounds(startX1, startY1+height1*5, width1+width0+10, height1);
			questions[0].setBounds(startX1+width1+width0+3,startY1+height1*0+2,15,15);
			questions[1].setBounds(startX1+width1+width0+3,startY1+height1*1+2,15,15);
			questions[2].setBounds(startX1+width1+width0+3,startY1+height1*2+2,15,15);
			questions[3].setBounds(startX1+width1+width0+85,startY1+height1*3+4,15,15);
			questions[4].setBounds(startX1+width1+width0+11,startY1+height1*4+6+getExtraHeight(),15,15);
			//questions[5].setBounds(startX1+width1+width0+10,startY1+height1*5+3,15,15);
			
			panelAvatarP1.add(questions[0]);
			panelAvatarP1.add(runsL);
			panelAvatarP1.add(runsB);
			panelAvatarP1.add(questions[1]);
			panelAvatarP1.add(tauL);
			panelAvatarP1.add(tauB);
			panelAvatarP1.add(questions[2]);
			panelAvatarP1.add(depthL);
			panelAvatarP1.add(depthB);
			panelAvatarP1.add(questions[3]);
			panelAvatarP1.add(strategyL);
			panelAvatarP1.add(strategy);
			panelAvatarP1.add(questions[4]);
			panelAvatarP1.add(keepTransB);
			add(panelAvatarP1);
			
			//panelAvatarP1.add(keepOraclesB);
			if(strategy.getSelectedIndex()==1){
				panelAvatarP1.add(aproxDepthL);
				panelAvatarP1.add(aproxDepth);
			}
		}		
		panelAvatarP2 = new JPanel();
		panelAvatarP2.setBorder(new TitledBorder(new LineBorder(purple,2), "Optional Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		JLabel minTranL = new JLabel("Min.Transient Size"), minCycleL = new JLabel("Min.States to Rewire ");
		JLabel maxPsizeL = new JLabel("Expansion Max.#States"), maxRewiringSizeL = new JLabel("Rewiring Max.#States");
		int width2=140, height2=19, startX2=10, startY2=20;
		if(flexible){
			minTranL.setIcon(helpImg);
			minCycleL.setIcon(helpImg);
			maxPsizeL.setIcon(helpImg);
			maxRewiringSizeL.setIcon(helpImg);
			minTranL.setToolTipText(minTransVar);
			minCycleL.setToolTipText(minCycleVar);
			maxPsizeL.setToolTipText(maxPSizeVar);
			maxRewiringSizeL.setToolTipText(maxRewiringSizeVar);
			panelAvatarP2.setLayout(new GridLayout(4,2));
			panelAvatarP2.add(minTranL);
			panelAvatarP2.add(minTranB);
			panelAvatarP2.add(minCycleL);
			panelAvatarP2.add(minCycleB);
			panelAvatarP2.add(maxPsizeL);
			panelAvatarP2.add(maxPsizeB);
			panelAvatarP2.add(maxRewiringSizeL);
			panelAvatarP2.add(maxRewiringSizeB);
			g.gridy=1;
			add(panelAvatarP2,g);
		} else {
			questions[6].setToolTipText(minTransVar);
			questions[7].setToolTipText(minCycleVar);
			questions[8].setToolTipText(maxPSizeVar);
			panelAvatarP2.setLayout(null);
			panelAvatarP2.setBounds(shiftX, 143+getExtraHeight(), width, 82);
			minTranL.setBounds(startX2, startY2+height2*0, width2, height2);
			minCycleL.setBounds(startX2, startY2+height2*1, width2, height2);
			maxPsizeL.setBounds(startX2, startY2+height2*2, width2, height2);
			minTranB.setBounds(startX2+width2, startY2+height2*0, width0, height2);
			minCycleB.setBounds(startX2+width2, startY2+height2*1, width0, height2);
			maxPsizeB.setBounds(startX2+width2, startY2+height2*2, width0, height2);
			questions[6].setBounds(startX2+width2+width0+3, startY2+height2*0+2, 15, 15);
			questions[7].setBounds(startX2+width2+width0+3, startY2+height2*1+2, 15, 15);
			questions[8].setBounds(startX2+width2+width0+3, startY2+height2*2+2, 15, 15);
			panelAvatarP2.add(questions[6]);
			panelAvatarP2.add(minTranL);
			panelAvatarP2.add(minTranB);
			panelAvatarP2.add(questions[7]);
			panelAvatarP2.add(minCycleL);
			panelAvatarP2.add(minCycleB);
			panelAvatarP2.add(questions[8]);
			panelAvatarP2.add(maxPsizeL);
			panelAvatarP2.add(maxPsizeB);
			add(panelAvatarP2);
		}
	}

	private int getExtraHeight(){
		if(strategy.getSelectedIndex()==1) return 20;
		return 0;
	}
	
	@Override
	public Simulation getSimulation(StatefulLogicalModel model, boolean plots, boolean quiet) throws Exception{
		AvatarSimulation sim = new AvatarSimulation(model); 
		sim.isGUI = true;
		sim.runs = Integer.valueOf(runsB.getText());
		sim.tauInit = Integer.valueOf(tauB.getText());
		sim.maxSteps = Integer.valueOf(depthB.getText());		
		sim.minCSize = Integer.valueOf(minCycleB.getText());
		sim.maxPSize = Integer.valueOf(maxPsizeB.getText());
		sim.maxRewiringSize = Integer.valueOf(maxRewiringSizeB.getText());
		sim.minTransientSize = Integer.valueOf(minTranB.getText());
		sim.keepTransients = keepTransB.isSelected();
		sim.keepOracle = true;
		sim.plots = plots;
		sim.quiet = quiet;
		String stg = (String) strategy.getSelectedItem();
		if(stg.contains("Matrix")) sim.strategy = AvatarStrategy.MatrixInversion;
		else if(stg.contains("Approx")){
			try {
				sim.approxDepth = Integer.parseInt(aproxDepth.getText());
		    } catch (NumberFormatException nfe) { sim.approxDepth=-1; }
			sim.strategy = AvatarStrategy.Approximate;
		} else sim.strategy = AvatarStrategy.RandomExit;
		sim.smallStateSpace = (int)Math.pow(2,10);
		return sim;
	}

	@Override
	public void load(AvatarParameters p) {
    	p.avaRuns=runsB.getText();
    	p.avaTau=tauB.getText();
    	p.avaDepth=depthB.getText();
    	p.avaAproxDepth=aproxDepth.getText();
    	p.avaMinTran=minTranB.getText();
    	p.avaMinCycle=minCycleB.getText();
    	p.avaMaxPSize=maxPsizeB.getText();
    	p.avaMaxRewiringSize=maxRewiringSizeB.getText();
    	p.avaKeepTrans=keepTransB.isSelected();
    	p.avaStrategy=strategy.getSelectedIndex();
	}
	
	@Override
	public void unload(AvatarParameters param) {
		runsB.setText(param.avaRuns);
		tauB.setText(param.avaTau);
		depthB.setText(param.avaDepth);
		aproxDepth.setText(param.avaAproxDepth);
		minTranB.setText(param.avaMinTran);
		minCycleB.setText(param.avaMinCycle);
		maxPsizeB.setText(param.avaMaxPSize);
		maxRewiringSizeB.setText(param.avaMaxRewiringSize);
		keepTransB.setSelected(param.avaKeepTrans);
		strategy.setSelectedIndex(param.avaStrategy);
	}
}
