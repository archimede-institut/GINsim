package org.ginsim.gui.service.tool.stg2htg;

import java.util.Vector;

/**
 * A simple interface to retrieve the results in the PathFinding thread.
 *
 */
public interface ResultHandler {
	public void setPath(Vector path);
	public void setProgress(int i);
	public void setProgressionText(String string);
}
