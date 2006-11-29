package fr.univmrs.ibdm.GINsim.regulatoryGraph.models;

import javax.swing.JSpinner;

import fr.univmrs.ibdm.GINsim.global.Tools;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * model controlling spin buttons for base and max value of a GsRegulatoryVertex
 */
public class GsVertexMinMaxSpinModel implements GsMinMaxSpinModel {
    
    private GsRegulatoryVertex vertex;
    private GsMinSpinModel m_min;
    private GsMaxSpinModel m_max;
	private GsRegulatoryGraph graph;
    
    /**
     */
    public GsVertexMinMaxSpinModel() {
        super();
        m_min = new GsMinSpinModel(this);
        m_max = new GsMaxSpinModel(this);
    }

    public Object getNextMaxValue() {
    	if (graph == null || vertex == null) {
    		return Tools.IZ;
    	}
        if (!graph.isEditAllowed()) {
            return new Integer(vertex.getMaxValue());
        }
        vertex.setMaxValue((short) (vertex.getMaxValue()+1), graph);
        m_max.update();
        return new Integer(vertex.getMaxValue());
    }

    public Object getPreviousMaxValue() {
    	if (graph == null || vertex == null) {
    		return Tools.IZ;
    	}
        if (!graph.isEditAllowed()) {
            return new Integer(vertex.getMaxValue());
        }
        vertex.setMaxValue((short) (vertex.getMaxValue()-1), graph);
        m_min.update();
        m_max.update();
        return new Integer(vertex.getMaxValue());
    }

    public Object getMaxValue() {
    	if (graph == null || vertex == null) {
    		return Tools.IZ;
    	}
        return new Integer(vertex.getMaxValue());
    }

    public void setMaxValue(Object value) {
        if (!graph.isEditAllowed()) {
            return;
        }
        if (value instanceof Integer ){
            vertex.setMaxValue(((Integer)value).shortValue(), graph);
        }
        if (value instanceof String) {
            try {
                vertex.setMaxValue((short)Integer.parseInt(value.toString()), graph);
            } catch (NumberFormatException e) {}
        }
        m_max.update();
        m_min.update();
    }
    
    public Object getNextMinValue() {
    	if (graph == null || vertex == null) {
    		return Tools.IZ;
    	}
        if (!graph.isEditAllowed()) {
            return new Integer(vertex.getBaseValue());
        }
        vertex.setBaseValue((short) (vertex.getBaseValue()+1), graph);
        graph.fireMetaChange();
        m_max.update();
        m_min.update();
        return new Integer(vertex.getBaseValue());
    }

    public Object getPreviousMinValue() {
    	if (graph == null || vertex == null) {
    		return Tools.IZ;
    	}
        if (!graph.isEditAllowed()) {
            return new Integer(vertex.getBaseValue());
        }
        vertex.setBaseValue((short) (vertex.getBaseValue()-1), graph);
        graph.fireMetaChange();
        m_min.update();
        return new Integer(vertex.getBaseValue());
    }

    public Object getMinValue() {
        if (vertex == null) {
            return Tools.IZ;
        }
        return new Integer(vertex.getBaseValue());
    }

    public void setMinValue(Object value) {
        if (!graph.isEditAllowed()) {
            return;
        }
        if (value instanceof Integer ){
            vertex.setBaseValue(((Integer)value).shortValue(), graph);
            graph.fireMetaChange();
            m_max.update();
            m_min.update();
        }
        if (value instanceof String) {
            try {
                vertex.setBaseValue((short)Integer.parseInt(value.toString()), graph);
                graph.fireMetaChange();
            } catch (NumberFormatException e) {}
            m_max.update();
            m_min.update();
        }
    }

    /**
     * set the edited vertex.
     * @param vertex
     * @param graph
     */
    public void setVertex(GsRegulatoryVertex vertex, GsRegulatoryGraph graph) {
        this.vertex = vertex;
        this.graph = graph;
        m_max.update();
        m_min.update();
    }
    
    public JSpinner getSMin() {
        JSpinner smin = new JSpinner(m_min);
        smin.setEditor(m_min.getEditor());
        return smin;
    }
    public JSpinner getSMax() {
        JSpinner smax = new JSpinner(m_max);
        smax.setEditor(m_max.getEditor());
        return smax;
    }
}
