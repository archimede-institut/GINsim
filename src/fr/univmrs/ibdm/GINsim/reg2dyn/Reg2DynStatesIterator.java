package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Vector;

/**
 * this iterator generates some initial states
 * they are constructing from list of value for each node...
 */
public final class Reg2DynStatesIterator {
	
	int[] state;
	int[] using;
	int nbGenes;
	Vector nodeOrder;
	Vector[] line;
	boolean goon;

	protected Reg2DynStatesIterator(Vector nodeOrder, Vector[] line) {
		this.nodeOrder = nodeOrder;
		this.line = line;
		nbGenes = nodeOrder.size();
		if (nbGenes < 1 | line.length != nbGenes) {
			goon = false;
			return;
		}
		goon = true;
		
		state = new int[nbGenes];
		using = new int[nbGenes];
		for(int i=0 ; i<nbGenes ; i++){
		    // initialize all genes on their first value
		    state[i] = ((Integer)line[i].get(0)).intValue();
		}
	}
	
	/**
	 * 
	 * @return true if other state can be generated
	 */
	public boolean hasNext() {
		return goon;
	}

	/**
	 * 
	 * @return the next state
	 */
	public int[] next() {
		if (!goon) {
			return null;
		}

        int[] ret = new int[nbGenes];
        for (int i=0 ; i<nbGenes ; i++) {
            ret[i] = state[i];
        }

		// go to the next one
		goon = false;
		for (int i=0 ; i<nbGenes ; i++) {
				
			if (using[i] < line[i].size()-1) {
				using[i]++;
				state[i] = ((Integer)line[i].get(using[i])).intValue();
				for (int j=0 ; j<i ; j++) {
					using[j] = 0;
                    state[j] = ((Integer)line[j].get(using[j])).intValue();
				}
				goon = true;
				break;
			}
		}
		return ret;
	}

}
