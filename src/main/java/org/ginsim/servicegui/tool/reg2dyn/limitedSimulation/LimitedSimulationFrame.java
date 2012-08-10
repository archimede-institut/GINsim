package org.ginsim.servicegui.tool.reg2dyn.limitedSimulation;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.OptionStore;
import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.core.graph.hierachicaltransitiongraph.StatesSet;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationHolder;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationStore;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.shell.MainFrame;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.gui.utils.widgets.Frame;
import org.ginsim.service.tool.reg2dyn.limitedsimulation.LimitedSimulationService;
import org.ginsim.service.tool.reg2dyn.limitedsimulation.OutgoingNodesHandlingStrategy;
import org.ginsim.service.tool.reg2dyn.limitedsimulation.SimulationConstraint;
import org.ginsim.service.tool.reg2dyn.limitedsimulation.StatesToHierarchicalEditTab;

public class LimitedSimulationFrame extends StackDialog {
	private static final String OPTION_LIMITED_SIMULATION_INCLUDE_FIRST_OUTGOING_STATES = "limitedSimulation_includeFirstOutgoingStates";
	private static final long serialVersionUID = 5659168888297711105L;
	private HierarchicalTransitionGraph htg;
	private JPanel mainPanel;
	private JCheckBox strategy;
	private PerturbationHolder mutantstore = new PerturbationStore();
	private MutantSelectionPanel mutantPanel = null;

	public LimitedSimulationFrame(JFrame frame, HierarchicalTransitionGraph htg) {
		super(frame, "STR_limitedSimulation", 475, 260);
		this.htg = htg;
		setMainPanel(getMainPanel());			
	}

	
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
		
			c.gridx = 0;
			c.gridy = 0;
			JLabel label = new JLabel(Translator.getString("STR_limitedSimulation_instructions"));
			mainPanel.add(label, c);
			
			c.gridy++;
			strategy = new JCheckBox(Translator.getString("STR_limitedSimulation_alsoIncludeFirstOutgoingStates"));
			Boolean isStrategySelected = OptionStore.getOption(OPTION_LIMITED_SIMULATION_INCLUDE_FIRST_OUTGOING_STATES, true);
			strategy.setSelected(isStrategySelected.booleanValue());
			strategy.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					OptionStore.setOption(OPTION_LIMITED_SIMULATION_INCLUDE_FIRST_OUTGOING_STATES, strategy.isSelected());
				}
			});
			mainPanel.add(strategy, c);

			c.gridy++;
			mainPanel.add(getMutantSelectionPanel(), c);

		}
		return mainPanel;
	}


	private Component getMutantSelectionPanel() {
		if (mutantPanel == null) {
			try {
				mutantPanel = new MutantSelectionPanel(this, htg.getAssociatedGraph(), mutantstore);				
			} catch (GsException e) {
				return null;
			}
		}
    	return mutantPanel;
	}
	
	private OutgoingNodesHandlingStrategy getStrategy() {
		if (strategy.isSelected()) {
			return OutgoingNodesHandlingStrategy.ADD_FIRST_OUTGOING_STATE;
		} 
		return OutgoingNodesHandlingStrategy.CONTAIN_TO_SELECTION;
	}
	
	private Perturbation getMutant() {
		return mutantstore.getPerturbation();
	}

	private StatesSet getStateSet() {
		GraphSelection<HierarchicalNode, ?> selection = GUIManager.getInstance().getGraphGUI(htg).getSelection();
		List<HierarchicalNode> selectedNodes = selection.getSelectedNodes();
		if (selectedNodes == null || selectedNodes.size() == 0) {
			
			return null;
		}
		
		StatesSet s = new StatesSet(selectedNodes.get(0).statesSet);
		if (selectedNodes.size() > 1) {
			for (Iterator<HierarchicalNode> iterator = selectedNodes.listIterator(1); iterator.hasNext();) {
				HierarchicalNode hierarchicalNode = iterator.next();
				s.merge(hierarchicalNode.statesSet);
			}
		}
		s.reduce();
		return s;
	}

	@Override
	protected void run() throws GsException {
		SimulationConstraint constraint = new SimulationConstraint(getStateSet(), getStrategy());
		if (!constraint.isValid()) {
			NotificationManager.publishError(htg, "no_hierarchicalNode_selected");
			return;
		}

		LimitedSimulationService service = ServiceManager.getManager().getService(LimitedSimulationService.class);
		DynamicGraph dynGraph = service.run(htg, constraint, getMutant());
		Frame frame = GUIManager.getInstance().newFrame(dynGraph);
		if (frame != null && frame instanceof MainFrame) {
			((MainFrame)frame).addTabToEditPanel(new StatesToHierarchicalEditTab(dynGraph, htg));
		}
	}

}
