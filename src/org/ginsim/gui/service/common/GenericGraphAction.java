package org.ginsim.gui.service.common;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.service.ServiceGUI;


public abstract class GenericGraphAction extends BaseAction{

	protected final Graph<?, ?> graph;
	
	public GenericGraphAction(Graph<?, ?> graph, String name, ServiceGUI serviceGUI) {
		this(graph, name, null, null, null, serviceGUI);
	}

	/**
     * 
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public GenericGraphAction(Graph<?, ?> graph, String name, String tooltip, ServiceGUI serviceGUI) {
		this(graph, name, null, tooltip, null, serviceGUI);
	}
	
	/**
     * @param name Entry to insert in the menu
     * @param icon icon image for menu and toolbar
     * @param tooltip Long description of the action
     * @param accelerator the keyboard bytecut
     */
	public GenericGraphAction(Graph<?, ?> graph, String name, ImageIcon icon, String tooltip, KeyStroke accelerator, ServiceGUI serviceGUI) {
		super(name, icon, tooltip, accelerator, null, serviceGUI);
		this.graph = graph;
	}
	
}
