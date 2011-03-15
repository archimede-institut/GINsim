package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.xml.sax.SAXException;

import fr.univmrs.tagc.GINsim.export.generic.Dotify;
import fr.univmrs.tagc.common.GsException;

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



/**
 * <p>Define the nodes of the Hierarchical Transition Graph.</p>
 * 
 * <p>A node has a unique id <b>uid</b>, a <b>type</b> defining the kind of Strongly Connected Component 
 * it contains and <b>statesSet</b>, the set of all the states it contains.</p>
 * <p>During its construction</p>
 * 
 * @author Duncan Berenguier
 *
 */
public class GsHierarchicalNode implements Dotify {

	/**
	 * Defining the node type
	 */
	public static final byte TYPE_TRANSIENT_COMPONENT = 0;
	public static final byte TYPE_TRANSIENT_CYCLE = 1; //FIXME: Rename me to TRANSIENT_CYCLE
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
	public static final Color TYPE_TRANSIENT_COMPONENT_ALONE_COLOR = new Color(175, 255, 86);

	/**
	 * OmddNode status to indicate the state is present in the GsStateSet but unprocessed 
	 */
	public static final int STATUS_UNPROCESSED = 1;
	/**
	 * OmddNode status to indicate the state is present in the GsStateSet and processed 
	 */
	public static final int STATUS_PROCESSED = 2;
	
	
	/**
	 * A static long used to give a unique id to each HN
	 */
	private static long nextId = 2;

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
	public GsStatesSet statesSet;

	/**
	 * A list of states (byte[]) that are processing currently, and to add later.
	 */
	private List statePile = null;
	
	/**
	 * Count of processed states in this node (a node is processed when all its childs are processed.
	 */
	private int processed = 0;

	/**
	 * Count of states in this node.
	 */
	private int size = 0;

	
	/**
	 * Set of nodes for 'in'coming edges, that is a HashSet&lt;GsDirectedEdge&lt;GsHierarchicalNode, GsHierarchicalNode&gt;&gt;
	 */
	private Set in;

	/**
	 * The atteignability in terms of attractors
	 */
	private Set sigma;
	
	/**
	 * An array indicating such that childsCount[i] indicates the maxValue of the i-th gene
	 */
	private byte[] childsCount;

	/**
	 * An array indicating such that childsCount[i] indicates the maxValue of the i-th gene
	 */
	private GsHierarchicalNode master;

	
/* **************** CONSTRUCTORS ************/	
	
	/**
	 * Default constructor.
	 * Initialize a new node with a good uid, and type TYPE_TRANSIENT_COMPONENT
	 * By default the state set is not initialized
	 */
	public GsHierarchicalNode(byte[] childsCount) {
		this.statesSet = null;
		this.uid = nextId;
		nextId = nextId*2-1;
		this.type = TYPE_TRANSIENT_COMPONENT;
		this.childsCount = childsCount;
		this.master = this;
	}
	
/* **************** PILE ************/	

	/**
	 * Perform a mergeMultiple on all the omdd in the pile and the root.
	 * @param childsCount
	 */
	public void addAllTheStatesInPile() {
		if (statePile == null) return;
		if (statesSet == null) statesSet = new GsStatesSet(childsCount);
		statesSet.addStates(statePile);
		statePile = null;
		updateSize();
	}

	/**
	 * Add a new state to the pile.
	 * Note the new state is not added to the GsHierarchicalNode omdd aiming to add several omdd at the same time.
	 * 
	 * @see addPileToOmdd()
	 * 
	 * @param state the state to add
	 */
	public void addStateToThePile(byte[] state) {
		if (statePile == null) statePile = new LinkedList();
		statePile.add(state);
	}
	
