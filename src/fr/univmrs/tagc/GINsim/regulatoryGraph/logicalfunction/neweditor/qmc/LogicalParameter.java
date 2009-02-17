package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor.qmc;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Enumeration;

public class LogicalParameter {
	private int xBitSet, boolBitSet;
	private int size, np;
	private boolean premier;
	private BitSet id;
	private Hashtable bitToInter;

	public LogicalParameter(int i, int np, Hashtable inter, String p) {
		super();
		size = inter.size();
		xBitSet = 0;
		boolBitSet = 0;
		this.np = np;
		id = new BitSet(np);
		id.clear();
		String[] terms = p.split("  *");
		id.set(i);
		if (!p.equals("basal value"))
			for (int k = 0; k < terms.length; k++)
				if (inter.get(terms[k]) != null)
					boolBitSet |= (int)Math.pow(2, ((Integer)inter.get(terms[k])).intValue());
		String t;
		bitToInter = new Hashtable();
		for (Enumeration enu = inter.keys(); enu.hasMoreElements(); ) {
			t = (String)enu.nextElement();
			bitToInter.put(inter.get(t), t);
		}
		premier = false;
	}
	public void setId(int i) {
		id.clear();
		id.set(i);
	}
	private LogicalParameter(int s, int xb, int bb, BitSet id, int np, Hashtable b2i) {
		super();
		size = s;
		xBitSet = xb;
		boolBitSet = bb;
		premier = false;
		this.id = id;
		this.np = np;
		bitToInter = b2i;
	}
	public String getStringParameter(boolean cnf) {
		String s = "";
		for (int i = 0; i < size; i++)
			if (!getXBit(i))
				s += (cnf ? " | " : " & ") + (getBoolBit(i) ? "" : "!") + bitToInter.get(new Integer(i));
		s = s.substring(3);
		if (s.indexOf((cnf ? "|" : "&")) > 0) s = "(" + s + ")";
		return s;
	}
	public int getCardinality() {
		return Integer.bitCount(boolBitSet);
	}
	public void setPremier() {
		premier = true;
	}
	public boolean isPremier() {
		return premier;
	}
	public boolean getBoolBit(int i) {
		return ((boolBitSet & (int)Math.pow(2, i)) != 0);
	}
	public boolean getXBit(int i) {
		return ((xBitSet & (int)Math.pow(2, i)) != 0);
	}
	public int getXBitSet() {
		return xBitSet;
	}
	public int getBoolBitSet() {
		return boolBitSet;
	}
	public BitSet getId() {
		return id;
	}
	public LogicalParameter compareTo(LogicalParameter lp) {
		LogicalParameter rp = null;
		int p, xb, bb;
		BitSet b;

		if (getXBitSet() == lp.getXBitSet()) {
			p = getBoolBitSet() ^ lp.getBoolBitSet();
			if (Integer.bitCount(p) == 1) {
				xb = xBitSet | Integer.rotateLeft(1, Integer.numberOfTrailingZeros(p));
				bb = boolBitSet & lp.getBoolBitSet();
				b = (BitSet)id.clone();
				b.or(lp.getId());
				rp = new LogicalParameter(size, xb, bb, b, np, bitToInter);
			}
		}
		return rp;
	}
	public String getString() {
		StringBuffer s = new StringBuffer("");
		for (int i = 0; i < size; i++)
			if (getXBit(i))
				s.append("X\t");
			else
				s.append(getBoolBit(i) ? "1\t" : "0\t");
		s.append(id.get(0) ? "1" : "0");
		for (int i = 1; i < np; i++) s.append(id.get(i) ? "1" : "0");
		s.append("\t" + boolBitSet + "\t" + xBitSet);
		return s.toString();
	}
	public void invert() {
		boolBitSet = (int)((long)(Math.pow(2, size) - 1) ^ boolBitSet);
	}
	public boolean equals(Object o) {
		LogicalParameter lp = (LogicalParameter)o;
		boolean b = (xBitSet == lp.getXBitSet());
		int b1, b2, nbs;

		if (b) {
			nbs = 0xFFFFFFFF ^ xBitSet;
			b1 = boolBitSet & nbs;
			b2 = lp.getBoolBitSet() & nbs;
			b = (b1 == b2);
		}
		return b;
	}
}
