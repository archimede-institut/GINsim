package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.xml.XMLWriter;

/**
 * The list of perturbations.
 * It behaves like a single list, but it contains two separate lists for
 * perturbations of single components and multiple perturbations.
 * As a result, not all list operations are supported,
 * iterator() and get(int) are the main intended uses.
 * 
 * perturbations should be added using specialised methods.
 * 
 * @author Aurelien Naldi
 */
public class RegulatoryMutants implements List<Perturbation>, Iterable<Perturbation> {

	private final List<Perturbation> simplePerturbations = new ArrayList<Perturbation>();
	private final List<Perturbation> multiplePerturbations = new ArrayList<Perturbation>();

	
	/**
	 * Add a perturbation to fix a component.
	 * 
	 * @param component
	 * @param value
	 * @return
	 */
	public Perturbation addFixedPerturbation(NodeInfo component, int value) {
		Perturbation p = new PerturbationFixed(component, value);
		return addSimplePerturbation(p);
	}

	/**
	 * Add a perturbation to fix the value of a component in a range.
	 * 
	 * @param component
	 * @param min
	 * @param max
	 * @return
	 */
	public Perturbation addRangePerturbation(NodeInfo component, int min, int max) {
		if (min == max) {
			return addFixedPerturbation(component, min);
		}
		
		Perturbation p = new PerturbationRange(component, min, max);
		return addSimplePerturbation(p);
	}

	public Perturbation addMultiplePerturbation(List<Perturbation> perturbations) {
		if (!simplePerturbations.containsAll(perturbations)) {
			LogManager.debug("unknown perturbations when adding multiple...");
		}
		Perturbation p = new PerturbationMultiple(perturbations);
		multiplePerturbations.add(p);
		return p;
	}
	
	/**
	 * Add a new simple perturbation.
	 * First lookup if it exists to avoid duplicates.
	 * 
	 * @param p
	 * @return the added perturbation or an existing equivalent one.
	 */
	private Perturbation addSimplePerturbation(Perturbation p) {
		if (p == null) {
			throw new RuntimeException("Can not add an undefined perturbation");
		}
		
		// for for an existing perturbation
		for (Perturbation other: simplePerturbations) {
			if (other.equals(p)) {
				return other;
			}
		}
		
		// no equivalent perturbation way found: add it
		simplePerturbations.add(p);
		return p;
	}
	

	@Override
	public int size() {
		return simplePerturbations.size() + multiplePerturbations.size();
	}

	@Override
	public Perturbation get(int index) {
		int nbsimple = simplePerturbations.size();
		if (index < nbsimple) {
			return simplePerturbations.get(index);
		}
		
		return multiplePerturbations.get(index-nbsimple);
	}


	@Override
	public Iterator<Perturbation> iterator() {
		if (multiplePerturbations.size() == 0) {
			return simplePerturbations.iterator();
		}
		return new JoinedIterator<Perturbation>(simplePerturbations.iterator(), multiplePerturbations.iterator());
	}


	@Override
	public void clear() {
		multiplePerturbations.clear();
		simplePerturbations.clear();
	}

	@Override
	public boolean contains(Object o) {
		return simplePerturbations.contains(o) || multiplePerturbations.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o: c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int indexOf(Object o) {
		int idx = simplePerturbations.indexOf(o);
		if (idx >= 0) {
			return idx;
		}

		idx = multiplePerturbations.indexOf(o);
		if (idx >= 0) {
			return idx + simplePerturbations.size();
		}
		return -1;
	}


	@Override
	public boolean isEmpty() {
		return simplePerturbations.isEmpty() && multiplePerturbations.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		int idx = multiplePerturbations.lastIndexOf(o);
		if (idx >= 0) {
			return simplePerturbations.size() + idx;
		}

		return simplePerturbations.lastIndexOf(o);
	}

	@Override
	public boolean remove(Object o) {
		boolean removed = multiplePerturbations.remove(o);
		if (removed) {
			return removed;
		}
		
		// FIXME: clean removal of simple perturbations?
		return false;
	}


	@Override
	public Perturbation remove(int index) {
		int nbsimple = simplePerturbations.size();
		if (index >= nbsimple) {
			multiplePerturbations.remove(index-nbsimple);
		}
		
		// FIXME: clean removal of simple perturbations?
		return null;
	}

	
	public void toXML(XMLWriter out) throws IOException {
		
        out.openTag("mutantList");
        for (Perturbation p: simplePerturbations) {
        	// wrap them
            out.openTag("mutant");
            out.addAttr("name", p.toString());
            p.toXML(out);
            out.closeTag();
        }
        for (Perturbation p: multiplePerturbations) {
            p.toXML(out);
        }
        out.closeTag();

		
	}

	
	@Override
	public boolean add(Perturbation e) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void add(int index, Perturbation element) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(Collection<? extends Perturbation> c) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(int index, Collection<? extends Perturbation> c) {
		throw new UnsupportedOperationException();
	}

	
	@Override
	public ListIterator<Perturbation> listIterator() {
		throw new UnsupportedOperationException();
	}
	@Override
	public ListIterator<Perturbation> listIterator(int index) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	@Override
	public Perturbation set(int index, Perturbation element) {
		throw new UnsupportedOperationException();
	}
	@Override
	public List<Perturbation> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}
}


class JoinedIterator<T> implements Iterator<T> {

	private final Iterator<T> it1, it2;

	public JoinedIterator(Iterator<T> it1, Iterator<T> it2) {
		this.it1 = it1;
		this.it2 = it2;
	}
	
	@Override
	public boolean hasNext() {
		return it1.hasNext() || it2.hasNext();
	}

	@Override
	public T next() {
		if (it1.hasNext()) {
			return it1.next();
		}
		return it2.next();
	}

	@Override
	public void remove() {
		throw new RuntimeException("Remove not supported");
	}
	
}