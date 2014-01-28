package org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable;

import java.awt.Graphics;


public class TableDecoTree extends DecoTree implements TableTree {
	private static final long serialVersionUID = 1495859310903175881L;
	protected int visibleRow;
	
	public TableDecoTree(DTreeModel model, int h) {
		super(model, h);
		setEditable(true);
	}
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, 0, w, getRowCount() * getRowHeight());
	}
	public void paint(Graphics g) {
		g.translate(0, -visibleRow * getRowHeight());
		super.paint(g);
	}
	public void setVisibleRow(int row) {
		visibleRow = row;
	}
}
