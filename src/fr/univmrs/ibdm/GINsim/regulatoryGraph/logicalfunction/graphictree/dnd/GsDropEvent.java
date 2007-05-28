package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import java.awt.Point;

public class GsDropEvent {
  private Point point;
  private String action;

  public GsDropEvent(String action, Point point) {
    this.action = action;
    this.point = point;
  }

  public String getAction() {
    return action;
  }

  public Point getDropLocation() {
    return point;
  }
}
