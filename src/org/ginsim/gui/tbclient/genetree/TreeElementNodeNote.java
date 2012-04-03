package org.ginsim.gui.tbclient.genetree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.resource.ImageLoader;

import tbrowser.ihm.widget.TBToggleButton;

public class TreeElementNodeNote extends TreeElementDeco {
  private TBToggleButton b;
  private String proto, value;
  
  public TreeElementNodeNote(AbstractTreeElement e) {
    super(e);
    b = new TBToggleButton(ImageLoader.getImageIcon("annotation_off.png"));
    b.setSelectedIcon(ImageLoader.getImageIcon("annotation.png"));
    b.setInsets(2, 3, 2, 3);
    b.setContentAreaFilled(false);
    b.setForeground(fgColor);
    b.setFocusable(false);
    b.setFocusPainted(false);
    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        setNote(b.isSelected());
      }
    });
  }
  public void setSelected(boolean s) {
  	b.setSelected(s);
  }
  public TreeElementNodeNote(AbstractTreeElement e, Object o) {
    this(e);
    userObject = new Vector();
    ((Vector)userObject).addAll((Vector)o);
    proto = (String)((Vector)userObject).elementAt(1);
    value = (String)((Vector)userObject).elementAt(2);
  }
  public void setNote(boolean b) {
    super.check(b);
    RegulatoryNode vertex = (RegulatoryNode)((Vector)userObject).firstElement();
    proto = (String)((Vector)userObject).elementAt(1);
    value = (String)((Vector)userObject).elementAt(2);
    if (b)
      vertex.getAnnotation().addLink(toString(), vertex.getInteractionsModel().getGraph());
    else
      vertex.getAnnotation().delLink(toString(), vertex.getInteractionsModel().getGraph());
    for (int i = 0; i < getChildCount(); i++)
      ((AbstractTreeElement)getChild(i)).check(b);
  }
  public Vector getGraphicComponents(boolean sel) {
    Vector v = treeElement.getGraphicComponents(sel);
    b.setBackground(sel ? selColor : bgColor);
    v.addElement(b);
    return v;
  }
  public String toString() {
  	return proto + ":" + value;
  }
  public void check(boolean b) {

  }
}
