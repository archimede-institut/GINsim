package org.ginsim.gui.graph.canvas;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CanvasScrolPane extends JPanel implements ChangeListener {

	private final BoundedRangeModel hmodel, vmodel;
	private final SimpleCanvas canvas;
	
	private int width = 100, height = 100;
	private int x = 0, y = 0;
	private int dx = 100, dy=100;
	
	private boolean updating = false;
	
	public CanvasScrolPane(SimpleCanvas canvas) {
		super(new GridBagLayout());
		this.canvas = canvas;
		
		// add scrollbars
		GridBagConstraints cst = new GridBagConstraints();
		cst.gridx = 1;
		cst.weighty = 1;
		cst.fill = GridBagConstraints.VERTICAL;
		cst.anchor = GridBagConstraints.EAST;
		JScrollBar sbar = new JScrollBar(JScrollBar.VERTICAL);
		vmodel = sbar.getModel();
		add(sbar, cst);
		
		cst = new GridBagConstraints();
		cst.gridy = 1;
		cst.weightx = 1;
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.anchor = GridBagConstraints.SOUTH;
		sbar = new JScrollBar(JScrollBar.HORIZONTAL);
		hmodel = sbar.getModel();
		add(sbar, cst);		
		
		// add the canvas itself
		cst = new GridBagConstraints();
		cst.gridx = 0;
		cst.gridy = 0;
		cst.weightx = 1;
		cst.weighty = 1;
		cst.fill = GridBagConstraints.BOTH;
		add(canvas, cst);
		
		vmodel.addChangeListener(this);
		hmodel.addChangeListener(this);
		
		canvas.setScrollPane(this);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (updating) {
			return;
		}
		BoundedRangeModel rmodel = (BoundedRangeModel)e.getSource();
		if (rmodel.getValueIsAdjusting()) {
			return;
		}
		canvas.moveTo( hmodel.getValue(), vmodel.getValue() );
	}

	public void setScrollPosition(Rectangle visible, Dimension global) {
		if (hmodel.getValueIsAdjusting() || vmodel.getValueIsAdjusting()) {
			return;
		}
		if ( global.width == width && global.height == height &&
			 visible.x == x && visible.width == dx &&
			 visible.y == y && visible.height == dy ) {
			return;
		}
		
		updating = true;
		this.width = global.width;
		this.height = global.height;
		this.x = visible.x;
		this.y = visible.y;
		
		this.dx = visible.width;
		this.dy = visible.height;

		int full = width;
		if (x + dx > width) {
			full = x+dx;
		}
		hmodel.setRangeProperties(x, dx, 0, full, false);
		full = height;
		if (y + dy > height) {
			full = y+dy;
		}
		vmodel.setRangeProperties(y, dy, 0, full, false);
		updating = false;
	}
}
