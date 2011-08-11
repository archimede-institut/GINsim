package fr.univmrs.tagc.GINsim.gui.tbclient.genetree;

import java.net.URL;
import java.util.Vector;

import tbrowser.control.netbrowser.TBNetBrowser;
import tbrowser.ihm.widget.TBButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import fr.univmrs.tagc.common.managerresources.ImageLoader;

public class TreeElementLink extends TreeElementDeco {
  protected URL url;
  private TBButton b;

  public TreeElementLink(AbstractTreeElement e, URL u) {
    super(e);
    url = u;
    b = new TBButton(ImageLoader.getImageIcon("internet_link.png"));
    b.setInsets(2, 3, 2, 3);
    b.setContentAreaFilled(false);
    b.setForeground(fgColor);
    b.setFocusable(false);
    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
      	TBNetBrowser.displayURL(url.toString());
      }
    });
  }
  public TreeElementLink(AbstractTreeElement e, URL u, Object o) {
    this(e, u);
    userObject = o;
  }
  public void check(boolean b) {
    super.check(b);
    for (int i = 0; i < getChildCount(); i++)
      ((AbstractTreeElement)getChild(i)).check(b);
  }
  public Vector getGraphicComponents(boolean sel) {
    Vector v = treeElement.getGraphicComponents(sel);
    b.setBackground(sel ? selColor : bgColor);
    //v.addElement(Box.createHorizontalStrut(5));
    v.addElement(b);
    return v;
  }
}
