package fr.univmrs.ibdm.GINsim.graph;

/**
 * an interface to listener of the newgraph event: the graph in the frame changed
 */
public interface GsNewGraphListener {

    /**
     * notify that the graph has been replaced.
     * @param event
     */
	public void notifyNewGraph(GsNewGraphEvent event);
}
