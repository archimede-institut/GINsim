package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;

import fr.univmrs.tagc.common.TestTools;


public class TestMDDInRegulatoryGraph extends TestCase {
	File file = new File(TestTools.getTestDir(), "graph.ginml");

	GsRegulatoryGraph graph;

	public void testOMDD() {
		OmddNode[] t_omdd = graph.getAllTrees(true);
		System.out.println("as OMDD");
		for (int i=0 ; i<t_omdd.length ; i++) {
			System.out.println(i+": "+t_omdd[i]);
		}
		System.out.println();
	}
	
	public void testMultiMergeOMDD() {
	    byte[][] states = { {0,1,2}, {0,0,1}, {0,0,2}, {0,0,0} };
	    byte[] max = {1,1,2}; 
	    OmddNode node = OmddNode.multi_or(states, max);
	    
	    List l = new ArrayList();
	    l.add(node);
        states[0][0] = 1;
        states[2][0] = 1;
	    l.add(OmddNode.multi_or(states, max));
	    
	    node = OmddNode.multi_or(l).reduce();
	    
	    // path leading to 1:  0,0,* ; 0,1,2 ; 1,*,2
	    System.out.println(node);
	}
	
	public void testReduce() {
		OmddNode r = getFixtureForReduce();
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
	private OmddNode getFixtureForReduce() {
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
		OmddNode L0 = OmddNode.TERMINALS[0];
		OmddNode L2 = OmddNode.TERMINALS[2];
		OmddNode root = L0;
		for (int i=0 ; i<t_states.length ; i++) {
			OmddNode other = L2;
			for (int j=t_states[i].length-1 ; j>=0 ; j--) {
				if (t_states[i][j] == 0) {
					other = O(j,other,L0);
				} else if (t_states[i][j] == 1) {
					other = O(j,L0, other);
				}
			}
			root = root.merge(other,OmddNode.OR);
		}
		return root;
	}
	
	private OmddNode O(int level, OmddNode c0, OmddNode c1) {
		OmddNode o = new OmddNode();
		o.level = level;
		o.next = new OmddNode[2];
		o.next[0] = c0;
		o.next[1] = c1;
		return o;
	}

}
