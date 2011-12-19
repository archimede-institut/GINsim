package org.ginsim.gui.graph.regulatorygraph.models;

import org.ginsim.common.utils.DataUtils;
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
		if (graph == null || vertex == null) {
			return DataUtils.IZ;
		}
		if (!gui.isEditAllowed()) {
			return new Integer(vertex.getMaxValue());
		}
		vertex.setMaxValue((byte) (vertex.getMaxValue()+1), graph);
		return new Integer(vertex.getMaxValue());
	}


	public Object getPreviousValue() {
        if (graph == null || vertex == null) {
            return DataUtils.IZ;
        }
	    if (!gui.isEditAllowed()) {
	        return new Integer(vertex.getMaxValue());
	    }
	    vertex.setMaxValue((byte) (vertex.getMaxValue()-1), graph);
	    return new Integer(vertex.getMaxValue());
	}


	public Object getValue() {
        if (vertex == null) {
            return DataUtils.IZ;
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
                vertex.setMaxValue((byte)Integer.parseInt(value.toString()), graph);
            } catch (NumberFormatException e) {}
        }
	}
	
}