	/**
	 * Add a state directly to the stateSet.
	 * 
	 * @param state
	 * @param statusProcessed
	 */
	public void addState(byte[] state, int statusProcessed) {
		if (statesSet == null) statesSet = new GsStatesSet(childsCount);
		statesSet.addState(state, statusProcessed);
	}

	
/* **************** CONTAINS ************/	
	
	
	/**
	 * Indicates if the GsHierarchicalNode contains a state by searching both the pile and the omdd
	 * @param state
	 * @return true if it contains the state
	 */
	public boolean contains(byte [] state) {
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
	 * @param childsCount 
	 * @param helper 
	 */
	public void merge(GsHierarchicalNode slaveNode, Collection nodeSet) {// throws Exception {
		if (slaveNode == this) return;
		slaveNode = slaveNode.master;
//		if (slaveNode.type != this.type) {
//			throw new Exception("Error merging two node of different types : "+slaveNode.toLongString()+" in "+this.toLongString()); //FIXME : remove me
//		}
		if (this.statesSet != null && slaveNode.statesSet != null) { 			//Merge the set of states
			this.statesSet.merge(slaveNode.statesSet);
		}
		
		if (this.in == null) {													//Merge the set of edges
			this.in = slaveNode.in;
		} else {
			if (slaveNode.in != null) {
				this.in.addAll(slaveNode.in);
				slaveNode.in = this.in;
			}
		}
				
		if (slaveNode.statePile != null) {										//Merge the piles of states
			if (this.statePile == null) {
				this.statePile = slaveNode.statePile;
			} else {
				this.statePile.addAll(slaveNode.statePile); //merging the statePile
			}
		}
		nodeSet.remove(slaveNode);												//Make slaveNode a slaveNode !
		slaveNode.master = this;
	}
	

	
/* **************** SIZE ************/	

	
	public int getProcessedStates() {
		return processed;
	}
	
	public boolean isProcessed() throws GsException {
		if (statePile == null && size < processed) {
			throw new GsException(1, "Error size < processes Childs : "+this);
		}
		return statePile == null && size == processed;
	}
	
	public void processState(byte[] state) {
		statesSet.updateStatus(state, STATUS_PROCESSED);
		processed++;
	}

	
	public int getSize() {
		return size;
	}

	public void updateSize() {
		size = 0;
		processed = 0;
		if (statePile != null) {
			size = statePile.size();
		}
		int[] counts = statesSet.updateSize();
		size += counts[STATUS_UNPROCESSED] + counts[STATUS_PROCESSED];
		processed += counts[STATUS_PROCESSED];
		
	}
	

	/* **************** EDGES, ID AND SIGMA ************/	

	/**
	 * Return the set of incoming edges HashSet&lt;GsDirectedEdge&lt;GsHierarchicalNode, GsHierarchicalNode&gt;&gt;
	 */
	public Set getIncomingEdges() {
		if (master.in == null) {
			master.in = new HashSet();
		}
		return master.in;
	}

	/**
	 * Add a new incoming edge GsDirectedEdge&lt;GsHierarchicalNode, GsHierarchicalNode&gt;
	 * @param o
	 */
	public void addIncomingEdge(Object o) {
		if (master.in == null) {
			master.in = new HashSet();
		}
		master.in.add(o);
	}

	/**
	 * Add an edge between the masters nodes
	 * @param to
	 * @param htg
	 * @return true if an edge was added (no autoregulation
	 */
	public boolean addEdge(GsHierarchicalNode to, GsHierarchicalTransitionGraph htg) {
		if (!master.equals(to.master)) {
			htg.addEdge(master, to.master);
			return true;
		}
		return false;
	}

	
	/**
	 * Release all the references to the set of incoming edges.
	 */
	public void releaseEdges() {
		master.in = null;
	}

	/**
	 * return the set sigma
	 */
	public Set getSigma() {
		if (master.sigma == null) {
			master.sigma = new HashSet();
		}
		return master.sigma;
	}
	/**
	 * return the set sigma
	 */
	public void setSigma(Set sigma) {
		this.master.sigma = sigma;
	}
	
	public void releaseSigma() {
		master.sigma = null;
	}
	
	
	/**
	 * Return the long identifying this node "uniquely" (if in the range of long)
	 * @return
	 */
	public long getUniqueId() {
		return master.uid;
	}
	
	public int hashcode() {
		return (int)master.uid;
	}

	public void parse(String parse) throws SAXException {
		statesSet.parse(parse); //TODO:
	}


	
/* **************** TOSTRINGS ************/	

		public String toString() {
			if (size == 0) {
				updateSize();
			}
			if (size == 1) {
				StringBuffer s = new StringBuffer();
				byte[] t = (byte[]) ((List)statesToList()).get(0);
				for (int i = 0; i < t.length; i++) {
					s.append(String.valueOf(t[i]).charAt(0));
				}
				return s.toString()+(master != this ? "¤{"+master+"}":"");
			} 
			return "#"+size;
		}
		public String toLongString() {
			String name = "";
			if (size == 1) {
				StringBuffer s = new StringBuffer();
				byte[] t = (byte[]) ((List)statesToList()).get(0);
				for (int i = 0; i < t.length; i++) {
					s.append(String.valueOf(t[i]).charAt(0));
				}
				name = ",name: "+s.toString();
			}
			return "{"+processed+"/"+size+name+", type:"+typeToString()+"}";
		}
		public String getShortId() {
			if (size == 1) {
				return ""+hashCode();
			} else {
				return ""+hashCode()+"{"+processed+"/"+size+"}";
			}
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
			}
			res.append(statesSet.statesToString(addValue));
			return res.toString();
		}

	 
	/**
	 * Initialize and fill a list with all the states in the omdd
	 * Each item of the returned list is a string representation using wildcard * (-1).
	 * Note the order in the list is relative to the omdd structure.
	 * @return
	 */
	public List statesToList() {
		List v = statePile;
		if (v == null) {
			v = new LinkedList();
		}
		statesSet.statesToList(v);
		return v;
	}
		
