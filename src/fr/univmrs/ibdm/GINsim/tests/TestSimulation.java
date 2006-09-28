package fr.univmrs.ibdm.GINsim.tests;

import junit.framework.TestCase;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * test the simulation
 */
public class TestSimulation extends TestCase {
    
    GsRegulatoryGraph graph;
    
    /**
     * @param name
     */
    public TestSimulation (String name) {
        super(name);
    }

    private void getGraph() {
        if (graph == null) {
            graph = new GsRegulatoryGraph();
            GsRegulatoryVertex v = (GsRegulatoryVertex)graph.interactiveAddVertex(0, 0, 0);
            v.setMaxValue((short)2, graph);
        }
    }
    
    /**
     */
    public void testSynchrone() {
        getGraph();
        // TODO: refactor tons of stuff to allow running meaningfull tests
    }
}
