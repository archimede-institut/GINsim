package fr.univmrs.tagc.GINsim.gui.tbclient;

import tbrowser.ihm.results.TBInfoPanel;
import tbrowser.data.module.TBModule;
import java.util.Vector;

public class GsTBInfoPanel extends TBInfoPanel {
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
