package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;


import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;


/**
 * <p>A wrapper of OmddNode that adds the essentials methods to store states.</p>
 * 
 * <p>Each path terminating with a value > 0 indicates the state is present in the set.<br>
 * The value is used to indicates several status.</p>
 * 
 * @author Duncan Berenguier
 */
public class GsStatesSet {

	/**
	 * An array indicating such that childsCount[i] indicates the maxValue of the i-th gene
	 */
	private byte[] childsCount;
	
	/**
	 * Contain the count of states in the set
	 */
	private int size;
	
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
	}

/* **************** ADD STATE ************/	
	
	/**
	 * Add <b>state</b> to the set with <b>status</b><br>
	 * If the state was already there, the old status remains.<br>
	 *  NB: use updateStatus to change the status.
	 * @param state
	 * @param status
	 */
	public void addState(byte[] state, int status) {
		OmddNode newState = stateFromArray(state, status);
		if (root == null) root = newState;
		else root = root.merge(newState, OmddNode.OR);
	}

	/**
	 * Add <b>state</b> to the set with <b>status</b><br>
	 * If the state was already there, the old status remains.<br>
	 *  NB: use updateStatus to change the status.
	 * @param state
	 * @param status
	 */
	public void addState(String state, int status) {
		OmddNode newState = stateFromString(state, status);
		if (root == null) root = newState;
		else root = root.merge(newState, OmddNode.OR);
	}

	
/* **************** STATUS RELATED METHODS************/	
	
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
	public boolean updateStatus(byte[] state, byte newStatus) {
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


	/**
	 * @return the count of states in the set
	 */
	public int getSize() {
		return size;
	}	
}
