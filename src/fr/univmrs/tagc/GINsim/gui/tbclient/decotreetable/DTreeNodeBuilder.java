package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.Vector;

import javax.swing.*;

import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.*;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.table.DataValues;

public class DTreeNodeBuilder {
	protected AbstractDTreeElement currentElement;
	protected boolean inTable;

	public DTreeNodeBuilder(boolean inTable) {
		this.inTable = inTable;
	}
	public void newNode(JTree t, String s, Color fg) {
		currentElement = new DTreeElement(t, s, fg);
	}
	public void newNode(JTree t, String s, Color fg, Object uo) {
		currentElement = new DTreeElement(t, s, uo, fg);
	}
	public void setSelectable(boolean selected, ItemListener il) {
		currentElement = new DTreeElementSelectable(currentElement, selected, inTable, il);
	}
	public void setNode() {
		currentElement = new DTreeElementNode(currentElement);
	}
	public void setNode(AbstractDTreeElement n) {
		currentElement = n;
	}

	public DTreeNodeBuilder addValue(Object v, boolean editable) {
		DataValues dv = currentElement.getValues();
		if (dv == null) {
			dv = new DataValues();
			currentElement.setValues(dv);
		}
		dv.addValue(v, editable);
		return this;
	}
	public void addButton(ImageIcon ic, String text, ActionListener al, String ac) {
		currentElement = new DTreeElementButton(currentElement, ic, text, al, inTable, ac);
	}
	public void addLink(String url, ImageIcon ic, String text){
		try {
			currentElement = new DTreeElementLink(currentElement, new URL(url), ic, text, inTable);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void addToggleButton(ImageIcon ic_off, ImageIcon ic_on, String text, ActionListener al) {
		currentElement = new DTreeElementToggleButton(currentElement, ic_off, ic_on, text, al, inTable);
	}
	public void addComboBox(Vector v, ItemListener il) {
		currentElement = new DTreeElementComboBox(currentElement, v, il, inTable);
	}
	public void addLabel(String text, Color fg) {
		currentElement = new DTreeElementLabel(currentElement, text, fg, inTable);
	}
	public void addTextField(String text, int width, ActionListener al) {
		currentElement = new DTreeElementTextField(currentElement, text, width, al, inTable);
	}
	public AbstractDTreeElement getNode() {
		return currentElement;
	}
}
