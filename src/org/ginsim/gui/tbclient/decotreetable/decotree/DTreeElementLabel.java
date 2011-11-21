package org.ginsim.gui.tbclient.decotreetable.decotree;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JLabel;

public class DTreeElementLabel extends DTreeElementDeco {
	protected String value;
	private Color fg;

	public DTreeElementLabel(AbstractDTreeElement e, String v, Color fg, boolean inTable) {
		super(e, e.getTree());
		value = v;
		this.fg = fg;
		this.inTable = inTable;
	}
	public Vector getRenderingComponents(boolean sel) {
		Vector v = treeElement.getRenderingComponents(sel);
		JLabel l = new JLabel(value);
		l.setOpaque(false);
		l.setBackground(sel ? selBgColor : bgColor);
		l.setForeground(sel ? selFgColor : fg);
		v.addElement(l);
		return v;
	}
	public String toString() {
		return treeElement.toString() + value;
	}
	public String getValue() {
		return value;
	}
	public void concat(String s) {
		value += s;
	}
}
