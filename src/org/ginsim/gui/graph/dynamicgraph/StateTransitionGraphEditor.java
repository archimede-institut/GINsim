package org.ginsim.gui.graph.dynamicgraph;

import java.awt.Component;
import java.awt.GridBagConstraints;

import org.ginsim.common.application.Translator;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.GraphEventCascade;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.utils.data.GenericList;
import org.ginsim.core.utils.data.SimpleGenericList;
import org.ginsim.gui.utils.data.GenericPropertyInfo;
import org.ginsim.gui.utils.data.ObjectEditor;


public class StateTransitionGraphEditor extends ObjectEditor<DynamicGraph> implements GraphListener<DynamicGraph> {

	public static final int PROP_ID = 0;
	public static final int PROP_NODEORDER = 1;
	public static final int PROP_ANNOTATION = 2;
	public static final int PROP_RAW = 10;
	
	DynamicGraph graph;
	private SimpleGenericList<NodeInfo> nodeList;

	public StateTransitionGraphEditor(DynamicGraph graph) {
		GenericPropertyInfo pinfo = new GenericPropertyInfo(this, PROP_ID, Translator.getString("STR_name"), String.class);
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, PROP_NODEORDER, null, GenericList.class);
		pinfo.addPosition(0, 1, 2, 1, 1, 1, GridBagConstraints.SOUTH);
		pinfo.data = nodeList;
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, PROP_ANNOTATION, Translator.getString("STR_notes"), Annotation.class);
		pinfo.addPosition(3, 0, 1, 2, 4, 1, GridBagConstraints.SOUTH);
		v_prop.add(pinfo);
		
		setEditedObject(graph);
	}

	private void setEditedObject(DynamicGraph g) {
		if (g != this.graph) {
			if (this.graph != null) {
		        GraphManager.getInstance().removeGraphListener( this.graph, this);
			}
			this.graph = g;
			this.nodeList = new SimpleGenericList<NodeInfo>( graph.getNodeOrder());
			if (this.graph != null) {
		        GraphManager.getInstance().addGraphListener( this.graph, this);
			}
		}
		master = g;
		super.setEditedItem(g);
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
			return XMLWriter.isValidId(value);
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

	@Override
	public Component getComponent() {
		Component component = super.getComponent();
		refresh(true);
		return component;
	}

	@Override
	public GraphEventCascade graphChanged(DynamicGraph g, GraphChangeType type, Object data) {
		switch (type) {
		case NODEADDED:
		case NODEREMOVED:
		case NODEUPDATED:
			refresh(true);
		}
		return null;
	}
}
