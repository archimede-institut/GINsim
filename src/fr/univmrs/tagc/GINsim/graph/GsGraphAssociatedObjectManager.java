package fr.univmrs.tagc.GINsim.graph;

import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.ginsim.graph.common.Graph;

/**
 * Implement this interface to save/open objects associated to graph automatically.
 */
public interface GsGraphAssociatedObjectManager {

    /**
     * @return the name of the object (for the zip entry)
     */
    public String getObjectName();
    /**
     * @param graph
     * @return true if this graph has a relevant associated object
     */
    public boolean needSaving( Graph graph);
    /**
     * save the object associated with this graph.
     * 
     * @param out
     * @param graph
     */
    public void doSave(OutputStreamWriter out, Graph graph);
    /**
     * open associated object.
     * 
     * @param is
     * @param graph
     */
    public Object doOpen(InputStream is, Graph graph);
    /**
     * create the associated object for a graph
     * @param graph
     */
	public Object doCreate( Graph graph);
}
