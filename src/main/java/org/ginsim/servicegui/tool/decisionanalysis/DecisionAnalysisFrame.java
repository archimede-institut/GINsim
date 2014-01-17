package org.ginsim.servicegui.tool.decisionanalysis;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.common.application.GsException;
import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.utils.data.GenericListSelectionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.reg2dyn.SimulationParameterList;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;
import org.ginsim.service.tool.reg2dyn.SimulationParametersManager;
import org.ginsim.servicegui.tool.reg2dyn.PrioritySelectionPanel;


public class DecisionAnalysisFrame extends LogicalModelActionDialog  {
	private static final long serialVersionUID = -7619253564236142617L;
	private HierarchicalTransitionGraph htg;
	private JPanel mainPanel;
	private GenericListSelectionPanel selectPriorityClass;
	private RegulatoryGraph regGraph;
	private SimulationParameters currentParameter;
	
	
	public DecisionAnalysisFrame(JFrame frame, HierarchicalTransitionGraph graph, RegulatoryGraph lrg) {
		super(lrg, frame, "STR_htg_decision_analysis", 475, 260);
		this.htg = (HierarchicalTransitionGraph) graph;
		this.regGraph = lrg;
		setMainPanel(getMainPanel());			
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
        SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager.getInstance().getObject( regGraph, SimulationParametersManager.KEY, true);
		if (selectPriorityClass == null) {
			selectPriorityClass = new PrioritySelectionPanel(this, paramList.pcmanager);
			this.currentParameter = paramList.get(0);
			selectPriorityClass.setStore(currentParameter.store, SimulationParameters.PCLASS);
		}
		return selectPriorityClass;
	}

	@Override
	public void run(LogicalModel model) {
		DecisionAnalysis decisionAnalysis = new DecisionAnalysis(model, htg, currentParameter);
		this.brun.setEnabled(false);
		decisionAnalysis.run( GUIManager.getInstance().getGraphGUI( htg).getSelection().getSelectedNodes());
		this.brun.setEnabled(true);
		cancel();
	}
	
}
