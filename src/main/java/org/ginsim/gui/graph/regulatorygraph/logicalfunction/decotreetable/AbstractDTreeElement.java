package org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.JTree;


public abstract class AbstractDTreeElement {
	protected Color fgColor, bgColor, brdColor, selFgColor, selBgColor;
	protected boolean showBorder, inTable;
	protected JTree tree;
	protected int leftSpace;
	protected DTreeElementDeco previous;
	protected AbstractDTreeElement parent;

	public AbstractDTreeElement(JTree t) {
		super();
		fgColor = Color.black;
		bgColor = Color.white;
		brdColor = Color.red;
		selBgColor = Color.yellow.brighter();
		selFgColor = Color.black;
		showBorder = inTable = false;
		leftSpace = 0;
		tree = t;
		previous = null;
		parent = null;
	}
	public abstract Component getRendererComponent(boolean sel);

	public Component getEditorComponent(boolean sel) {
		return getRendererComponent(sel);
	}
	public void setDefaults(Color[] cols, int ls) {
		setLeftSpace(leftSpace);
		setFgColor(cols[0]);
		setBgColor(cols[1]);
		setSelFgColor(cols[2]);
		setSelBgColor(cols[3]);
	}
	public abstract Vector getRenderingComponents(boolean sel);

	public abstract Object getUserObject();
	public abstract void setUserObject(Object o);
	public abstract DataValues getValues();
	public abstract void setValues(DataValues v);

	public Vector getEditingComponents(boolean sel) {
		return getRenderingComponents(sel);
  }
	public boolean isLeaf() {
  	return true;
	}
	public int getChildCount() {
		return 0;
	}
	public AbstractDTreeElement getChild(int i) {
		return null;
	}
	public void check(boolean b) {

	}
	public int indexOfChild(Object o) {
		return -1;
	}
	public String toString() {
		return "";
	}
	public void addElement(AbstractDTreeElement e) {

	}
	public void setElementAt(int i, AbstractDTreeElement e) {

	}
	public void clearChilds() {

	}
	public boolean isEditable() {
		return false;
	}
	public boolean equals(Object o) {
		return toString().equalsIgnoreCase(o.toString());
	}
	public void setFgColor(Color c) {
		fgColor = c;
	}
	public Color getFgColor() {
		return fgColor;
	}
	public void setBgColor(Color c) {
		bgColor = c;
	}
	public void setShowBorder(boolean b) {
		showBorder = b;
	}
	public void setBorderColor(Color c) {
		brdColor = c;
	}
	public void setSelBgColor(Color c) {
		selBgColor = c;
	}
	public void setSelFgColor(Color c) {
		selFgColor = c;
	}
	public void setLeftSpace(int s) {
		leftSpace = s;
	}
	public boolean isSelected() {
		return false;
	}
	public String getValue() {
		return "";
  }
	public JTree getTree() {
		return tree;
	}
	public AbstractDTreeElement getNextElement() {
		return null;
	}
	public DTreeElementDeco getPrevious() {
		return previous;
	}
	public AbstractDTreeElement getParent() {
		return parent;
	}
	public void setPrevious(DTreeElementDeco p) {
		previous = p;
	}
	public void setParent(AbstractDTreeElement p) {
		parent = p;
	}
	public void setCheckBoxSelected(boolean b) {

	}
}
