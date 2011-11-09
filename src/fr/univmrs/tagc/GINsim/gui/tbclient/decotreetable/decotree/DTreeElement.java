package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;

import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.table.DataValues;

public class DTreeElement extends AbstractDTreeElement {
	private String title;
	protected Object userObject;
	protected DataValues values;
	private Color fg;

	public DTreeElement(JTree tree, String t, Color fg) {
		super(tree);
		title = t;
		userObject = null;
		values = null;
		this.fg = fg;
		selFgColor = fg;
	}
	public DTreeElement(JTree tree, String t, Object o, Color fg) {
		this(tree, t, fg);
		userObject = o;
	}
	public Vector getRenderingComponents(boolean sel) {
		Vector v = new Vector();
		JLabel l = new JLabel(title);
		l.setOpaque(false);
		l.setBackground(sel ? selBgColor : bgColor);
		l.setForeground(sel ? selFgColor : fg);
		v.addElement(l);
		return v;
	}
	public Component getRendererComponent(boolean sel) {
		DTreePanel p = new DTreePanel();
		p.addComponent((Component)getRenderingComponents(sel).firstElement(), 0, 0, 1, 1, 1.0, 0.0, DTreePanel.WEST, DTreePanel.NONE,
									 (inTable ? 1 : 0), 2, 0, 0, 0, 0);
		p.setBackground(sel ? selBgColor : bgColor);
		p.setForeground(sel ? selFgColor : fg);
		p.setOpaque(true);
		//p.setFocusable(true);
		if (showBorder) p.setBorder(BorderFactory.createLineBorder(brdColor));
		return p;
	}
	public String toString() {
		return title;
	}
	public Object getUserObject() {
		return userObject;
	}
	public void setUserObject(Object o) {
		userObject = o;
	}
	public DataValues getValues() {
		return values;
	}
	public void setValues(DataValues v) {
		values = v;
	}
}
