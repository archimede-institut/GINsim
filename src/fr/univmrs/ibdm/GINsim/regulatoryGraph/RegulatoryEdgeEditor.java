package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

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
import fr.univmrs.tagc.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.datastore.ObjectEditor;
import fr.univmrs.tagc.datastore.gui.GenericPropertyEditorPanel;

public class RegulatoryEdgeEditor extends ObjectEditor {

	
	GsGraph graph;
	GsRegulatoryMultiEdge medge;
	GsRegulatoryEdge edge;
	EdgeList edgeList;
	
	private static final int ANNOTATION = 0; 
	private static final int EDGES = 1; 
	private static final int EDGE = 2; 
	private static final int SOURCE = 11;
	private static final int TARGET = 12;
	
	static {
		GenericPropertyEditorPanel.addSupportedClass(GsRegulatoryEdge.class, RegulatoryEdgeEditPanel.class);
	}
	
	public RegulatoryEdgeEditor(GsRegulatoryGraph graph) {
		this.graph = graph;
		master = graph;
		
		// info on top
		GenericPropertyInfo pinfo = new GenericPropertyInfo(this, SOURCE, 
				Translator.getString("STR_from"), Action.class);
		pinfo.addPosition(0, 0);
		pinfo.addPosition(1, 0);
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, TARGET, 
				Translator.getString("STR_to"), Action.class);
		pinfo.addPosition(2, 0);
		pinfo.addPosition(3, 0);
		v_prop.add(pinfo);
		
		// edge list
		edgeList = new EdgeList(graph);
		pinfo = new GenericPropertyInfo(this, EDGES, null, GenericList.class);
		pinfo.addPosition(0, 1, 5, 1, 1, 1, GridBagConstraints.SOUTH);
		v_prop.add(pinfo);
		
		// edge edit panel
		pinfo = new GenericPropertyInfo(this, EDGE, null, GsRegulatoryEdge.class);
		pinfo.data = graph;
		pinfo.addPosition(0, 2, 5, 1, 1, 0, GridBagConstraints.SOUTH);
		v_prop.add(pinfo);
		
		// annotation
		pinfo = new GenericPropertyInfo(this, ANNOTATION, null, Annotation.class);
		pinfo.addPosition(5, 0, 1, 3, 4, 1, GridBagConstraints.SOUTH);
		v_prop.add(pinfo);
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
			case EDGE:
				return edge;
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
		switch (prop) {
			case SOURCE:
				return medge.getSource().getId();
			case TARGET:
				return medge.getTarget().getId();
		}
		return null;
	}

	public void performAction(int prop) {
		switch (prop) {
			case SOURCE:
				graph.getGraphManager().select(medge.getSourceVertex());
				break;
			case TARGET:
				graph.getGraphManager().select(medge.getTargetVertex());
				break;
		}
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

class EdgeList extends GenericList {

	GsRegulatoryGraph graph;
	GsRegulatoryMultiEdge medge;
	
	EdgeList(GsRegulatoryGraph graph) {
		this.graph = graph;
		canAdd = true;
		canEdit = true;
		canRemove = true;
		t_type = new Class[1];
		t_type[0] = GsRegulatoryEdge.class;
	}
	
	void setMEdge(GsRegulatoryMultiEdge medge) {
		this.medge = medge;
		refresh();
	}

    protected void addEdge(int value) {
		int index = medge.addEdge(GsRegulatoryMultiEdge.SIGN_POSITIVE, value, graph);
		if (index != -1) {
			refresh();
		}
    }
    
	public int add(int position) {
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

	public Object getElement(String filter, int i) {
		return medge.getEdge(i);
	}

	public int getNbElements(String filter) {
		return medge.getEdgeCount();
	}
	public boolean remove(String filter, int[] t_index) {
		if (medge.getEdgeCount() > 1 && t_index.length == 1) {
			medge.removeEdge(t_index[0], graph);
			return true;
		}
		return false;
	}

	public boolean edit(String filter, int i, int col, Object o) {
		return false;
	}
	public boolean move(int[] sel, int diff) {
		return false;
	}
	public void run(String filter, int row, int col) {
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
			edgeList.add();
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
