package org.ginsim.core.graph.common;

/**
 * possible types of graph changes (edit events), used when firing and listening for change events.
 * 
 * @author Aurelien Naldi
 */
public enum GraphChangeType {
	
	NODEADDED, NODEREMOVED, NODEUPDATED, NODEDAMAGED(false),
	EDGEADDED, EDGEREMOVED, EDGEUPDATED, EDGEDAMAGED(false),
	ASSOCIATEDADDED, ASSOCIATEDREMOVED, ASSOCIATEDUPDATED,
	GRAPHMERGED, METADATACHANGE, PARSINGENDED,
	GRAPHSAVED, GRAPHVIEWCHANGED, OTHERCHANGE;

	private GraphChangeType() {
		this(true);
	}
	
	private GraphChangeType(boolean needssaving) {
		this.needssaving = needssaving;
	}
	
	public final boolean needssaving;
	

}
