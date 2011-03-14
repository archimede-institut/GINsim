package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import java.awt.Color;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.xml.sax.SAXException;

import fr.univmrs.tagc.GINsim.export.generic.Dotify;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.common.GsException;

public class GsDynamicalHierarchicalNode implements Dotify {
	public static final byte TYPE_TRANSIENT_COMPONENT = 0;
	public static final byte TYPE_CYCLE = 1;
	public static final byte TYPE_TERMINAL_CYCLE = 2;
	public static final byte TYPE_STABLE_STATE = 3;
	
	public static final String TYPE_TRANSIENT_COMPONENT_STRING = "transientComponent";
	public static final String TYPE_TERMINAL_CYCLE_STRING = "terminalCycle";
	public static final String TYPE_CYCLE_STRING = "cycle";
	public static final String TYPE_STABLE_STATE_STRING = "stableState";
	
	public static final Color TYPE_TRANSIENT_COMPONENT_COLOR = new Color(78, 154, 6);
	public static final Color TYPE_CYCLE_COLOR = new Color(114, 159, 207);
	public static final Color TYPE_TERMINAL_CYCLE_COLOR = new Color(32, 74, 135);
	public static final Color TYPE_STABLE_STATE_COLOR = new Color(164, 0, 0);
	public static final Color TYPE_TRANSIENT_COMPONENT_ALONE_COLOR = new Color(109, 179, 43);

	private static long nextId = 0;
	
	/**
	 * The root of the decision diagram storing the states.
	 */
	public OmddNode root;

	/**
	 * The type (transient, terminal cycle or stable state) of the component.
	 */
	private byte type = TYPE_TRANSIENT_COMPONENT;
	
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
	 * The unique id 
	 */
	private long uid;
	
	/**
	 * A new node with a certain initial state.
	 * @param state an initial state to add in the graph
	 * @param childsCount
	 * @param childValue the value to add at the end of the omdd (default 1).
	 */
	public GsDynamicalHierarchicalNode(byte[] state, byte[] childsCount, int childValue) {
		this.size = 1;
		if (childValue == 2) {
			processed = 1;
			root = stateFromArray(state, childsCount, childValue);
		} else {
			root = null;
			addStateToThePile(state);
		}
		this.uid = nextId++;
	}
	
	/**
	 * A new node with a certain initial state.
	 * @param state an initial state to add in the graph
	 * @param childsCount
	 */
	public GsDynamicalHierarchicalNode(byte[] state, byte[]childsCount) {
		this(state, childsCount, 1);
	}

	/**
	 * A new node.
	 */
	public GsDynamicalHierarchicalNode() {
		root = null;
		this.uid = nextId++;
	}

	/**
	 * @throws ParseException 
	 */
	public GsDynamicalHierarchicalNode(String sid) throws SAXException {
		try {
			this.uid = Integer.parseInt(sid.substring(1));
		} catch (NumberFormatException e) {
			throw new SAXException(e);
		}
		root = null;
	}

	/**
	 * Perform a mergeMultiple on all the omdd in the pile and the root.
	 * @param childsCount
	 */
	public void addPileToOmdd(byte[] childsCount) {
		if (statePile == null) return;
		if (root == null) {
			root = OmddNode.multi_or(statePile, childsCount);
		} else {
			root = root.merge(OmddNode.multi_or(statePile, childsCount), OmddNode.OR);			
		}
		statePile = null;
		updateSize(childsCount);
	}

	/**
	 * Generate a new omdd for a given state and add it to the pile.
	 * Note the new state omdd is not added to the GsDynamicalHierarchicalNode omdd aiming to add several omdd at the same time.
	 * 
	 * @see addPileToOmdd()
	 * 
	 * @param state the state to add
	 * @param childsCount
	 */
	public void addStateToThePile(byte[] state) {
		if (statePile == null) statePile = new LinkedList();
		statePile.add(state);
	}
	
	
	/**
	 * Generate a new omdd for a given state (a byte) and add it to the omdd.
	 *  
	 * @param state the state to add
	 * @param childsCount
	 */
	public void addState(String state, byte[] childsCount) {
		if (root == null) {
			root = stateFromString(state, childsCount);
		} else {
			root = root.merge(stateFromString(state, childsCount), OmddNode.OR);		
		}
		size++;
	}
	
