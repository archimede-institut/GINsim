package org.ginsim.service.tool.reg2dyn;

import org.ginsim.core.graph.common.Graph;




public interface SimulationManager {

    public void endSimu( Graph graph);

    /**
     * set the progress level, to give the user some feedback
     * @param n
     */
    public void setProgress(int n);
    /**
     * set the progress level, to give the user some feedback
     * @param n
     */
    public void setProgress(String s);

    public void addStableState(SimulationQueuedState item);

}