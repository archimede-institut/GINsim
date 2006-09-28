package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Vector;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * this iterator generates all states for a regulatory graph
 */
public final class reg2DynFullIterator {
	
	int[] state;
	int nbGenes;
	Vector nodeOrder;
	boolean goon;

	// to inform the progressbar
	long nbStates;
	long statenumber;
	long progressStep;
	Reg2dynFrame frame;

	protected reg2DynFullIterator(Vector nodeOrder, Reg2dynFrame frame) {
		this.nodeOrder = nodeOrder;
		this.frame = frame;
		nbGenes = nodeOrder.size();
		if (nbGenes < 1) {
			goon = false;
			return;
		}
		goon = true;
		
		state = new int[nbGenes];
		// generate the state before the first state and calculate the number of states
		nbStates = ((GsRegulatoryVertex)nodeOrder.get(0)).getMaxValue()+1;
		state[0] = -1;
		for(int i=1 ; i<nbGenes ; i++){
			nbStates *= ((GsRegulatoryVertex)nodeOrder.get(i)).getMaxValue()+1;
			state[i]=0;
		}
		statenumber = 0;
		progressStep = nbStates/100;
		if (progressStep == 0) {
			progressStep++;
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
		
		// try to increment a gene's state
		goon = false;
		for (int i=0 ; i<nbGenes ; i++) {
				
			if (state[i] < ((GsRegulatoryVertex)nodeOrder.elementAt(i)).getMaxValue()) {
				state[i]++;
				for (int j=0 ; j<i ; j++) {
					state[j] = 0;
				}
				goon = true;
				break;
			}
		}
		int[] ret = (int[])state.clone();
		statenumber++;
		return ret;
	}
}
