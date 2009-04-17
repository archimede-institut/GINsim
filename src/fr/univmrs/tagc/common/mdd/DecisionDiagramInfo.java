package fr.univmrs.tagc.common.mdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * everything we need to know about a decision diagram: its order, the name of
 * its decision variables, and some data to help keeping it small.
 */
abstract public class DecisionDiagramInfo {

	private static final MDDLeaf NEGATIVE;
	private static final MDDLeaf[] TERMINALS;
	public static final int MAXTERM = 10;

	private static long initId = Long.MIN_VALUE;
	static {
		NEGATIVE = new MDDLeaf(-1, new Long(initId++));
		TERMINALS = new MDDLeaf[MAXTERM];
		for (int i=0 ; i<MAXTERM ; i++) {
			TERMINALS[i] = new MDDLeaf(i, new Long(initId++));
		}
	}

	public static final DecisionDiagramInfo getHashDDI(int maxlevel) {
		return new HashDDI(maxlevel);
	}
	public static final DecisionDiagramInfo getBalancedDDI(int maxlevel) {
		return new BalancedDDI(maxlevel);
	}

	long nextid = initId;
	private final MDDVarInfo[] t_vinfo;
	protected DecisionDiagramInfo(Vector v_vars) {
		t_vinfo = new MDDVarInfo[v_vars.size()];
		for (int i=0 ; i<t_vinfo.length ; i++) {
			t_vinfo[i] = new MDDVarInfo(i);
		}
	}

	protected DecisionDiagramInfo(int maxlevel) {
		t_vinfo = new MDDVarInfo[maxlevel];
		for (int i=0 ; i<t_vinfo.length ; i++) {
			t_vinfo[i] = new MDDVarInfo(i);
		}
	}

	protected DecisionDiagramInfo(MDDVarInfo[] t_vinfo) {
		this.t_vinfo = t_vinfo;
	}

	public MDDVarInfo getVarInfo(int order) {
		return t_vinfo[order];
	}

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
		super(maxlevel+1);
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
			node = new MDDVarNode(getVarInfo(level), next, new Long(nextid++));
			t_maps[level].put(key.toString(), node);
		}
		return node;
	}

}


class BalancedDDI extends DecisionDiagramInfo {

	MDDVarNode[] t_dd;

	BalancedDDI(int maxlevel) {
		super(maxlevel+1);
		t_dd = new MDDVarNode[maxlevel+1];
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
			t_dd[level] = new MDDVarNode(getVarInfo(level), next, new Long(nextid++));
			return t_dd[level];
		}
		return insert(level, t_dd[level], next);
	}

	private MDDNode insert(int level, MDDVarNode current, MDDNode[] next) {
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
					MDDVarNode node = new MDDVarNode(getVarInfo(level), next, new Long(nextid++));
					current.p = node;
					return node;
				}
				return insert(level, current.p, next);
			}
			if (l1 < l2) {
				if (current.n == null) {
					MDDVarNode node = new MDDVarNode(getVarInfo(level), next, new Long(nextid++));
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

class SimpleHashDDI {
	public static final int MAXTERM = 10;

	Map m_nodes = new HashMap();
	List nodes = new ArrayList();
	int size = 0;

	SimpleHashDDI(int maxlevel) {
		for (byte i=0 ; i<MAXTERM ; i++) {
			nodes.add(new int[] {-1,i});
		}
		nodes.add(new int[] {-1,-1});
	}

	public SimpleNode node(int level, SimpleNode[] next) {
		int[] key = new int[next.length+1];
		key[0] = level;
		for (byte i=0 ; i<next.length ; i++) {
			key[i+1] = next[i].idx;
		}
		return new SimpleNode(this, node(key));
	}
	public int node(int level, int[] next) {
		int[] key = new int[next.length+1];
		key[0] = level;
		for (byte i=0 ; i<next.length ; i++) {
			key[i+1] = next[i];
		}
		return node(key);
	}
	public int node(int[] key) {
		int idx;
		try {
			idx = ((Integer)m_nodes.get(key)).intValue();
			System.out.println("reuse");
		} catch (Exception e) {
			// create and add the node
			idx = nodes.size();
			m_nodes.put(key, new Integer(idx));
			nodes.add(key);
			size++;
			System.out.println("create: "+key + " -->" + m_nodes.get(key));
		}
		return idx;
	}
	public SimpleNode leaf(int value) {
		return new SimpleNode(this,leaf_idx(value));
	}
	public int leaf_idx(int value) {
		if (value == -1) {
			return MAXTERM;
		}
		if (value > -1 && value<MAXTERM) {
			return value;
		}
		System.out.println("DEBUG: unknown terminal DD node");
		return -1;
	}
	
	protected int do_and(int node1, int node2) {
		if (node1 <= MAXTERM) {
			switch (node1) {
			case 0:
				return node1;
			default:
				return node2;
			}
		}
		if (node2 <= MAXTERM) {
			switch (node2) {
			case 0:
				return node2;
			default:
				return node1;
			}
		}
		int[] t1 = (int[])nodes.get(node1);
		int[] t2 = (int[])nodes.get(node2);
		if (t1[0] == t2[0]) {
			int[] key = new int[t1.length];
			key[0] = t1[0];
			for (byte i=0 ; i<key.length ; i++) {
				key[i] = do_and(t1[i], t2[i]);
			}
			return node(key);
		}
		if (t1[0] < t2[0]) {
			int[] tmp = t1;
			t1 = t2;
			t2 = tmp;
		}
		int[] key = new int[t1.length];
		key[0] = t1[0];
		for (byte i=0 ; i<key.length ; i++) {
			key[i] = do_and(t1[i], node2);
		}
		return node(key);
	}
	protected int do_or(int node1, int node2) {
		if (node1 <= MAXTERM) {
			switch (node1) {
			case 0:
				return node2;
			default:
				return node1;
			}
		}
		if (node2 <= MAXTERM) {
			switch (node2) {
			case 0:
				return node1;
			default:
				return node2;
			}
		}
		int[] t1 = (int[])nodes.get(node1);
		int[] t2 = (int[])nodes.get(node2);
		if (t1[0] == t2[0]) {
			int[] key = new int[t1.length];
			key[0] = t1[0];
			for (byte i=0 ; i<key.length ; i++) {
				key[i] = do_or(t1[i], t2[i]);
			}
			return node(key);
		}
		if (t1[0] < t2[0]) {
			int[] tmp = t1;
			t1 = t2;
			t2 = tmp;
		}
		int[] key = new int[t1.length];
		key[0] = t1[0];
		for (byte i=0 ; i<key.length ; i++) {
			key[i] = do_or(t1[i], node2);
		}
		return node(key);
	}
	protected int do_not(int node) {
		if (node <= MAXTERM) {
			switch (node) {
			case 0:
				return 1;
			default:
				return 0;
			}
		}
		int[] t1 = (int[])nodes.get(node);
		int[] key = new int[t1.length];
		key[0] = t1[0];
		for (byte i=0 ; i<key.length ; i++) {
			key[i] = do_not(t1[i]);
		}
		return node(key);
	}

	public int getNodeCount() {
		return size;
	}
}

class SimpleNode {
	int idx;
	SimpleHashDDI ddi;
	
	protected SimpleNode(SimpleHashDDI ddi, int idx) {
		this.ddi = ddi;
		this.idx = idx;
	}

	public SimpleNode or(SimpleNode other) {
		return new SimpleNode(ddi, ddi.do_or(idx, other.idx));
	}
	public SimpleNode and(SimpleNode other) {
		return new SimpleNode(ddi, ddi.do_and(idx, other.idx));
	}
}