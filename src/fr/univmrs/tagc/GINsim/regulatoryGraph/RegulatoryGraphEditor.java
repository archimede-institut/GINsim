package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.awt.GridBagConstraints;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.tagc.GINsim.graph.GsGraphListener;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.GenericList;
import fr.univmrs.tagc.common.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.common.datastore.ObjectEditor;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;
import fr.univmrs.tagc.common.manageressources.Translator;


public class RegulatoryGraphEditor extends ObjectEditor implements GsGraphListener {

	public static final int PROP_ID = 0;
	public static final int PROP_NODEORDER = 1;
	public static final int PROP_ANNOTATION = 2;
	public static final int PROP_RAW = 10;
	
	GsGraph graph;
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
				this.graph.removeGraphListener(this);
			}
			this.graph = (GsGraph)o;
			this.nodeList = new GsGraphOrderList(graph);
			if (this.graph != null) {
				this.graph.addGraphListener(this);
			}
		}
		master = o;
		super.setEditedObject(o);
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
			return Tools.isValidId(value);
		}
		return false;
	}

	public boolean isValidValue(int prop, int value) {
		return false;
	}

	public boolean setValue(int prop, String value) {
		try {
			if (prop == PROP_ID) {
					graph.setGraphName(value);
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

	public GsGraphEventCascade edgeAdded(Object data) {
		return null;
	}

	public GsGraphEventCascade edgeRemoved(Object data) {
		return null;
	}

	public GsGraphEventCascade edgeUpdated(Object data) {
		return null;
	}

	public GsGraphEventCascade graphMerged(Object data) {
		return null;
	}

	public GsGraphEventCascade vertexAdded(Object data) {
		refresh(true);
		return null;
	}

	public GsGraphEventCascade vertexRemoved(Object data) {
		refresh(true);
		return null;
	}

	public GsGraphEventCascade vertexUpdated(Object data) {
		refresh(true);
		return null;
	}

	public void endParsing() {
	}
}

class GsGraphOrderList extends SimpleGenericList {
	GsRegulatoryGraph graph = null;
	
	GsGraphOrderList(GsGraph graph) {
		super(graph.getNodeOrder());
		if (graph instanceof GsRegulatoryGraph) {
			canOrder = true;
			canEdit = true;
			nbAction = 1;
			this.graph = (GsRegulatoryGraph)graph;
		}
	}
	
	protected boolean doEdit(Object data, Object value) {
		try {
			graph.changeVertexId(data, (String)value);
			return true;
		} catch (GsException e) {
			return false;
		}
	}

	protected void doRun(int row, int col) {
		graph.getGraphManager().select(v_data.get(row));
	}
	
}
