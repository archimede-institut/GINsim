package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import java.util.Vector;

public class DTreeElementNode extends DTreeElementDeco {
	protected Vector sons;

	public DTreeElementNode(AbstractDTreeElement e) {
		super(e, e.getTree());
		sons = new Vector();
	}
	public void addElement(AbstractDTreeElement e) {
		sons.addElement(e);
	}
	public void setElementAt(int i, AbstractDTreeElement e) {
		sons.setElementAt(e, i);
	}
	public boolean isLeaf() {
		return false;
	}
	public int getChildCount() {
		return sons.size();
	}
	public AbstractDTreeElement getChild(int i) {
		if (i > (sons.size() - 1)) return null;
		return (AbstractDTreeElement)sons.elementAt(i);
	}
	public int indexOfChild(Object o) {
		return sons.indexOf(o);
	}
	public void check(boolean b) {
		super.check(b);
		for (int i = 0; i < getChildCount(); i++) getChild(i).check(b);
	}
	public void clearChilds() {
		sons.clear();
	}

}
