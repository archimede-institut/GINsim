package fr.univmrs.ibdm.GINsim.regulatoryGraph.models;

import java.util.List;

import javax.swing.AbstractListModel;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsEdgeIndex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;


/**
 * Model for JList containing a list GsDirectedEdge
 * add display all regulaion data
 */
public class GsIncomingEdgeListModel extends AbstractListModel {
	
	private static final long serialVersionUID = 6359093601750388688L;
	//list of incoming edge
	private List edge;
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
		for (int i=0;i<edge.size();i++) {
			//for each incoming edge
			Object obj=edge.get(i);
			if (obj instanceof GsDirectedEdge) {
				GsDirectedEdge de = (GsDirectedEdge)obj;
				//increase the size of the number of interaction 
				count += ((GsRegulatoryMultiEdge)de.getUserObject()).getEdgeCount();
			}
		}
		size=count;
	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		if (edge==null) return null; //no data
		int j=0; 
		for (int i=0;i<edge.size();i++) {
			//for each incoming edge
			Object obj=edge.get(i);
			if (obj instanceof GsDirectedEdge) {
				GsDirectedEdge de=(GsDirectedEdge)obj;
				if (((GsRegulatoryMultiEdge)de.getUserObject()).getEdgeCount() + j <= index) {
					//if index is not in the current edge
					j += ((GsRegulatoryMultiEdge)de.getUserObject()).getEdgeCount();
				} else {
					//return the good data
					return new GsEdgeIndex((GsRegulatoryMultiEdge)de.getUserObject(),index-j);
				}
			}
		}
		return null;
	}

	/**
	 * get the list of incoming edge
	 * @return the list of incoming edge
	 */
	public List getEdge() {
		return edge;
	}

	/**
	 * set the list of incoming edge
	 * @param list the list of incoming edge
	 */
	public void setEdge(List list) {
		edge = list;
		updateSize();
		fireContentsChanged(this,0,size);
	}
	
	/**
	 * get the index in list of a GsEdgeIndex
	 * @param edg an GsEdgeIndex object
	 * @return it's index in the edge list
	 */
	public int getIndex(GsEdgeIndex edg) {
		if (edge == null) {
            return 0;
        }
		int j = 0;
		for (int i=0 ; i<edge.size() ; i++) {
			Object obj = edge.get(i);
			if (obj instanceof GsDirectedEdge) {
				GsDirectedEdge de = (GsDirectedEdge)obj;
				if (edg.data == de.getUserObject()) {
					return j+edg.index;
				}
				j += ((GsRegulatoryMultiEdge)de.getUserObject()).getEdgeCount();				
			}
		}
		return 0;
	}


}
