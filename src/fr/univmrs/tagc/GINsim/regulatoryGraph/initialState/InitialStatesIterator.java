package fr.univmrs.tagc.GINsim.regulatoryGraph.initialState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

public class InitialStatesIterator implements Iterator {

	Iterator helper;
	Iterator helperIterator = null;
	List nodeOrder;
	
	public InitialStatesIterator(List nodeOrder, Map m_initstates) {
		this.nodeOrder = nodeOrder;
		if (m_initstates == null || m_initstates.size() < 1) {
			List v = new ArrayList();
			v.add(new GsInitialState());
			helperIterator = v.iterator();
		} else {
			helperIterator = m_initstates.keySet().iterator();
		}
		helper = new Reg2DynStatesIterator(nodeOrder, 
				((GsInitialState)helperIterator.next()).getMap());
	}
	
	public boolean hasNext() {
		return helper.hasNext();
	}

	public Object next() {
		Object ret = helper.next();
		if (!helper.hasNext() && helperIterator != null && helperIterator.hasNext()) {
			helper = new Reg2DynStatesIterator(nodeOrder,
					((GsInitialState)helperIterator.next()).getMap());
		}
		return ret;
	}

	public void remove() {
		// not supported
	}
}


/**
 * this iterator generates some initial states
 * they are constructing from list of value for each node...
 */
final class Reg2DynStatesIterator implements Iterator {
	
	int[] state;
	int[] using;
	int nbGenes;
	List nodeOrder;
	int[][] line;
	boolean goon;

	public Reg2DynStatesIterator(List nodeOrder, Map m_line) {
		this.nodeOrder = nodeOrder;
        
		line = new int[nodeOrder.size()][];
		for (int i=0 ; i<nodeOrder.size() ; i++) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(i);
			List v_val = (List)m_line.get(vertex);
			if (v_val == null || v_val.size() == 0) {
				line[i] = new int[vertex.getMaxValue()+1];
				for (int j=0 ; j<line[i].length ; j++) {
					line[i][j] = j;
				}
			} else {
				line[i] = new int[v_val.size()];
				for (int j=0 ; j<line[i].length ; j++) {
					line[i][j] = ((Integer)v_val.get(j)).intValue();
				}
			}
		}
        
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
		    state[i] = line[i][0];
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
	public Object next() {
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
				
			if (using[i] < line[i].length-1) {
				using[i]++;
				state[i] = line[i][using[i]];
				for (int j=0 ; j<i ; j++) {
					using[j] = 0;
                    state[j] = line[j][using[j]];
				}
				goon = true;
				break;
			}
		}
		return ret;
	}

	public void remove() {
		// not implemented
	}
}
