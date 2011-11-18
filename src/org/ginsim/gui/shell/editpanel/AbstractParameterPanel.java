package org.ginsim.gui.shell.editpanel;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.JPanel;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUI;

/**
 * All parameter panels must extend this abstract class to be updated on selection changes
 */
public abstract class AbstractParameterPanel<T> extends JPanel implements GUIEditor<T> {
	private static final long	serialVersionUID	= 8326061792159035277L;
	
	protected final GraphGUI<?, ?, ?> gui;
    protected final Graph<?,?> graph;
    protected final Frame frame;
    
    protected AbstractParameterPanel(GraphGUI<?, ?, ?> gui) {
    	this.gui = gui;
    	this.graph = gui.getGraph();
    	this.frame = GUIManager.getInstance().getFrame(graph);
    }
    
    protected AbstractParameterPanel(Graph<?,?> graph) {
		this(GUIManager.getInstance().getGraphGUI(graph));
	}

	@Override
	public final Component getComponent() {
		return this;
	}
    
}
