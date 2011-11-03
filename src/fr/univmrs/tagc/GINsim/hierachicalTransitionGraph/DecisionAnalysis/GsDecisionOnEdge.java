package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.DecisionAnalysis;

import java.util.List;

import fr.univmrs.tagc.GINsim.data.ToolTipsable;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * 
 * Store a label that represent the genes that are updated between two states source, and target.
 * In a HTG, to compute the label of an edge, call this function for each couple of states corresponding to the edge.
 */
public class GsDecisionOnEdge implements ToolTipsable {
	
    private static final int CHANGE_NONE = 0;
	private static final int CHANGE_INCREASE = 1;
	private static final int CHANGE_DECREASE = -1;
	private static final int CHANGE_BOTH = 3;
	private int[] genesUpdated;
	private List<GsRegulatoryVertex> nodeOrder;

	/**
	 * Initialize a new label
	 * @param geneCount the count of genes in the LRG
	 */
	public GsDecisionOnEdge(int geneCount, List<GsRegulatoryVertex> nodeOrder) {
        this.genesUpdated = new int[geneCount];
        this.nodeOrder = nodeOrder;
    }
	
	/**
	 * Compute the changes of a given edge (source, target)
	 * @param source
	 * @param target
	 */
	public void computeChange(byte[] source, byte[] target) {
		for (int i = 0; i < source.length; i++) {
			int change = target[i] - source[i];
			if (change != 0) {
				int currentChange = genesUpdated[i];
				if (currentChange != change) {
					if (currentChange == CHANGE_NONE) {
						genesUpdated[i] = change;
					} else if (currentChange != CHANGE_BOTH) {
						genesUpdated[i] = CHANGE_BOTH;
					}
				}
			}
		}
	}
	

	/**
	 * Return a string representation of the label with the name of the genes.
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < genesUpdated.length; i++) {
			switch (genesUpdated[i]) {
			case CHANGE_NONE: break;
			case CHANGE_DECREASE: 
				if (s.length() > 0) s.append(' ');
				s.append(nodeOrder.get(i));
				s.append('-');
				break;
			case CHANGE_INCREASE: 
				if (s.length() > 0) s.append(' ');
				s.append(nodeOrder.get(i));
				s.append('+');
				break;
			case CHANGE_BOTH: 
				if (s.length() > 0) s.append(' ');
				s.append(nodeOrder.get(i));
				s.append('x');
				break;
			}
		}
		return s.toString();
	}

	@Override
	public String toToolTip() {
		return toString();
	}
}
