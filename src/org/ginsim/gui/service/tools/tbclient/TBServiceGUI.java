package org.ginsim.gui.service.tools.tbclient;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

@ProviderFor( GsServiceGUI.class)
@StandaloneGUI
public class TBServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof GsRegulatoryGraph && GUIManager.getInstance().getFrame(graph) != null) {
			List<Action> actions = new ArrayList<Action>();
			actions.add( new TBAction( (GsRegulatoryGraph) graph));
			return actions;
		}
		return null;
	}
}

class TBAction extends GsToolsAction {

	private final GsRegulatoryGraph graph;
	private GsTBClientPanel clientPanel;

	public TBAction( GsRegulatoryGraph graph) {

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
//			clientPanel = new GsTBClientPanel(graph);
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
