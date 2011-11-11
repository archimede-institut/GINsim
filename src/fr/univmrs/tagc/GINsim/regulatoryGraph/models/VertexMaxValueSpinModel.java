package fr.univmrs.tagc.GINsim.regulatoryGraph.models;

import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.models.SpinModel;

/**
 * model controlling spin buttons for base and max value of a GsRegulatoryVertex
 */
public class VertexMaxValueSpinModel extends SpinModel {

    private GsRegulatoryVertex vertex;
	private final GsRegulatoryGraph graph;
	private final GraphGUI<GsRegulatoryGraph, GsRegulatoryVertex, GsRegulatoryMultiEdge> gui;

    /**
     */
    public VertexMaxValueSpinModel(GsRegulatoryGraph graph) {
        super();
        this.graph = graph;
        this.gui = GUIManager.getInstance().getGraphGUI(graph);
    }


	public void setEditedObject(Object rawValue) {
        this.vertex = (GsRegulatoryVertex)rawValue;
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