	/**
	 * Generate a new omdd for a given state (a byte) and add it to the omdd.
	 *  
	 * @param state the state to add
	 * @param childsCount
	 */
	public void addState(byte[] state, byte[] childsCount, int childValue) {
		if (root == null) {
			root = stateFromArray(state, childsCount, childValue);
		} else {
			root = root.merge(stateFromArray(state, childsCount, childValue), OmddNode.OR);
		}
		size++;
	}
	
//	/**
//	 * remove a state from the omdd
//	 * @param state the state to remove
//	 * @return true if the link should be updated (ie. the omdd is affected and this size > 0)
//	 */
//	public boolean remove(byte[] state, Set nodeSet) {
//		int res = root.remove(state);
//		if (res >= 1) {
//			size--;
//			if (res == 2) {
//				processed--;
//			}
//			if (size == 0) {
//				nodeSet.remove(this);
//				return false;
//			} else {
//				return true;
//			}
//		}
//		return false;
//	}


	/**
	 * Generate a new omdd with a child 1 for a given state and return it
	 * @param state the state to add
	 * @param childsCount
	 * @return the omdd
	 */
	public OmddNode stateFromArray(byte[] state, byte[] childsCount) {
		return stateFromArray(state, childsCount, 1);
	}
	
	/**
	 * Generate a new omdd for a given state and return it
	 * 
	 * @param state the state to add
	 * @param childsCount
	 * @param child_value the value to append at the leaf.
	 * @return the omdd
	 */
	public OmddNode stateFromArray(byte[] state, byte[] childsCount, int child_value) {
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
	 * @param state the state to add
	 * @param childsCount
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
	 * @return true if it contains the state
	 */
	public boolean contains(byte [] state) {
		if (root == null) return false;
		if (statePile != null) { //First search the state in the Pile
			for (Iterator it = statePile.iterator(); it.hasNext();) {
				byte[] stateInPile = (byte[]) it.next();
				boolean found = true;
				for (int i = 0; i < stateInPile.length; i++) {
					if (state[i] != stateInPile[i]) {
						found = false;
						break;
					}
				}
				if (found) {
					return true;
				}
			}
		}
		return root.testStatus(state) != 0;
	}
	
	/**
	 * Reduce the omdd
	 */
	public void reduce() {
		if (root != null) {
			root = root.reduce();			
		}
	}

	public String toString() {
		if (size == 1) {
			return firstStatesToString().toString();
		}
		return "#"+size;
	}
	public String toString(int nbNodes) {
		if (size == 1) {
			StringBuffer s = new StringBuffer();
			byte[] t = (byte[]) ((List)statesToList(nbNodes)).get(0);
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
			byte[] t = (byte[]) ((List)statesToList(nbNodes)).get(0);
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
	public String getShortId() {
		if (size == 1) {
			return ""+hashCode();
		} else {
			return ""+hashCode()+"{"+processed+"/"+size+"}";
		}
	}
	public StringBuffer write() {
		return root.write();
	}
		
	public String statesToString(int nbNodes) {
		return statesToString(nbNodes, false);
	}
	
	public String statesToString(int nbNodes, boolean addValue) {
		StringBuffer res = new StringBuffer();
		if (statePile != null) {
			for (Iterator it = statePile.iterator(); it.hasNext();) {
				byte[] stateInPile = (byte[]) it.next();
				for (int i = 0; i < stateInPile.length; i++) {
					res.append(stateInPile[i]);
				}
	        	if (addValue) {
	        		res.append("-1\n");
	        	} else {
	        		res.append("\n");
	        	}
			}
		} else {
			int length = nbNodes+(addValue?3:1);
			StringBuffer s = new StringBuffer(length);
			
			for (int i = 0; i < length; i++) {
				s.append('.');
			}
			statesToString(root, s, res, 0, nbNodes, addValue);	
		}
		return res.toString();
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
	 * Generate a list of all the states in the node
	 * Each item of the returned list is a string representation using wildcard *.
	 * Note the order in the list is relative to the omdd structure.
	 * @return
	 */
	public List statesToList(int nbNodes) {
		if (statePile != null) {
			return statePile;
		}
		List v = new LinkedList();
		byte[] t = new byte[nbNodes];
		statesToList(root, v, t, 0, nbNodes);	
		return v;
	}
	
	private void statesToList(OmddNode omdd, List v, byte[] t, int last_depth, int nbNodes) {
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
	 * Return a string representation of the first state in the node.
	 */
	public StringBuffer firstStatesToString() {
		StringBuffer s = new StringBuffer();
		if (statePile != null) {
			byte[] stateInPile = (byte[]) statePile.get(0);
			for (int i = 0; i < stateInPile.length; i++) {
				s.append(stateInPile[i]);
			}
		} else {
			firstStatesToString(root, s, 0);
		}
		return s;
	}
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
	public static byte typeFromString(String type) {
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
	 * @param childsCount 
	 * @param helper 
	 */
	public void merge(GsDynamicalHierarchicalNode slaveNode, Collection nodeSet, int nbNodes, byte[] childsCount) throws Exception {
		if (slaveNode == this) return;
		if (slaveNode.type != this.type) {
			throw new Exception("Error merging two node of different types : "+slaveNode.toLongString(nbNodes)+" in "+this.toLongString(nbNodes)); //FIXME : remove me
		}
		if (this.root != null && slaveNode.root != null) {
			this.root = this.root.merge(slaveNode.root, OmddNode.OR);
		}
		
		if (slaveNode.in != null) {
			for (Iterator it = slaveNode.in.iterator(); it.hasNext();) {
				GsDynamicalHierarchicalNode source = (GsDynamicalHierarchicalNode) it.next();
				source.out.remove(slaveNode);
				source.out.add(this);
				this.addIncomingEdges(source);
			}				
		}
		if (slaveNode.out != null) {
			for (Iterator it = slaveNode.out.iterator(); it.hasNext();) {
				GsDynamicalHierarchicalNode target = (GsDynamicalHierarchicalNode) it.next();
				target.in.remove(slaveNode);
				target.in.add(this);
				this.addOutgoingEdges(target);
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

	
	public void setType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public int size() {
		return size;
	}

	public void updateSize(byte[] childsCount) {
		size = 0;
		processed = 0;
		if (statePile != null) {
			size = statePile.size();
		}
		updateSize(root, 0, 1, childsCount);
	}
	
	
	private void updateSize(OmddNode omdd, int last_depth, int coef, byte[] childsCount) {
		if (omdd.next == null) {
			if (omdd.value == 0) return;

			int s = 1;
			for (int i = last_depth; i < childsCount.length-1; i++) {
				s *= childsCount[i];
			}
			size += s*coef;
			if (omdd.value == 2) {
				processed += s*coef;
			}
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

	public void addOutgoingEdges(Object o) {
		if (out == null) {
			out = new HashSet();
		}
		out.add(o);
	}

	public void addIncomingEdges(Object o) {
		if (in == null) {
			in = new HashSet();
		}
		in.add(o);
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
	
	public void processState(byte[] state, byte[] childsCount) {
		if (root.testStatus(state) == 1) {
			root = root.merge(stateFromArray(state, childsCount, 2), OmddNode.MAX);
			processed++;
		}
	}
	
	public int getSize() {
		return size;
	}

	public long getUniqueId() {
		return uid;
	}
	
	public int hashcode() {
		return (int)uid;
	}

	public void parse(String parse, byte[] childCount) throws SAXException {
		try {
			root = OmddNode.read(parse, childCount);
			reduce();
			updateSize(childCount);
		} catch (ParseException e) {
			throw new SAXException(e);
		}
	}

	public String toDot() {
		String options;
    	if (this.getType() == GsDynamicalHierarchicalNode.TYPE_CYCLE) 				options = "shape=ellipse,style=filled,color=\"#C8E4A5\"";
    	else if (this.getType() == GsDynamicalHierarchicalNode.TYPE_STABLE_STATE) 	options = "shape=box,style=filled, width=\"1.1\", height=\"1.1\",color=\"#9CBAEB\"";
    	else if (this.getType() == GsDynamicalHierarchicalNode.TYPE_TERMINAL_CYCLE) options = "shape=circle,style=filled, width=\"1.1\", height=\"1.1\",color=\"#F5AC6F\"";
    	else 																		options = "shape=point,style=filled,color=\"#00FF00\"";
		return  this.getUniqueId()+" [label=\""+this.toString()+"\", "+options+"];";
	}
	
	public String toDot(Object to) {
		return  this.getUniqueId()+" -> "+((GsDynamicalHierarchicalNode) to).getUniqueId()+";";
	}
	
}