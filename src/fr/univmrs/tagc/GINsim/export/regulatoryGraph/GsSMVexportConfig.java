package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateStore;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.common.datastore.ObjectStore;

/**
 * Configure SMV export
 */
public class GsSMVexportConfig implements GsInitialStateStore {

	public static final int CFG_SYNC = 0;
	public static final int CFG_ASYNC = 1;
	public static final int CFG_ASYNCBIS = 2;
	public static final int CFG_APNN = 3;

    GsRegulatoryGraph graph;
    Map m_initStates;
    Map m_input;
    ObjectStore store = new ObjectStore();
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
    
    public int getType() {
        return type;
    }
    
	public void setTest(String text) {
		thetest = text;
	}

	public Map getInitialState() {
		return m_initStates;
	}

    public Map getInputState() {
        return m_input;
    }	
}
