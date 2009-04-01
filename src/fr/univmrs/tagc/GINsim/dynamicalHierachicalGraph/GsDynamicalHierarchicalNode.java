package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

public class GsDynamicalHierarchicalNode {
		
	public static final int TYPE_TRANSIENT_COMPONENT = 0;
	public static final int TYPE_TERMINAL_CYCLE = 1;
	public static final int TYPE_STABLE_STATE = 2;
	
	public static final String TYPE_TRANSIENT_COMPONENT_STRING = "transientComponent";
	public static final String TYPE_TERMINAL_CYCLE_STRING = "terminalCycle";
	public static final String TYPE_STABLE_STATE_STRING = "stableState";
	
	/**
	 * The root of the decision diagram storing the states.
	 */
	public OmddNode root;

	public GsDynamicalHierarchicalGraph g;
	
	/**
	 * The type (transient, terminal cycle or stable state) of the component.
	 */
	public int type = TYPE_TRANSIENT_COMPONENT;
	
	private List statePile = null;

	/**
	 * A new node with a certain initial state.
	 * @param g a DynamicalHierarchicalGraph
	 * @param state an initial state to add in the graph
	 */
	public GsDynamicalHierarchicalNode(GsDynamicalHierarchicalGraph g, int[] state) {
		this.g = g;
		root = stateFromArray(state);
	}

	/**
	 * A new node.
	 * @param g a DynamicalHierarchicalGraph
	 */
	public GsDynamicalHierarchicalNode(GsDynamicalHierarchicalGraph g) {
		this.g = g;
		root = OmddNode.TERMINALS[0];
	}

	/**
	 * A new node from a string written with the stateToString() method
	 * @param g a DynamicalHierarchicalGraph
	 * @param parse the string from stateToString()
	 * @param type a string representation of the type
	 * 
	 */
	public GsDynamicalHierarchicalNode(GsDynamicalHierarchicalGraph g, String parse, String type) {
		this(g, parse);
		setTypeFromString(type);
	}
	
	/**
	 * A new node from a string written with the stateToString() method
	 * @param g a DynamicalHierarchicalGraph
	 * @param parse the string
	 */
	public GsDynamicalHierarchicalNode(GsDynamicalHierarchicalGraph g, String parse) {
		this.g = g;
		root = OmddNode.TERMINALS[0];
		parse(parse);
	}

	/**
	 * Perform a mergeMultiple on all the omdd in the pile and the root.
	 */
	public void addPileToOmdd() {
		root.mergeMultiple((OmddNode[]) statePile.toArray(), OmddNode.OR);
		statePile = null;
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
	public void addStateToThePile(String state) {
		if (statePile == null) statePile = new LinkedList();
		statePile.add(stateFromString(state));
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
	public void addStateToThePile(int[] state) {
		if (statePile == null) statePile = new LinkedList();
		statePile.add(stateFromArray(state));
	}

	/**
	 * Generate a new omdd for a given state and return it
	 * @param g the graph (for the regulatory node order)
	 * @param state the state to add
	 * @return the omdd
	 */
	public OmddNode stateFromArray(int[] state) {
		OmddNode child = OmddNode.TERMINALS[1];
		int[] childsCount = g.getChildsCount();
		
		for (int level = state.length-1; level >=0 ; level--) {
			OmddNode omdd = new OmddNode();
			omdd.level = level;
			int nbrChilds = childsCount[level];
			for (int i = 0; i < nbrChilds; i++) {
				if (i == state[level]) omdd.next[i] = child;
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
	public OmddNode stateFromString(String state) {
		OmddNode child = OmddNode.TERMINALS[1];
		int[] childsCount = g.getChildsCount();
		
		for (int level = state.length()-1; level >=0 ; level--) {			
			OmddNode omdd = new OmddNode();
			omdd.level = level;
			int nbrChilds = childsCount[level];
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
	public boolean contains(int [] state) {
		return root.testStatus(state) != 0;
	}
	
	/**
	 * Reduce the omdd
	 */
	public void reduce() {
		root.reduce();
	}

	public String toString() {
		return "type:"+typeToString()+"\n"+statesToString();
	}
	
	/**
	 * A new node from a string written with the stateToString() method
	 * @param parse
	 */
	public void parse(String parse) {
		String[] schemas = parse.split("\n");
		for (int i = 0; i < schemas.length; i++) {
			addStateToThePile(schemas[i]);
		}
		addPileToOmdd();		
	}
		
	public String statesToString() {
		StringBuffer s = new StringBuffer();
		return statesToString(root, s);
	}
		
	private String statesToString(OmddNode omdd, StringBuffer s) { //FIXME : test before use
        if (omdd.next == null) {
        	for (int i = omdd.level; i < g.getNodeOrder().size()-1; i++) {
				s.append("*");
			}
        	s.append("*\n");
        	String res = s.toString();
        	s.delete(omdd.level, s.length()-1);
        	return res;
        }
        
        s.append(omdd.level);
        for (int i = 0 ; i < omdd.next.length ; i++) {
        	statesToString(omdd.next[i], s);
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
	public List statesToList() {
		StringBuffer s = new StringBuffer(g.getNodeOrder().size());
		List v = new LinkedList();
		statesToList(root, v, s);
		return v;
	}
	
	private void statesToList(OmddNode omdd, List v, StringBuffer s) { //FIXME : test before use
        if (omdd.next == null) {
        	for (int i = omdd.level; i < g.getNodeOrder().size()-1; i++) {
				s.setCharAt(i, '*');
			}
			s.setCharAt(s.length()-1, '*');
			v.add(s.toString());
        	return;
        }
        
		s.setCharAt(s.length()-1, String.valueOf(omdd.level).charAt(0));
        for (int i = 0 ; i < omdd.next.length ; i++) {
        	statesToList(omdd.next[i], v ,s);
        }

	}

	
	/**
	 * Return a string representation for the type of this node.
	 * @return
	 */
	public String typeToString() {
		return typeToString(this.type);
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
		return  TYPE_TRANSIENT_COMPONENT;
	}
	public void setTypeFromString(String type) {
		this.type = typeFromString(type);
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