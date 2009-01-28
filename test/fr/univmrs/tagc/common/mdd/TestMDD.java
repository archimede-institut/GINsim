package fr.univmrs.tagc.common.mdd;

import java.util.Vector;

import junit.framework.TestCase;


public class TestMDD extends TestCase {

	static final int maxlevel = 70;
	static final int maxchildtest = 20;

	// for the N Queens test
	int N = 3;
	
	MDDNode n_true;
	MDDNode n_false;
	MDDNode[] nextTrue;
	MDDNode[] nextFalse;


	public void testConstruction() {
		int base, c;
		SimpleHashDDI ddi = new SimpleHashDDI(100);
		base = c = ddi.getNodeCount();
		assertEquals(0, base);

		SimpleNode[] next = new SimpleNode[] { ddi.leaf(0), ddi.leaf(1) };
		SimpleNode node = ddi.node(4, next);
		c++;

		assertEquals(c, ddi.getNodeCount());

		node = ddi.node(4, next);
		assertEquals(c, ddi.getNodeCount());

		next = new SimpleNode[] { ddi.leaf(0), ddi.leaf(0) };
		node = ddi.node(4, next);
		assertEquals(c, ddi.getNodeCount());

		next = new SimpleNode[] { ddi.leaf(1), ddi.leaf(0) };
		node = ddi.node(4, next);
		c++;
		assertEquals(c, ddi.getNodeCount());

		next = new SimpleNode[] { node, node };
		SimpleNode newnode = ddi.node(3, next);
		assertSame(node, newnode);
		assertEquals(c, ddi.getNodeCount());
	}

	public void testSimpleMerge() {
		SimpleHashDDI ddi = new SimpleHashDDI(100);
		SimpleNode n_true = ddi.leaf(1);
		SimpleNode n_false = ddi.leaf(0);

		SimpleNode node = ddi.node(3, new SimpleNode[] { n_true, n_false });
		assertNotSame(node, n_true);
		assertNotSame(node, n_false);

		assertSame(n_true, node.or(n_true));
		assertSame(node, node.or(n_false));
		assertSame(node, node.and(n_true));
		assertSame(n_false, node.and(n_false));
	}

	public void testPerfs() {

		Vector[] t_v = new Vector[maxlevel+1];
		SimpleHashDDI ddi = new SimpleHashDDI(100);

		SimpleNode n_true = ddi.leaf(1);
		SimpleNode n_false = ddi.leaf(0);
		long t = System.currentTimeMillis();
		ddi = new SimpleHashDDI(100);
		for (int i=maxlevel ; i>-1 ; i--) {
			t_v[i] = new Vector();

			SimpleNode[] next = new SimpleNode[] { n_true, n_false };
			t_v[i].add(ddi.node(i, next));

			next = new SimpleNode[] { n_false, n_true };
			t_v[i].add(ddi.node(i, next));

			for (int j=i+1 ; j<maxlevel+1 ; j++) {

				int m = Math.min(t_v[j].size(), maxchildtest);

				SimpleNode n1 = (SimpleNode)t_v[j].get(0);

				next = new SimpleNode[] { n1, n_true };
				t_v[i].add(ddi.node(i, next));

				for (int k=1 ; k<m ; k++) {
					SimpleNode n2 = (SimpleNode)t_v[j].get(k);
					next = new SimpleNode[] { n2, n_false };
					t_v[i].add(ddi.node(i, next));

					next = new SimpleNode[] { n2, n_true };
					t_v[i].add(ddi.node(i, next));

					next = new SimpleNode[] { n2, n1 };
					t_v[i].add(ddi.node(i, next));
				}
			}
		}
	}
}