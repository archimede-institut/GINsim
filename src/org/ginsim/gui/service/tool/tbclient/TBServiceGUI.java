package org.ginsim.gui.service.tool.tbclient;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( ServiceGUI.class)
@StandaloneGUI
public class TBServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph && GUIManager.getInstance().getFrame(graph) != null) {
			List<Action> actions = new ArrayList<Action>();
			actions.add( new TBAction( (RegulatoryGraph) graph));
			return actions;
		}
		return null;
	}
}

class TBAction extends ToolAction {

	private final RegulatoryGraph graph;
	private TBClientPanel clientPanel;

	public TBAction( RegulatoryGraph graph) {

		super( "Show TBrowser tab", "Open a socket connexion with a running instance of TBrowser");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// TODO : REFACTORING ACTION
		// TODO: The TBrowser.getInstance was written in the plugin creator.. is this the right place to put it?

		GUIManager.error(new GsException(GsException.GRAVITY_INFO, "TB client disabled"), GUIManager.getInstance().getFrame(graph));

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
