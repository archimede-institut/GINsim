package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.TestFileUtils;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

/**
 * Test some MDD features.
 * This is not a real test case yet..
 */
public class TestMDDInRegulatoryGraph {
	File file = new File(TestFileUtils.getTestFileDir(), "graph.ginml");

	RegulatoryGraph graph;

	public void testOMDD() {
		OMDDNode[] t_omdd = graph.getAllTrees(true);
		System.out.println("as OMDD");
		for (int i=0 ; i<t_omdd.length ; i++) {
			System.out.println(i+": "+t_omdd[i]);
		}
		System.out.println();
	}
	
	public void testMultiMergeOMDD() {
	    byte[][] states = { {0,1,2}, {0,0,1}, {0,0,2}, {0,0,0} };
	    byte[] max = {1,1,2}; 
	    OMDDNode node = OMDDNode.multi_or(states, max);
	    
	    List l = new ArrayList();
	    l.add(node);
        states[0][0] = 1;
        states[2][0] = 1;
	    l.add(OMDDNode.multi_or(states, max));
	    
	    node = OMDDNode.multi_or(l).reduce();
	    
	    // path leading to 1:  0,0,* ; 0,1,2 ; 1,*,2
	    System.out.println(node);
	}
	
	public void testReduce() {
		OMDDNode r = getFixtureForReduce();
		System.out.println(r.getString(0));
		r.reduce();
		System.out.println(r.getString(0));
	}

	/**
	 * 		
	   0=0
		  1=0
		    2=0
		      3=0
		        4=0
		          5=0
		            6=0
		              7=1
		                9=0 ==> 2
		        4=1
		          5=0
		            6=1
		              7=0
		                8=0
		                  9=0 ==> 2

	 * @return
	 */
	private OMDDNode getFixtureForReduce() {
		int[][] t_states = {
				{0,0,0,0,0,0,0,1,-1,0},
				{0,0,0,0,1,0,1,0,0,0},
				{0,0,0,1,1,0,1,0,0,0},
				{0,1,0,0,1,0,1,0,0,0},
				{0,1,0,1,0,0,0,1,0,0},
				{0,1,0,1,1,0,1,0,0,0},
				{0,1,1,0,0,1,0,1,0,0},
				{0,1,1,0,1,0,1,0,0,0},
				{0,1,1,1,-1,0,1,0,0,0},
		};
		OMDDNode L0 = OMDDNode.TERMINALS[0];
		OMDDNode L2 = OMDDNode.TERMINALS[2];
		OMDDNode root = L0;
		for (int i=0 ; i<t_states.length ; i++) {
			OMDDNode other = L2;
			for (int j=t_states[i].length-1 ; j>=0 ; j--) {
				if (t_states[i][j] == 0) {
					other = O(j,other,L0);
				} else if (t_states[i][j] == 1) {
					other = O(j,L0, other);
				}
			}
			root = root.merge(other,OMDDNode.OR);
		}
		return root;
	}
	
	private OMDDNode O(int level, OMDDNode c0, OMDDNode c1) {
		OMDDNode o = new OMDDNode();
		o.level = level;
		o.next = new OMDDNode[2];
		o.next[0] = c0;
		o.next[1] = c1;
		return o;
	}

}
