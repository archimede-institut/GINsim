package org.ginsim.gui.service.tools.reg2dyn;

import org.ginsim.graph.Graph;




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