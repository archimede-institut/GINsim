package org.ginsim.servicegui.tool.dataviewer;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.Graph;
import org.ginsim.core.service.EStatus;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.shell.actions.GenericGraphAction;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.gui.service.StandaloneGUI;
import org.ginsim.gui.utils.dialog.stackdialog.HandledStackDialog;
import org.mangosdk.spi.ProviderFor;


/**
 * GUI to view the associated objects.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( EStatus.DEVELOPMENT)
public class DataViewerServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new DataViewerAction( graph, this));
		return actions;
	}

	public int getInitialWeight() {
		return W_GRAPH_COLORIZE+40;
	}
}

class DataViewerAction extends GenericGraphAction {
	private static final long serialVersionUID = 8294301473668672512L;
	
	protected DataViewerAction( Graph graph, ServiceGUI serviceGUI) {
        super( graph, "View data", null, "View associated data", null, serviceGUI);

	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		DataViewer viewer = new DataViewer(graph);
		new HandledStackDialog(viewer);
	}
}
