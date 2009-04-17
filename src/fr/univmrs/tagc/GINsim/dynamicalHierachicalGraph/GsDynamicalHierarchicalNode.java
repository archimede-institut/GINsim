package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fr.univmrs.tagc.GINsim.reg2dyn.DynamicalHierarchicalSimulationHelper;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

public class GsDynamicalHierarchicalNode {
		
	public static final int TYPE_TRANSIENT_COMPONENT = 0;
	public static final int TYPE_CYCLE = 10;
	public static final int TYPE_TERMINAL_CYCLE = 11;
	public static final int TYPE_STABLE_STATE = 20;
	
	public static final String TYPE_TRANSIENT_COMPONENT_STRING = "transientComponent";
	public static final String TYPE_TERMINAL_CYCLE_STRING = "terminalCycle";
	public static final String TYPE_CYCLE_STRING = "cycle";
	public static final String TYPE_STABLE_STATE_STRING = "stableState";
	
	/**
	 * The root of the decision diagram storing the states.
	 */
	public OmddNode root;
	
	/**
	 * When merge of two node append, one become an self of the other
	 */
	private GsDynamicalHierarchicalNode self;
	
	/**
	 * The type (transient, terminal cycle or stable state) of the component.
	 */
	private int type = TYPE_TRANSIENT_COMPONENT;
	
	private List statePile = null;
	
	private int size = 0;
	
	/**
	 * A new node with a certain initial state.
	 * @param g a DynamicalHierarchicalGraph
	 * @param state an initial state to add in the graph
	 */
	public GsDynamicalHierarchicalNode(byte[] state, byte[]childsCount) {
		self = this;
		root = stateFromArray(state, childsCount);
		this.size = 1;
	}

	/**
	 * A new node.
	 * @param g a DynamicalHierarchicalGraph
	 */
	public GsDynamicalHierarchicalNode() {
		self = this;
		root = OmddNode.TERMINALS[0];
	}

	/**
	 * A new node from a string written with the stateToString() method
	 * @param g a DynamicalHierarchicalGraph
	 * @param parse the string from stateToString()
	 * @param type a string representation of the type
	 * 
	 */
	public GsDynamicalHierarchicalNode(String parse, String type, byte[] childsCount) {
		this(parse, childsCount);
		setTypeFromString(type);
	}
	
	/**
	 * A new node from a string written with the stateToString() method
	 * @param g a DynamicalHierarchicalGraph
	 * @param parse the string
	 */
	public GsDynamicalHierarchicalNode(String parse, byte[] childsCount) {
		self = this;
		root = OmddNode.TERMINALS[0];
		parse(parse, childsCount);
	}

	/**
	 * Perform a mergeMultiple on all the omdd in the pile and the root.
	 */
	public void addPileToOmdd() {
		if (self.statePile == null) return;
		self.size += self.statePile.size();
		self.root = self.root.mergeMultiple(statePile, OmddNode.OR);
		self.statePile = null;
	}
	
	/**
	 * Generate a new omdd for a given state (a string) and add it to the pile.
	 * Note the new state omdd is not added to the GsDynamicalHierarchicalNode omdd aiming to add several omdd at the same time.
	 * 
	 * @see addPileToOmdd()
	 * 
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
)	 */
	public void addStateToThePile(String state, byte[] childsCount) {
		if (self.statePile == null) self.statePile = new LinkedList();
		self.statePile.add(self.stateFromString(state, childsCount));
	}
	
	/**
	 * Generate a new omdd for a given state and add it to the pile.
	 * Note the new state omdd is not added to the GsDynamicalHierarchicalNode omdd aiming to add several omdd at the same time.
	 * 
	 * @see addPileToOmdd()
	 * 
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
)	 */
	public void addStateToThePile(byte[] state, byte[] childsCount) {
		if (self.statePile == null) self.statePile = new LinkedList();
		self.statePile.add(self.stateFromArray(state, childsCount));
	}
	
	
	/**
	 * Generate a new omdd for a given state (a byte) and add it to the omdd.
	 *  
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
)	 */
	public void addState(String state, byte[] childsCount) {
		self.root = self.root.merge(self.stateFromString(state, childsCount), OmddNode.OR);		
		self.size++;
	}
	
	/**
	 * Generate a new omdd for a given state (a byte) and add it to the omdd.
	 *  
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
)	 */
	public void addState(byte[] state, byte[] childsCount) {
		self.root = self.root.merge(self.stateFromArray(state, childsCount), OmddNode.OR);		
		self.size++;
	}


