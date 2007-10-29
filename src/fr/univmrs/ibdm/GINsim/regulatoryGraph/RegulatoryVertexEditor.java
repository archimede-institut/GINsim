package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.GridBagConstraints;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.annotation.Annotation;
import fr.univmrs.ibdm.GINsim.annotation.AnnotationPanel;
import fr.univmrs.ibdm.GINsim.global.Tools;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsVertexMinMaxSpinModel;
import fr.univmrs.tagc.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.datastore.ObjectEditor;
import fr.univmrs.tagc.datastore.gui.GenericPropertyEditorPanel;
import fr.univmrs.tagc.datastore.models.MinMaxSpinModel;

public class RegulatoryVertexEditor extends ObjectEditor {

	public static final int PROP_ID = 0;
	public static final int PROP_NAME = 1;
	public static final int PROP_MAX = 2;
	public static final int PROP_BASAL = 3;
	public static final int PROP_ANNOTATION = 5;
	public static final int PROP_RAW = 10;
	private Vector v_prop = new Vector();
	
	GsRegulatoryVertex vertex;
	GsRegulatoryGraph graph;

	static {
		GenericPropertyEditorPanel.addSupportedClass(Annotation.class, AnnotationPanel.class);
		GenericPropertyEditorPanel.addSupportedClass(GsRegulatoryVertex.class, GsInteractionPanel.class);
		GenericPropertyEditorPanel.addSupportedClass(GsLogicalFunctionPanel.class, GsLogicalFunctionPanel.class);
	}
	
	public RegulatoryVertexEditor(GsRegulatoryGraph graph) {
		this.graph = graph;
		master = graph;
		GenericPropertyInfo pinfo = new GenericPropertyInfo(this, PROP_ID, Translator.getString("STR_id"), String.class);
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, PROP_NAME, Translator.getString("STR_name"), String.class);
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, PROP_RAW, null, MinMaxSpinModel.class);
		pinfo.data = new GsVertexMinMaxSpinModel(graph);
		pinfo.addPosition(0,3);
		pinfo.addPosition(1, 3);
		pinfo.addPosition(0, 2);
		pinfo.addPosition(1, 2);
		v_prop.add(pinfo);

		// build the group [note, parameter, function]
		GenericPropertyInfo[] t = new GenericPropertyInfo[3];
		pinfo = new GenericPropertyInfo(this, PROP_ANNOTATION, Translator.getString("STR_notes"), Annotation.class);
		t[0] = pinfo;
		pinfo = new GenericPropertyInfo(this, PROP_RAW, Translator.getString("STR_parameters"), GsRegulatoryVertex.class);
		pinfo.data = graph;
		t[1] = pinfo;
		pinfo = new GenericPropertyInfo(this, PROP_RAW, Translator.getString("STR_function"), GsLogicalFunctionPanel.class);
		pinfo.data = graph;
		t[2] = pinfo;
		
		// and add the group
		pinfo = new GenericPropertyInfo(this, -1, null, GenericPropertyInfo[].class);
		pinfo.data = t;
		pinfo.name = Translator.getString("STR_parameters");
		pinfo.addPosition(0, 4, 2, 1, 0, 0, GridBagConstraints.SOUTH);
		pinfo.addPosition(2, 0, 1, 5, 1, 1, GridBagConstraints.SOUTH);
		v_prop.add(pinfo);
	}
	
	public void setEditedObject(Object o) {
		this.vertex = (GsRegulatoryVertex)o;
		super.setEditedObject(o);
	}
	
	public int getIntValue(int prop) {
		switch (prop) {
			case PROP_MAX:
				return vertex.getMaxValue();
			case PROP_BASAL:
				return vertex.getBaseValue();
		}
		return 0;
	}

	public Vector getProperties() {
		return v_prop;
	}

	public String getStringValue(int prop) {
		switch (prop) {
			case PROP_ID:
				return vertex.getId();
			case PROP_NAME:
				return vertex.getName();
			case PROP_MAX:
				return ""+vertex.getMaxValue();
			case PROP_BASAL:
				return ""+vertex.getBaseValue();
		}
		return null;
	}

	public boolean isValidValue(int prop, String value) {
		try {
			switch (prop) {
				case PROP_ID:
					return Tools.isValidId(value) && !graph.idExists(value);
				case PROP_NAME:
					return true;
				case PROP_MAX:
				case PROP_BASAL:
					return isValidValue(prop, Integer.parseInt(value));
			}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isValidValue(int prop, int value) {
		switch (prop) {
			case PROP_MAX:
				return value>0 && value<10;
			case PROP_BASAL:
				return value>-1 && value<=vertex.getMaxValue();
		}
		return false;
	}

	public boolean setValue(int prop, String value) {
		try {
			switch (prop) {
				case PROP_ID:
					graph.changeVertexId(vertex, value);
					return true;
				case PROP_NAME:
					vertex.setName(value);
					return true;
				case PROP_MAX:
				case PROP_BASAL:
					return setValue(prop, Integer.parseInt(value));
			}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean setValue(int prop, int value) {
		switch (prop) {
			case PROP_MAX:
				vertex.setMaxValue((short)value, graph);
				return vertex.getMaxValue() == value;
			case PROP_BASAL:
				vertex.setBaseValue((short)value, graph);
				return vertex.getBaseValue() == value;
		}
		return false;
	}

	public Object getRawValue(int prop) {
		switch (prop) {
			case PROP_ANNOTATION:
				return vertex.getAnnotation();
			case PROP_RAW:
				return vertex;
		}
		return null;
	}
}