package org.ginsim.gui.shell.editpanel;

import java.awt.Frame;

import javax.swing.JPanel;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;



/**
 * All parameter panels must extend this abstract class to be updated on selection changes
 */
public abstract class AbstractParameterPanel extends JPanel {
	private static final long	serialVersionUID	= 8326061792159035277L;
	
	protected final GraphGUI<?, ?, ?> gui;
    protected final Graph<?,?> graph;
    protected final Frame frame;
    
    public AbstractParameterPanel(GraphGUI<?, ?, ?> gui) {
    	this.gui = gui;
    	this.graph = gui.getGraph();
    	this.frame = GUIManager.getInstance().getFrame(graph);
    }
    
    public AbstractParameterPanel(Graph<?,?> graph) {
		this(GUIManager.getInstance().getGraphGUI(graph));
	}

	/**
     * inform the panel that the select object changed.
     * @param obj the currently selected object
     */
    public abstract void setEditedItem(Object obj);

}
