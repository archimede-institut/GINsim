package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import java.awt.Dimension;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.JTextField;

public class DTreeElementTextField extends DTreeElementDeco implements KeyListener {
	private String text;
	private int width;
	private JTextField tf;
	private ActionListener listener;

	public DTreeElementTextField(AbstractDTreeElement e, String s, int w, ActionListener l, boolean inTable) {
		super(e, e.getTree());
		text = s;
		width = w;
		listener = l;
		this.inTable = inTable;
	}
	public Vector getRenderingComponents(boolean sel) {
		Vector v = treeElement.getRenderingComponents(sel);
		JTextField tf2 = new JTextField(text);
		tf2.setPreferredSize(new Dimension(width, Math.min(getTree().getRowHeight() - 3, tf2.getPreferredSize().height)));
		tf2.setForeground(fgColor);
		tf2.setOpaque(true);
		tf2.addKeyListener(this);
		tf2.setBackground(sel ? selBgColor : bgColor);
		tf2.setForeground(sel ? selFgColor : fgColor);
		v.addElement(tf2);
		return v;
	}
	public Vector getEditingComponents(boolean sel) {
		Vector v = treeElement.getEditingComponents(sel);
		tf = new JTextField(text);
		tf.setPreferredSize(new Dimension(width, Math.min(getTree().getRowHeight() - 6, tf.getPreferredSize().height)));
		tf.setForeground(fgColor);
		tf.setOpaque(true);
		tf.setFocusable(true);
		tf.addKeyListener(this);
		if (listener != null) tf.addActionListener(listener);
		tf.setBackground(sel ? selBgColor : bgColor);
		tf.setForeground(sel ? selFgColor : fgColor);
		v.addElement(tf);
		return v;
	}
	public boolean isEditable() {
		return true;
	}
	public void keyTyped(KeyEvent keyEvent) {
	}
	public void keyPressed(KeyEvent keyEvent) {
	}
	public void keyReleased(KeyEvent keyEvent) {
		text = ((JTextField)keyEvent.getSource()).getText();
	}
}
