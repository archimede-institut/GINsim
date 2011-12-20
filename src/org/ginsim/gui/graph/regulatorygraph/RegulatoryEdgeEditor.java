package org.ginsim.gui.graph.regulatorygraph;

import java.awt.GridBagConstraints;
import java.util.ArrayList;

import javax.swing.Action;

import org.ginsim.common.utils.Translator;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.core.notification.resolvable.resolution.NotificationResolution;
import org.ginsim.core.utils.data.GenericList;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.utils.data.GenericPropertyEditorPanel;
import org.ginsim.gui.utils.data.GenericPropertyInfo;
import org.ginsim.gui.utils.data.ObjectEditor;


public class RegulatoryEdgeEditor extends ObjectEditor<RegulatoryMultiEdge> {

	private final Graph graph;
	private final GraphGUI gui;
	RegulatoryEdge edge;
	EdgeList edgeList;
	
	private static final int ANNOTATION = 0; 
	private static final int EDGES = 1; 
	private static final int EDGE = 2; 
	private static final int SOURCE = 11;
	private static final int TARGET = 12;
	
	static {
		GenericPropertyEditorPanel.addSupportedClass(RegulatoryEdge.class, RegulatoryEdgeEditPanel.class);
	}
	
	public RegulatoryEdgeEditor(RegulatoryGraph graph) {
		this.graph = graph;
		this.gui = GUIManager.getInstance().getGraphGUI(graph);
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
		pinfo = new GenericPropertyInfo(this, EDGE, null, RegulatoryEdge.class);
		pinfo.data = graph;
		pinfo.addPosition(0, 2, 5, 1, 1, 0, GridBagConstraints.SOUTH);
		v_prop.add(pinfo);
		
		// annotation
		pinfo = new GenericPropertyInfo(this, ANNOTATION, null, Annotation.class);
		pinfo.addPosition(5, 0, 1, 3, 4, 1, GridBagConstraints.SOUTH);
		v_prop.add(pinfo);
	}
	
	@Override
	public void setEditedItem(RegulatoryMultiEdge o) {
		if (o == null) {
			return;
		}
		this.edge = o.getEdge(0);
		edgeList.setMEdge(o);
		super.setEditedItem(o);
	}


	public Object getRawValue(int prop) {
		switch (prop) {
			case ANNOTATION:
				return edge.getAnnotation();
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
				if (value > -1 && value < o.getEdgeCount()) {
					edge = o.getEdge(value);
					
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
				return o.getSource().getId();
			case TARGET:
				return o.getTarget().getId();
		}
		return null;
	}

	public void performAction(int prop) {
		switch (prop) {
			case SOURCE:
				gui.selectNode(o.getSource());
				break;
			case TARGET:
				gui.selectNode(o.getTarget());
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

	RegulatoryGraph graph;
	RegulatoryMultiEdge medge;
	
	EdgeList(RegulatoryGraph graph) {
		this.graph = graph;
		addOptions = new ArrayList();
		canAdd = true;
		canEdit = true;
		canRemove = true;
		t_type = new Class[1];
		t_type[0] = RegulatoryEdge.class;
	}
	
	void setMEdge(RegulatoryMultiEdge medge) {
		this.medge = medge;
		addOptions.clear();
	    int[] t = medge.getFreeValues();
	    for (int i=0 ; i<t.length ; i++) {
	    	int th = t[i];
	    	if (th != -1) {
	    		addOptions.add(new Integer(t[i]));
	    	}
	    }
		refresh();
	}

    protected void addEdge(int value) {
		int index = medge.addEdge(RegulatoryEdgeSign.POSITIVE, value, graph);
		if (index != -1) {
			setMEdge(medge);
		}
    }
    
	public int add(int position, int mode) {
		if (mode == -1 || mode >= addOptions.size()) {
			
			NotificationResolution resolution = new NotificationResolution(){
				
				public boolean perform( Graph graph, Object[] data, int index){
					
					RegulatoryMultiEdge medge = (RegulatoryMultiEdge)data[0];
					EdgeList edge_list = (EdgeList) data[1];
					
					if (edge_list.medge == medge) {
						RegulatoryNode vertex = medge.getSource();
						vertex.setMaxValue((byte)(vertex.getMaxValue()+1), (RegulatoryGraph)graph);
						edge_list.add();
						return true;
					}
					return false;
				}
				
				public String[] getOptionsName() {
					
					String[] t = {"Add value"};
					return t;
				}
			};
			
			NotificationManager.publishResolvableWarning( this, "STR_noMoreValueForInteraction", graph, new Object[]{ medge, this}, resolution);
			
	    	return -1;
		}
		this.addEdge(((Integer)addOptions.get(mode)).intValue());
		return 0;
	}

	public Object getElement(String filter, int startIndex, int i) {
		return medge.getEdge(i);
	}

	public int getNbElements(String filter, int startIndex) {
		return medge.getEdgeCount();
	}
	public boolean remove(String filter, int startIndex, int[] t_index) {
		if (medge.getEdgeCount() > 1 && t_index.length == 1) {
			medge.removeEdge(t_index[0], graph);
			setMEdge(medge);
			return true;
		}
		return false;
	}

	public boolean edit(String filter, int startIndex, int i, int col, Object o) {
		return false;
	}
	public boolean move(int[] sel, int diff) {
		return false;
	}
	public void run(String filter, int startIndex, int row, int col) {
	}
}
