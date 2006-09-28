package fr.univmrs.ibdm.GINsim.dynamicGraph;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;

/**
 * table model for the path constraint table.
 */
public class GsDynamicAnalyserPathModel extends DefaultTableModel {

    private static final long serialVersionUID = 1737959610482682294L;
    
    private int len;
    private int nbBad;
    private Vector nodeOrder;
    private Vector v;
    private Vector v_in;
    private GsGraphManager gmanager;
    private GsDynamicSearchPathConfig searchConfig;
    
    /**
     * @param graph
     * @param v_path
     * @param v_inPath
     * @param config
     */
    public GsDynamicAnalyserPathModel(GsDynamicGraph graph, Vector v_path, Vector v_inPath, GsDynamicSearchPathConfig config) {
        nodeOrder = graph.getNodeOrder();
        len = nodeOrder.size();
        this.searchConfig = config;
        this.gmanager = graph.getGraphManager();
        this.v = v_path;
        v_in = v_inPath;
        if (v == null) {
            v = new Vector(2);
        }
        if (v.size() == 0 ) {
            v.add(new int[len]);
            v.add(new int[len]);
            if (gmanager.containsVertex(new GsDynamicNode((int[])v.get(0)))) {
                v_in.add(Boolean.TRUE);
                v_in.add(Boolean.TRUE);
                nbBad = 0;
            } else {
                v_in.add(Boolean.FALSE);
                v_in.add(Boolean.FALSE);
                nbBad = 2;
            }
        }
    }

    public int getColumnCount() {
        return len;
    }
    public int getRowCount() {
        if (v == null) {
            return 0;
        }
        return v.size();
    }
    public Object getValueAt(int row, int column) {
        if (row < v.size() && column < len) {
            return ""+((int[])v.get(row))[column];
        }
        return null;
    }
    public void setValueAt(Object aValue, int row, int column) {
        if (aValue != null && aValue instanceof String && row < v.size() && column < len) {
            int[] t = (int[])v.get(row);
            try {
                t[column] = Integer.parseInt((String)aValue);
                if (gmanager.containsVertex(new GsDynamicNode(t))) {
                    if (v_in.get(row).equals(Boolean.FALSE)) {
                        nbBad--;
                    }
                    v_in.set(row, Boolean.TRUE);
                } else {
                    if (v_in.get(row).equals(Boolean.TRUE)) {
                        nbBad++;
                    }
                    v_in.set(row, Boolean.FALSE);
                }
                searchConfig.changed(nbBad == 0);
                fireTableRowsUpdated(row,row);
            } catch (Exception e) {}
        }
    }
    
    public String getColumnName(int column) {
        if (column < len) {
            return nodeOrder.get(column).toString();
        }
        return super.getColumnName(column);
    }

    /**
     * @param index
     */
    public void add(int index) {
        if (index < 0 || index >= v.size()) {
            add(0);
            return;
        }

        v.add(index+1, ((int[])v.get(index)).clone());
        v_in.add(index+1, v_in.get(index));
        if (v_in.get(index).equals(Boolean.FALSE)) {
            nbBad++;
        }
        searchConfig.changed(nbBad == 0);
        fireTableRowsInserted(index, index);
    }
    /**
     * @param index
     */
    public void del(int index) {
        if (index < 0 || index >= v.size() || v.size() <= 2) {
            return;
        }
        v.remove(index);
        if (v_in.get(index).equals(Boolean.FALSE)) {
            nbBad--;
        }
        v_in.remove(index);
        searchConfig.changed(nbBad == 0);
        fireTableRowsDeleted(index, index);
    }

    /**
     * test if the entered states are valid (ie are in the graph)
     * @return true if all states are in the graph
     */
    public boolean isOk() {
        return (nbBad == 0);
    }
}
