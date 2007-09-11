package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class GsTreeMenu extends JPopupMenu {
  /**
	 * 
	 */
	private static final long	serialVersionUID	= 8209694715096362318L;
public static final String COPY = "Copy";
  public static final String CUT = "Cut";
  public static final String PASTE = "Paste";
  public static final String DELETE = "Delete";
  public static final String CREATE_1_FUNCTION = "1 function";
  public static final String CREATE_N_FUNCTIONS = "N functions";

  private Hashtable items = new Hashtable();

  public GsTreeMenu(ActionListener al	) {
	super();
	init(al);
  }
  public void init(ActionListener al) {
    addItem(null, COPY, false, al);
    addItem(null, CUT, false, al);
    addItem(null, PASTE, false, al);
    addItem(null, DELETE, false, al);
    addSeparator();
    JMenu createMenu = new JMenu("Create");
	add(createMenu);
    addItem(createMenu, CREATE_1_FUNCTION, false, al);
	addItem(createMenu, CREATE_N_FUNCTIONS, false, al);
  }
  private void addItem(JMenu parent, String text, boolean enabled, ActionListener al) {
    JMenuItem item = new JMenuItem(text);
    item.setEnabled(enabled);
    item.setActionCommand(text);
    if (al != null) item.addActionListener(al);
    items.put(text, item);
    if (parent != null)
      parent.add(item);
    else
      add(item);
  }
  public void setEnabled(String command, boolean b) {
    if (items.keySet().contains(command))
      ((JMenuItem)items.get(command)).setEnabled(b);
  }
}
