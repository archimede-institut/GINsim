package org.ginsim.servicegui.tool.pathfinding;

import java.util.List;

/**
 * A simple interface to retrieve the results in the PathFinding thread.
 *
 */
public interface ResultHandler {
	public void setPath(List<Object> path);
}
