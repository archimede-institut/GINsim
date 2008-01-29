package fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.annotation.AnnotationPanel;
import fr.univmrs.ibdm.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.reg2dyn.GsRegulatoryMutantListener;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.datastore.SimpleGenericList;
import fr.univmrs.tagc.datastore.ValueList;
import fr.univmrs.tagc.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.widgets.EnhancedJTable;

/**
 * Associate a list of mutants to the regulatory graph, and offer the UI to edit this list.
 */
public class GsRegulatoryMutants extends SimpleGenericList implements GsGraphListener {

    /**
     * edit mutants associated with a graph
     * @param graph
     * @return a panel to configure mutants
     */
    public static JPanel getMutantConfigPanel(GsRegulatoryGraph graph) {
        GsRegulatoryMutants mutants = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, true);
        MutantPanel mpanel = new MutantPanel();
        Map m = new HashMap();
        m.put(GsRegulatoryMutantDef.class, mpanel);
        GenericListPanel lp = new GenericListPanel(m, "mutantList");
        lp.setList(mutants);
        mpanel.setEditedObject(mutants, lp, graph.getNodeOrder());
    	return lp;
    }

    Vector v_listeners = new Vector();
    GsRegulatoryGraph graph;
    
    /**
     * edit mutants associated with the graph
     * @param graph
     */
    public GsRegulatoryMutants(GsRegulatoryGraph graph) {
        this.graph = graph;
        graph.addGraphListener(this);
        
        prefix = "mutant_";
        canAdd = true;
        canOrder = true;
        canRemove = true;
        canEdit = true;
    }
    
    public GsGraphEventCascade edgeAdded(Object data) {
        return null;
    }
    public GsGraphEventCascade edgeRemoved(Object data) {
        return null;
    }
    public GsGraphEventCascade vertexAdded(Object data) {
        return null;
    }
    public GsGraphEventCascade vertexRemoved(Object data) {
        Vector v = new Vector();
        for (int i=0 ; i<v_data.size() ; i++) {
            GsRegulatoryMutantDef m = (GsRegulatoryMutantDef)v_data.get(i);
            for (int j=0 ; j<m.v_changes.size() ; j++) {
                GsRegulatoryMutantChange change = (GsRegulatoryMutantChange)m.v_changes.get(j);
                if (change.vertex == data) {
                    m.v_changes.remove(change);
                    v.add(m);
                }
            }
        }
        if (v.size() > 0) {
            return new MutantCascadeUpdate (v);
        }
        return null;
    }
	public GsGraphEventCascade graphMerged(Object data) {
		return null;
	}
    public GsGraphEventCascade vertexUpdated(Object data) {
        Vector v = new Vector();
        for (int i=0 ; i<v_data.size() ; i++) {
            GsRegulatoryMutantDef m = (GsRegulatoryMutantDef)v_data.get(i);
            for (int j=0 ; j<m.v_changes.size() ; j++) {
                GsRegulatoryMutantChange change = (GsRegulatoryMutantChange)m.v_changes.get(j);
                if (change.vertex == data) {
                    // check that it is up to date
                    GsRegulatoryVertex vertex = (GsRegulatoryVertex)data;
                    if (change.max > vertex.getMaxValue()) {
                        change.max = vertex.getMaxValue();
                        if (change.min > vertex.getMaxValue()) {
                            change.min = vertex.getMaxValue();
                        }
                        v.add(m);
                    }
                }
            }
        }
        if (v.size() > 0) {
            return new MutantCascadeUpdate (v);
        }
        return null;
    }
    public GsGraphEventCascade edgeUpdated(Object data) {
        return null;
    }
    
    /**
     * @param o
     * @return the index of o, -1 if not found
     */
    public int indexOf(Object o) {
        return v_data.indexOf(o);
    }
    /**
     * register a new listener for this object
     * @param listener
     */
    public void addListener(GsRegulatoryMutantListener listener) {
        v_listeners.add(listener);
    }
    /**
     * un-register a listener
     * @param listener
     */
    public void removeListener(GsRegulatoryMutantListener listener) {
        v_listeners.remove(listener);
    }

    /**
     * get a mutant by its name.
     * @param value
     * @return the correct mutant, or null if none.
     */
    public GsRegulatoryMutantDef get(String value) {
        for (int i=0 ; i<v_data.size() ; i++) {
            GsRegulatoryMutantDef mdef = (GsRegulatoryMutantDef)v_data.get(i);
            if (mdef.name.equals(value)) {
                return mdef;
            }
        }
        return null;
    }

	protected Object doCreate(String name) {
        GsRegulatoryMutantDef m = new GsRegulatoryMutantDef();
        m.name = name;
		return m;
	}
}

class MutantCascadeUpdate implements GsGraphEventCascade {
    protected MutantCascadeUpdate(Vector v) {
        this.v = v;
    }
    Vector v;

    public String toString() {
        StringBuffer s = new StringBuffer("updated mutants:");
        for (int i=0 ; i<v.size() ; i++) {
            s.append(" ");
            s.append(v.get(i));
        }
        return s.toString();
    }
}

class MutantPanel extends JPanel {
    private static final long serialVersionUID = 2625670418830465925L;
    
    GenericListPanel lp;

