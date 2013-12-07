package org.ginsim.core.graph.hierachicaltransitiongraph;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ginsim.core.utils.Dotify;
import org.xml.sax.SAXException;


/*  SUMMARY
 * 
 * **************** CONSTRUCTORS ************/	
/* **************** PILE ************/	
/* **************** CONTAINS ************/	
/* **************** MERGE ************/	
/* **************** SIZE ************/	
/* **************** EDGES, ID AND SIGMA ************/	
/* **************** TOSTRINGS ************/	
/* **************** TYPE GETTERS, SETTERS, TESTERS (isStable) AND CONVERSIONS ************/		
/* **************** TO DOT (DOTIFY) ************/	
/* **************** COMPARABLE ************/	


/**
 * <p>Define the nodes of the Hierarchical Transition Graph.</p>
 * 
 * <p>A node has a unique id <b>uid</b>, a <b>type</b> defining the kind of Strongly Connected Component 
 * it contains and <b>statesSet</b>, the set of all the states it contains.</p>
 * 
 *
 */
public class HierarchicalNode implements Comparable<Object>, Dotify {

	public static final byte TYPE_TRANSIENT_COMPONENT = 0;
	public static final byte TYPE_TRANSIENT_CYCLE = 1;
	public static final byte TYPE_TERMINAL_CYCLE = 2;
	public static final byte TYPE_STABLE_STATE = 3;
	
	public static final String TYPE_TRANSIENT_COMPONENT_STRING = "transientComponent";
	public static final String TYPE_TERMINAL_CYCLE_STRING = "terminalCycle";
	public static final String TYPE_TRANSIENT_CYCLE_STRING = "cycle";
	public static final String TYPE_STABLE_STATE_STRING = "stableState";
	
	public static final Color TYPE_TRANSIENT_COMPONENT_ALONE_COLOR = Color.white;
	public static final Color TYPE_TRANSIENT_COMPONENT_COLOR = new Color(225, 225, 230);
	public static final Color TYPE_TRANSIENT_CYCLE_COLOR = new Color(128, 158, 208);

	public static final Color TYPE_EDEN_TRANSIENT_COMPONENT_COLOR = new Color(225, 255, 225);

	public static final Color TYPE_TERMINAL_CYCLE_COLOR = new Color(240, 200, 200);
	public static final Color TYPE_STABLE_STATE_COLOR = Color.red;

	
	/**
	 * A static long used to give a unique id to each HN
	 */
	private static long nextId = 0;

	/**
	 * The unique id of the node, used for efficient comparison of HN.
	 */
	private long uid;
	
	
	/**
	 * The type (transient, terminal cycle or stable state) of the component.
	 */
	private byte type = TYPE_TRANSIENT_COMPONENT;

	/**
	 * The set of states
	 */
	public StatesSet statesSet;

	/**
	 * A list of states (byte[]) that are processing currently, and to add later.
	 */
	private List<byte[]> statePile = null;
	
	/**
	 * Count of states in this node.
	 */
	private int size = 0;

	/**
	 * Count of states in this node.
	 */
	private short label_count = -1;


	/**
	 * The atteignability in terms of attractors
	 */
	private HierarchicalSigmaSet sigma;
	
	private HierarchicalTransitionGraph htg;
	private Set<HierarchicalNode> out;
	private Set<HierarchicalNode> in;

	
/* **************** CONSTRUCTORS ************/	
	
	/**
	 * Default constructor.
	 * Initialize a new node with a good uid, and type TYPE_TRANSIENT_COMPONENT
	 * By default the state set is not initialized
	 */
	public HierarchicalNode(HierarchicalTransitionGraph htg) {
		this.statesSet = null;
		this.uid = nextId++;
		this.type = TYPE_TRANSIENT_COMPONENT;
		this.htg = htg;
		this.in = new HashSet<HierarchicalNode>();
		this.out = new HashSet<HierarchicalNode>();
	}
	
/* **************** PILE ************/	

	/**
	 * Perform a mergeMultiple on all the omdd in the pile and the root.
	 */
	public void addAllTheStatesInQueue() {
		if (statePile == null) return;
		if (statesSet == null) statesSet = htg.createStateSet();
		statesSet.addStates(statePile);
		statePile = null;
		updateSize();
	}

