package org.ginsim.service.tool.avatar.domain;

import java.util.Collection;
import java.util.List;

/**
 * Abstract class enclosing a state-set<br>
 * State-sets can be implemented in multiple ways (e.g. lists/maps of states, MDDs, etc.)<br>
 * Subclasses can be defined accordingly<br>
 * 
 * @author Rui Henriques
 * @version 1.0
 */
public abstract class AbstractStateSet {

	private String key;

	/**
	 * Checks whether a given state is contained in the state-set
	 * @param s state to be checked
	 * @return true if the given state is contained in the state-set
	 */
	public abstract boolean contains(State s);
	public abstract int size();
	public abstract Collection<String> getKeys();
	
	/**
	 * Returns the unique identifier of the state-set<br>
	 * If two state-sets have the same states they share the same key
	 * @return the unique identifier of the state-set
	 */
	public String getKey(){
		return key;
	}

	public void setKey(String _key){
		key = _key;
	}
}
