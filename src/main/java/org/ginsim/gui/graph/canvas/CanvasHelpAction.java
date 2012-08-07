package org.ginsim.gui.graph.canvas;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class CanvasHelpAction extends AbstractAction {
	private final SimpleCanvas canvas;
	
	public CanvasHelpAction(SimpleCanvas canvas) {
		super("Show canvas help");
		this.canvas = canvas;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		canvas.help();
	}
}
