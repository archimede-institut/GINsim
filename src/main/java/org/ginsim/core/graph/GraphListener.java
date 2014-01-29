package org.ginsim.core.graph;


/**
 * Listen to graph edit events.
 * 
 * @author Aurelien Naldi
 */
public interface GraphListener<G extends GraphModel<?,?>> {

	GraphEventCascade graphChanged(G g, GraphChangeType type, Object data);
	
}
