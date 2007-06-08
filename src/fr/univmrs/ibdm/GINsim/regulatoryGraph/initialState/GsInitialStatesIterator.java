package fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.reg2dyn.Reg2DynStatesIterator;

/**
 * this iterator generates some initial states
 * they are constructing from list of value for each node...
 * TODO: this could deal with nicer/smaller code base: 
 *   * merge with Reg2DynStatesIterator instead of reusing it
 *   * deprecate/remove Reg2DynFullIterator
 */
public final class GsInitialStatesIterator implements Iterator {
	
	Iterator it_line;
	Iterator it_state;
	Vector nodeOrder;
	boolean goon = false;;

	public GsInitialStatesIterator(Vector nodeOrder, Map m_line) {
		this.nodeOrder = nodeOrder;
		if (m_line != null && !m_line.isEmpty()) {
			it_line = m_line.keySet().iterator();
			it_state = new Reg2DynStatesIterator(nodeOrder, ((GsInitialState)it_line.next()).getMap());
		} else {
			it_state = new Reg2DynStatesIterator(nodeOrder, new HashMap());
		}
		goon = it_state.hasNext();
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
		if (goon) {
			int[] next = (int[])it_state.next();
			if (!it_state.hasNext()) {
				if (it_line == null || !it_line.hasNext()) {
					goon = false;
				} else {
					it_state = new Reg2DynStatesIterator(nodeOrder, ((GsInitialState)it_line.next()).getMap());
				}
			}
	        return next;
		}
		return null;
	}
	public void remove() {
		// not implemented
	}
}
