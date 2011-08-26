package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.DecisionAnalysis;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalTransitionGraph;
import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParameterList;
import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParameters;
import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParametersManager;
import fr.univmrs.tagc.GINsim.reg2dyn.PrioritySelectionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.datastore.gui.GenericListSelectionPanel;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class GsDecisionAnalysisFrame extends StackDialog implements ActionListener {
	private static final long serialVersionUID = -7619253564236142617L;
	private JFrame frame;
	private GsHierarchicalTransitionGraph htg;
	private JPanel mainPanel;
	private GenericListSelectionPanel selectPriorityClass;
	private GsRegulatoryGraph regGraph;
	private GsSimulationParameters currentParameter;
	
	
	
	public GsDecisionAnalysisFrame(JFrame parent, String id, int w, int h) {
		super(parent, id, w, h);
		this.frame = parent;
	}

	public GsDecisionAnalysisFrame(JFrame frame, GsGraph graph) {
		super(frame, "STR_htg_decision_analysis", 475, 260);
		this.frame = frame;
		if (graph instanceof GsHierarchicalTransitionGraph) {
			this.htg = (GsHierarchicalTransitionGraph) graph;
			this.regGraph = (GsRegulatoryGraph) htg.getAssociatedGraph();
			setMainPanel(getMainPanel());			
		} else {
			return;
		}
    }
	
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
		
			c.gridx = 0;
			c.gridy = 0;
			JLabel label = new JLabel(Translator.getString("STR_htg_decision_analysis_instructions"));
			mainPanel.add(label, c);
			c.gridy++;
			
			mainPanel.add(getPriorityClassSelector(), c);
			c.gridy++;
			
		}
		return mainPanel;
	}


	
	private GenericListSelectionPanel getPriorityClassSelector() {
        GsSimulationParameterList paramList = (GsSimulationParameterList)regGraph.getObject(GsSimulationParametersManager.key, true);
		if (selectPriorityClass == null) {
			selectPriorityClass = new PrioritySelectionPanel(this, paramList.pcmanager);
			this.currentParameter = (GsSimulationParameters)paramList.getElement(null, 0);
			selectPriorityClass.setStore(currentParameter.store, GsSimulationParameters.PCLASS);
		}
		return selectPriorityClass;
	}

	

	protected void run() {
		GsDecisionAnalysis gsDecisionAnalysis = new GsDecisionAnalysis(htg, currentParameter);
		this.brun.setEnabled(false);
		gsDecisionAnalysis.run();
		this.brun.setEnabled(true);
		cancel();
	}
	
	public void actionPerformed(ActionEvent e) {
//		if (e.getSource() == leftOperandCB) {
	}
	public JFrame getFrame() {
		return frame;
	}

	
}
