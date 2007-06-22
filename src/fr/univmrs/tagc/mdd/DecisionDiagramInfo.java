package fr.univmrs.tagc.mdd;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * everything we need to know about a decision diagram: its order, the name of
 * its decision variables, and some data to help keeping it small.
 */
abstract public class DecisionDiagramInfo {

	private static final MDDNode NEGATIVE;
	private static final MDDNode[] TERMINALS;
	public static final int MAXTERM = 10;

	private static long initId = Long.MIN_VALUE;
	static {
		NEGATIVE = new MDDNode(-1, new Long(initId++));
		TERMINALS = new MDDNode[MAXTERM];
		for (int i=0 ; i<MAXTERM ; i++) {
			TERMINALS[i] = new MDDNode(i, null, new Long(initId++));
		}
	}

	public static final DecisionDiagramInfo getHashDDI(int maxlevel) {
		return new HashDDI(maxlevel);
	}
	public static final DecisionDiagramInfo getBalancedDDI(int maxlevel) {
		return new BalancedDDI(maxlevel);
	}

	long nextid = initId;

	public String getName(int order) {
		return "" + order;
	}

	/***
	 * @return the number of known nodes
	 */
	public int getNodeCount() {
		return (int)(nextid-initId);
	}

	/**
	 * reset known nodes
	 */
	public abstract void reset();

	public MDDNode getLeaf(int value) {
		if (value == -1) {
			return NEGATIVE;
		}
		if (value > -1 && value<MAXTERM) {
			return TERMINALS[value];
		}
		System.out.println("DEBUG: unknown terminal DD node");
		return null;
	}

	public abstract MDDNode getNewNode(int level, MDDNode[] next);
}


class HashDDI extends DecisionDiagramInfo {

	Map[] t_maps;
	Vector v_nodes = new Vector();


	HashDDI(int maxlevel) {
		t_maps = new Map[maxlevel+1];

	}

	public void reset() {
		for (int i=0 ; i<t_maps.length ; i++) {
			if (t_maps[i] != null) {
				t_maps[i].clear();
			}
		}
	}

	public MDDNode getNewNode(int level, MDDNode[] next) {
		if (next == null) {
			return getLeaf(level);
		}
		long keyTmp = next[0].key.longValue();
		StringBuffer key = new StringBuffer(""+keyTmp);
		boolean allEq = true;
		for (int i=1 ; i<next.length ; i++) {
			key.append(","+next[i].toString());
			if (allEq && keyTmp != next[i].key.longValue()) {
				allEq = false;
			}
		}
//		String key = ""+next[0]+","+next[1];
		if (allEq) {
			return next[0];
		}
		MDDNode node = null;
		if (t_maps[level] == null) {
			t_maps[level] = new HashMap();
		} else {
			node = (MDDNode)t_maps[level].get(key.toString());
		}
		if (node == null) {
			node = new MDDNode(level, next, new Long(nextid++));
			t_maps[level].put(key.toString(), node);
		}
		return node;
	}

}


class BalancedDDI extends DecisionDiagramInfo {

	MDDNode[] t_dd;

	BalancedDDI(int maxlevel) {
		t_dd = new MDDNode[maxlevel+1];
	}

	public MDDNode getNewNode(int level, MDDNode[] next) {
		if (next == null) {
			return getLeaf(level);
		}
		long l = next[0].key.longValue();
		boolean allEq = true;
		for (int i=1 ; i<next.length ; i++) {
			if (l != next[i].key.longValue()) {
				allEq = false;
				break;
			}
		}
		if (allEq) {
			return next[0];
		}
		if (t_dd[level] == null) {
			t_dd[level] = new MDDNode(level, next, new Long(nextid++));
			return t_dd[level];
		}
		return insert(level, t_dd[level], next);
	}

	private MDDNode insert(int level, MDDNode current, MDDNode[] next) {
		// TODO use AVL instead of basic binary tree (i.e. implement "re-balance" operation)
		for (int i=0 ; i<next.length ; i++) {
			if (i>current.next.length) {
				// This should not happen, right ?
				System.out.println("debug: different number of children for nodes of same order");
				return null;
			}
			long l1 = current.next[i].key.longValue();
			long l2 = next[i].key.longValue();
			if (l1 > l2) {
				if (current.p == null) {
					MDDNode node = new MDDNode(level, next, new Long(nextid++));
					current.p = node;
					return node;
				}
				return insert(level, current.p, next);
			}
			if (l1 < l2) {
				if (current.n == null) {
					MDDNode node = new MDDNode(level, next, new Long(nextid++));
					current.n = node;
					return node;
				}
				return insert(level, current.n, next);
			}
		}
		return current;
	}


	public void reset() {
	}

}