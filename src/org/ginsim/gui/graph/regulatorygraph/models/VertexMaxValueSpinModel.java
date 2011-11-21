package org.ginsim.gui.graph.regulatorygraph.models;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;

import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.models.SpinModel;

/**
 * model controlling spin buttons for base and max value of a RegulatoryVertex
 */
public class VertexMaxValueSpinModel extends SpinModel {

    private RegulatoryVertex vertex;
	private final RegulatoryGraph graph;
	private final GraphGUI<RegulatoryGraph, RegulatoryVertex, RegulatoryMultiEdge> gui;

    /**
     */
    public VertexMaxValueSpinModel(RegulatoryGraph graph) {
        super();
        this.graph = graph;
        this.gui = GUIManager.getInstance().getGraphGUI(graph);
    }


	public void setEditedObject(Object rawValue) {
        this.vertex = (RegulatoryVertex)rawValue;
        fireStateChanged();
	}

	public Object getNextValue() {
		if (graph == null || vertex == null) {
			return Tools.IZ;
		}
		if (!gui.isEditAllowed()) {
			return new Integer(vertex.getMaxValue());
		}
		vertex.setMaxValue((byte) (vertex.getMaxValue()+1), graph);
		return new Integer(vertex.getMaxValue());
	}


	public Object getPreviousValue() {
        if (graph == null || vertex == null) {
            return Tools.IZ;
        }
	    if (!gui.isEditAllowed()) {
	        return new Integer(vertex.getMaxValue());
	    }
	    vertex.setMaxValue((byte) (vertex.getMaxValue()-1), graph);
	    return new Integer(vertex.getMaxValue());
	}


	public Object getValue() {
        if (vertex == null) {
            return Tools.IZ;
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
