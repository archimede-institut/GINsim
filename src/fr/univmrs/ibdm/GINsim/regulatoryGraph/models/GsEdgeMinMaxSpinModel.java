package fr.univmrs.ibdm.GINsim.regulatoryGraph.models;

import javax.swing.JList;
import javax.swing.JSpinner;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;

/**
 * model controlling the behavior of min and max spinbuttons for an edge
 */
public class GsEdgeMinMaxSpinModel implements GsMinMaxSpinModel {
    
    private int index = 0;
    private GsRegulatoryMultiEdge medge = null;
    private GsMinSpinModel m_min;
    private GsMaxSpinModel m_max;
    private JList jlist = null;
    private GsGraph graph = null;
    private boolean update = true;
    
    /**
     * @param graph
     * @param jlist
     */
    public GsEdgeMinMaxSpinModel(GsGraph graph, JList jlist) {
        super();
        this.graph = graph;
        this.jlist = jlist;
        m_min = new GsMinSpinModel(this);
        m_max = new GsMaxSpinModel(this);
    }

    public Object getNextMaxValue() {
        if (!graph.isEditAllowed()) {
            return getMaxValue();
        }
        short cur = medge.getMax(index);
        short max = medge.getSource().getMaxValue();
        if (update && cur != -1 && cur <= max) {
            graph.fireMetaChange();
        }
        
        if (cur == -1 || cur == max) {
            medge.setMax(index, (short)-1);
        } else {
        	medge.setMax(index, (short)(medge.getMax(index)+1));
        }
        m_max.update();
        ((GsDirectedEdgeListModel)jlist.getModel()).update();
        return new Integer(medge.getMax(index));
    }

    public Object getPreviousMaxValue() {
        if (!graph.isEditAllowed()) {
            return getMaxValue();
        }
        short cur = medge.getMax(index);
        short max = medge.getSource().getMaxValue();
        if (update && (cur > 1 || cur == -1)) {
            graph.fireMetaChange();
        }
        
        if (cur == -1) {
            medge.setMax(index, max);
        } else if (cur > 1) {
            medge.setMax(index, (short)(medge.getMax(index)-1));
            m_min.update();
        }
        m_max.update();
        ((GsDirectedEdgeListModel)jlist.getModel()).update();
        return new Integer(medge.getMax(index));
    }

    public Object getMaxValue() {
        if (medge == null || medge == null) {
            return "";
        }
        if (medge.getMax(index) == -1) {
            return "Max";
        }
        return new Integer(medge.getMax(index));
    }

    public void setMaxValue(Object value) {
        if (!graph.isEditAllowed()) {
            return;
        }
        if (value instanceof String) {
            if ("Max".equalsIgnoreCase((String)value) || "m".equalsIgnoreCase((String)value)) { 
                medge.setMax(index, (short)-1);
                if (update) {
                    graph.fireMetaChange();
                }
            }
            try {
                medge.setMax(index, (short)Integer.parseInt(value.toString()));
                if (update) {
                    graph.fireMetaChange();
                }
            } catch (NumberFormatException e) {}

        } else if (value instanceof Integer ){
            medge.setMax(index, ((Integer)value).shortValue());
        }
        m_max.update();
        m_min.update();
        ((GsDirectedEdgeListModel)jlist.getModel()).update();
    }
    public Object getNextMinValue() {
        if (!graph.isEditAllowed()) {
            return getMinValue();
        }
        short cur = medge.getMin(index);
        short max = medge.getSource().getMaxValue();
        if (update && cur < max) {
            graph.fireMetaChange();
        }
        
        if (cur < max) {
            medge.setMin(index, (short)(cur+1));
            m_max.update();
        }
        m_min.update();
        ((GsDirectedEdgeListModel)jlist.getModel()).update();
       return new Integer(medge.getMin(index));
    }

    public Object getPreviousMinValue() {
        if (!graph.isEditAllowed()) {
            return getMinValue();
        }
        short cur = medge.getMin(index);
        if (update && cur > 1) {
            graph.fireMetaChange();
        }
        
        if (cur > 1) {
            medge.setMin(index, (short)(cur-1));
        }
        m_min.update();
        ((GsDirectedEdgeListModel)jlist.getModel()).update();
        return new Integer(medge.getMin(index));
    }

    public Object getMinValue() {
        if (medge == null || medge == null) {
            return "";
        }
        return new Integer(medge.getMin(index));
    }

    public void setMinValue(Object value) {
        if (!graph.isEditAllowed()) {
            return;
        }
        if (value instanceof String) {
            try {
                medge.setMin(index, (short)Integer.parseInt(value.toString()));
                if (update) {
                    graph.fireMetaChange();
                }
            } catch (NumberFormatException e) {}

        } else if (value instanceof Integer ){
            medge.setMin(index, ((Integer)value).shortValue());
            m_min.update();
            m_max.update();
            ((GsDirectedEdgeListModel)jlist.getModel()).update();
        }
    }

    /**
     * change the edited edge.
     * @param index
     */
    public void setEdge(int index) {
        update = false;
        this.index = index;
        m_max.update();
        m_min.update();
        update = true;
    }
    /**
     * change the edited multiedge.
     * @param medge
     */
    public void setMedge(GsRegulatoryMultiEdge medge) {
        this.medge = medge;
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
