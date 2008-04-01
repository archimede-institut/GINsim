package fr.univmrs.tagc.common.mdd;

import java.util.Vector;

import junit.framework.TestCase;


public abstract class TestMDD extends TestCase {

	static final int maxlevel = 70;
	static final int maxchildtest = 20;
	DecisionDiagramInfo ddi;

	// for the N Queens test
	int N = 3;
	
	MDDNode n_true;
	MDDNode n_false;
	MDDNode[] nextTrue;
	MDDNode[] nextFalse;


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
	}
	
	public void testNQueens() {
		n_true = ddi.getLeaf(1);
		n_false = ddi.getLeaf(0);
		ddi.reset();
		
		MDDNode queen = n_true;
		nextTrue = new MDDNode[] {n_false, n_true};
		nextFalse = new MDDNode[] {n_true, n_false};
		int[] t_xor = new int[N];
		// add constraints
		for (int i=0 ; i<N ; i++) {
			// one on each row
			int start = i*N;
			for (int j=0 ; j<N ; j++) {
				t_xor[j] = start+j;
			}
			queen = queen.merge(ddi, buildOr(t_xor), DecisionDiagramAction.ACTION_AND);

			// one on each col
			for (int j=0 ; j<N ; j++) {
				t_xor[j] = j*N+i;
			}
			queen = queen.merge(ddi, buildOr(t_xor), DecisionDiagramAction.ACTION_AND);
			
			// add constraints for each case
			for (int j=0 ; j<N ; j++) {
				queen = queen.merge(ddi, buildCst(i, j), DecisionDiagramAction.ACTION_AND);
			}
		}
	}

	private MDDNode buildCst(int r, int c) {
		MDDNode ret = ddi.getNewNode(N*r+c, nextTrue);
		// no other queen on the same row/col
		int start = N*r;
		for (int i=c+1 ; i<N ; i++) {
			ret = ret.merge(ddi, ddi.getNewNode(start+i, nextFalse), DecisionDiagramAction.ACTION_AND);
		}
		for (int i=r+1 ; i<N ; i++) {
			ret = ret.merge(ddi, ddi.getNewNode(N*i+c, nextFalse), DecisionDiagramAction.ACTION_AND);
		}
		
		// and for diagonals
		for (int i=r+1, j=c+1 ; i<N && j<N ; i++, j++) {
			ret = ret.merge(ddi, ddi.getNewNode(i*N+j, nextFalse), DecisionDiagramAction.ACTION_AND);
		}
		for (int i=r-1, j=c+1 ; i>=0 && j<N ; i--, j++) {
			ret = ret.merge(ddi, ddi.getNewNode(i*N+j, nextFalse), DecisionDiagramAction.ACTION_AND);
		}
		ret = ret.merge(ddi, ddi.getNewNode(N*r+c, nextFalse), DecisionDiagramAction.ACTION_OR);
		return ret;
	}

	private MDDNode buildXor(int[] t_xor) {
		MDDNode ret = n_true;
		for (int i=0 ; i<t_xor.length ; i++) {
			MDDNode row = n_true;
			for (int j=0 ; j<t_xor.length ; i++) {
				if (i == j) {
					row = row.merge(ddi, ddi.getNewNode(t_xor[j], nextTrue), DecisionDiagramAction.ACTION_AND);
				} else {
					row = row.merge(ddi, ddi.getNewNode(t_xor[j], nextFalse), DecisionDiagramAction.ACTION_AND);
				}
			}
			ret = ret.merge(ddi, row, DecisionDiagramAction.ACTION_OR);
		}
		return ret;
	}
	
	private MDDNode buildOr(int[] t_xor) {
		MDDNode ret = n_false;
		for (int i=0 ; i< t_xor.length ; i++) {
			ret = ret.merge(ddi, ddi.getNewNode(t_xor[i], nextTrue), DecisionDiagramAction.ACTION_OR);
		}
		return ret;
	}
}