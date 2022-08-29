package org.ginsim.gui.graph.regulatorygraph;

import java.awt.GridBagConstraints;

import org.ginsim.common.application.Txt;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.LogicalFunctionPanel;
import org.ginsim.gui.utils.data.GenericPropertyEditorPanel;
import org.ginsim.gui.utils.data.GenericPropertyInfo;
import org.ginsim.gui.utils.data.ObjectEditor;
import org.ginsim.gui.utils.data.models.SpinModel;



public class RegulatoryNodeEditor extends ObjectEditor<RegulatoryNode> {

	public static final int PROP_ID = 0;
	public static final int PROP_NAME = 1;
    public static final int PROP_MAX = 2;
    public static final int PROP_INPUT = 3;
	public static final int PROP_ANNOTATION = 5;
	public static final int PROP_RAW = 10;
	
	RegulatoryGraph graph;

	static {
		GenericPropertyEditorPanel.addSupportedClass(RegulatoryNode.class, InteractionPanel.class);
		GenericPropertyEditorPanel.addSupportedClass(LogicalFunctionPanel.class, LogicalFunctionPanel.class);
	}
	
	public RegulatoryNodeEditor(RegulatoryGraph graph) {
		this.graph = graph;
		master = graph;
		GenericPropertyInfo pinfo = new GenericPropertyInfo(this, PROP_ID, Txt.t("STR_id"), String.class);
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, PROP_NAME, Txt.t("STR_name"), String.class);
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, PROP_RAW, Txt.t("STR_max"), SpinModel.class);
		pinfo.data = new NodeMaxValueSpinModel(graph);
		pinfo.addPosition(0,3);
		pinfo.addPosition(1, 3);
		pinfo.addPosition(0, 2);
		pinfo.addPosition(1, 2);
		v_prop.add(pinfo);
        pinfo = new GenericPropertyInfo(this, PROP_INPUT, Txt.t("STR_Fixed_input"), Boolean.class);
        v_prop.add(pinfo);

		// build the group [note, parameter, function]
		GenericPropertyInfo[] t = new GenericPropertyInfo[2];
		pinfo = new GenericPropertyInfo(this, PROP_RAW, Txt.t("STR_parameters"), RegulatoryNode.class);
		pinfo.data = graph;
		t[0] = pinfo;
		pinfo = new GenericPropertyInfo(this, PROP_RAW, Txt.t("STR_function"), LogicalFunctionPanel.class);
		pinfo.data = graph;
		t[1] = pinfo;
		
		// and add the group
		pinfo = new GenericPropertyInfo(this, -1, null, GenericPropertyInfo[].class);
		pinfo.data = t;
		pinfo.name = Txt.t("STR_parameters");
		pinfo.addPosition(0, 4, 2, 1, 0, 0, GridBagConstraints.SOUTH);
		pinfo.addPosition(2, 0, 1, 5, 1, 1, GridBagConstraints.SOUTH);
		v_prop.add(pinfo);
	}
	
	public int getIntValue(int prop) {
		switch (prop) {
            case PROP_MAX:
                return o.getMaxValue();
            case PROP_INPUT:
                return o.isInput() ? 1 : 0;
		}
		return 0;
	}

	public String getStringValue(int prop) {
		switch (prop) {
			case PROP_ID:
				return o.getId();
			case PROP_NAME:
				return o.getName();
			case PROP_MAX:
				return ""+o.getMaxValue();
            case PROP_INPUT:
                return ""+o.isInput();
		}
		return null;
	}

	public boolean isValidValue(int prop, String value) {
		try {
			switch (prop) {
				case PROP_ID:
					return XMLWriter.isValidId(value) && !graph.idExists(value);
				case PROP_NAME:
					return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isValidValue(int prop, int value) {
		switch (prop) {
			case PROP_MAX:
				return value>0 && value<10;
            case PROP_INPUT:
                return value== 0 || value==1;
		}
		return false;
	}

	public boolean setValue(int prop, String value) {
		try {
			switch (prop) {
				case PROP_ID:
					graph.changeNodeId(o, value);
					return true;
				case PROP_NAME:
					o.setName(value, graph);
					return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean setValue(int prop, int value) {
		switch (prop) {
			case PROP_MAX:
				o.setMaxValue((byte)value, graph);
				return o.getMaxValue() == value;
            case PROP_INPUT:
                boolean nv = value != 0;
                if (nv) {
                	if (graph.getIncomingEdges(o).size() > 0) {
                		NotificationManager.getManager().publishWarning(graph, "Can not set regulated node as input");
                		return false;
                	}
                	if (o.getV_logicalParameters().size() > 0) {
                		NotificationManager.getManager().publishWarning(graph, "Can not set node as input having parameters");
                		return false;
                	}
                }
               	o.setInput(nv, graph);
                return o.isInput() == nv;
		}
		return false;
	}

	public Object getRawValue(int prop) {
		switch (prop) {
			case PROP_ANNOTATION:
				return o.getAnnotation();
			case PROP_RAW:
				return o;
            case PROP_INPUT:
                return o.isInput() ? Boolean.TRUE : Boolean.FALSE;
		}
		return null;
	}

	@Override
	public Object[] getArgs() {
		return new Object[] {graph};
	}

}
