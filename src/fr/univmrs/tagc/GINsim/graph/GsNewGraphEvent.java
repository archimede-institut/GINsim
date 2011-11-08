package fr.univmrs.tagc.GINsim.graph;

import org.ginsim.graph.Graph;


/**
 * Provide information about the new graph in the GsMainFrame
 */
public class GsNewGraphEvent {

	private Graph oldGraph;
	private Graph newGraph;
	private boolean anAssociation;
	private Object source;	
	
	
	/**
	 * Create a new graph event
	 * @param source
	 * @param oldG Graph
	 * @param newG Graph
	 * @param association true if the associated graph was changed
	 */
	public GsNewGraphEvent(Object source, Graph oldG, Graph newG, boolean association) {
		this.source=source;
		oldGraph=oldG;
		newGraph=newG;
		anAssociation=association;
	}

	/**
	 * get the new graph
	 * @return the new graph
	 */
	public Graph getNewGraph() {
		return newGraph;
	}

	/**
	 * get the old graph
	 * @return the old graph
	 */
	public Graph getOldGraph() {
		return oldGraph;
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() {
		return new GsNewGraphEvent(source,oldGraph,newGraph,anAssociation);
	}

	/**
	 * to know if the associated graph was changed
	 * @return true if the associated graph was changed
	 */
	public boolean isAnAssociation() {
		return anAssociation;
	}

//	/**
//	 * @return
//	 */
//	public Object getSource() {
//		return source;
//	}

}
