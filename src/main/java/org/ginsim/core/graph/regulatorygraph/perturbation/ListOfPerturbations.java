package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.*;

import org.colomoto.biolqm.NodeInfo;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.GraphChangeType;
import org.ginsim.core.graph.GraphEventCascade;
import org.ginsim.core.graph.GraphListener;
import org.ginsim.core.graph.objectassociation.UserSupporter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/**
 * The list of perturbations associated to a regulatory graph.
 * It contains two separate lists for perturbations of single components and multiple perturbations.
 * For convenience, it provides an iterator for all perturbations.
 * 
 * Perturbations should be added using specialised methods.
 * 
 * @author Aurelien Naldi
 */
public class ListOfPerturbations extends ArrayList<Perturbation> implements GraphListener<RegulatoryGraph>, UserSupporter {


	private final Map<String, Perturbation> perturbationUsers = new HashMap<String, Perturbation>();
	private final Map<String, Perturbation> aliases = new HashMap<String, Perturbation>();
	
	private final RegulatoryGraph lrg;
	
	public ListOfPerturbations(RegulatoryGraph lrg) {
		this.lrg = lrg;
		GSGraphManager.getInstance().addGraphListener(lrg, this);
	}
	
	/**
	 * Add a perturbation to fix a component.
	 * 
	 * @param component
	 * @param value
	 * @return
	 */
	public Perturbation addFixedPerturbation(NodeInfo component, int value) {
		Perturbation p = new PerturbationFixed(component, value);
		return addPerturbation(p);
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
		return addPerturbation(p);
	}

	/**
	 * Add a perturbation to fix a component.
	 * 
	 * @param component
	 * @param value
	 * @return
	 */
	public Perturbation addRegulatorPerturbation(NodeInfo regulator, NodeInfo component, int value) {
		Perturbation p = new PerturbationRegulator(regulator, component, value);
		return addPerturbation(p);
	}

	public Perturbation addMultiplePerturbation(List<Perturbation> perturbations) {
		if (!containsAll(perturbations)) {
			LogManager.debug("unknown perturbations when adding multiple...");
		}
		Perturbation p = new PerturbationMultiple(perturbations);
		return addPerturbation(p);
	}
	
	/**
	 * Add a new simple perturbation.
	 * First lookup if it exists to avoid duplicates.
	 * 
	 * @param p
	 * @return the added perturbation or an existing equivalent one.
	 */
	private Perturbation addPerturbation(Perturbation p) {
		if (p == null) {
			throw new RuntimeException("Can not add an undefined perturbation");
		}
		
		// look for a similar existing perturbation
		for (Perturbation other: this) {
			if (other.equals(p)) {
				return other;
			}
		}
		
		// no equivalent perturbation way found: add it
		add(p);
		lrg.fireGraphChange(GraphChangeType.ASSOCIATEDUPDATED, this);
		return p;
	}
	
	/**
	 * Set an alias for a perturbation.
	 * This is used to retrieve named perturbations from the old zginml files.
	 * 
	 * @param name
	 * @param perturbation
	 */
	@Deprecated
	public void setAliases(String name, Perturbation perturbation) {
		if (name == null || perturbation == null) {
			return;
		}
		if (name.equals(perturbation.toString())) {
			return;
		}
		if (aliases.containsKey(name)) {
			LogManager.debug("Duplicated perturbation name: "+name);
			return;
		}
		aliases.put(name, perturbation);
	}
	
	/**
	 * Get a perturbation by its (old) name.
	 * This should only be used for compatibility with the old zginml files where perturbations are named. 
	 * 
	 * @param name the name of the perturbation
	 * @return the corresponding perturbation
	 */
	@Deprecated
	public Perturbation get(String name) {
		return aliases.get(name);
	}

	/**
	 * Announce the use of a perturbation.
	 * 
	 * @param key identify the user
	 * @param perturbation the used perturbation (must be in the list of perturbations)
	 */
	public void usePerturbation(String key, Perturbation perturbation) {
		if (key == null) {
			return;
		}
		
		if (perturbation == null) {
			perturbationUsers.remove(key);
			return;
		}
		
		if (!contains(perturbation)) {
			throw new RuntimeException("Can only use existing perturbations");
		}
		
		perturbationUsers.put(key, perturbation);
	}

	/**
	 * Retrieve the perturbation used by a specific user.
	 * 
	 * @param key identify the user
	 * @return the used perturbation or null if none was found
	 */
	public Perturbation getUsedPerturbation(String key) {
		return perturbationUsers.get(key);
	}
	
	public void toXML(XMLWriter out) throws IOException {
		
        out.openTag("perturbationConfig");
        
        out.openTag("listOfPerturbations");
        for (Perturbation p: this) {
        	// wrap them
            out.openTag("mutant");
            out.addAttr("name", p.toString());
            p.toXML(out);
            out.closeTag();
        }
        out.closeTag();

        out.openTag("listOfUsers");
        for (String key: perturbationUsers.keySet()) {
        	out.openTag("user");
        	out.addAttr("key", key);
        	out.addAttr("value", perturbationUsers.get(key).toString());
        	out.closeTag();
        }
        out.closeTag();
        
        out.closeTag();
	}

	public NodeInfo[] getNodes() {
		List<RegulatoryNode> nodes = lrg.getNodeOrder();
		NodeInfo[] ret = new NodeInfo[nodes.size()];
		int idx = 0;
		for (RegulatoryNode node: nodes) {
			ret[idx++] = node.getNodeInfo();
		}
		return ret;
	}

    public RegulatoryGraph getGraph() {
        return lrg;
    }

	@Override
	public GraphEventCascade graphChanged(RegulatoryGraph g, GraphChangeType type, Object data) {
		switch (type) {
		case NODEREMOVED:
			NodeInfo ni = ((RegulatoryNode)data).getNodeInfo();

            // search perturbations to remove
            List<Perturbation> removed = new ArrayList<Perturbation>();
            for (Perturbation p: this) {
                if (p.affectsNode(ni)) {
                    removed.add(p);
                }
            }

            if (removed.size() > 0) {
                removeAll(removed);
				lrg.fireGraphChange(GraphChangeType.ASSOCIATEDUPDATED, this);
			}
		}
		return null;
	}

	public void removePerturbations(Collection<Perturbation> removed) {
		List<Perturbation> extraRemoved = new ArrayList<Perturbation>();
		for (Perturbation p: this) {
			if (p instanceof PerturbationMultiple) {
				PerturbationMultiple pm = (PerturbationMultiple)p;
				for (Perturbation pi: removed) {
					if (pm.perturbations.contains(pi)) {
						extraRemoved.add(p);
						break;
					}
				}
			}
		}
        removeAll(extraRemoved);
		removeAll(removed);

		// also remove references to the removed perturbations 
		List<String> userRemoved = new ArrayList<String>();
		for (String key: perturbationUsers.keySet()) {
			Perturbation p = perturbationUsers.get(key);
			if (p == null || removed.contains(p) || extraRemoved.contains(p)) {
				userRemoved.add(key);
			}
		}
		for (String key: userRemoved) {
			perturbationUsers.remove(key);
		}
		
		lrg.fireGraphChange(GraphChangeType.ASSOCIATEDUPDATED, this);
	}

	@Override
	public void update(String oldID, String newID) {
		Perturbation p = perturbationUsers.get(oldID);
		if (p != null && newID != null) {
			perturbationUsers.put(newID, p);
		}
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