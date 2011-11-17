package org.ginsim.gui.service.export.petrinet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GsExportAction;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParameterList;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParametersManager;
import org.ginsim.gui.service.tools.reg2dyn.PriorityClassDefinition;
import org.ginsim.gui.service.tools.reg2dyn.PriorityClassManager;
import org.ginsim.gui.service.tools.reg2dyn.PrioritySelectionPanel;
import org.ginsim.service.export.petrinet.BasePetriNetExport;
import org.ginsim.service.export.petrinet.GsPetriNetExportAPNN;
import org.ginsim.service.export.petrinet.GsPetriNetExportINA;
import org.ginsim.service.export.petrinet.GsPetriNetExportPNML;
import org.ginsim.service.export.petrinet.PNConfig;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStatePanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateStore;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.InitialStatesIterator;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.widgets.StackDialog;

/**
 * GUI Action to export a LRG into Petri net
 * 
 */
public class PetriNetExportServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof GsRegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new PetriNetExportAction((GsRegulatoryGraph)graph));
			return actions;
		}
		return null;
	}
	
}

class PetriNetExportAction extends GsExportAction<GsRegulatoryGraph> {

	static final String PNFORMAT = "export.petriNet.defaultFormat";

	PNConfig config;
	
	public PetriNetExportAction(GsRegulatoryGraph graph) {
		super(graph, "STR_PetriNet", "STR_PetriNet_descr");
	}

	protected void doExport( String filename) {
		// FIXME: call the right subformat to do the job
	}

	@Override
	public JComponent getConfigPanel() {
		config = new PNConfig(graph);
		return new PNExportConfigPanel(config);
	}

	@Override
	protected GsFileFilter getFileFilter() {
		// FIXME return the filter associated to the selected format
		// return config.format.ffilter
		
		return null;
	}

}

class PNExportConfigPanel extends JPanel {
    private static final long serialVersionUID = 9043565812912568136L;

	PrioritySelectionPanel priorityPanel = null;

	protected PNExportConfigPanel ( PNConfig config, StackDialog dialog) {
    	GsRegulatoryGraph graph = config.graph;
    	MutantSelectionPanel mutantPanel = null;
    	
    	GsInitialStatePanel initPanel = new GsInitialStatePanel(dialog, graph, false);
    	initPanel.setParam(config);
    	
    	setLayout(new GridBagLayout());
    	mutantPanel = new MutantSelectionPanel(dialog, graph, config.store);
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(mutantPanel, c);

		GsSimulationParameterList paramList = (GsSimulationParameterList) ObjectAssociationManager.getInstance().getObject(graph, GsSimulationParametersManager.key, true);
        priorityPanel = new PrioritySelectionPanel(dialog, paramList.pcmanager);
        priorityPanel.setStore(config.store, 1);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(priorityPanel, c);
		priorityPanel.setFilter(PriorityClassManager.FILTER_NO_SYNCHRONOUS);

		c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 2;
    	c.gridwidth = 2;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.fill = GridBagConstraints.BOTH;
    	add(initPanel, c);
    }
}
