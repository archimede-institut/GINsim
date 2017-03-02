package org.ginsim.servicegui.export.petrinet;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.colomoto.biolqm.io.petrinet.APNNFormat;
import org.colomoto.biolqm.io.petrinet.INAFormat;
import org.colomoto.biolqm.io.petrinet.PNMLFormat;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.GSServiceManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.SimpleServiceGUI;
import org.ginsim.gui.service.StandaloneGUI;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.service.format.PetriNetINAFormatService;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to export a LRG into Petri net
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( EStatus.RELEASED)
public class PetriNetExportServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof RegulatoryGraph) {
			RegulatoryGraph lrg = (RegulatoryGraph)graph;
			List<Action> actions = new ArrayList<Action>();
			actions.add(new PetriNetExportAction(lrg, new INAFormat()));
			actions.add(new PetriNetExportAction(lrg, new APNNFormat()));
			actions.add(new PetriNetExportAction(lrg, new PNMLFormat()));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_EXPORT_SPECIFIC + 50;
	}

}
