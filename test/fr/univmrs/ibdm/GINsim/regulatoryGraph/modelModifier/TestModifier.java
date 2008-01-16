package fr.univmrs.ibdm.GINsim.regulatoryGraph.modelModifier;

import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import junit.framework.TestCase;

public class TestModifier extends TestCase {

	GsRegulatoryGraph graph = new GsRegulatoryGraph();
	ModelSimplifierConfig cfg = new ModelSimplifierConfig();
	ModelSimplifier simplifier = new ModelSimplifier(graph, cfg, null);

	/**
	 * basic test of model simplification, without funny stuff
	 * @throws GsException 
	 */
    public void testBasicModification() throws GsException {
    	
    	/* deleted node (level 3):
    	 *   !A & D
    	 */
    	OmddNode tmp = new OmddNode();
    	tmp.level = 4;
    	tmp.next = new OmddNode[2];
    	tmp.next[0] = OmddNode.TERMINALS[0];
    	tmp.next[1] = OmddNode.TERMINALS[1];
    	
    	OmddNode deleted = new OmddNode();
    	deleted.level = 1;
    	deleted.next = new OmddNode[2];
    	deleted.next[0] = tmp;
    	deleted.next[1] = OmddNode.TERMINALS[0];

    	/* original node:
    	 *   (A & !C) | B | C 
    	 */
    	tmp = new OmddNode();
    	tmp.level = 3;
    	tmp.next = new OmddNode[2];
    	tmp.next[0] = OmddNode.TERMINALS[0];
    	tmp.next[1] = OmddNode.TERMINALS[1];
    	
    	OmddNode tmp2 = new OmddNode();
    	tmp2.level = 2;
    	tmp2.next = new OmddNode[2];
    	tmp2.next[0] = tmp;
    	tmp2.next[1] = OmddNode.TERMINALS[1];

    	tmp = new OmddNode();
    	tmp.level = 3;
    	tmp.next = new OmddNode[2];
    	tmp.next[0] = OmddNode.TERMINALS[1];
    	tmp.next[1] = OmddNode.TERMINALS[0];

    	OmddNode ori = new OmddNode();
    	ori.level = 1;
    	ori.next = new OmddNode[2];
    	ori.next[0] = tmp2;
    	ori.next[1] = tmp;

    	ModelSimplifier simplifier = new ModelSimplifier(graph, cfg, null);
    	OmddNode result = simplifier.remove(ori, deleted, 3);
    	
    	/*  expected result:
    	 *    A | B | D
    	 */
        assertEquals(result.toString(), "((N[1]=0 && ((N[2]=0 && ((N[4]=0 && 0) ; (N[4]=1 && 1))) ; (N[2]=1 && 1))) ; (N[1]=1 && 1))");
    }
    public void testUnNeededModification() throws GsException {
    	
    	/* deleted node (level 3):
    	 *   !A & D
    	 */
    	OmddNode tmp = new OmddNode();
    	tmp.level = 4;
    	tmp.next = new OmddNode[2];
    	tmp.next[0] = OmddNode.TERMINALS[0];
    	tmp.next[1] = OmddNode.TERMINALS[1];
    	
    	OmddNode deleted = new OmddNode();
    	deleted.level = 1;
    	deleted.next = new OmddNode[2];
    	deleted.next[0] = tmp;
    	deleted.next[1] = OmddNode.TERMINALS[0];

    	/* original node:
    	 *   A | B 
    	 */
    	tmp = new OmddNode();
    	tmp.level = 2;
    	tmp.next = new OmddNode[2];
    	tmp.next[0] = OmddNode.TERMINALS[0];
    	tmp.next[1] = OmddNode.TERMINALS[1];
    	
    	OmddNode ori = new OmddNode();
    	ori.level = 1;
    	ori.next = new OmddNode[2];
    	ori.next[0] = tmp;
    	ori.next[1] = OmddNode.TERMINALS[1];

    	/*  expected result:
    	 *    A | B
    	 */
    	OmddNode result = simplifier.remove(ori, deleted, 3);
        assertEquals(result.toString(), "((N[1]=0 && ((N[2]=0 && 0) ; (N[2]=1 && 1))) ; (N[1]=1 && 1))");
        
        // some other trivial tests:
        tmp.level = 4;
    	result = simplifier.remove(tmp, deleted, 3);
        assertEquals(result.toString(), "((N[4]=0 && 0) ; (N[4]=1 && 1))");
    	result = simplifier.remove(ori, deleted, 3);
        assertEquals(result.toString(), "((N[1]=0 && ((N[4]=0 && 0) ; (N[4]=1 && 1))) ; (N[1]=1 && 1))");
        
    }
    
    /**
     * This will be a more complex test, with consistency checks and so on.
     */
    public void testComplexStuff() {
        assertEquals("Hello World!", "Hello World!");
    }
}
