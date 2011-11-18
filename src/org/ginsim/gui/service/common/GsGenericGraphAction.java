package org.ginsim.gui.service.common;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.ginsim.graph.common.Graph;


public abstract class GsGenericGraphAction extends BaseAction{

	protected final Graph<?, ?> graph;
	
	public GsGenericGraphAction(Graph<?, ?> graph, String name) {
		this(graph, name, null, null, null);
	}

	/**
     * 
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public GsGenericGraphAction(Graph<?, ?> graph, String name, String tooltip) {
		this(graph, name, null, tooltip, null);
	}
	
	/**
     * @param name Entry to insert in the menu
     * @param icon icon image for menu and toolbar
     * @param tooltip Long description of the action
     * @param accelerator the keyboard bytecut
     */
	public GsGenericGraphAction(Graph<?, ?> graph, String name, ImageIcon icon, String tooltip, KeyStroke accelerator) {
		super(name, icon, tooltip, accelerator, null);
		this.graph = graph;
	}
	
}
