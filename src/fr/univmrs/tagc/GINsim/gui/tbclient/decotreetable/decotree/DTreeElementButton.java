package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class DTreeElementButton extends DTreeElementDeco {
	private JButton b;
	private String title, actionCommand;
	private ImageIcon imageIcon;
	private ActionListener listener;

	public DTreeElementButton(AbstractDTreeElement e, ImageIcon ic, String title, ActionListener l, boolean inTable, String ac) {
		super(e, e.getTree());
		this.title = title;
		this.imageIcon = ic;
		listener = l;
		this.inTable = inTable;
		actionCommand = ac;
	}
	public void setListener(ActionListener l) {
		listener = l;
	}
	public Vector getRenderingComponents(boolean sel) {
		Vector v = treeElement.getRenderingComponents(sel);
		b = new JButton(title, imageIcon) {
			public Insets getInsets() {
				return new Insets(0, 0, 0, 0);
			}
		};

		if (actionCommand != null) b.setActionCommand(actionCommand);
		b.setContentAreaFilled(true);
		b.setForeground(fgColor);
		if (listener != null) b.addActionListener(listener);
		b.setBackground(sel ? selBgColor : bgColor);
		b.setForeground(sel ? selFgColor : fgColor);
		b.setOpaque(true);
		v.addElement(b);
		return v;
	}
	public boolean isEditable() {
		return true;
	}
}