	/**
	 * Return a string representation of the first state in the node.
	 */
	public StringBuffer firstStatesToString() {
		StringBuffer s = new StringBuffer(childsCount.length);
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
			case TYPE_TRANSIENT_CYCLE:
				return TYPE_CYCLE_STRING;
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
		 * @param type is either TYPE_STABLE_STATE_STRING or TYPE_TERMINAL_CYCLE_STRING or TYPE_TRANSIENT_COMPONENT_STRING
		 * @return
		 */
		public static byte typeFromString(String type) {
			if (type.equals(TYPE_STABLE_STATE_STRING)) return TYPE_STABLE_STATE;
			if (type.equals(TYPE_TERMINAL_CYCLE_STRING)) return TYPE_TERMINAL_CYCLE;
			if (type.equals(TYPE_CYCLE_STRING)) return TYPE_TRANSIENT_CYCLE;
			return  TYPE_TRANSIENT_COMPONENT;
		}
		
/* **************** TO DOT (DOTIFY) ************/	
		
		public String toDot() {
			String options;
	    	if (this.getType() == TYPE_TRANSIENT_CYCLE) 				options = "shape=ellipse,style=filled,color=\"#C8E4A5\"";
	    	else if (this.getType() == TYPE_STABLE_STATE) 	options = "shape=box,style=filled, width=\"1.1\", height=\"1.1\",color=\"#9CBAEB\"";
	    	else if (this.getType() == TYPE_TERMINAL_CYCLE) options = "shape=circle,style=filled, width=\"1.1\", height=\"1.1\",color=\"#F5AC6F\"";
	    	else 											options = "shape=point,style=filled,color=\"#00FF00\"";
			return  this.getUniqueId()+" [label=\""+this.toString()+"\", "+options+"];";
		}
		
		public String toDot(Object to) {
			return  this.getUniqueId()+" -> "+((GsHierarchicalNode) to).getUniqueId()+";";
		}

	
	}



//
//
//
//	/**
//	 * A new node with a certain initial state.
//	 * @param state an initial state to add in the graph
//	 * @param childsCount
//	 * @param childValue the value to add at the end of the omdd (default 1).
//	 */
//	public GsDynamicalHierarchicalNode(byte[] state, byte[] childsCount, int childValue) {
//		this.size = 1;
//		if (childValue == 2) {
//			processed = 1;
//			root = stateFromArray(state, childsCount, childValue);
//		} else {
//			root = null;
//			addStateToThePile(state);
//		}
//		this.uid = nextId++;
//	}
//	
//	/**
//	 * A new node with a certain initial state.
//	 * @param state an initial state to add in the graph
//	 * @param childsCount
//	 */
//	public GsDynamicalHierarchicalNode(byte[] state, byte[]childsCount) {
//		this(state, childsCount, 1);
//	}
