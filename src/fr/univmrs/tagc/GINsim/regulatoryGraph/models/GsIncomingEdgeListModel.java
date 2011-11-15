package fr.univmrs.tagc.GINsim.regulatoryGraph.models;

import java.util.Collection;

import javax.swing.AbstractListModel;

import org.ginsim.graph.regulatorygraph.GsRegulatoryEdge;
import org.ginsim.graph.regulatorygraph.GsRegulatoryMultiEdge;



/**
 * Model for JList containing a list GsDirectedEdge
 * add display all regulaion data
 */
public class GsIncomingEdgeListModel extends AbstractListModel {
	
	private static final long serialVersionUID = 6359093601750388688L;
	//list of incoming edge
	private Collection<GsRegulatoryMultiEdge> edge;
	//size of the list
	private int size;
	
	
	/**
	 * default constructor
	 */
	public GsIncomingEdgeListModel() {
		super();
		edge = null;
	}

	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * recalculate the size of the list
	 * 
	 */
	public void updateSize() {
		if (edge==null) {
			//no data
			size=0;
			return;
		} 
		int count=0; 
		for (GsRegulatoryMultiEdge me: edge) {
			//increase the size of the number of interaction 
			count += me.getEdgeCount();
		}
		size=count;
	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		if (edge==null) {
			return null; //no data
		}
		int j=0;
		for (GsRegulatoryMultiEdge me: edge) {
			if (me.getEdgeCount() + j <= index) {
				//if index is not in the current edge
				j += me.getEdgeCount();
			} else {
				//return the good data
				return me.getEdge(index-j);
			}
		}
		return null;
	}

	/**
	 * get the list of incoming edge
	 * @return the list of incoming edge
	 */
	public Collection<GsRegulatoryMultiEdge> getEdge() {
		return edge;
	}

	/**
	 * set the list of incoming edge
	 * @param list the list of incoming edge
	 */
	public void setEdge(Collection<GsRegulatoryMultiEdge> list) {
		edge = list;
		updateSize();
		fireContentsChanged(this,0,size);
	}
	
	/**
	 * get the index in list of a GsEdgeIndex
	 * @param e an GsEdgeIndex object
	 * @return it's index in the edge list
	 */
	public int getIndex(GsRegulatoryEdge e) {
		if (edge == null) {
            return 0;
        }
		int j = 0;
		for (GsRegulatoryMultiEdge me: edge) {
			if (e.me == me) {
				return j+e.index;
			}
			j += me.getEdgeCount();				
		}
		return 0;
	}
}
