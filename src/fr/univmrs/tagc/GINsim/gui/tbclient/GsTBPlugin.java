package fr.univmrs.tagc.GINsim.gui.tbclient;

import java.awt.event.WindowListener;

import javax.swing.JFrame;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.BaseMainFrame;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.common.GsException;

import tbrowser.TBrowser;

public class GsTBPlugin implements GsPlugin, GsActionProvider {
  private GsPluggableActionDescriptor[] t_action = null;
  private GsTBClientPanel clientPanel;

  public GsTBPlugin() {
    super();
		TBrowser.getInstance();
	}

  public void registerPlugin() {
    GsRegulatoryGraphDescriptor.registerActionProvider(this);
  }

  public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
    if (!graph.isVisible() || actionType != ACTION_ACTION) {
      return null;
    }
    if (t_action == null) {
      t_action = new GsPluggableActionDescriptor[1];
      t_action[0] = new GsPluggableActionDescriptor("Show TBrowser tab", "Open a socket connexion with a running instance of TBrowser", null, this, ACTION_ACTION, 0/*, true*/);
    }
    return t_action;
  }

  public void runAction(int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
    if (!((BaseMainFrame)frame).removeTab("TBrowser")) {
      clientPanel = new GsTBClientPanel(graph);
      ((BaseMainFrame) frame).addTab("TBrowser", clientPanel, true, BaseMainFrame.FLAG_ANY);
      WindowListener[] wl = frame.getWindowListeners();
      for (int i = 0; i < wl.length; i++) {
        frame.removeWindowListener(wl[i]);
    }
      frame.addWindowListener(clientPanel);
      for (int i = 0; i < wl.length; i++) {
        frame.addWindowListener(wl[i]);
          //t_action[0] = new GsPluggableActionDescriptor("Hide TBrowser tab", "Close a socket connexion with a running instance of TBrowser", null, this, ACTION_ACTION, 0/*, true*/);
          //((GsMainFrame)frame).getEventDispatcher().fireGraphChange(frame, graph, graph, false);
    }
    }
    else {
      clientPanel.closeTBConnexion();
      clientPanel = null;
     // t_action[0] = new GsPluggableActionDescriptor("Show TBrowser tab", "Open a socket connexion with a running instance of TBrowser", null, this, ACTION_ACTION, 0/*, true*/);
     // ((GsMainFrame)frame).getEventDispatcher().fireGraphChange(frame, graph, graph, false);
    }
  }

}
