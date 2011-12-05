package org.ginsim.servicegui.common;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.ginsim.core.graph.common.Graph;


public abstract class GenericGraphAction extends BaseAction{

	protected final Graph<?, ?> graph;
	
	public GenericGraphAction(Graph<?, ?> graph, String name) {
		this(graph, name, null, null, null);
	}

	/**
     * 
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public GenericGraphAction(Graph<?, ?> graph, String name, String tooltip) {
		this(graph, name, null, tooltip, null);
	}
	
	/**
     * @param name Entry to insert in the menu
     * @param icon icon image for menu and toolbar
     * @param tooltip Long description of the action
     * @param accelerator the keyboard bytecut
     */
	public GenericGraphAction(Graph<?, ?> graph, String name, ImageIcon icon, String tooltip, KeyStroke accelerator) {
		super(name, icon, tooltip, accelerator, null);
		this.graph = graph;
	}
	
}