	/**
	 * Add a new state to the pile.
	 * Note the new state is not added to the HierarchicalNode omdd aiming to add several omdd at the same time.
	 * 
	 * 
	 * @param state the state to add
	 */
	public void addStateToThePile(byte[] state) {
		if (statePile == null) statePile = new LinkedList<byte[]>();
		statePile.add(state);
	}
	
	/**
	 * Add the state directly to the stateSet. Set the leaf value to status
	 * 
	 * @param state
	 * @param status
	 */
	public void addState(byte[] state, int status) {
		if (statesSet == null) statesSet = htg.createStateSet();
		statesSet.addState(state, status);
	}

	
/* **************** CONTAINS ************/	
	
	
	/**
	 * Indicates if the HierarchicalNode contains a state by searching both the pile and the omdd
	 * @param state
	 * @return true if it contains the state
	 */
	public boolean contains(byte [] state) {
		if (statePile != null) { //First search the state in the Pile
			for (Iterator<byte[]> it = statePile.iterator(); it.hasNext();) {
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
		if (statesSet == null) return false;
		return statesSet.contains(state);
	}
	
/* **************** MERGE ************/	
	
	/**
	 * Merge the slave (or its master) node into this (or its master).
	 * All the function call on the slave are redirected to this.
	 * The states of the slave are merged with this (or its master)
	 * 
	 * @param slaveNode
	 * @param nodeSet 
	 * @param htg 
	 */
	public void merge(HierarchicalNode slaveNode, Collection<HierarchicalNode> nodeSet, HierarchicalSigmaSetFactory sigmaSetFactory, HierarchicalTransitionGraph htg) {
		if (slaveNode == this) return;
		nodeSet.remove(slaveNode);												//Make slaveNode a slaveNode !
		if (this.statesSet != null) {
			if (slaveNode.statesSet != null) { 			//Merge the set of states
				this.statesSet.merge(slaveNode.statesSet);
			}
		} else {
			this.statesSet = slaveNode.statesSet;
		}
		if (slaveNode.statePile != null) {										//Merge the piles of states
			if (this.statePile == null) {
				this.statePile = slaveNode.statePile;
			} else {
				this.statePile.addAll(slaveNode.statePile); //merging the statePile
			}
		}
		if (slaveNode.sigma != null) {
			if (this.sigma == null) {
				this.sigma = slaveNode.sigma;
			} else {
				sigmaSetFactory.merge(this, slaveNode);
			}
		}
		
		
		if (slaveNode.getOut() != null) {
			for (Iterator<HierarchicalNode> it = slaveNode.getOut().iterator(); it.hasNext();) {
				HierarchicalNode to = (HierarchicalNode) it.next();
				if (!to.equals(this)) {
					this.addEdgeTo(to);
					to.getIn().remove(slaveNode);
				}
			}			
		}
		if (slaveNode.getIn() != null) {
			for (Iterator<HierarchicalNode> it = slaveNode.getIn().iterator(); it.hasNext();) {
				HierarchicalNode from = (HierarchicalNode) it.next();
				if (!from.equals(this)) {
					from.addEdgeTo(this);
					from.getOut().remove(slaveNode);
				}
			}			
		}
		if (this.uid > slaveNode.uid) this.uid = slaveNode.uid;
	}
	

	
/* **************** SIZE ************/	
	
	public int getSize() {
		return size;
	}

	/**
	 * Compute the size. This function reduce the stateSet but doesn't add the states in the queue to the stateSet. 
	 */
	public void updateSize() {
		size = 0;
		//processed = 0;
		if (statePile != null) {
			size = statePile.size();
		}
		if (statesSet != null) {
			statesSet.reduce();
			int[] counts = statesSet.updateSize();
			size += counts[1];// + counts[STATUS_PROCESSED];
			//processed += counts[STATUS_PROCESSED];
			
		}		
	}
	

	/* **************** EDGES, ID AND SIGMA ************/	


	/**
	 * Add an edge between to a node
	 * @param to
	 * @return true if an edge was added (no autoregulation)
	 */
	public boolean addEdgeTo(HierarchicalNode to) {
		to.getIn().add(this);
		getOut().add(to);
		return true;
	}

	/**
	 * @param out the out to set
	 */
	public void setOut(Set<HierarchicalNode> out) {
		this.out = out;
	}

	/**
	 * @return the out
	 */
	public Set<HierarchicalNode> getOut() {
		return out;
	}

	/**
	 * @param in the in to set
	 */
	public void setIn(Set<HierarchicalNode> in) {
		this.in = in;
	}

	/**
	 * @return the in
	 */
	public Set<HierarchicalNode> getIn() {
		return in;
	}


	public void releaseEdges() {
		this.setIn(null);
		this.out = null;
	}

	
	/**
	 * return the set sigma
	 */
	public HierarchicalSigmaSet getSigma() {
		return sigma;
	}
	/**
	 * return the set sigma
	 */
	public void setSigma(HierarchicalSigmaSet sigma) {
		this.sigma = sigma;
	}
	
	
	/**
	 * Return the long identifying this node "uniquely" (if in the range of long)
	 * @return
	 */
	public long getUniqueId() {
		return uid;
	}


	public int hashcode() {
		return (int)uid;
	}

	public void parse(String parse) throws SAXException {
		if (statesSet == null) statesSet = htg.createStateSet();
		statesSet.parse(parse);
	}


	public void setLabelCount(short intValue) {
		this.label_count = intValue;
	}

/* **************** TOSTRINGS ************/	

	public String toString() {
		if (size == 0) {
			updateSize();
		}
		if (size == 1) {
			StringBuffer s = new StringBuffer();
			byte[] t = (byte[]) ((List<byte[]>)statesToList()).get(0);
			for (int i = 0; i < t.length; i++) {
				s.append(String.valueOf(t[i]).charAt(0));
			}
			switch (type) {
			case TYPE_TRANSIENT_COMPONENT:
				return "i-"+s.toString();
			case TYPE_STABLE_STATE:
				return "ss-"+s.toString();
			}
		}
		if (label_count > 0) {
			switch (type) {
			case TYPE_TRANSIENT_COMPONENT:
				return "i#"+size+"("+label_count+")";
			case TYPE_TERMINAL_CYCLE:
				return "ca#"+size+"("+label_count+")";
			case TYPE_TRANSIENT_CYCLE:
				return "ct#"+size+"("+label_count+")";
			}
		} else {
			switch (type) {
			case TYPE_TRANSIENT_COMPONENT:
				return "i#"+size;
			case TYPE_TERMINAL_CYCLE:
				return "ca#"+size;
			case TYPE_TRANSIENT_CYCLE:
				return "ct#"+size;
			}

		}
		return "#"+size;
	}

	public String toLongString() {
		String name = "";
		if (size == 1) {
			StringBuffer s = new StringBuffer();
			byte[] t = ((List<byte[]>)statesToList()).get(0);
			for (int i = 0; i < t.length; i++) {
				s.append(String.valueOf(t[i]).charAt(0));
			}
			name = ",name: "+s.toString();
		}
		return "{"+size+name+", type:"+typeToString()+"}";
	}
	public String getShortId() {
		return ""+hashCode();
	}
	public String getLongId() {
		return uid+"::"+toString();
	}
	public StringBuffer write() {
		return statesSet.write();
	}
		
	public String statesToString() {
		return statesToString(false);
	}
	
	public String statesToString(boolean addValue) {
		StringBuffer res = new StringBuffer();
		if (statePile != null) {
			for (Iterator<byte[]> it = statePile.iterator(); it.hasNext();) {
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
		}
		if (statesSet != null) res.append(statesSet.statesToString(addValue));
		return res.toString();
	}

	 
	/**
	 * Initialize and fill a list with all the states in the omdd
	 * Each item of the returned list is a string representation using wildcard * (-1).
	 * Note the order in the list is relative to the omdd structure.
	 * @return a list made of all the states as schemata (using *)
	 */
	public List<byte[]> statesToList() {
		addAllTheStatesInQueue();
		List<byte[]> v = new LinkedList<byte[]>();
		statesSet.statesToSchemaList(v);
		return v;
	}
		
	/**
	 * Return a string representation of the first state in the node.
	 */
	public StringBuffer firstStatesToString() {
		StringBuffer s = new StringBuffer(htg.getChildsCount().length);
		if (statePile != null) {
			byte[] stateInPile = (byte[]) statePile.get(0);
			for (int i = 0; i < stateInPile.length; i++) {
				s.append(stateInPile[i]);
			}
		} else {
			return statesSet.firstStatesToString();
		}
		return s;
	}
	

/* **************** TYPE GETTERS, SETTERS, TESTERS AND CONVERSIONS ************/		
	
	public boolean isStable() {
		return type == TYPE_STABLE_STATE;
	}
	public boolean isTransient() {
		return type == TYPE_TRANSIENT_COMPONENT;
	}
	public boolean isCycle() {
		return type == TYPE_TERMINAL_CYCLE || type == TYPE_TRANSIENT_CYCLE;
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
	
	/**
	 * Return a string representation for the type of this node.
	 * @return the string reprensetation
	 */
	public String typeToString() {
		return typeToString(type);
	}
	/**
	 * Return a string representation for a given type.
	 * @param type is either TYPE_STABLE_STATEG or TYPE_TERMINAL_CYCLE or TYPE_TRANSIENT_COMPONENT
	 * @return the string reprensetation
	 */
	public static String typeToString(int type) {
		switch (type) {
		case TYPE_STABLE_STATE:
			return TYPE_STABLE_STATE_STRING;
		case TYPE_TERMINAL_CYCLE:
			return TYPE_TERMINAL_CYCLE_STRING;
		case TYPE_TRANSIENT_CYCLE:
			return TYPE_TRANSIENT_CYCLE_STRING;
		case TYPE_TRANSIENT_COMPONENT:
			return TYPE_TRANSIENT_COMPONENT_STRING;
		default:
			return null;
		}
	}
	/**
	 * Set the type from a string
	 * @param type a string from the constants TYPE_STABLE_STATE_STRING, TYPE_TERMINAL_CYCLE_STRING...
	 */
	public void setTypeFromString(String type) {
		this.type = typeFromString(type);
	}
	/**
	 * Return an int representation for a given string
	 * @param type is either TYPE_STABLE_STATE_STRING or TYPE_TERMINAL_CYCLE_STRING or TYPE_TRANSIENT_COMPONENT_STRING OR TYPE_CYCLE_STRING
	 * @return the type constant
	 */
	public static byte typeFromString(String type) {
		if (type.equals(TYPE_STABLE_STATE_STRING)) return TYPE_STABLE_STATE;
		if (type.equals(TYPE_TERMINAL_CYCLE_STRING)) return TYPE_TERMINAL_CYCLE;
		if (type.equals(TYPE_TRANSIENT_CYCLE_STRING)) return TYPE_TRANSIENT_CYCLE;
		return  TYPE_TRANSIENT_COMPONENT;
	}
		
/* **************** TO DOT (DOTIFY) ************/	
	
	public String toDot() {
		String options;
    	if (this.getType() == TYPE_TRANSIENT_CYCLE) 			options = "shape=ellipse,style=filled,color=\"#5DA1D0\"";
    	else if (this.getType() == TYPE_STABLE_STATE) 			options = "shape=box,style=filled, width=\"1.1\", height=\"1.1\",color=\"#F5AC6F\"";
    	else if (this.getType() == TYPE_TERMINAL_CYCLE) 		options = "shape=circle,style=filled, width=\"1.1\", height=\"1.1\",color=\"#004B88\"";
    	else if (this.getType() == TYPE_TRANSIENT_COMPONENT) 	options = "shape=box,style=filled, width=\"1.1\", height=\"1.1\",color=\"#229C00\"";
    	else 													options = "shape=point,style=filled,color=\"#F5AC6F\"";
		return  this.getUniqueId()+" [label=\""+this.toString()+"\", "+options+"];";
	}
	
	public String toDot(Object to) {
		return  this.getUniqueId()+" -> "+((HierarchicalNode) to).getUniqueId()+";";
	}

	
/* **************** COMPARABLE ************/	
	public int compareTo(Object arg0) {
		return (int) (this.uid - ((HierarchicalNode)arg0).uid);
	}
}