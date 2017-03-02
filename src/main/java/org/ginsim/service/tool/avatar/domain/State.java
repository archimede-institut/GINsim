package org.ginsim.service.tool.avatar.domain;

import org.colomoto.biolqm.io.avatar.AvatarUtils;


public class State {

	public String key="";
	public byte[] state;
	public double probability; 

	public State(byte[] s){ 
		this(s,1);
	}
	public State(byte[] s, double prob){
		key = Dictionary.toKey(s);
		//for(byte i : s) key += i;
		state = s; 
		probability = prob;
	}
	public String toString(){
		return AvatarUtils.toString(state)+"(prob="+probability+",key="+key+")";
	}
	public String toShortString() {
		return AvatarUtils.toString(state)+"(prob="+probability+")";
	}
}
