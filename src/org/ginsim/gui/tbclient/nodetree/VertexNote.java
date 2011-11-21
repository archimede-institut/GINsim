package org.ginsim.gui.tbclient.nodetree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.gui.tbclient.decotreetable.decotree.AbstractDTreeElement;
import org.ginsim.gui.tbclient.decotreetable.decotree.DTreeElementToggleButton;


public class VertexNote extends DTreeElementToggleButton {
	private String proto, value;

	public VertexNote(AbstractDTreeElement e, Object o, ImageIcon offIc, ImageIcon onIc, boolean inTable) {
		super(e, offIc, onIc, null, null, inTable);
		Vector v = (Vector)getUserObject();
		if (v == null) {
			v = new Vector();
			setUserObject(v);
		}
		v.addAll((Vector)o);
    proto = (String)v.elementAt(1);
    value = (String)v.elementAt(2);
    tb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegulatoryVertex vertex = (RegulatoryVertex)((Vector)getUserObject()).firstElement();
        String proto = (String)((Vector)getUserObject()).elementAt(1);
        String value = (String)((Vector)getUserObject()).elementAt(2);
        setNote(tb.isSelected());
			}
    });
	}
	public void setNote(boolean b) {
    super.check(b);
    RegulatoryVertex vertex = (RegulatoryVertex)((Vector)getUserObject()).firstElement();
    proto = (String)((Vector)getUserObject()).elementAt(1);
    value = (String)((Vector)getUserObject()).elementAt(2);
    if (b)
      vertex.getAnnotation().addLink(toString(), vertex.getInteractionsModel().getGraph());
    else
      vertex.getAnnotation().delLink(toString(), vertex.getInteractionsModel().getGraph());
    for (int i = 0; i < getChildCount(); i++)
      (getChild(i)).check(b);
  }
	public String toString() {
  	return proto + ":" + value;
  }
}
