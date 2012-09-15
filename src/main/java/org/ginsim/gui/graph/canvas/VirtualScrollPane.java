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

/**
 * ScrollPane-like panel to host a "smart" canvas.
 * Instead of showing only part of the viewed widget (as a regular ScrollPane does),
 * it will collaborate with the widget based on its virtual dimensions and visible area.
 * 
 * @author Aurelien Naldi
 */
public class VirtualScrollPane extends JPanel implements ChangeListener {

	private final BoundedRangeModel hmodel, vmodel;
	private final VirtualScrollable view;
	
	private int width = 100, height = 100;
	private int x = 0, y = 0;
	private int dx = 100, dy=100;
	
	private boolean updating = false;
	
	public VirtualScrollPane(VirtualScrollable view) {
		super(new GridBagLayout());
		this.view = view;
		
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
		add(view.getComponent(), cst);
		
		vmodel.addChangeListener(this);
		hmodel.addChangeListener(this);
		
		view.setScrollPane(this);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (updating) {
			return;
		}
		BoundedRangeModel rmodel = (BoundedRangeModel)e.getSource();
		view.setScrollPosition( hmodel.getValue(), vmodel.getValue() );
	}

	public void fireViewUpdated() {
		if (hmodel.getValueIsAdjusting() || vmodel.getValueIsAdjusting()) {
			return;
		}
		
		Rectangle visible = view.getVisibleArea();
		Dimension global = view.getVirtualDimension();
		
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
