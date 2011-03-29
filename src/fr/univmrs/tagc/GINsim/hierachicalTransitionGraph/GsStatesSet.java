package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;


import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.SAXException;

import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

/* SUMMARY
 * 
 * **************** CONSTRUCTORS ************/	
/* **************** ADD STATE ************/	
/* **************** SIZE CONSISTENCY CHECK ************/	
/* **************** CONTAINS, UPDATESTATUS, REDUCE************/	
/* **************** STATES CONSTRUCTORS ************/	
/* **************** GETTERS AND SETTERS ************/	
/* **************** STATE TO LISTS ************/	
/* **************** TO STRINGS ************/	
/* **************** PARSING ************/	


/**
 * <p>A wrapper of OmddNode that adds the essentials methods to store states.</p>
 * 
 * <p>Each path terminating with a value > 0 indicates the state is present in the set.<br>
 * The value is used to indicates several status.</p>
 * 
 * <h3>Working with the size</h3>
 * <p>The value of the <b>size</b> attribute, that is the number of state inside the set, could be inconsistent.<br> 
 * When adding a state (or a merge several Omdds), there is no efficient way to know if the state is added or if it was already present.<br>
 * In this class, the size is always incremented when we try to add a new state. Therefore the value of the size could be inconsistent, more 
 * precisely be an overapproximation of the real value<br>
 * To cope with this problem, there is several methods to work with the <b>size</b> :
 * 	<ul>
 * 		<li><tt>public int isSizeConsistent()</tt> indicates if the size is consistent</li>
 * 		<li><tt>public int updateSize()</tt> count the current size, by exploring the whole diagram</li>
 * 		<li><tt>public int getSize()</tt> gives the size or INCONSISTENT_SIZE is the size is not consistent</li>
 * 		<li><tt>public int getSizeOrOverApproximation()</tt> gives the size no matter its consistency</li>
 * 		<li><tt>public int getSizeOrUpdate()</tt>gives the size and eventually update it before if needed</li>
 * 	</ul>
 * </p>

 * @author Duncan Berenguier
 */
public class GsStatesSet {

	public static final int INCONSISTENT_SIZE = -42;
	
	/**
	 * An array indicating such that childsCount[i] indicates the maxValue of the i-th gene
	 */
	private byte[] childsCount;
	
	/**
	 * Contain the count of states in the set
	 */
	private int size;

	/**
	 * Indicates if the count of states is consistent. Mainsly because merge_or could not add any state in the set if it is already present. 
	 */
	private boolean size_consistancy;

	
	/**
	 * The root of the diagram (OmddNode) 
	 */
	private OmddNode root;


/* **************** CONSTRUCTORS ************/	


	/**
	 * Initialize a new set of states
	 */
	public GsStatesSet(byte[] childsCount) {
		this.childsCount = childsCount;
		this.size = 0;
		this.size_consistancy = true;
	}

/* **************** ADD STATE ************/	
	
	/**
	 * Add <b>state</b> to the set with <b>status</b><br>
	 * If the state was already there, the old status remains.<br>
	 *  NB: use updateStatus to change the status.
	 * @param state
	 * @param newStatus
	 */
	public void addState(byte[] state, int newStatus) {
		OmddNode newState = stateFromArray(state, newStatus);
		if (root == null) {
			root = newState;
			size = 1;
			size_consistancy = true;
		}
		else  {
			root = root.merge(newState, OmddNode.OR);
			size += 1;//Introduce an overapproximation
			size_consistancy = false;
		}
	}

	/**
	 * Add <b>state</b> to the set with <b>status</b><br>
	 * If the state was already there, the old status remains.<br>
	 *  NB: use updateStatus to change the status.
	 * @param state
	 * @param newStatus
	 */
	public void addState(String state, int newStatus) {
		addState(byteArrayFromString(state), newStatus);
	}
	
