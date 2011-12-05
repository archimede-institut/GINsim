package org.ginsim.gui.graph.regulatorygraph;

import java.awt.GridBagConstraints;
import java.util.Collection;

import org.ginsim.annotation.Annotation;
import org.ginsim.core.GraphEventCascade;
import org.ginsim.exception.GsException;
import org.ginsim.graph.common.GraphListener;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.resource.Translator;
import org.ginsim.utils.DataUtils;
import org.ginsim.utils.data.GenericList;
import org.ginsim.utils.data.GenericPropertyInfo;
import org.ginsim.utils.data.ObjectEditor;
import org.ginsim.utils.data.SimpleGenericList;




public class RegulatoryGraphEditor extends ObjectEditor implements GraphListener<RegulatoryNode, RegulatoryMultiEdge> {

	public static final int PROP_ID = 0;
	public static final int PROP_NODEORDER = 1;
	public static final int PROP_ANNOTATION = 2;
	public static final int PROP_RAW = 10;
	
	RegulatoryGraph graph;
	private GsGraphOrderList nodeList;

	public RegulatoryGraphEditor() {
		GenericPropertyInfo pinfo = new GenericPropertyInfo(this, PROP_ID, Translator.getString("STR_name"), String.class);
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, PROP_NODEORDER, null, GenericList.class);
		pinfo.addPosition(0, 1, 2, 1, 1, 1, GridBagConstraints.SOUTH);
		pinfo.data = nodeList;
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, PROP_ANNOTATION, Translator.getString("STR_notes"), Annotation.class);
		pinfo.addPosition(3, 0, 1, 2, 4, 1, GridBagConstraints.SOUTH);
		v_prop.add(pinfo);
	}

	public void setEditedObject(Object o) {
		if (o != this.graph) {
			if (this.graph != null) {
				this.graph.removeGraphListener( this);
			}
			this.graph = (RegulatoryGraph) o;
			this.nodeList = new GsGraphOrderList( graph);
			if (this.graph != null) {
				this.graph.addGraphListener(this);
			}
		}
		master = o;
		super.setEditedItem(o);
	}
	
	public int getIntValue(int prop) {
		return 0;
	}

	public String getStringValue(int prop) {
		switch (prop) {
			case PROP_ID:
				return graph.getGraphName();
		}
		return null;
	}

	public boolean isValidValue(int prop, String value) {
		if (prop == PROP_ID) {
			return DataUtils.isValidId(value);
		}
		return false;
	}

	public boolean isValidValue(int prop, int value) {
		return false;
	}

	public boolean setValue(int prop, String value) {
		try {
			if (prop == PROP_ID) {
					graph.setGraphName( value);
					return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean setValue(int prop, int value) {
		return false;
	}

	public Object getRawValue(int prop) {
		switch (prop) {
			case PROP_ANNOTATION:
				return graph.getAnnotation();
			case PROP_NODEORDER:
				return nodeList;
			case PROP_RAW:
				return graph;
		}
		return null;
	}

	public GraphEventCascade edgeAdded(RegulatoryMultiEdge data) {
		return null;
	}

	public GraphEventCascade edgeRemoved(RegulatoryMultiEdge data) {
		return null;
	}

	public GraphEventCascade edgeUpdated(RegulatoryMultiEdge data) {
		return null;
	}

	public GraphEventCascade graphMerged(Collection<RegulatoryNode> data) {
		return null;
	}

	public GraphEventCascade nodeAdded(RegulatoryNode data) {
		refresh(true);
		return null;
	}

	public GraphEventCascade nodeRemoved(RegulatoryNode data) {
		refresh(true);
		return null;
	}

	public GraphEventCascade nodeUpdated(RegulatoryNode data) {
		refresh(true);
		return null;
	}

	public void endParsing() {
	}
}

class GsGraphOrderList extends SimpleGenericList {
	RegulatoryGraph graph = null;
	
	GsGraphOrderList( RegulatoryGraph graph) {
		super(graph.getNodeOrder());
		if (graph instanceof RegulatoryGraph) {
			canOrder = true;
			canEdit = true;
			nbAction = 1;
			this.graph = (RegulatoryGraph) graph;
		}
	}
	
	protected boolean doEdit(Object data, Object value) {
		try {
			graph.changeNodeId(data, (String)value);
			return true;
		} catch (GsException e) {
			return false;
		}
	}

	protected void doRun(int row, int col) {
		GUIManager.getInstance().getGraphGUI(graph).selectNode(v_data.get(row));
	}
	
}
