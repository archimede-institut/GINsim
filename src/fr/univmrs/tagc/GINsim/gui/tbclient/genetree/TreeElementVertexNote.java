package fr.univmrs.tagc.GINsim.gui.tbclient.genetree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import tbrowser.ihm.widget.TBToggleButton;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.managerresources.ImageLoader;

public class TreeElementVertexNote extends TreeElementDeco {
  private TBToggleButton b;
  private String proto, value;
  
  public TreeElementVertexNote(AbstractTreeElement e) {
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
      	GsRegulatoryVertex vertex = (GsRegulatoryVertex)((Vector)userObject).firstElement();
        String proto = (String)((Vector)userObject).elementAt(1);
        String value = (String)((Vector)userObject).elementAt(2);
        setNote(b.isSelected());
      }
    });
  }
  public void setSelected(boolean s) {
  	b.setSelected(s);
  }
  public TreeElementVertexNote(AbstractTreeElement e, Object o) {
    this(e);
    userObject = new Vector();
    ((Vector)userObject).addAll((Vector)o);
    proto = (String)((Vector)userObject).elementAt(1);
    value = (String)((Vector)userObject).elementAt(2);
  }
  public void setNote(boolean b) {
    super.check(b);
    GsRegulatoryVertex vertex = (GsRegulatoryVertex)((Vector)userObject).firstElement();
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
