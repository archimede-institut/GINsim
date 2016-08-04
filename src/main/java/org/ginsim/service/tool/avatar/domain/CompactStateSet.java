package org.ginsim.service.tool.avatar.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.colomoto.logicalmodel.io.avatar.AvatarUtils;

/**
 * Representation of a state-set recurring to a compact set of patterns defining the internal states
 * 
 * @author Rui Henriques
 * @version 1.0
 */
public class CompactStateSet extends AbstractStateSet {

	private List<byte[]> states = new ArrayList<byte[]>();
	
	/**
	 * Creates a state-set using the provided state patterns and unique identifier 
	 * @param hashkey key identifying the state-set (state-sets with the same states share the same key)
	 * @param statepatterns a list of patterns representing the states of a given state set<br>
	 *        &emsp;&emsp;a pattern is represented as a byte array, where each position defines the possible states of a component (according to a well defined-order)<br>
	 *        &emsp;&emsp;when all the states of a given component are allowed to occur, the associated position in byte array is specified as -1
	 */
	public CompactStateSet(String hashkey, List<byte[]> statepatterns){
		setKey(hashkey);
		states = statepatterns; 
	}
	
	/**
	 * Accesses the states in the state-set
	 * @return states in the state-set
	 */
	public List<byte[]> getStates(){
		return states;
	}
	
	/**
	 * Checks whether there are states in the state-set
	 * @return true if the state-set has no states
	 */
	public boolean isEmpty() { 
		return states.isEmpty(); 
	}
	
	/* (non-Javadoc)
	 * @see org.ginsim.service.tool.avatar.domain.AbstractStateSet#contains(org.ginsim.service.tool.avatar.domain.State)
	 */
	public boolean contains(State state) {
		byte[] s1 = state.state;
		for(byte[] s2 : states)
			if(contains(s2,s1)) return true;
		return false; 
	}
	private boolean contains(byte[] s2, byte[] s1) {
		for(int i=0, l=Math.min(s1.length,s2.length); i<l; i++)
			if(s2[i]!=-1 && s1[i]!=s2[i]) return false;
		return true;
	}
	
	@Override
	public String toString() { 
		return AvatarUtils.toString(states); 
	}

	@Override
	public int size() {
		return states.size();
	}

	@Override
	public Collection<String> getKeys() {
		return null;
	}
}
