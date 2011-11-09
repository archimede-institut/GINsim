package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class DTreeElementToggleButton extends DTreeElementDeco implements ActionListener {

	public class TBToggleButton extends JToggleButton {
		private static final long serialVersionUID = -5254426961700408614L;
		private Insets insets;

	  public TBToggleButton() {
			super();
			insets = new Insets(0, 0, 0, 0);
		}
		public TBToggleButton(Action a) {
			super(a);
			insets = new Insets(0, 0, 0, 0);
		}
		public TBToggleButton(Icon icon) {
			super(icon);
			insets = new Insets(0, 0, 0, 0);
		}
		public TBToggleButton(Icon icon, boolean selected) {
			super(icon, selected);
			insets = new Insets(0, 0, 0, 0);
		}
		public TBToggleButton(String text) {
			super(text);
			insets = new Insets(0, 0, 0, 0);
		}
		public TBToggleButton(String text, boolean selected) {
			super(text, selected);
			insets = new Insets(0, 0, 0, 0);
		}
		public TBToggleButton(String text, Icon icon) {
			super(text, icon);
			insets = new Insets(0, 0, 0, 0);
		}
		public TBToggleButton(String text, Icon icon, boolean selected) {
			super(text, icon, selected);
			insets = new Insets(0, 0, 0, 0);
		}
		public Insets getInsets() {
			return insets;
		}
		public void setInsets(int top, int left, int bottom, int right) {
			insets = new Insets(top, left, bottom, right);
		}
	}

	protected TBToggleButton tb, tb2;
	private ImageIcon offIc, onIc;
	private String text;
	private ActionListener al;
	private boolean selected;

	public DTreeElementToggleButton(AbstractDTreeElement e, ImageIcon offIc, ImageIcon onIc, String text, ActionListener l, boolean inTable) {
		super(e, e.getTree());
		this.offIc = offIc;
		this.onIc = onIc;
		this.text = text;
		al = l;
		selected = false;
		this.inTable = inTable;
	}
	public Vector getRenderingComponents(boolean sel) {
		Vector v = treeElement.getRenderingComponents(sel);
		tb2 = new TBToggleButton((text != null ? text : ""), offIc);
    tb2.setSelectedIcon(onIc);
    tb2.setInsets(2, 3, 2, 3);
    tb2.setContentAreaFilled(false);
    tb2.setFocusable(false);
    tb2.setFocusPainted(false);
    tb2.addActionListener(al);
    tb2.addActionListener(this);
    tb2.setBorderPainted(text != null);
    tb2.setSelected(selected);
    tb2.setBackground(sel ? selBgColor : bgColor);
		tb2.setForeground(sel ? selFgColor : fgColor);
		v.addElement(tb2);
		return v;
	}
	public Vector getEditingComponents(boolean sel) {
		Vector v = treeElement.getEditingComponents(sel);
		tb = new TBToggleButton((text != null ? text : ""), offIc);
    tb.setSelectedIcon(onIc);
    tb.setInsets(2, 3, 2, 3);
    tb.setContentAreaFilled(false);
    tb.setFocusable(false);
    tb.setFocusPainted(false);
    tb.addActionListener(al);
    tb.addActionListener(this);
    tb.setBorderPainted(text != null);
    tb.setSelected(selected);
    tb.setBackground(sel ? selBgColor : bgColor);
		tb.setForeground(sel ? selFgColor : fgColor);
		v.addElement(tb);
		return v;
	}
/*	public Component getEditorComponent(boolean sel) {
		Vector v = getEditingComponents(sel);
		DTreePanel p = new DTreePanel();
		for (int i = 0; i < v.size(); i++)
			p.addComponent((Component)v.elementAt(i), i + 1, 1, 1, 1, (i < (v.size() - 1) ? 0.0 : 1.0), 1.0,
					DTreePanel.WEST, DTreePanel.NONE, (inTable ? 1 : 0), 2, 0, 0, 0, 0);
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
	public void actionPerformed(ActionEvent e) {
		selected = (tb2 != null ? tb2.isSelected() : false) || (tb != null ? tb.isSelected() : false);
	}
	public void setSelected(boolean b) {
		selected = b;
	}
	public boolean isSelected() {
		return selected;
	}
}
