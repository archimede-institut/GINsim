package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import java.util.Vector;

import javax.swing.JCheckBox;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.Icon;

public class DTreeElementSelectable extends DTreeElementDeco implements ActionListener {
	public class JCB extends JCheckBox {
		private static final long serialVersionUID = 1796211813329393241L;
		private DTreeElementSelectable dTreeElement;

		public JCB(DTreeElementSelectable e, boolean b) {
			super((Icon)null, b);
			dTreeElement = e;
		}
		public Insets getMargin() {
			return new Insets(0, 0, 0, 0);
		}
		public DTreeElementSelectable getElement() {
			return dTreeElement;
		}
	}
	private JCB cb;
	private boolean checked;
	private ItemListener il;

	public DTreeElementSelectable(AbstractDTreeElement e, boolean sel, boolean inTable, ItemListener l) {
		super(e, e.getTree());
		checked = sel;
		this.inTable = inTable;
		il = l;
	}
	public void check(boolean b) {
		checked = b;
		//setSelected(b);
	}
	public Vector getRenderingComponents(boolean sel) {
		Vector v = treeElement.getRenderingComponents(sel);
		cb = new JCB(this, checked);
		cb.setForeground(fgColor);
		cb.setOpaque(false);
		cb.addActionListener(this);
		if (il != null) cb.addItemListener(il);
		cb.setBackground(sel ? selBgColor : bgColor);
		cb.setForeground(sel ? selFgColor : fgColor);
		v.insertElementAt(cb, 0);
		return v;
	}
/*	public Component getEditorComponent(boolean sel) {
		Vector v = getEditingComponents(sel);
		DTreePanel p = new DTreePanel();
		for (int i = 0; i < v.size(); i++)
			p.addComponent((Component)v.elementAt(i), i + 1, 0, 1, 1, (i < (v.size() - 1) ? 0.0 : 1.0), 1.0,
					DTreePanel.WEST, DTreePanel.NONE, (inTable ? 1 : 0), 2, 0, 0, 0, 0);
		p.setBackground(sel ? selBgColor : bgColor);
		p.setForeground(sel ? selFgColor : fgColor);
		p.setOpaque(true);
		//p.setFocusable(true);
		if (showBorder) p.setBorder(BorderFactory.createLineBorder(brdColor));
		return p;
	}*/

	public void actionPerformed(ActionEvent e) {
		checked = !checked;
		check(checked);
		tree.treeDidChange();
		tree.repaint();
	}
	public void setCheckBoxSelected(boolean b) {
		cb.setSelected(b);
		checked = b;
	}
	public boolean isSelected() {
		return checked;
	}
	public boolean isEditable() {
		return true;
	}
}
