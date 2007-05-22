package fr.univmrs.ibdm.GINsim.export.regulatoryGraph;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialStateStore;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsMutantStore;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;

/**
 * Configure SMV export
 */
public class GsSMVexportConfig implements GsMutantStore, GsInitialStateStore {

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
    
	public void setTest(String text) {
		thetest = text;
	}

    public GsRegulatoryMutantDef getMutant() {
        return mutant;
    }
	public void setMutant(GsRegulatoryMutantDef mutant) {
		this.mutant = mutant;
	}

	public Map getInitialState() {
		return m_initStates;
	}
}