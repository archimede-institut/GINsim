package org.ginsim.service.tool.avatar.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.colomoto.biolqm.io.avatar.AvatarLogicalModelException;

/**
 * Approximate representation of the probabilities of the transitions within and going out of a cycle.<br>
 * Useful when assuming that the transitions to the states out of a cycle are equiprobable (Uniform transitions).
 * 
 * @author Rui Henriques
 * @version 1.0
 */
public class ApproximateFinalPaths implements FinalPaths {

	private Map<String,List<Integer>> exitKeys;
	private Map<Integer,List<String>> exitStates; 
	private Map<Integer,Double> exitProbs;
	private int key;

	/**
	 * Creates an empty cycle
	 */
	public ApproximateFinalPaths(){
		exitKeys = new HashMap<String,List<Integer>>();
		exitStates = new HashMap<Integer,List<String>>();
		exitProbs = new HashMap<Integer,Double>();
		key = 0;
	}

	/* (non-Javadoc)
	 * @see org.ginsim.service.tool.avatar.domain.FinalPaths#addOutputPaths(java.util.Collection, java.util.Collection, double)
	 */
	public void addOutputPaths(Collection<String> states, Collection<String> exits, double prob){
		for(String s : states){ 
			if(!exitKeys.containsKey(s)) exitKeys.put(s, new ArrayList<Integer>());
			exitKeys.get(s).add(key);
		}
		List<String> exitsList = new ArrayList<String>(exits);
		exitStates.put(key, exitsList);
		exitProbs.put(key, prob);
		key++;
	}
	
	/* (non-Javadoc)
	 * @see org.ginsim.service.tool.avatar.domain.FinalPaths#addOutputPaths(java.util.Collection, java.util.Collection, double[][])
	 */
	public void addOutputPaths(Collection<String> states, Collection<String> exits, double[][] prob) throws AvatarLogicalModelException{
		throw new AvatarLogicalModelException("Approximate search not compatible with matrices of probabilities");
	}
	
	/* (non-Javadoc)
	 * @see org.ginsim.service.tool.avatar.domain.FinalPaths#getPaths(java.lang.String)
	 */
	public Map<String, Double> getPaths(String state) {
		Map<String,Double> result = new HashMap<String,Double>();
		if(!exitKeys.containsKey(state)) return result;
		for(Integer key : exitKeys.get(state)){
			double prob = exitProbs.get(key);
			for(String exit : exitStates.get(key)){
				if(result.containsKey(exit)) result.put(exit, result.get(exit)+prob);
				else result.put(exit, prob);
			}
		}
		return result;
	}
	
}