	/**
	 * Generate a new omdd for a given state and return it
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
	 * @return the omdd
	 */
	public OmddNode stateFromArray(byte[] state, byte[] childsCount) {
		OmddNode child = OmddNode.TERMINALS[1];
		
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
	 * Generate a new omdd for a given state and return it
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
	 * @return the omdd
	 */
	public OmddNode stateFromString(String state, byte[] childsCount) {
		OmddNode child = OmddNode.TERMINALS[1];
		
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
	 * Indicates if the GsDynamicalHierarchicalNode contains a state
	 * @param state
	 * @return if it contains the state
	 */
	public boolean contains(byte [] state) {
		return self.root.testStatus(state) != 0;
	}
	
	/**
	 * Reduce the omdd
	 */
	public void reduce() {
		self.root.reduce();
	}

	public String toString() {
		return "#"+self.size;
	}
	public String toString(int nbNodes) {
		if (self.size == 1) {
			StringBuffer s = new StringBuffer();
			byte[] t = (byte[]) (self.statesToList(nbNodes)).get(0);
			for (int i = 0; i < t.length; i++) {
				s.append(String.valueOf(t[i]).charAt(0));
			}
			return s.toString();
		}
		return "#"+self.size;
	}
	public String getId() {
		return toString();
	}
	
	/**
	 * A new node from a string written with the stateToString() method
	 * @param parse
	 */
	public void parse(String parse, byte[] childsCount) {
		String[] schemas = parse.split("\n");
		for (int i = 0; i < schemas.length; i++) {
			self.addStateToThePile(schemas[i], childsCount);
		}
		self.addPileToOmdd();		
	}
		
	public String statesToString(int nbNodes) {
		StringBuffer s = new StringBuffer();
		return self.statesToString(self.root, s, nbNodes);
	}
		
	private String statesToString(OmddNode omdd, StringBuffer s, int nbNodes) { //FIXME : test before use
        if (omdd.next == null) {
        	for (int i = omdd.level; i < nbNodes-1; i++) {
				s.append("*");
			}
        	s.append("*\n");
        	String res = s.toString();
        	s.delete(omdd.level, s.length()-1);
        	return res;
        }
        
        s.append(omdd.level);
        for (int i = 0 ; i < omdd.next.length ; i++) {
        	statesToString(omdd.next[i], s, nbNodes);
        }

    	String res = s.toString();
    	s.deleteCharAt(s.length()-1);
    	return res;
	}
	
	/**
	 * Generate a list of all the states in the node
	 * Each item of the returned list is a string representation using wildcard *.
	 * Note the order in the list is relative to the omdd structure.
	 * @return
	 */
	public List statesToList(int nbNodes) {
		List v = new LinkedList();
		byte[] t = new byte[nbNodes];
		self.statesToList(root, v, t, 0, nbNodes);
		return v;
	}
	
	private void statesToList(OmddNode omdd, List v, byte[] t, int last_depth, int nbNodes) { //FIXME : test before use
        if (omdd.next == null) {
        	if (omdd.value == 0) return;
        	for (int i = last_depth+1; i < nbNodes; i++) {
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
        	statesToList(omdd.next[i], v ,t, omdd.level, nbNodes);
        }

	}

	
	/**
	 * Return a string representation for the type of this node.
	 * @return
	 */
	public String typeToString() {
		return typeToString(self.type);
	}
	/**
	 * Return a string representation for a given type.
	 * @param type is either TYPE_STABLE_STATEG or TYPE_TERMINAL_CYCLE or TYPE_TRANSIENT_COMPONENT
	 * @return
	 */
	public static String typeToString(int type) {
		switch (type) {
		case TYPE_STABLE_STATE:
			return TYPE_STABLE_STATE_STRING;
		case TYPE_TERMINAL_CYCLE:
			return TYPE_TERMINAL_CYCLE_STRING;
		case TYPE_CYCLE:
			return TYPE_CYCLE_STRING;
		case TYPE_TRANSIENT_COMPONENT:
			return TYPE_TRANSIENT_COMPONENT_STRING;
		default:
			return null;
		}
	}
	
	/**
	 * Return an int representation for a given string
	 * @param type is either TYPE_STABLE_STATE_STRING or TYPE_TERMINAL_CYCLE_STRING or TYPE_TRANSIENT_COMPONENT_STRING
	 * @return
	 */
	public static int typeFromString(String type) {
		if (type.equals(TYPE_STABLE_STATE_STRING)) return TYPE_STABLE_STATE;
		if (type.equals(TYPE_TERMINAL_CYCLE_STRING)) return TYPE_TERMINAL_CYCLE;
		if (type.equals(TYPE_CYCLE_STRING)) return TYPE_CYCLE;
		return  TYPE_TRANSIENT_COMPONENT;
	}
	public void setTypeFromString(String type) {
		self.type = typeFromString(type);
	}
	
	/**
	 * Merge the slave (or its master) node into this (or its master).
	 * All the function call on the slave are redirected to this.
	 * The states of the slave are merged with this (or its master)
	 * 
	 * @param slaveNode
	 */
	public void merge(GsDynamicalHierarchicalNode slaveNode, Set nodeSet) {
		merge(slaveNode, nodeSet, null);
	}
	public void merge(GsDynamicalHierarchicalNode slaveNode, Set nodeSet, DynamicalHierarchicalSimulationHelper helper) {
		GsDynamicalHierarchicalNode masterNode = this.self;
		if (slaveNode.self == masterNode) return;
		
		//Get the slave or its master
		GsDynamicalHierarchicalNode slaveCurrentMaster = slaveNode.self;
		if (nodeSet != null) nodeSet.remove(slaveCurrentMaster);
		slaveNode.self = masterNode;
		slaveNode = slaveCurrentMaster;
				
		masterNode.root.merge(slaveNode.root, OmddNode.OR); //merging the omdds.
		slaveNode.root = null;
		
		if (helper != null) { //Merge the arcs;
			Set s_slave = (Set) helper.arcs.remove(slaveNode);
			Set s_master = (Set) helper.arcs.remove(masterNode);
			if (s_slave != null) {
				if (s_master != null) {
					s_master.addAll(s_slave);
				} else {
					helper.arcs.put(masterNode, s_slave);
				}
			}
		}
		
		masterNode.size += slaveCurrentMaster.size;
		
		if (slaveNode.statePile != null) {
			if (masterNode.statePile == null) {
				masterNode.statePile = slaveNode.statePile;
			} else {
				masterNode.statePile.addAll(slaveNode.statePile); //merging the statePile
			}
		}
		
		slaveNode.self = masterNode; //routing of the methods
	}
	
	/**
	 * Indicates if this node is an alias of another one
	 */
	public boolean isAlias() {
		return this != self;
	}
	
	/**
	 * Get the master of this node, ie. the node to which this one is associated.
	 */
	public GsDynamicalHierarchicalNode getMasterNode() {
		return self;
	}
	
	public boolean equals(GsDynamicalHierarchicalNode other) {
		return this.self == other.self;
	}

	public boolean isStable() {
		return self.type == TYPE_STABLE_STATE;
	}
	public boolean isTransient() {
		return self.type == TYPE_TRANSIENT_COMPONENT;
	}
	public boolean isCycle() {
		return self.type == TYPE_TERMINAL_CYCLE || self.type == TYPE_CYCLE;
	}
	public boolean isTerminal() {
		return self.type == TYPE_TERMINAL_CYCLE || self.type == TYPE_STABLE_STATE;
	}

	
	public void setType(int type) {
		self.type = type;
	}

	public int getType() {
		return self.type;
	}

	public int size() {
		return self.size;
	}

	public void updateSize(int nbNodes) {
		self.size = 0;
		updateSize(self.root, 0, nbNodes);
	}
	
	
	public void updateSize(OmddNode omdd, int last_depth, int nbNodes) {
	   if (omdd.next == null) {
	    	if (omdd.value == 0) return;
	    	self.size += nbNodes - last_depth - 1;
	    	return;
	    }
	    for (int i = 0 ; i < omdd.next.length ; i++) {
	    	self.updateSize(omdd.next[i], omdd.level, nbNodes);
	    }
	}

}


/* OLD RECURSIVE FUNCTION
	public OmddNode stateFromArray(int[] state) {
		return stateFromArray(state, state.length-1, OmddNode.TERMINALS[1]);
	}

	
	private OmddNode stateFromArray(int[] state, int level, OmddNode child) {
		OmddNode omdd = new OmddNode();
		omdd.level = level;
		int nbrChilds = childsCount[level];
		omdd.next = new OmddNode[nbrChilds];
		for (int i = 0; i < nbrChilds; i++) {
			if (i == state[level]) omdd.next[i] = child;
			else omdd.next[i] = OmddNode.TERMINALS[0];
		}
		
		if (level == 0) return omdd;
		else return stateFromArray(state, level--, omdd);
		
	}
*/