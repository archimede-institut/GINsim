package org.ginsim.gui.service.tools.decisionanalysis;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.tools.reg2dyn.SimulationParameterList;
import org.ginsim.gui.service.tools.reg2dyn.SimulationParameters;
import org.ginsim.gui.service.tools.reg2dyn.SimulationParametersManager;
import org.ginsim.gui.service.tools.reg2dyn.PrioritySelectionPanel;

import fr.univmrs.tagc.common.datastore.gui.GenericListSelectionPanel;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.StackDialog;
import fr.univmrs.tagc.common.managerresources.Translator;

public class DecisionAnalysisFrame extends StackDialog implements ActionListener {
	private static final long serialVersionUID = -7619253564236142617L;
	private HierarchicalTransitionGraph htg;
	private JPanel mainPanel;
	private GenericListSelectionPanel selectPriorityClass;
	private RegulatoryGraph regGraph;
	private SimulationParameters currentParameter;
	
	
	
	public DecisionAnalysisFrame(JFrame frame, Graph graph) throws GsException{
		super(frame, "STR_htg_decision_analysis", 475, 260);

		if (graph instanceof HierarchicalTransitionGraph) {
			this.htg = (HierarchicalTransitionGraph) graph;
			this.regGraph = (RegulatoryGraph) htg.getAssociatedGraph();
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
        SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager.getInstance().getObject( regGraph, SimulationParametersManager.key, true);
		if (selectPriorityClass == null) {
			selectPriorityClass = new PrioritySelectionPanel(this, paramList.pcmanager);
			this.currentParameter = (SimulationParameters)paramList.getElement(null, 0);
			selectPriorityClass.setStore(currentParameter.store, SimulationParameters.PCLASS);
		}
		return selectPriorityClass;
	}

	

	protected void run() throws GsException{
		
		DecisionAnalysis decisionAnalysis = new DecisionAnalysis(htg, currentParameter);
		this.brun.setEnabled(false);
		decisionAnalysis.run( GUIManager.getInstance().getGraphGUI( htg).getSelection().getSelectedNodes());
		this.brun.setEnabled(true);
		cancel();
	}
	
	public void actionPerformed(ActionEvent e) {
//		if (e.getSource() == leftOperandCB) {
	}
	
}
