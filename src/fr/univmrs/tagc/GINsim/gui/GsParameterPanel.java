package fr.univmrs.tagc.GINsim.gui;

import javax.swing.JPanel;

import fr.univmrs.tagc.GINsim.graph.GsGraph;

/**
 * All parameter panels must extend this abstract class to be updated on selection changes
 */
public abstract class GsParameterPanel extends JPanel {

    protected GsMainFrame mainFrame;
    protected GsGraph graph;
    
    /**
     * inform the panel that the select object changed.
     * @param obj the currently selected object
     */
    public abstract void setEditedObject(Object obj);

    /**
     * @param mainFrame
     */
    public void setMainFrame(GsMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setGraph(mainFrame.getGraph());
    }
    
    /**
     * @param graph
     */
    public void setGraph(GsGraph graph) {
    	    this.graph = graph;
    }
}
