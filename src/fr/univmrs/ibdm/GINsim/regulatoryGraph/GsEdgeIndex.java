package fr.univmrs.ibdm.GINsim.regulatoryGraph;

/**
 * Simple class for pointing on a specific Edge regulation data in an edge
 */
public class GsEdgeIndex {
    
	/** the designed multiedge */
	public GsRegulatoryMultiEdge data;
    /** index of the real edge in the multiEdge */
	public int index;
		
	/**
	 * @param me a directed edge
	 * @param i of a regulation data in this edge
	 */
	public GsEdgeIndex(GsRegulatoryMultiEdge me,int i) {
		index=i;
		data=me;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof GsEdgeIndex)) {
			return false;
		}
		GsEdgeIndex other = (GsEdgeIndex)o;
		return (data.equals(other.data) && index == other.index);
	}
	
	public String toString() {
		String max="";
		
		if (data.getMax(index)==-1) max="Max";
		else max = ""+data.getMax(index);
		return data.getSource() + " "+index+"  ["+data.getMin(index)+","+max+"]  "
				+ GsRegulatoryEdge.SIGN_SHORT[data.getSign(index)];
	}
	
	public Object clone() {
		return new GsEdgeIndex(data,index);
	}

    /**
     * @return the name of the associated edge
     */
    public String getSEdge() {
        return data.getFullId(index);
    }

}