	/**
	 * Add <b>states</b> to the set with status 1<br>
	 * If the states were already there, their old status remains.<br>
	 *  NB: use updateStatus to change the status.
	 * @param states, a <b>List&lt;byte[]&gt;</b> of states to add
	 * @param status their corresponding status
	 */
	public void addStates(List states) {
		if (root == null) {
			root = OmddNode.multi_or(states, childsCount);
			size = states.size();
			size_consistancy = true;
		} else {
			root = root.merge(OmddNode.multi_or(states, childsCount), OmddNode.OR);
			size += states.size();//Introduce an overapproximation
			size_consistancy = false;
		}
	}
	
	/**
	 * Merge this set with the stateSet of the slaveNode.
	 * @param slaveNodeStateSet
	 */
	public void merge(GsStatesSet slaveNodeStateSet) {
		if (this.root != null && slaveNodeStateSet.root != null) {
			this.root = this.root.merge(slaveNodeStateSet.root, OmddNode.OR);
			this.size += slaveNodeStateSet.getSizeOrOverApproximation();
			this.size_consistancy = false;
		}
	}
	
	
/* **************** SIZE CONSISTENCY CHECK ************/	
	
	/**
	 * Count the number of states in the Omdd and update the <b>size</b> accordingly
	 * @return an array indiced by the status and counting the number of state per status
	 */
	public int[] updateSize() {
		int[] counts = new int[10];
		size = 0;
		updateSize(root, -1, 1, counts);
		size_consistancy = true;
		return counts;
	}

	/**
	 * A simple recursive parse of the diagram, counting the states in the diagram (all of them in <b>size</b> and depending on their values in <b>counts</b>.
	 * 
	 * @param omdd the current omddNode to parse
	 * @param last_depth 
	 * @param coef
	 * @param counts
	 */
	private void updateSize(OmddNode omdd, int last_depth, int coef, int[] counts) {
		if (omdd.next == null) {
			if (omdd.value == 0) return;
	
			int s = 1;
			for (int i = last_depth; i < childsCount.length-1; i++) {
				s *= childsCount[i];
			}
			counts[omdd.value] += s*coef;
			size += s*coef;
			return;
		}
	
		for (int i = last_depth+1; i < omdd.level; i++) {
			coef *= childsCount[i];
		}
		
		for (int i = 0 ; i < omdd.next.length ; i++) {
			updateSize(omdd.next[i], omdd.level, coef, counts);
		}
	}

	/**
	 * Indicates if the size is in a consistent state, that is if it really reflect the number of state in the set, or only an overapproximation.
	 * @return true if size is equal to the exact count of state in the set.
	 */
	public boolean isSizeConsistent() {
		return size_consistancy;
	}
	

	/**
	 * Gives the count of states in the diagram or INCONSISTENT_SIZE if the size is in inconsistent state.
	 * 
	 * @return the count of states in the set or INCONSISTENT_SIZE
	 */
	public int getSize() {
		if (size_consistancy == true) return size;
		else return INCONSISTENT_SIZE;
	}	

	/**
	 * Gives the count of states in the diagram or its overapproximation if the size is in inconsistent state.
	 * 
	 * @return the count of states in the set
	 */
	public int getSizeOrOverApproximation() {
		return size;
	}	

	
	/**
	 * <p>Gives the count of states in the diagram.<br />
	 * If the size is in inconsistent state, then call <tt>updateSize()</tt> before.</p>
	 * 
	 * <p>Note that by calling this function, you will not be able to retrieve the count of state per status.</p>
	 * 
	 * @return the count of states in the set
	 */
	public int getSizeOrUpdate() {
		if (size_consistancy == true) return size;
		else {
			updateSize();
			return size;
		}
	}	

	

/* **************** CONTAINS, UPDATESTATUS, REDUCE************/	
	
	/**
	 * Test if the set contains <b>state</b> that is, if its status is not 0
	 * @param state
	 * @return true if the set contains <b>state</b>
	 */

