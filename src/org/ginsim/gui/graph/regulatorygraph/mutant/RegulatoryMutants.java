package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.ginsim.annotation.AnnotationPanel;
import org.ginsim.core.GraphEventCascade;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphListener;
import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.regulatorygraph.mutant.RegulatoryMutantDef;
import org.ginsim.graph.regulatorygraph.mutant.RegulatoryMutantChange;

import org.ginsim.gui.service.tool.reg2dyn.RegulatoryMutantListener;

import fr.univmrs.tagc.common.datastore.SimpleGenericList;
import fr.univmrs.tagc.common.datastore.ValueList;
import fr.univmrs.tagc.common.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.EnhancedJTable;
import fr.univmrs.tagc.common.widgets.SplitPane;
import fr.univmrs.tagc.common.widgets.StockButton;

/**
 * Associate a list of mutants to the regulatory graph, and offer the UI to edit this list.
 */
public class RegulatoryMutants extends SimpleGenericList implements GraphListener<RegulatoryNode, RegulatoryMultiEdge> {

    /**
     * edit mutants associated with a graph
     * @param graph
     * @return a panel to configure mutants
     */
    public static JPanel getMutantConfigPanel(RegulatoryGraph graph) {
        RegulatoryMutants mutants = (RegulatoryMutants) ObjectAssociationManager.getInstance().getObject(graph, MutantListManager.key, true);
        MutantPanel mpanel = new MutantPanel();
        Map m = new HashMap();
        m.put(RegulatoryMutantDef.class, mpanel);
        GenericListPanel lp = new GenericListPanel(m, "mutantList");
        lp.setList(mutants);
        mpanel.setEditedObject(mutants, lp, graph);
    	return lp;
    }

    Vector v_listeners = new Vector();
    Graph<RegulatoryNode,RegulatoryMultiEdge> graph;
    
    /**
     * edit mutants associated with the graph
     * @param graph
     */
    public RegulatoryMutants( Graph<RegulatoryNode,RegulatoryMultiEdge> graph) {
        this.graph = graph;
        graph.addGraphListener(this);
        
        prefix = "mutant_";
        canAdd = true;
        canOrder = true;
        canRemove = true;
        canEdit = true;
    }
    
