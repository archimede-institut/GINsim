package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fr.univmrs.tagc.GINsim.reg2dyn.DynamicalHierarchicalSimulationHelper;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.common.GsException;

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
	 * The type (transient, terminal cycle or stable state) of the component.
	 */
	private int type = TYPE_TRANSIENT_COMPONENT;
	
	private List statePile = null;
	
	/**
	 * Count of states in this node
	 */
	private int size = 0;
	
	/**
	 * Count of processed states in this nodes (a node is processed when all its childs are processed.
	 */
	private int processed = 0;
	
	/**
	 * Set of nodes for 'in'coming edges and 'out'going edges. 
	 */
	private Set in, out;
	
	/**
	 * A new node with a certain initial state.
	 * @param g a DynamicalHierarchicalGraph
	 * @param state an initial state to add in the graph
	 */
	public GsDynamicalHierarchicalNode(short[] state, short[]childsCount) {
		root = stateFromArray(state, childsCount);
		this.size = 1;
	}

	/**
	 * A new node.
	 * @param g a DynamicalHierarchicalGraph
	 */
	public GsDynamicalHierarchicalNode() {
		root = OmddNode.TERMINALS[0];
	}

	/**
	 * A new node from a string written with the stateToString() method
	 * @param g a DynamicalHierarchicalGraph
	 * @param parse the string from stateToString()
	 * @param type a string representation of the type
	 * 
	 */
	public GsDynamicalHierarchicalNode(String parse, String type, short[] childsCount) {
		this(parse, childsCount);
		setTypeFromString(type);
	}
	
	/**
	 * A new node from a string written with the stateToString() method
	 * @param g a DynamicalHierarchicalGraph
	 * @param parse the string
	 */
	public GsDynamicalHierarchicalNode(String parse, short[] childsCount) {
		root = OmddNode.TERMINALS[0];
		parse(parse, childsCount);
	}

	/**
	 * Perform a mergeMultiple on all the omdd in the pile and the root.
	 */
	public void addPileToOmdd(short[] childsCount) {
		if (statePile == null) return;
		root = root.mergeMultiple(statePile, OmddNode.OR);
		statePile = null;
		updateSize(childsCount);
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
	public void addStateToThePile(String state, short[] childsCount) {
		if (statePile == null) statePile = new LinkedList();
		statePile.add(stateFromString(state, childsCount));
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
	public void addStateToThePile(short[] state, short[] childsCount) {
		if (statePile == null) statePile = new LinkedList();
		statePile.add(stateFromArray(state, childsCount));
	}
	
	
	/**
	 * Generate a new omdd for a given state (a short) and add it to the omdd.
	 *  
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
)	 */
	public void addState(String state, short[] childsCount) {
		root = root.merge(stateFromString(state, childsCount), OmddNode.OR);		
		size++;
	}
	
	/**
	 * Generate a new omdd for a given state (a short) and add it to the omdd.
	 *  
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
)	 */
	public void addState(short[] state, short[] childsCount) {
		root = root.merge(stateFromArray(state, childsCount), OmddNode.OR);		
		size++;
	}


	/**
	 * Generate a new omdd with a child 1 for a given state and return it
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
	 * @return the omdd
	 */
	public OmddNode stateFromArray(short[] state, short[] childsCount) {
		return stateFromArray(state, childsCount, 1);
	}
	
	/**
	 * Generate a new omdd for a given state and return it
	 * 
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
	 * @param child the value to append at the leaf.
	 * @return the omdd
	 */
	public OmddNode stateFromArray(short[] state, short[] childsCount, int child_value) {
		OmddNode child = OmddNode.TERMINALS[child_value];
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
	public OmddNode stateFromString(String state, short[] childsCount) {
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
	public boolean contains(short [] state) {
		return root.testStatus(state) != 0;
	}
	
	/**
	 * Reduce the omdd
	 */
	public void reduce() {
		root.reduce();
	}

	public String toString() {
		if (size == 1) {
			StringBuffer s = new StringBuffer();
			short[] t = (short[]) ((List)statesToListShort(4)).get(0);
			for (int i = 0; i < t.length; i++) {
				s.append(String.valueOf(t[i]).charAt(0));
			}
			return s.toString();
		}
		return "#"+size;
	}
	public String toString(int nbNodes) {
		if (size == 1) {
			StringBuffer s = new StringBuffer();
			short[] t = (short[]) ((List)statesToList(nbNodes)).get(0);
			for (int i = 0; i < t.length; i++) {
				s.append(String.valueOf(t[i]).charAt(0));
			}
			return s.toString();
		}
		return "#"+size;
	}
	public String toLongString(int nbNodes) {
		String name = "";
		if (size == 1) {
			StringBuffer s = new StringBuffer();
			short[] t = (short[]) ((List)statesToList(nbNodes)).get(0);
			for (int i = 0; i < t.length; i++) {
				s.append(String.valueOf(t[i]).charAt(0));
			}
			name = ",name: "+s.toString();
		}
		
		return "{"+processed+"/"+size+name+", type:"+typeToString()+"}";
	}
	public String getId() {
		return toString();
	}
	
	/**
	 * A new node from a string written with the stateToString() method
	 * @param parse
	 */
	public void parse(String parse, short[] childsCount) {
		String[] schemas = parse.split("\n");
		for (int i = 0; i < schemas.length; i++) {
			addStateToThePile(schemas[i], childsCount);
		}
		addPileToOmdd(childsCount);		
	}
		
	public String statesToString(int nbNodes) {
		StringBuffer s = new StringBuffer();
		return statesToString(root, s, nbNodes);
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
		short[] t = new short[nbNodes];
		statesToList(root, v, t, 0, nbNodes);
		return v;
	}
	
	private void statesToList(OmddNode omdd, List v, short[] t, int last_depth, int nbNodes) { //FIXME : test before use
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
	    	t[omdd.level] = (short) i;
        	statesToList(omdd.next[i], v ,t, omdd.level, nbNodes);
        }

	}

	/**
	 * Generate a list of all the states in the node
	 * Each item of the returned list is a string representation using wildcard *.
	 * Note the order in the list is relative to the omdd structure.
	 * @return
	 */
	public List statesToListShort(int maxNodes) {
		List v = new LinkedList();
		short[] t = new short[maxNodes];
		statesToListShort(root, v, t, 0, maxNodes);
		return v;
	}
	
	private void statesToListShort(OmddNode omdd, List v, short[] t, int last_depth, int maxNodes) { //FIXME : test before use
        if (omdd.next == null) {
        	if (omdd.value == 0) return;
        	for (int i = last_depth+1; i < maxNodes; i++) {
				t[i] = -1;
			}
			v.add(t.clone());
        	return;
        }
        
    	for (int i = last_depth+1; i < omdd.level && i < maxNodes; i++) {
			t[i] = -1;
		}
        for (int i = 0 ; i < omdd.next.length ; i++) {
	    	if (omdd.level < maxNodes) t[omdd.level] = (short) i;
	    	statesToListShort(omdd.next[i], v ,t, omdd.level, maxNodes);
        }

	}

	
	/**
	 * Return a string representation for the type of this node.
	 * @return
	 */
	public String typeToString() {
		return typeToString(type);
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
		this.type = typeFromString(type);
	}
	
	/**
	 * Merge the slave (or its master) node into this (or its master).
	 * All the function call on the slave are redirected to this.
	 * The states of the slave are merged with this (or its master)
	 * 
	 * @param slaveNode
	 */
	public void merge(GsDynamicalHierarchicalNode slaveNode, Set nodeSet, int nbNodes) throws Exception {
		merge(slaveNode, nodeSet, nbNodes, null);
	}
	public void merge(GsDynamicalHierarchicalNode slaveNode, Set nodeSet, int nbNodes, DynamicalHierarchicalSimulationHelper helper) throws Exception {
		if (slaveNode == this) return;
		if (slaveNode.type != this.type) {
			throw new Exception("Error merging two node of different types : "+slaveNode.toLongString(nbNodes)+" in "+this.toLongString(nbNodes)); //FIXME : remove me
		}
		
		this.root = this.root.merge(slaveNode.root, OmddNode.OR);
		
		if (helper != null) { //Merge the arcs;
			if (slaveNode.in != null) {
				for (Iterator it = slaveNode.in.iterator(); it.hasNext();) {
					GsDynamicalHierarchicalNode node = (GsDynamicalHierarchicalNode) it.next();
					node.out.remove(slaveNode);
					helper.addEdge(node, this);
				}				
			}
			if (slaveNode.out != null) {
				for (Iterator it = slaveNode.out.iterator(); it.hasNext();) {
					GsDynamicalHierarchicalNode node = (GsDynamicalHierarchicalNode) it.next();
					node.in.remove(slaveNode);
					helper.addEdge(this, node);
				}
			}
		}
				
		if (slaveNode.statePile != null) {
			if (this.statePile == null) {
				this.statePile = slaveNode.statePile;
			} else {
				this.statePile.addAll(slaveNode.statePile); //merging the statePile
			}
		}
		nodeSet.remove(slaveNode);
	}
	
	public boolean isStable() {
		return type == TYPE_STABLE_STATE;
	}
	public boolean isTransient() {
		return type == TYPE_TRANSIENT_COMPONENT;
	}
	public boolean isCycle() {
		return type == TYPE_TERMINAL_CYCLE || type == TYPE_CYCLE;
	}
	public boolean isTerminal() {
		return type == TYPE_TERMINAL_CYCLE || type == TYPE_STABLE_STATE;
	}

	
	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public int size() {
		return size;
	}

	public void updateSize(short[] childsCount) {
		size = 0;
		updateSize(root, 0, 1, childsCount);
	}
	
	
	private void updateSize(OmddNode omdd, int last_depth, int coef, short[] childsCount) {
		if (omdd.next == null) {
			if (omdd.value == 0) return;

			int s = 1;
			for (int i = last_depth; i < childsCount.length-1; i++) {
				s *= childsCount[i];
			}
			size += s*coef;
			return;
		}

		for (int i = last_depth+1; i < omdd.level; i++) {
			coef *= childsCount[i];
		}
		
		for (int i = 0 ; i < omdd.next.length ; i++) {
			updateSize(omdd.next[i], omdd.level, coef, childsCount);
		}
	}

	public Set getOutgoingEdges() {
		if (out == null) {
			out = new HashSet();
		}
		return out;
	}

	public Set getIncomingEdges() {
		if (in == null) {
			in = new HashSet();
		}
		return in;
	}

	public void releaseEdges() {
		in = null;
		out = null;
	}
	
	public int getProcessedStates() {
		return processed;
	}
	
	public boolean isProcessed() throws GsException {
		if (statePile == null && size < processed) {
			throw new GsException(1, "Error size < processes Childs : "+this);
		}
		return statePile == null && size == processed;
	}
	
	public void processState(short[] state, short[] childsCount) {
		if (root.testStatus(state) == 1) {
			root = root.merge(stateFromArray(state, childsCount, 2), OmddNode.MAX);
			processed++;
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