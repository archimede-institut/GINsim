package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.util.*;

public class LogicalParameterList extends AbstractList implements List {

	private List fromFunctions = new ArrayList();
	private List manual = new ArrayList();
	int nbDup = 0;
	boolean updateDup = true;
	
	public boolean addLogicalParameter (GsLogicalParameter newParam, boolean isManual) {
		List l, l2;
		if (isManual) {
			l = manual;
			l2 = fromFunctions;
		} else {
			l = fromFunctions;
			l2 = manual;
		}
		Iterator it = l.iterator();
		while (it.hasNext()) {
			if (it.next().equals(newParam)) {
				return false;
			}
		}
		l.add(newParam);
		findDup(newParam, l2);
		return true;
	}

	
	/**
	 * Cleanup duplicated parameters: find them and 
	 * remove them from the list of manually entered parameters.
	 * This is used by the parser to avoid some mess.
	 */
	public void cleanupDup() {
		Iterator it = fromFunctions.iterator();
		while (it.hasNext()) {
			GsLogicalParameter param = (GsLogicalParameter)it.next();
			param.isDup = false;
			param.hasConflict = false;
		}
		for (int i=manual.size()-1 ; i>=0 ; i--) {
			GsLogicalParameter param = (GsLogicalParameter)manual.get(i);
			param.isDup = false;
			param.hasConflict = false;
			it = fromFunctions.iterator();
			while (it.hasNext()) {
				GsLogicalParameter other = (GsLogicalParameter)it.next();
				if (param.equals(other)) {
					if (other.getValue() == param.getValue()) {
						manual.remove(i);
					} else {
						param.hasConflict = true;
						other.hasConflict = true;
					}
					break;
				}
			}
		}
		updateDup = true;
	}
	
	public int size() {
		return manual.size() + fromFunctions.size();
	}
	
	public Object get(int index) {
		int s1 = manual.size();
		if (index < s1) {
			return manual.get(index);
		}
		return fromFunctions.get(index-s1);
	}
	
	public Object remove(int index) {
		if (index < manual.size()) {
			GsLogicalParameter param = (GsLogicalParameter)manual.remove(index);
			if (param != null && (param.isDup || param.hasConflict)) {
				refreshDupAndConflicts();
			}
			return param;
		}
		return null;
	}

	public void setFunctionParameters(List logicalParameters) {
		this.fromFunctions = logicalParameters;
		refreshDupAndConflicts();
	}
	
	/**
	 * visit the two lists of logical parameters, looking for duplicates and conflicts
	 */
	private void refreshDupAndConflicts() {
		if (!updateDup) {
			return;
		}
		nbDup = 0;
		Iterator it = manual.iterator();
		while (it.hasNext()) {
			GsLogicalParameter param = (GsLogicalParameter)it.next();
			param.isDup = false;
			param.hasConflict = false;
		}
		it = fromFunctions.iterator();
		while (it.hasNext()) {
			findDup((GsLogicalParameter)it.next(), manual);
		}
	}
	
	public Iterator iterator() {
		return new LogicalParameterIterator(manual.iterator(), fromFunctions.iterator());
	}

	public Iterator iterator(boolean manual) {
		if (manual) {
			return this.manual.iterator();
		}
		return fromFunctions.iterator();
	}


	public void updateInteraction(int index, Vector edges) {
		if (index >= manual.size()) {
			return;
		}
	    GsLogicalParameter I = (GsLogicalParameter)manual.get(index);
	    List oldList = I.getEdges();
	    I.setEdges(edges);
		for (int i=0 ; i<manual.size() ; i++) {
			if ( i!= index && manual.get(i).equals(I)) {
			    I.setEdges(oldList);
				return;
			}
		}
		refreshDupAndConflicts();
	}
	
	private void findDup(GsLogicalParameter param, List l) {
		if (!updateDup) {
			return;
		}
		param.isDup = false;
		param.hasConflict = false;
		Iterator it = l.iterator();
		while (it.hasNext()) {
			GsLogicalParameter p = (GsLogicalParameter)it.next();
			if (p.equals(param)) {
				nbDup++;
				if (p.getValue() == param.getValue()) {
					p.isDup = param.isDup = true;
				} else {
					p.hasConflict = param.hasConflict = true;
				}
				break;
			}
		}
	}


	public int getRealSize() {
		return size()-nbDup;
	}

	public int getManualSize() {
		return manual.size();
	}


	public void cleanupInteraction() {
		for (int i=manual.size()-1 ; i>=0 ; i--) {
			if (((GsLogicalParameter)manual.get(i)).isDurty()) {
				manual.remove(i);
			}
		}
	}

	public void applyNewMaxValue(short max, GsRegulatoryGraph graph, List l) {
		Iterator it = manual.iterator();
		while (it.hasNext()) {
			GsLogicalParameter param = (GsLogicalParameter)it.next();
			if (param.getValue() > max) {
				l.add(param);
			}
		}
	}

	public void setParameterValue(int rowIndex, int value,
			GsRegulatoryGraph graph) {
		if (rowIndex >= manual.size()) {
			return;
		}
		GsLogicalParameter param = (GsLogicalParameter)manual.get(rowIndex);
		param.setValue(value, graph);
		if (param.hasConflict || param.isDup) {
			findDup(param, fromFunctions);
		}
	}
	
	public void setUpdateDup(boolean updateDup) {
		this.updateDup = updateDup;
		refreshDupAndConflicts();
	}

	public boolean moveElement(int index, int to) {
		if (index < 0 || index > manual.size() || to < 0 || to >= manual.size()) {
			return false;
		}
        Object obj=manual.remove(index);
        manual.add(to, obj);

		return true;
	}
}

class LogicalParameterIterator implements Iterator {

	Object next = null;
	Iterator it_manual;
	Iterator it_func;
	
	
	public LogicalParameterIterator(Iterator it_manual, Iterator it_func) {
		this.it_manual = it_manual;
		this.it_func = it_func;
		if (it_manual.hasNext()) {
			next = it_manual.next();
		} else {
			while (it_func.hasNext()) {
				GsLogicalParameter p = (GsLogicalParameter)it_func.next();
				if (!p.hasConflict && !p.isDup) {
					next = p;
					break;
				}
			}
		}
	}

	public boolean hasNext() {
		return next != null;
	}

	public Object next() {
		Object ret = next;
		if (it_manual.hasNext()) {
			next = it_manual.next();
		} else {
			next = null;
			while (it_func.hasNext()) {
				GsLogicalParameter p = (GsLogicalParameter)it_func.next();
				if (!p.hasConflict && !p.isDup) {
					next = p;
					break;
				}
			}
		}
		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
