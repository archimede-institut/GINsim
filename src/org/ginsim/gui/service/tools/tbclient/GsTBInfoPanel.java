package org.ginsim.gui.service.tools.tbclient;

import java.util.Vector;

import tbrowser.data.module.TBModule;
import tbrowser.ihm.results.TBInfoPanel;

public class GsTBInfoPanel extends TBInfoPanel {
  private static final long serialVersionUID = -5932617499905893848L;
  GsTBInfoProfilePanel profilePanel;

  public GsTBInfoPanel(GsTBClientPanel p) {
    super("Info");
    profilePanel = new GsTBInfoProfilePanel(p);
    addTab(profilePanel);
  }
  public void initModule(TBModule m, Vector g) {
    super.initModule(m);
    if (m.isDataLoaded()) {
      profilePanel.init(m, g);
    }
  }
  public void clear() {
    super.clear();

  }
}
