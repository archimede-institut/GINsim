package fr.univmrs.ibdm.GINsim.tests;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGinsimGraphDescriptor;
import fr.univmrs.ibdm.GINsim.jgraph.GsJgraphDirectedEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * Tests for the regulatory graph
 */
public class TestRegulatoryGraph extends TestCase {
    
    /**
     * @param name
     */
    public TestRegulatoryGraph (String name) {
        super(name);
    }

    /**
     */
    public void testCreation() {
        GsRegulatoryGraph graph = new GsRegulatoryGraph();
        try {
            graph.setGraphName("testgraph");
            assertTrue("testgraph".equals(graph.getGraphName()));
        } catch (GsException e) {
            fail("could not set graph name");
        }
        Object v1 = graph.interactiveAddVertex(0, 20, 15);
        if (v1 != null && v1 instanceof GsRegulatoryVertex) {
            GsRegulatoryVertex v = (GsRegulatoryVertex)v1;
            v.setBaseValue((short)3);
            assertEquals(3, v.getMaxValue());
            v.setMaxValue((short)2, graph);
            assertEquals(2, v.getBaseValue());
        } else {
            fail("bad newly created vertex");
        }
        
        Object v2 = graph.interactiveAddVertex(0, 20, 50);
        graph.interactiveAddEdge(v1, v2, 0);
        Object e1 = ((GsJgraphDirectedEdge)graph.getGraphManager().getEdge(v1,v2)).getUserObject();
        if (e1 != null && e1 instanceof GsRegulatoryMultiEdge) {
            GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)e1;
            me.setMax(0, (short)2);
            assertEquals(2, me.getMax(0));
            me.setMax(0, (short)3);
            assertEquals(2, me.getMax(0));
        } else {
            String s = (e1==null)?"null":e1.getClass().toString(); 
            fail("bad newly created edge: "+s);
        }
        File f;
        try {
            f = File.createTempFile("test-GINsim", ".ginml");
            f.deleteOnExit();
            graph.setSaveFileName(f.getAbsolutePath(), 1);
            try {
                graph.save();
                assertTrue(f.exists());
                GsRegulatoryGraph opened = (GsRegulatoryGraph)GsGinsimGraphDescriptor.getInstance().open(f);
                assertTrue("testgraph".equals(opened.getGraphName()));
                Iterator it = opened.getGraphManager().getVertexIterator();
                while (it.hasNext()) {
                    GsRegulatoryVertex v = (GsRegulatoryVertex)it.next();
                    if ("G0".equals(v.getId())) {
                        assertEquals(2, v.getMaxValue());
                    } else {
                        assertTrue("G1".equals(v.getId()));
                        assertEquals(1, v.getMaxValue());
                    }
                }
                // TODO: check visual settings!
            } catch (GsException e) {
                fail("error while saving / opening");
            }
        } catch (IOException e2) {
            fail("could not get temporary file");
        }
    }
}
