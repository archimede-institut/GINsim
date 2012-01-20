package org.ginsim.core.graph.common;

/**
 * possible types of graph changes, used when firing and listening for change events.
 * 
 * @author Aurelien Naldi
 */
public enum GraphChangeType {
	NODEADDED, NODEREMOVED, NODEUPDATED,
	EDGEADDED, EDGEREMOVED, EDGEUPDATED,
	ASSOCIATEDADDED, ASSOCIATEDREMOVED, ASSOCIATEDUPDATED,
	GRAPHMERGED, METADATACHANGE, PARSINGENDED,
	OTHERCHANGE;
	
}
