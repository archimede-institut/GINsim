package org.ginsim.gui.graph.regulatorygraph;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.List;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.Txt;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.GraphChangeType;
import org.ginsim.core.graph.GraphEventCascade;
import org.ginsim.core.graph.GraphListener;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.utils.data.*;


public class RegulatoryGraphEditor extends ObjectEditor<RegulatoryGraph> implements GraphListener<RegulatoryGraph> {

	public static final int PROP_ID = 0;
	public static final int PROP_NODEORDER = 1;
	public static final int PROP_ANNOTATION = 2;
	public static final int PROP_RAW = 10;

	RegulatoryGraph graph;
    private final HelpedList helped;

	public RegulatoryGraphEditor(RegulatoryGraph graph) {
		GenericPropertyInfo pinfo = new GenericPropertyInfo(this, PROP_ID, Txt.t("STR_name"), String.class);
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, PROP_NODEORDER, null, HelpedList.class);
		pinfo.addPosition(0, 1, 2, 1, 1, 1, GridBagConstraints.SOUTH);
		pinfo.data = graph.getNodeOrder();
		v_prop.add(pinfo);
		pinfo = new GenericPropertyInfo(this, PROP_ANNOTATION, Txt.t("STR_notes"), Annotation.class);
		pinfo.addPosition(3, 0, 1, 2, 4, 1, GridBagConstraints.SOUTH);
		v_prop.add(pinfo);

        ListPanelHelper helper = new NodeOrderHelper(graph);
        this.helped = new HelpedList(helper);
		setEditedObject(graph);
	}

	private void setEditedObject(RegulatoryGraph g) {
		if (g != this.graph) {
			if (this.graph != null) {
		        GraphManager.getInstance().removeGraphListener( this.graph, this);
                helped.list = null;
			}
			this.graph = g;
			if (this.graph != null) {
		        GraphManager.getInstance().addGraphListener( this.graph, this);
                helped.list = this.graph.getNodeOrder();
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
				return helped;
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
	public GraphEventCascade graphChanged(RegulatoryGraph g, GraphChangeType type, Object data) {
		switch (type) {
		case NODEADDED:
		case NODEREMOVED:
		case NODEUPDATED:
			refresh(true);
		}
		return null;
	}
}

class NodeOrderHelper extends ListPanelHelper<RegulatoryNode, List<RegulatoryNode>> {

    private final RegulatoryGraph graph;

    public NodeOrderHelper(RegulatoryGraph graph) {
        this.graph = graph;
    }

    @Override
    public boolean moveData(List<RegulatoryNode> list, int[] sel, int diff) {
        boolean b = super.moveData(list, sel, diff);
        if (b) {
            graph.fireGraphChange(GraphChangeType.METADATACHANGE, null);
        }
        return b;
    }

    @Override
    public boolean setValue(List<RegulatoryNode> list, int row, int column, Object value) {
		try {
            RegulatoryNode node = list.get(row);
			graph.changeNodeId(node, (String)value);
			return true;
		} catch (GsException e) {
			return false;
		}
	}

    public String[] getActionLabels() {
        return new String[] {"->"};
    }

    public void runAction(List<RegulatoryNode> list, int row, int col) {
        GUIManager.getInstance().getGraphGUI(graph).getSelection().selectNode(list.get(row));
    }
}
