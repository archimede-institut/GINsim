package fr.univmrs.tagc.mdd;

import java.util.Vector;

import junit.framework.TestCase;


public abstract class TestMDD extends TestCase {

	static final int maxlevel = 20;
	static final int maxchildtest = 20;
	DecisionDiagramInfo ddi;

	public void testConstruction() {
		ddi.reset();
		int base, c;
		base = c = ddi.getNodeCount();
		assertEquals(0, base);

		MDDNode[] next = new MDDNode[] { ddi.getLeaf(0), ddi.getLeaf(1) };
		MDDNode node = ddi.getNewNode(4, next);
		c++;

		assertEquals(c, ddi.getNodeCount());

		node = ddi.getNewNode(4, next);
		assertEquals(c, ddi.getNodeCount());

		next = new MDDNode[] { ddi.getLeaf(0), ddi.getLeaf(0) };
		node = ddi.getNewNode(4, next);
		assertEquals(c, ddi.getNodeCount());

		next = new MDDNode[] { ddi.getLeaf(1), ddi.getLeaf(0) };
		node = ddi.getNewNode(4, next);
		c++;
		assertEquals(c, ddi.getNodeCount());

		next = new MDDNode[] { node, node };
		MDDNode newnode = ddi.getNewNode(3, next);
		assertSame(node, newnode);
		assertEquals(c, ddi.getNodeCount());
	}

	public void testSimpleMerge() {
		MDDNode n_true = ddi.getLeaf(1);
		MDDNode n_false = ddi.getLeaf(0);

		MDDNode node = ddi.getNewNode(3, new MDDNode[] { n_true, n_false });
		assertNotSame(node, n_true);
		assertNotSame(node, n_false);

		assertSame(n_true, node.merge(ddi, n_true, DecisionDiagramAction.ACTION_OR));
		assertSame(node, node.merge(ddi, n_false, DecisionDiagramAction.ACTION_OR));
		assertSame(node, node.merge(ddi, n_true, DecisionDiagramAction.ACTION_AND));
		assertSame(n_false, node.merge(ddi, n_false, DecisionDiagramAction.ACTION_AND));
	}

	public void testPerfs() {

		Vector[] t_v = new Vector[maxlevel+1];

		MDDNode n_true = ddi.getLeaf(1);
		MDDNode n_false = ddi.getLeaf(0);
		long t = System.currentTimeMillis();
		System.out.println("start");
		ddi.reset();
		for (int i=maxlevel ; i>-1 ; i--) {
			t_v[i] = new Vector();

			MDDNode[] next = new MDDNode[] { n_true, n_false };
			t_v[i].add(ddi.getNewNode(i, next));

			next = new MDDNode[] { n_false, n_true };
			t_v[i].add(ddi.getNewNode(i, next));

			for (int j=i+1 ; j<maxlevel+1 ; j++) {

				int m = Math.min(t_v[j].size(), maxchildtest);

				MDDNode n1 = (MDDNode)t_v[j].get(0);

				next = new MDDNode[] { n1, n_true };
				t_v[i].add(ddi.getNewNode(i, next));

				for (int k=1 ; k<m ; k++) {
					MDDNode n2 = (MDDNode)t_v[j].get(k);
					next = new MDDNode[] { n2, n_false };
					t_v[i].add(ddi.getNewNode(i, next));

					next = new MDDNode[] { n2, n_true };
					t_v[i].add(ddi.getNewNode(i, next));

					next = new MDDNode[] { n2, n1 };
					t_v[i].add(ddi.getNewNode(i, next));
				}
			}
		}
		System.out.println("done: "+(System.currentTimeMillis()-t));
		System.out.println("result: "+ddi.getNodeCount());
	}
}