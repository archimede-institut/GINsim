package org.ginsim.core.graph.common;

import org.ginsim.core.GraphEventCascade;


/**
 * listen graph events
 */
public interface GraphListener<G extends GraphModel<?,?>> {

	GraphEventCascade graphChanged(G g, GraphChangeType type, Object data);
	
}
