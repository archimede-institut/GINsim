package org.ginsim.servicegui.tool.dataviewer;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GenericGraphAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.gui.utils.dialog.stackdialog.HandledStackDialog;
import org.mangosdk.spi.ProviderFor;


/**
 * GUI to view the associated objects.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( ServiceStatus.UNDER_DEVELOPMENT)
public class DataViewerServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new DataViewerAction( graph));
		return actions;
	}

	@Override
	public int getWeight() {
		return W_GENERIC;
	}
}

class DataViewerAction extends GenericGraphAction {
	private static final long serialVersionUID = 8294301473668672512L;
	
	protected DataViewerAction( Graph graph) {
        super( graph, "View data", null, "View associated data", null);

	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		DataViewer viewer = new DataViewer(graph);
		new HandledStackDialog(viewer);
	}
}
