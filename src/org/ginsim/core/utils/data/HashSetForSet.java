package org.ginsim.core.utils.data;

import java.util.HashSet;


/**
 * 
 * A regular HashSet's hashcode method depends on its content
 * Therefore, if the content of a set change, its hashcode change.
 * 
 * This class bring a simple solution to build a set of sets.
 *
 */
public class HashSetForSet extends HashSet {
	private static final long serialVersionUID = 3304535120427566273L;
	private static int staticmyid = 1;
	private int myid;
	
	public HashSetForSet() {
		this(16);
	}
	public HashSetForSet(int initialCapacity) {
		super(initialCapacity);
		myid = staticmyid++;
	}
	
	public int hashCode() {
		return myid;
	}
	
	public boolean equals(Object o) {
		if (o instanceof HashSetForSet) return this.myid == ((HashSetForSet)o).myid;
		return false;
	}
}
