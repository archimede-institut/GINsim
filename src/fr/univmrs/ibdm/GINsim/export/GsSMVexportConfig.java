package fr.univmrs.ibdm.GINsim.export;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;

/**
 * Configure SMV export
 */
public class GsSMVexportConfig {

	public static int CFG_SYNC = 0;
	public static int CFG_ASYNC = 1;
	
    GsRegulatoryGraph graph;
    Map m_initStates;
    public GsRegulatoryMutantDef mutant;
    public int type;
    
    String thetest = "";
    
	/**
	 * @param graph
	 */
	public GsSMVexportConfig(GsRegulatoryGraph graph) {
        m_initStates = new HashMap();
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
    public Map getInitStates() {
        return m_initStates;
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