    GsRegulatoryMutantModel model;
    EnhancedJTable table_change;
    GsRegulatoryMutantDef curMutant = null;
    private GsRegulatoryMutants mutants;
    Vector v_nodeOrder;
    AnnotationPanel ap;

    public MutantPanel() { 
        setLayout(new GridBagLayout());
        
        model = new GsRegulatoryMutantModel();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane sp = new JScrollPane();
        table_change = new EnhancedJTable(model);
        sp.setViewportView(table_change);
        add(sp, c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        JButton bdel = new JButton("X");
        bdel.setForeground(Color.RED);
        bdel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        });
        add(bdel,c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        ap = new AnnotationPanel();
        add(ap,c);
    }
    
    protected void delete() {
        int[] t_sel = table_change.getSelectedRows();
        for (int i=t_sel.length-1 ; i>=0 ; i--) {
            model.delete(t_sel[i]);
        }
    }
    
    protected void updateSelection() {
        int[] t_sel = lp.getSelection();
       if (t_sel == null || t_sel.length != 1) {
           curMutant = null;
           return;
       }
       curMutant = (GsRegulatoryMutantDef)mutants.getElement(null, t_sel[0]);
       model.setEditedObject(curMutant, v_nodeOrder);
       ap.setEditedObject(curMutant.annotation);
    }
    
    void setEditedObject(GsRegulatoryMutants mutants, GenericListPanel lp, Vector v_nodeOrder) {
    	this.lp = lp;
        this.mutants = mutants;
        this.v_nodeOrder = v_nodeOrder;
        
        lp.addSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateSelection();
            }
        });
    }
}


class GsRegulatoryMutantModel extends AbstractTableModel {
    private static final long serialVersionUID = 864660594916225977L;

    private GsRegulatoryMutantDef curMutant;
    Vector v_genes;
    ValueList vlist;
    
    /**
     * @param nodeOrder
     * @param curMutant
     * @param v_node_order
     */
    public void setEditedObject(GsRegulatoryMutantDef curMutant, Vector v_node_order) {
        this.curMutant = curMutant;
        if (vlist == null) {
            vlist = new ValueList(v_node_order, -1, "Select a gene...");
        } else {
            vlist.reset(v_node_order, -1, "Select a gene...");
        }
        fireTableStructureChanged();
    }

    protected void delete(int i) {
        if (curMutant == null || i == -1 || curMutant.v_changes.size() <= i) {
            return;
        }
        curMutant.v_changes.remove(i);
        fireTableStructureChanged();
    }

    public int getRowCount() {
        if (curMutant == null) {
            return 0;
        }
        return curMutant.getNbChanges()+1;
    }

    public int getColumnCount() {
        return 3;
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Translator.getString("STR_node");
        case 1:
            return Translator.getString("STR_min");
        case 2:
            return Translator.getString("STR_max");
        }
        return null;
    }

    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return ValueList.class;
        }
        if (columnIndex > 0 && columnIndex < 3) {
            return String.class;
        }
        return Object.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount()) {
            return false;
        }
        if (rowIndex == curMutant.getNbChanges()) {
            if (columnIndex == 0) {
                return true;
            }
            return false;
        }
        switch (columnIndex) {
            case 1:
            case 2:
                return true;
        }
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount()) {
            return null;
        }
        int value = -1;
        if (rowIndex == curMutant.getNbChanges()) {
            if (columnIndex == 0) {
                return vlist;
            }
            return "";
        }
        switch (columnIndex) {
            case 0:
                return curMutant.getName(rowIndex);
            case 1:
                value = curMutant.getMin(rowIndex);
                break;
            case 2:
                value = curMutant.getMax(rowIndex);
                break;
            default:
                return null;
        }
        if (value == -1) {
            return "";
        }
        return ""+value;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount() || columnIndex < 0 || columnIndex > 2) {
            return;
        }
        
        if (rowIndex == curMutant.getNbChanges()) {
            if (columnIndex == 0) {
                    ValueList value = (ValueList)getValueAt(rowIndex, columnIndex);
                    curMutant.addChange((GsRegulatoryVertex)value.get(value.getSelectedIndex()));
                    fireTableStructureChanged();
            }
            return;
        }

        
        if ("".equals(aValue) || "-".equals(aValue)) {
            curMutant.setMin(rowIndex, (short)-1);
            curMutant.setMax(rowIndex, (short)-1);
            fireTableCellUpdated(rowIndex, 1);
            fireTableCellUpdated(rowIndex, 2);
            return;
        }
        
        short val;
        try {
            val = (short)Integer.parseInt((String)aValue);
        } catch (Exception e) {
            return;
        }
        
        if (val == -1) {
            curMutant.setMin(rowIndex, (short)-1);
            curMutant.setMax(rowIndex, (short)-1);
            fireTableCellUpdated(rowIndex, 1);
            fireTableCellUpdated(rowIndex, 2);
            return;
        }
        switch (columnIndex) {
            case 1:
                curMutant.setMin(rowIndex, val);
                break;
            case 2:
                curMutant.setMax(rowIndex, val);
                break;
        }
        fireTableCellUpdated(rowIndex, 1);
        fireTableCellUpdated(rowIndex, 2);
    }
}