    public GraphEventCascade edgeAdded(RegulatoryMultiEdge data) {
        return null;
    }
    public GraphEventCascade edgeRemoved(RegulatoryMultiEdge data) {
        return null;
    }
    public GraphEventCascade nodeAdded(RegulatoryNode data) {
        return null;
    }
    public GraphEventCascade nodeRemoved(RegulatoryNode data) {
        Vector v = new Vector();
        for (int i=0 ; i<v_data.size() ; i++) {
            RegulatoryMutantDef m = (RegulatoryMutantDef)v_data.get(i);
            for (int j=0 ; j<m.getChanges().size() ; j++) {
                RegulatoryMutantChange change = (RegulatoryMutantChange)m.getChange( j);
                if (change.getNode() == data) {
                    m.removeChange( change);
                    v.add(m);
                }
            }
        }
        if (v.size() > 0) {
            return new MutantCascadeUpdate (v);
        }
        return null;
    }
	public GraphEventCascade graphMerged(Collection<RegulatoryNode> data) {
		return null;
	}
    public GraphEventCascade nodeUpdated(RegulatoryNode data) {
        Vector v = new Vector();
        for (int i=0 ; i<v_data.size() ; i++) {
            RegulatoryMutantDef m = (RegulatoryMutantDef)v_data.get(i);
            for (int j=0 ; j<m.getChanges().size() ; j++) {
                RegulatoryMutantChange change = (RegulatoryMutantChange)m.getChange(j);
                if (change.getNode() == data) {
                    // check that it is up to date
                    RegulatoryNode vertex = (RegulatoryNode)data;
                    if (change.getMax() > vertex.getMaxValue()) {
                        change.setMax( vertex.getMaxValue());
                        if (change.getMin() > vertex.getMaxValue()) {
                            change.setMin( vertex.getMaxValue());
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
    public GraphEventCascade edgeUpdated(RegulatoryMultiEdge data) {
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
    public void addListener(RegulatoryMutantListener listener) {
        v_listeners.add(listener);
    }
    /**
     * un-register a listener
     * @param listener
     */
    public void removeListener(RegulatoryMutantListener listener) {
        v_listeners.remove(listener);
    }

    /**
     * get a mutant by its name.
     * @param value
     * @return the correct mutant, or null if none.
     */
    public RegulatoryMutantDef get(String value) {
        for (int i=0 ; i<v_data.size() ; i++) {
            RegulatoryMutantDef mdef = (RegulatoryMutantDef)v_data.get(i);
            if (mdef.getName().equals(value)) {
                return mdef;
            }
        }
        return null;
    }

	protected Object doCreate(String name, int mode) {
        RegulatoryMutantDef m = new RegulatoryMutantDef();
        m.setName( name);
		return m;
	}

	public void endParsing() {
	}
}

class MutantCascadeUpdate implements GraphEventCascade {
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

class MutantPanel extends SplitPane {
    private static final long serialVersionUID = 2625670418830465925L;
    
    GenericListPanel lp;

    GsRegulatoryMutantModel model;
    EnhancedJTable table_change;
    RegulatoryMutantDef curMutant = null;
    private RegulatoryMutants mutants;
    RegulatoryGraph graph;
    AnnotationPanel ap;

    public MutantPanel() { 
        setOrientation(VERTICAL_SPLIT);
        setName("mutantdef");
        model = new GsRegulatoryMutantModel();
        JPanel panel = new JPanel();
        ap = new AnnotationPanel();
        setBottomComponent(ap);
        setTopComponent(panel);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(new JLabel(Translator.getString("STR_perturbation_definition")), c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane sp = new JScrollPane();
        table_change = new EnhancedJTable(model);
        sp.setViewportView(table_change);
        panel.add(sp, c);
        int[] maxcols = {0,170 , 1,30 , 2,30};
        table_change.setMaxCols(maxcols);
        table_change.setDefaultRenderer(Object.class, new MutantTableRenderer());
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        JButton bdel = new StockButton("list-remove.png", true);
        bdel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        });
        panel.add(bdel,c);
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        JButton bup = new StockButton("go-up.png", true);
        bup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doMoveUp();
            }
        });
        panel.add(bup,c);
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        JButton bdown = new StockButton("go-down.png", true);
        bdown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doMoveDown();
            }
        });
        panel.add(bdown,c);
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
       curMutant = (RegulatoryMutantDef)mutants.getElement(null, t_sel[0]);
       model.setEditedObject(curMutant, graph);
       ap.setEditedObject(curMutant.getAnnotation());
    }
    
    void setEditedObject(RegulatoryMutants mutants, GenericListPanel lp, RegulatoryGraph graph) {
    	this.lp = lp;
        this.mutants = mutants;
        this.graph = graph;
        ap.setGraph(graph);
        
        lp.addSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateSelection();
            }
        });
        updateSelection();
    }
    
    
    protected void doMoveUp() {
        if (curMutant == null) {
            return;
        }
        int[] index = table_change.getSelectedRows();
        if (!curMutant.move(index, -1)) {
            return;
        }

        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)table_change.getSelectionModel();
        selectionModel.clearSelection();
        int min, max;
        int i=0;
        while (i<index.length) {
            min = index[i++];
            max = min;
            while (i<index.length) {
                if (index[i] == max+1) {
                    i++;
                    max++;
                } else {
                    break;
                }
            }
            selectionModel.addSelectionInterval(min, max);
        }
    }
    protected void doMoveDown() {
        if (curMutant == null) {
            return;
        }
        int[] index=table_change.getSelectedRows();
        if (!curMutant.move(index, 1)) {
            return;
        }

        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)table_change.getSelectionModel();
        selectionModel.clearSelection();
        int min, max;
        int i=0;
        while (i<index.length) {
            min = index[i++];
            max = min;
            while (i<index.length) {
                if (index[i] == max+1) {
                    i++;
                    max++;
                } else {
                    break;
                }
            }
            selectionModel.addSelectionInterval(min, max);
        }   
    }
}


