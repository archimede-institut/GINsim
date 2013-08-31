package org.ginsim.core.graph.dynamicgraph;

import org.ginsim.core.graph.common.Edge;

public class DynamicEdge extends Edge<DynamicNode> {

	public final ChangeType changeType;
	
	public DynamicEdge(DynamicGraph g, DynamicNode source,	DynamicNode target) {
		super(g, source, target);
		
		ChangeType change = ChangeType.NOCHANGE;
		for (int i=0 ; i<source.state.length ; i++) {
			change = change.update(source.state[i], target.state[i]);
		}
		this.changeType = change;
	}
	
	/**
     * get the coordinate of the first change between the two states.
     * @param diffstate
     * @return
     */
	public int getFirstChangeIndex() {
    	for (int i = 0; i < source.state.length; i++) {
    		if (source.state[i] != target.state[i]) {
    			return i;
    		}
    	}
		return 0;
	}

}
