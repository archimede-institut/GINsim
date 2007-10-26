package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fr.univmrs.ibdm.GINsim.annotation.Annotation;
import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationAction;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.tagc.datastore.GenericList;
import fr.univmrs.tagc.datastore.GenericListListener;
import fr.univmrs.tagc.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.datastore.ObjectEditor;

public class RegulatoryEdgeEditor extends ObjectEditor {

	
	GsGraph graph;
	GsRegulatoryMultiEdge medge;
	GsRegulatoryEdge edge;
	EdgeList edgeList;
	
	private static final int ANNOTATION = 0; 
	private static final int EDGES = 1; 
	
	
	public RegulatoryEdgeEditor(GsRegulatoryGraph graph) {
		this.graph = graph;
		edgeList = new EdgeList(graph);
		GenericPropertyInfo pinfo = new GenericPropertyInfo(this, EDGES, null, GenericList.class);
		v_prop.add(pinfo);
		pinfo.addPosition(0, 0, 1, 1, 1, 1, GridBagConstraints.SOUTH);
		pinfo = new GenericPropertyInfo(this, ANNOTATION, null, Annotation.class);
		v_prop.add(pinfo);
		pinfo.addPosition(1, 0, 1, 1, 4, 1, GridBagConstraints.SOUTH);
	}
	
	public void setEditedObject(Object o) {
		this.medge = (GsRegulatoryMultiEdge)((GsDirectedEdge)o).getUserObject();
		if (medge == null) {
			return;
		}
		this.edge = medge.getEdge(0);
		edgeList.setMEdge(medge);
		super.setEditedObject(o);
	}


	public Object getRawValue(int prop) {
		switch (prop) {
			case ANNOTATION:
				return edge.annotation;
			case EDGES:
				return edgeList;
		}
		return null;
	}

	public boolean setValue(int prop, int value) {
		switch (prop) {
			case EDGES:
				if (value > -1 && value < medge.getEdgeCount()) {
					edge = medge.getEdge(value);
					
					// FIXME: hack to trigger a refresh and avoid an infinite loop
					// it should be replaced by something cleaner...
					refresh(false);
					
					return true;
				}
		}
		return false;
	}

	public int getIntValue(int prop) {
		return 0;
	}
	public String getStringValue(int prop) {
		return null;
	}

	public boolean isValidValue(int prop, String value) {
		return false;
	}

	public boolean isValidValue(int prop, int value) {
		return false;
	}

	public boolean setValue(int prop, String value) {
		return false;
	}
}

class EdgeList implements GenericList {

	GsRegulatoryGraph graph;
	GsRegulatoryMultiEdge medge;
	Vector v_listener = new Vector();
	Vector v_type = new Vector();
	private Map	m_ed = new HashMap();
	
	EdgeList(GsRegulatoryGraph graph) {
		this.graph = graph;
		v_type.add(GsRegulatoryEdge.class);
		m_ed.put(GsRegulatoryEdge.class, new RegulatoryEdgeCellEditor(graph));
	}
	
	void setMEdge(GsRegulatoryMultiEdge medge) {
		this.medge = medge;
		Iterator it = v_listener.iterator();
		while (it.hasNext()) {
			((GenericListListener)it.next()).contentChanged();
		}
	}

    protected void addEdge(int value) {
		int index = medge.addEdge(GsRegulatoryMultiEdge.SIGN_POSITIVE, value, graph);
		if (index != -1) {
			// FIXME: refresh
		}
    }
    
	public int add(int index, int type) {
	    if (!graph.isEditAllowed()) {
	        return -1;
	    }
	    int[] t = medge.getFreeValues();
	    if (t[0] == -1) {
	    	GsGraphNotificationAction notifAction = new AddEdgeNotificationAction(this);
	    	graph.addNotificationMessage(new GsGraphNotificationMessage(graph,
	    			Translator.getString("STR_noMoreValueForInteraction"),
	    			notifAction,
	    			medge,
	    			GsGraphNotificationMessage.NOTIFICATION_WARNING));
	    } else if (t[1] == -1) {
	    	addEdge(t[0]);
	    } else {
	    	JPopupMenu menu = new JPopupMenu("select value");
	    	for (int i=0 ; i<t.length ; i++) {
	    		if (t[i] == -1) {
	    			break;
	    		}
	    		JMenuItem item = new JMenuItem(new AddEdgeMenuAction(this, t[i]));
	    		menu.add(item);
	    	}
	    	menu.setVisible(true);
	    }
		return 0;
	}

	public int filterThreshold() {
		return Integer.MAX_VALUE;
	}
	public Object getElement(int i) {
		return medge.getEdge(i);
	}

	public int getNbElements() {
		return medge.getEdgeCount();
	}
	public boolean remove(int[] t_index) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addListListener(GenericListListener l) {
		v_listener.add(l);
	}
	public void removeListListener(GenericListListener l) {
		v_listener.remove(l);
	}
	public boolean canAdd() {
		return true;
	}
	public boolean canCopy() {
		return false;
	}
	public boolean canEdit() {
		return true;
	}
	public boolean canOrder() {
		return false;
	}
	public boolean canRemove() {
		return true;
	}
	public int copy(int i) {
		return -1;
	}
	public boolean doInlineAddRemove() {
		return false;
	}
	public boolean edit(int i, Object o) {
		return false;
	}
	public Object getAction(int row) {
		return null;
	}
	public String getName() {
		return null;
	}
	public Vector getObjectType() {
		return v_type;
	}
	public boolean hasAction() {
		return false;
	}
	public boolean moveElement(int src, int dst) {
		return false;
	}
	public void run(int i) {
	}
	public void setFilter(String filter) {
	}

	public Map getCellEditor() {
		return m_ed;
	}
}

class AddEdgeNotificationAction implements GsGraphNotificationAction {
	EdgeList edgeList;
	AddEdgeNotificationAction(EdgeList edgeList) {
		this.edgeList = edgeList;
	}
	public boolean timeout(GsGraph graph, Object data) {
		return true;
	}

	public boolean perform(GsGraph graph, Object data, int index) {
		if (edgeList.medge == data) {
			GsRegulatoryVertex vertex = ((GsRegulatoryMultiEdge)data).getSource();
			vertex.setMaxValue((short)(vertex.getMaxValue()+1), (GsRegulatoryGraph)graph);
			edgeList.add(-1,-1);
			return true;
		}
		return false;
	}
	public String[] getActionName() {
		String[] t = {"add value"};
		return t;
	}
}

class AddEdgeMenuAction extends AbstractAction {
	private static final long serialVersionUID = -7038482131591956858L;
	int value;
	EdgeList edgeList;
	AddEdgeMenuAction(EdgeList edgeList, int value) {
		this.value = value;
		this.edgeList = edgeList;
		this.putValue( Action.NAME, ""+value);
	}
	public void actionPerformed(ActionEvent e) {
		edgeList.addEdge(value);
	}
}
