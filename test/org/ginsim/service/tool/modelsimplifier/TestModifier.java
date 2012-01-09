package org.ginsim.service.tool.modelsimplifier;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifier;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfig;
import org.junit.Assert;
import org.junit.Test;


public class TestModifier {

	RegulatoryGraph graph = GraphManager.getInstance().getNewGraph();
	ModelSimplifierConfig cfg = new ModelSimplifierConfig();
	ModelSimplifier simplifier = new ModelSimplifier(graph, cfg, null, true);

	/**
	 * basic test of model simplification, without funny stuff
	 * @throws GsException 
	 */
	@Test
    public void testBasicModification() throws GsException {
    	
    	/* deleted node (level 3):
    	 *   !A & D
    	 */
    	OMDDNode tmp = new OMDDNode();
    	tmp.level = 4;
    	tmp.next = new OMDDNode[2];
    	tmp.next[0] = OMDDNode.TERMINALS[0];
    	tmp.next[1] = OMDDNode.TERMINALS[1];
    	
    	OMDDNode deleted = new OMDDNode();
    	deleted.level = 1;
    	deleted.next = new OMDDNode[2];
    	deleted.next[0] = tmp;
    	deleted.next[1] = OMDDNode.TERMINALS[0];

    	/* original node:
    	 *   (A & !C) | B | C 
    	 */
    	tmp = new OMDDNode();
    	tmp.level = 3;
    	tmp.next = new OMDDNode[2];
    	tmp.next[0] = OMDDNode.TERMINALS[0];
    	tmp.next[1] = OMDDNode.TERMINALS[1];
    	
    	OMDDNode tmp2 = new OMDDNode();
    	tmp2.level = 2;
    	tmp2.next = new OMDDNode[2];
    	tmp2.next[0] = tmp;
    	tmp2.next[1] = OMDDNode.TERMINALS[1];

    	tmp = new OMDDNode();
    	tmp.level = 3;
    	tmp.next = new OMDDNode[2];
    	tmp.next[0] = OMDDNode.TERMINALS[1];
    	tmp.next[1] = OMDDNode.TERMINALS[0];

    	OMDDNode ori = new OMDDNode();
    	ori.level = 1;
    	ori.next = new OMDDNode[2];
    	ori.next[0] = tmp2;
    	ori.next[1] = tmp;

    	ModelSimplifier simplifier = new ModelSimplifier(graph, cfg, null, true);
    	OMDDNode result = simplifier.remove(ori, deleted, 3);
    	
    	/*  expected result:
    	 *    A | B | D
    	 */
        Assert.assertEquals(result.toString(), "((N[1]=0 && ((N[2]=0 && ((N[4]=0 && 0) ; (N[4]=1 && 1))) ; (N[2]=1 && 1))) ; (N[1]=1 && 1))");
    }
	
	@Test
    public void testUnNeededModification() throws GsException {
    	
    	/* deleted node (level 3):
    	 *   !A & D
    	 */
    	OMDDNode tmp = new OMDDNode();
    	tmp.level = 4;
    	tmp.next = new OMDDNode[2];
    	tmp.next[0] = OMDDNode.TERMINALS[0];
    	tmp.next[1] = OMDDNode.TERMINALS[1];
    	
    	OMDDNode deleted = new OMDDNode();
    	deleted.level = 1;
    	deleted.next = new OMDDNode[2];
    	deleted.next[0] = tmp;
    	deleted.next[1] = OMDDNode.TERMINALS[0];

    	/* original node:
    	 *   A | B 
    	 */
    	tmp = new OMDDNode();
    	tmp.level = 2;
    	tmp.next = new OMDDNode[2];
    	tmp.next[0] = OMDDNode.TERMINALS[0];
    	tmp.next[1] = OMDDNode.TERMINALS[1];
    	
    	OMDDNode ori = new OMDDNode();
    	ori.level = 1;
    	ori.next = new OMDDNode[2];
    	ori.next[0] = tmp;
    	ori.next[1] = OMDDNode.TERMINALS[1];

    	/*  expected result:
    	 *    A | B
    	 */
    	OMDDNode result = simplifier.remove(ori, deleted, 3);
        Assert.assertEquals(result.toString(), "((N[1]=0 && ((N[2]=0 && 0) ; (N[2]=1 && 1))) ; (N[1]=1 && 1))");
        
        // some other trivial tests:
        tmp.level = 4;
    	result = simplifier.remove(tmp, deleted, 3);
    	Assert.assertEquals(result.toString(), "((N[4]=0 && 0) ; (N[4]=1 && 1))");
    	result = simplifier.remove(ori, deleted, 3);
    	Assert.assertEquals(result.toString(), "((N[1]=0 && ((N[4]=0 && 0) ; (N[4]=1 && 1))) ; (N[1]=1 && 1))");
        
    }
    
    /**
     * This will be a more complex test, with consistency checks and so on.
     */
	@Test
    public void testComplexStuff() throws GsException {
    	// can we remove a regulator with an auto-regulation if it is not functional ?
    	OMDDNode tmp = new OMDDNode();
    	tmp.level = 2;
    	tmp.next = new OMDDNode[2];
    	tmp.next[0] = OMDDNode.TERMINALS[0];
    	tmp.next[1] = OMDDNode.TERMINALS[1];
    	OMDDNode deleted = new OMDDNode();
    	deleted.level = 1;
    	deleted.next = new OMDDNode[2];
    	deleted.next[0] = tmp;
    	deleted.next[1] = OMDDNode.TERMINALS[1];
    	
    	tmp = new OMDDNode();
    	tmp.level = 2;
    	tmp.next = new OMDDNode[2];
    	tmp.next[0] = OMDDNode.TERMINALS[0];
    	tmp.next[1] = OMDDNode.TERMINALS[1];
    	OMDDNode target = new OMDDNode();
    	target.level = 1;
    	target.next = new OMDDNode[2];
    	target.next[0] = OMDDNode.TERMINALS[0];
    	target.next[1] = tmp;
    	
    	OMDDNode result = simplifier.remove(target, deleted, 2);
    	Assert.assertEquals(result.level, 1);
    	Assert.assertEquals(result.next[0], OMDDNode.TERMINALS[0]);
    	Assert.assertEquals(result.next[1], OMDDNode.TERMINALS[1]);
    }
}
