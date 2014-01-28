package org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JComboBox;

public class DTreeElementComboBox extends DTreeElementDeco implements ItemListener {
	private Vector choices;
	private int selectedChoice = 0;
	private ItemListener listener;

	public DTreeElementComboBox(AbstractDTreeElement e, Vector v, ItemListener l, boolean inTable) {
		super(e, e.getTree());
		choices = v;
		if (choices == null) choices = new Vector();
		listener = l;
		this.inTable = inTable;
	}
	public Vector getRenderingComponents(boolean sel) {
		Vector v = treeElement.getRenderingComponents(sel);
		JComboBox cb = new JComboBox(choices);
		cb.setSelectedIndex(selectedChoice);
		cb.setForeground(fgColor);
		cb.setOpaque(true);
		cb.setBackground(sel ? selBgColor : bgColor);
		cb.setForeground(sel ? selFgColor : fgColor);
		cb.addItemListener(this);
		if (listener != null) cb.addItemListener(listener);
		v.addElement(cb);
		return v;
	}
/*	public Component getEditorComponent(boolean sel) {
		Vector<Component> v = getEditingComponents(sel);
		DTreePanel p = new DTreePanel();
		for (int i = 0; i < v.size(); i++)
			p.addComponent(v.elementAt(i), i + 1, 1, 1, 1, (i < (v.size() - 1) ? 0.0 : 1.0), 1.0, DTreePanel.WEST, DTreePanel.NONE, 0, 2, 0, 0, 0, 0);
		p.setBackground(sel ? selBgColor : bgColor);
		p.setForeground(sel ? selFgColor : fgColor);
		p.setOpaque(true);
		//p.setFocusable(true);
		if (showBorder) p.setBorder(BorderFactory.createLineBorder(brdColor));
		return p;
	}*/
	public boolean isEditable() {
		return true;
	}

	public void itemStateChanged(ItemEvent itemEvent) {
		selectedChoice = ((JComboBox)itemEvent.getItemSelectable()).getSelectedIndex();
	}
}