class GsRegulatoryMutantModel extends AbstractTableModel {
    private static final long serialVersionUID = 864660594916225977L;

    private RegulatoryMutantDef curMutant;
    private RegulatoryGraph graph;
    ValueList vlist;
    
    /**
     * @param nodeOrder
     * @param curMutant
     * @param v_node_order
     */
    public void setEditedObject(RegulatoryMutantDef curMutant, RegulatoryGraph graph) {
    	this.graph = graph;
        this.curMutant = curMutant;
        List nodeOrder = graph.getNodeOrder();
        if (vlist == null) {
            vlist = new ValueList(nodeOrder, -1, "Select a gene...");
        } else {
            vlist.reset(nodeOrder, -1, "Select a gene...");
        }
        fireTableDataChanged();
    }

    protected void delete(int i) {
        if (curMutant == null || i == -1 || curMutant.getChanges().size() <= i) {
            return;
        }
        curMutant.removeChange(i);
        fireTableDataChanged();
    }

    public int getRowCount() {
        if (curMutant == null) {
            return 0;
        }
        return curMutant.getNbChanges()+1;
    }

    public int getColumnCount() {
        return 4;
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Translator.getString("STR_node");
        case 1:
            return Translator.getString("STR_min");
        case 2:
            return Translator.getString("STR_max");
        case 3:
            return Translator.getString("STR_condition");
        }
        return null;
    }

    public Class getColumnClass(int columnIndex) {
    	switch (columnIndex) {
			case 0:
				return ValueList.class;
			case 1:
			case 2:
	            return Integer.class;
			case 3:
	            return String.class;
			default:
		        return Object.class;
		}
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount()) {
            return false;
        }
        if (rowIndex == curMutant.getNbChanges()) {
            if (columnIndex == 0 || columnIndex == 1) {
                return true;
            }
            return false;
        }
        switch (columnIndex) {
            case 1:
            case 2:
            case 3:
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
            case 3:
                return curMutant.getCondition(rowIndex);
            default:
                return null;
        }
        if (value == -1) {
            return "";
        }
        return ""+value;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount() || columnIndex < 0 || columnIndex > 3) {
            return;
        }

        if (rowIndex == curMutant.getNbChanges()) {
            if (columnIndex == 0) {
                    ValueList value = (ValueList)getValueAt(rowIndex, columnIndex);
                    curMutant.addChange((RegulatoryNode)value.get(value.getSelectedIndex()));
                    fireTableDataChanged();
            }
            return;
        }
        
        if (columnIndex == 3) {
        	curMutant.setCondition(rowIndex, graph, (String)aValue);
        }
        
        if ("".equals(aValue) || "-".equals(aValue)) {
            curMutant.setMin(rowIndex, (byte)-1);
            curMutant.setMax(rowIndex, (byte)-1);
            fireTableCellUpdated(rowIndex, 1);
            fireTableCellUpdated(rowIndex, 2);
            return;
        }
        
        byte val;
        if (aValue instanceof Integer) {
        	val = ((Integer)aValue).byteValue();
        } else {
	        try {
	            val = (byte)Integer.parseInt(aValue.toString());
	        } catch (Exception e) {
	            return;
	        }
        }
        
        if (val == -1) {
            curMutant.setMin(rowIndex, (byte)-1);
            curMutant.setMax(rowIndex, (byte)-1);
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

	public boolean hasValidCondition(int row) {
		return curMutant.hasValidCondition(row);
	}
}

class MutantTableRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = -2173326249965764544L;

    public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus ,
            										int row , int column ) {
        Component cmp = super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
        cmp.setBackground(Color.WHITE);
        if( table != null && column == 3) {
        	GsRegulatoryMutantModel model = (GsRegulatoryMutantModel)table.getModel();
        	if (!model.hasValidCondition(row)) {
        		cmp.setBackground(Color.red);
        	}
        }
        return cmp;
    }
}
