package org.ginsim.servicegui.tool.tbclient;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.application.GsException;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.gui.service.common.ToolAction;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( ServiceStatus.DEPRECATED)
public class TBServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph && GUIManager.getInstance().getFrame(graph) != null) {
			List<Action> actions = new ArrayList<Action>();
			actions.add( new TBAction( (RegulatoryGraph) graph, this));
			return actions;
		}
		return null;
	}
	
	@Override
	public int getInitialWeight() {
		return -1;
	}
}

class TBAction extends ToolAction {

	private final RegulatoryGraph graph;
//	private TBClientPanel clientPanel;

	public TBAction( RegulatoryGraph graph, ServiceGUI serviceGUI) {

		super( "Show TBrowser tab", "Open a socket connexion with a running instance of TBrowser", serviceGUI);
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// TODO : REFACTORING ACTION
		// TODO: The TBrowser.getInstance was written in the plugin creator.. is this the right place to put it?

		GUIMessageUtils.openErrorDialog( new GsException(GsException.GRAVITY_INFO, "TB client disabled"), GUIManager.getInstance().getFrame(graph));

//		TBrowser.getInstance();
//		if (!((BaseMainFrame) frame).removeTab("TBrowser")) {
//			clientPanel = new TBClientPanel(graph);
//			((BaseMainFrame) frame).addTab("TBrowser", clientPanel, true, BaseMainFrame.FLAG_ANY);
//			WindowListener[] wl = frame.getWindowListeners();
//			for (int i = 0; i < wl.length; i++) {
//				frame.removeWindowListener(wl[i]);
//			}
//			frame.addWindowListener(clientPanel);
//			for (int i = 0; i < wl.length; i++) {
//				frame.addWindowListener(wl[i]);
//			}
//		} else {
//			clientPanel.closeTBConnexion();
//			clientPanel = null;
//		}
	}

}