	public boolean contains(byte[] state) {
		return root.testStatus(state) != 0;
	}
	
	
	/**
	 * Change the status associated of <b>state</b> in the set to </b>newStatus</b>
	 * @param state
	 * @param newStatus
	 * @return <ul>
	 * 		<li><b>true</b> if the status is changed or was already equal to newStatus.</li>
	 * 		<li><b>false</b> if the state wasn't in the set.</li>
	 * </ul>
	 */
	public boolean updateStatus(byte[] state, int newStatus) {
		int currentStatus = root.testStatus(state);
		if (currentStatus == newStatus) return true;
		if (currentStatus > 0) {
			root = root.merge(stateFromArray(state, 2), OmddNode.MAX);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Force the reduction (compaction) of the omdd.<br>
	 * This operation is expensive !
	 */
	public void reduce() {
		if (root != null) {
			root = root.reduce();			
		}
	}
	
	
/* **************** STATES CONSTRUCTORS ************/	
	

	/**
	 * Generate a new omdd corresponding to the <b>state</b> and with the status 1
	 * @param state the state to generate
	 * @return the omdd
	 */
	public OmddNode stateFromArray(byte[] state) {
		return stateFromArray(state, 1);
	}
	
	/**
	 * Generate a new omdd corresponding to the <b>state</b> and with the status <b>status</b>
	 * @param state the state to generate
	 * @param status the value to append at the leaf.
	 * @return the omdd
	 */
	public OmddNode stateFromArray(byte[] state, int status) {
		OmddNode child = OmddNode.TERMINALS[status];
		for (int level = state.length-1; level >=0 ; level--) {
			OmddNode omdd = new OmddNode();
			omdd.level = level;
			int nbrChilds = childsCount[level];
			omdd.next = new OmddNode[nbrChilds];
			for (int i = 0; i < nbrChilds; i++) {
				if (i == state[level]) {
					omdd.next[i] = child;
				}
				else omdd.next[i] = OmddNode.TERMINALS[0];
			}
			child = omdd;
		}
		return child;	
	}

	/**
	 * Generate a new omdd corresponding to the <b>state</b> and with the status 1
	 * @param state the state to generate
	 * @return the omdd
	 */
	public OmddNode stateFromString(String state) {
		return stateFromString(state, 1);
	}
	/**
	 * Generate a new omdd corresponding to the <b>state</b> and with the status 1>
	 * @param state the state to generate
	 * @return the omdd
	 */
	public OmddNode stateFromString(String state, int status) {
		OmddNode child = OmddNode.TERMINALS[status];
		
		for (int level = state.length()-1; level >=0 ; level--) {			
			OmddNode omdd = new OmddNode();
			omdd.level = level;
			int nbrChilds = childsCount[level];
			omdd.next = new OmddNode[nbrChilds];
			for (int i = 0; i < nbrChilds; i++) {
				if (i == Integer.parseInt(""+state.charAt(level))) omdd.next[i] = child;
				else omdd.next[i] = OmddNode.TERMINALS[0];
			}
			child = omdd;
		}
		return child;	
	}

	/**
	 * Transform a String into an array of byte
	 * @param state
	 * @return
	 */
	private byte[] byteArrayFromString(String state) {
		byte[] array = new byte[state.length()];
		for (int i = 0; i < state.length(); i++) {
			array[i] = (byte) Character.getNumericValue(state.charAt(i));
		}
		return array;
	}
	
/* **************** GETTERS AND SETTERS ************/	
	
	
	/**
	 * <p>Return An array indicating such that childsCount[i] indicates the maxValue of the i-th gene</p>
	 * 
	 * <p>Use :</p> <pre>
	 * 	int countChilds = childsCount[level];
	 * 	for (int i = 0; i < countChilds; i++) {
	 * 		....omdd.next[i]....
	 * </pre>
	 * @return the ChildsCount array
	 */
	public byte[] getChildsCount() {
		return childsCount;
	}


/* **************** STATE TO LISTS ************/	
	
	/**
	 * Initialize and fill a list with all the states in the omdd
	 * Each item of the returned list is a string representation using wildcard * (-1).
	 * Note the order in the list is relative to the omdd structure.
	 * @return a list made of all the states as schemata (using *)
	 */
	public List statesToList() {
		List v = new LinkedList();
		byte[] t = new byte[childsCount.length];
		statesToList(root, v, t, -1);	
		return v;
	}
	
	/**
	 * Fill a list with all the states in the omdd
	 * Each item of the returned list is a string representation using wildcard * (-1).
	 * Note the order in the list is relative to the omdd structure.
	 * @return a list made of all the states as schemata (using *)
	 */
	public void statesToList(List v) {
		byte[] t = new byte[childsCount.length];
		statesToList(root, v, t, -1);	
	}
	
	private void statesToList(OmddNode omdd, List v, byte[] t, int last_depth) {
        if (omdd.next == null) {
        	if (omdd.value == 0) return;
        	for (int i = last_depth+1; i < childsCount.length; i++) {
				t[i] = -1;
			}
			v.add(t.clone());
        	return;
        }
        
    	for (int i = last_depth+1; i < omdd.level; i++) {
			t[i] = -1;
		}
        for (int i = 0 ; i < omdd.next.length ; i++) {
	    	t[omdd.level] = (byte) i;
        	statesToList(omdd.next[i], v ,t, omdd.level);
        }

	}
	
	
	
	
/* **************** TO STRINGS ************/	

	/**
	 * Return a parenthesis based reprensetation of the omdd
	 */
	public StringBuffer write() {
		return root.write();
	}

	
	/**
	 * Return a String representation of the omdd
	 */
	
	public StringBuffer statesToString(boolean addValue) {
		StringBuffer res = new StringBuffer();
		int length = childsCount.length+(addValue?3:1);
		StringBuffer s = new StringBuffer(length);
		
		for (int i = 0; i < length; i++) {
			s.append('.');
		}
		statesToString(root, s, res, 0, childsCount.length, addValue);			
		return res;
	}
	private void statesToString(OmddNode omdd, StringBuffer s, StringBuffer res, int last_depth, int nbNodes, boolean addValue) {
        if (omdd.next == null) {
        	if (omdd.value == 0) return;
        	for (int i = last_depth+1; i < nbNodes; i++) {
        		s.setCharAt(i, '*');
			}
        	if (addValue) {
        		s.setCharAt(nbNodes, '-');
        		s.setCharAt(nbNodes+1, String.valueOf(omdd.value).charAt(0));
        		s.setCharAt(nbNodes+2, '\n');
        	} else {
        		s.setCharAt(nbNodes, '\n');
        	}
        	res.append(s);
        	return;
        }
        
    	for (int i = last_depth+1; i < omdd.level; i++) {
    		s.setCharAt(i, '*');
		}
        for (int i = 0 ; i < omdd.next.length ; i++) {
        	s.setCharAt(omdd.level, String.valueOf(i).charAt(0));
        	statesToString(omdd.next[i], s, res, omdd.level, nbNodes, addValue);
        }
        return;
	}
	
	/**
	 * Return a String representation of a state in the set (the first in the pile or the first reach in the omdd)
	 */
	public StringBuffer firstStatesToString() {
		StringBuffer s  = new StringBuffer(childsCount.length);
		firstStatesToString(root, s, 0);
		return s;
	}
	
	/**
	 * Return a String representation of a state in the set (the first in the pile or the first reach in the omdd)
	 * @param omdd
	 * @param s
	 * @param last_depth
	 * @return is used internally for the recursion
	 */
	private boolean firstStatesToString(OmddNode omdd, StringBuffer s, int last_depth) {
        if (omdd.next == null) {
        	if (omdd.value == 0) return false;
        	return true;
        }
        if (s.length() <= omdd.level) {
    		for (int j = s.length(); j <= omdd.level; j++) {
    			s.append('*');
			}
    	}
        for (int i = 0 ; i < omdd.next.length ; i++) {
        	s.setCharAt(omdd.level, String.valueOf(i).charAt(0));
        	boolean res = firstStatesToString(omdd.next[i], s, omdd.level);
        	if (res) return true;
        }
        return false;
	}

	
	
/* **************** PARSING ************/	
	public void parse(String parse) throws SAXException {
		try {
			root = OmddNode.read(parse, childsCount);
			reduce();
			updateSize();
		} catch (ParseException e) {
			throw new SAXException(e);
		}
		
	}

}
