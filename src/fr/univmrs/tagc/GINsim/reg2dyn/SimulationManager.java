package fr.univmrs.tagc.GINsim.reg2dyn;

import fr.univmrs.tagc.GINsim.graph.GsGraph;


public interface SimulationManager {

    public void endSimu(GsGraph graph);

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