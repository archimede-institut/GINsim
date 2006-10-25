package fr.univmrs.ibdm.GINsim.export;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutantDef;

/**
 * Configure SMV export
 */
public class GsSMVexportConfig {

	public static int CFG_SYNC = 0;
	public static int CFG_ASYNC = 1;
	
    GsRegulatoryGraph graph;
    short[] initstates;
    public GsRegulatoryMutantDef mutant;
    int type;
    
    String thetest = "";
    
	/**
	 * @param graph
	 */
	public GsSMVexportConfig(GsRegulatoryGraph graph) {
        initstates = new short[graph.getNodeOrder().size()];
        for (int i=0 ; i<initstates.length ; i++) {
            initstates[i] = -1;
        }
        this.graph = graph;
	}
	
	public String getTest() {
		return thetest;
	}
	
    /**
     * @return true if the "sync" option has been selected
     */
    public boolean isSync() {
        return type == CFG_SYNC;
    }
    
    /**
     * @return an array giving desired initial states (-1 for no constraint)
     * TODO: share this initial state with simulation parameters ?
     */
    public short[] getInitStates() {
        return initstates;
    }

    /**
     * @return the selected mutant (can be null)
     */
    public GsRegulatoryMutantDef getMutant() {
        return mutant;
    }

	public void setTest(String text) {
		thetest = text;
	}
}
