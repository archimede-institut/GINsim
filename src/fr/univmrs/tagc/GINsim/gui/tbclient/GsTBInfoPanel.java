package fr.univmrs.tagc.GINsim.gui.tbclient;

import tbrowser.ihm.results.TBInfoPanel;
import tbrowser.data.module.TBModule;
import java.util.Vector;

public class GsTBInfoPanel extends TBInfoPanel {
  GsTBInfoProfilePanel profilePanel;

  public GsTBInfoPanel() {
    super("Info");
    profilePanel = new GsTBInfoProfilePanel();
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
