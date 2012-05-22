package org.ginsim.gui.graph.regulatorygraph.models;

import org.ginsim.common.utils.MaskUtils;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.utils.data.models.SpinModel;



/**
 * model controlling spin buttons for base and max value of a RegulatoryNode
 */
public class NodeMaxValueSpinModel extends SpinModel {

	private static final Integer IZ = new Integer(0);
	
    private RegulatoryNode vertex;
	private final RegulatoryGraph graph;
	private final GraphGUI<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> gui;

    /**
     */
    public NodeMaxValueSpinModel(RegulatoryGraph graph) {
        super();
        this.graph = graph;
        this.gui = GUIManager.getInstance().getGraphGUI(graph);
    }


	public void setEditedObject(Object rawValue) {
        this.vertex = (RegulatoryNode)rawValue;
        fireStateChanged();
	}

	public Object getNextValue() {
		if (graph == null || vertex == null || !gui.isEditAllowed()) {
			return getValue();
		}
		return new Integer(vertex.getMaxValue()+1);
	}


	public Object getPreviousValue() {
		if (graph == null || vertex == null || !gui.isEditAllowed()) {
			return getValue();
		}
	    return new Integer(vertex.getMaxValue()-1);
	}


	public Object getValue() {
        if (graph == null || vertex == null) {
            return IZ;
        }
        return new Integer(vertex.getMaxValue());
	}


	public void setValue(Object value) {
        if (!gui.isEditAllowed()) {
            return;
        }
        if (value instanceof Integer ){
            vertex.setMaxValue(((Integer)value).byteValue(), graph);
        }
        if (value instanceof String) {
            try {
                vertex.setMaxValue(Byte.parseByte(value.toString()), graph);
            } catch (NumberFormatException e) {}
        }
        fireStateChanged();
	}
	
}
