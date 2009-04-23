package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import java.awt.Component;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JTree;

import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.table.DataValues;


public abstract class DTreeElementDeco extends AbstractDTreeElement {
	protected AbstractDTreeElement treeElement;

	public DTreeElementDeco(AbstractDTreeElement e, JTree t) {
		super(t);
		treeElement = e;
		e.setPrevious(this);
	}
	public void setNextElement(AbstractDTreeElement e) {
		treeElement = e;
	}
	public void check(boolean b) {
		treeElement.check(b);
	}
	public boolean isLeaf() {
		return treeElement.isLeaf();
	}
	public String toString() {
		return treeElement.toString();
	}
	public void addElement(AbstractDTreeElement e) {
		treeElement.addElement(e);
	}
	public void setElementAt(int i, AbstractDTreeElement e) {
		treeElement.setElementAt(i, e);
	}
	public Vector getRenderingComponents(boolean sel) {
		return treeElement.getRenderingComponents(sel);
	}
	public Object getUserObject() {
		return treeElement.getUserObject();
	}
	public void setUserObject(Object o) {
		treeElement.setUserObject(o);
	}
	public DataValues getValues() {
		return treeElement.getValues();
	}
	public void setValues(DataValues v) {
		treeElement.setValues(v);
	}
	public void setCheckBoxSelected(boolean b) {
		treeElement.setCheckBoxSelected(b);
	}
	public Component getRendererComponent(boolean sel) {
		Vector v = getRenderingComponents(sel);
		DTreePanel p = new DTreePanel();
		for (int i = 0; i < v.size(); i++)
			p.addComponent((Component)v.elementAt(i), i + 1, 0, 1, 1, (i < (v.size() - 1) ? 0.0 : 1.0), 1.0,
					DTreePanel.WEST, DTreePanel.VERTICAL, (inTable ? 1 : 0), 2, 0, 0, 0, 0);
		p.setBackground(sel ? selBgColor : bgColor);
		p.setForeground(sel ? selFgColor : fgColor);
		p.setOpaque(true);
		if (showBorder) p.setBorder(BorderFactory.createLineBorder(brdColor));
		return p;
	}
	public boolean isSelected() {
		return (treeElement.isSelected());
	}
	public void setSelected(boolean b) {

	}
	public boolean isEditable() {
		return super.isEditable() || treeElement.isEditable();
	}
	public String getValue() {
		return treeElement.getValue();
	}
	public JTree getTree() {
		return treeElement.getTree();
	}
	public AbstractDTreeElement getNextElement() {
		return treeElement;
	}
	public int getChildCount() {
		return treeElement.getChildCount();
	}
	public AbstractDTreeElement getChild(int i) {
		return treeElement.getChild(i);
	}
	public int indexOfChild(Object o) {
		return treeElement.indexOfChild(o);
	}
	public void clearChilds() {
		treeElement.clearChilds();
	}
}
