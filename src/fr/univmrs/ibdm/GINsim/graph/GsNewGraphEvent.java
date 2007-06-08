package fr.univmrs.ibdm.GINsim.graph;


/**
 * Provide information about the new graph in the GsMainFrame
 */
public class GsNewGraphEvent {

	private GsGraph oldGraph;
	private GsGraph newGraph;
	private boolean anAssociation;
	private Object source;	
	
	
	/**
	 * Create a new graph event
	 * @param source
	 * @param oldG Graph
	 * @param newG Graph
	 * @param association true if the associated graph was changed
	 */
	public GsNewGraphEvent(Object source,GsGraph oldG,GsGraph newG, boolean association) {
		this.source=source;
		oldGraph=oldG;
		newGraph=newG;
		anAssociation=association;
	}

	/**
	 * get the new graph
	 * @return the new graph
	 */
	public GsGraph getNewGraph() {
		return newGraph;
	}

	/**
	 * get the old graph
	 * @return the old graph
	 */
	public GsGraph getOldGraph() {
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
