package org.ginsim.gui.service.common;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;

public abstract class LayoutAction<G extends Graph<?,?>> extends BaseAction {

	public final G graph;
	
	public LayoutAction(G graph, String name) {
		this(graph, name, null, null, null);
	}

	/**
     * 
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public LayoutAction(G graph, String name, String tooltip) {
		
		this(graph, name, null, tooltip, null);
	}
	
	/**
     * 
     * @param name Entry to insert in the menu
     * @param icon icon image for menu and toolbar
     * @param tooltip Long description of the action
     * @param accelerator the keyboard bytecut
     */
	public LayoutAction(G graph, String name, ImageIcon icon, String tooltip, KeyStroke accelerator) {
		
		super(name, icon, tooltip, accelerator, null);
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg) {
		
		doLayout(arg);
		GraphGUI<G, ?, ?> gui = GUIManager.getInstance().getGraphGUI(graph);
		if (gui != null) {
			gui.repaint();
		}
	}

	abstract public void doLayout(ActionEvent arg);
}
