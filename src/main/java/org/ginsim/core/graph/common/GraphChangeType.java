package org.ginsim.core.graph.common;

/**
 * possible types of graph changes (edit events), used when firing and listening for change events.
 * 
 * @author Aurelien Naldi
 */
public enum GraphChangeType {
	NODEADDED, NODEREMOVED, NODEUPDATED, NODEDAMAGED,
	EDGEADDED, EDGEREMOVED, EDGEUPDATED, EDGEDAMAGED,
	ASSOCIATEDADDED, ASSOCIATEDREMOVED, ASSOCIATEDUPDATED,
	GRAPHMERGED, METADATACHANGE, PARSINGENDED,
	GRAPHSAVED, GRAPHVIEWCHANGED, OTHERCHANGE;
	
}
