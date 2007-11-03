package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.datastore.GenericListListener;
import fr.univmrs.tagc.datastore.SimpleGenericList;
import fr.univmrs.tagc.datastore.gui.GenericListPanel;

/**
 * configure priority classes.
  */
public class GsReg2dynPriorityClassConfig extends JPanel {

    private static final long serialVersionUID = -3214357334096594239L;

    protected static final int UP = 0;
    protected static final int DOWN = 1;
    protected static final int NONE = 2;
    
    protected static final String[] t_typeName = {" [+]", " [-]", ""};
    
    private JComboBox cb_auto;

    private JButton but_insert;
    private JButton but_remove;
    
    private Vector v_class;
    private Vector v_nodeOrder;
    GenericListPanel contentPanel;
    GenericListPanel availablePanel;
    SimpleGenericList contentList = new SimpleGenericList();
    SimpleGenericList availableList = new SimpleGenericList();
    
    private Vector v_content = new Vector();
    private Vector v_avaible = new Vector();
    
    private Map m_elt;
    private GsReg2dynPriorityClass currentClass;

    PriorityClassList classList;
    GenericListPanel listPanel = null;

    private JToggleButton but_group;
    
    private static final int AUTO_MANY = 1;
    private static final int AUTO_PLUS_MINUS = 2;
    private static final String[] t_sauto = { "One unique class", "One class for each node", "Splitting transitions – one unique class" };
    
    /**
     * @param frame
     * @param nodeOrder
     * @param param
     */
    public GsReg2dynPriorityClassConfig(Vector nodeOrder, GsSimulationParameters param) {
        this.v_class = param.getVclass();
        this.m_elt = param.getMelt();
        this.v_nodeOrder = nodeOrder;
        initialize();
        contentList.setData(v_content);
        availableList.setData(v_avaible);
    }
    
    private void initialize() {
        setLayout(new GridBagLayout());
        
        GridBagConstraints c_binsert = new GridBagConstraints();
        GridBagConstraints c_bremove = new GridBagConstraints();
        GridBagConstraints c_bgroup = new GridBagConstraints();
        GridBagConstraints c_scroll_class = new GridBagConstraints();
        GridBagConstraints c_scroll_in = new GridBagConstraints();
        GridBagConstraints c_scroll_av = new GridBagConstraints();
        GridBagConstraints c_cautoconf = new GridBagConstraints();
        GridBagConstraints c_classLabel = new GridBagConstraints();
        GridBagConstraints c_availableLabel = new GridBagConstraints();
        GridBagConstraints c_contentLabel = new GridBagConstraints();
        
        c_binsert.gridx = 4;
        c_binsert.gridy = 1;
        c_bremove.gridx = 4;
        c_bremove.gridy = 2;

        c_bgroup.gridx = 1;
        c_bgroup.gridy = 2;
        c_bgroup.anchor = GridBagConstraints.NORTH;
        
        c_classLabel.gridx = 0;
        c_classLabel.gridy = 0;
        c_classLabel.weightx = 1;
        c_classLabel.fill = GridBagConstraints.HORIZONTAL;
        c_scroll_class.gridx = 0;
        c_scroll_class.gridy = 1;
        c_scroll_class.gridheight = 4;
        c_scroll_class.fill = GridBagConstraints.BOTH;
        c_scroll_class.weightx = 1;
        c_scroll_class.weighty = 1;
        
        c_cautoconf.gridx = 0;
        c_cautoconf.gridy = 5;
	    c_cautoconf.gridwidth = 3;
        c_cautoconf.fill = GridBagConstraints.HORIZONTAL;
        
        
        c_contentLabel.gridx = 3;
        c_contentLabel.gridy = 0;
        c_contentLabel.weightx = 1;
        c_contentLabel.fill = GridBagConstraints.HORIZONTAL;
        c_scroll_in.gridx = 3;
        c_scroll_in.gridy = 1;
        c_scroll_in.gridheight = 4;
        c_scroll_in.fill = GridBagConstraints.BOTH;
        c_scroll_in.weightx = 1;
        c_scroll_in.weighty = 1;
        
        c_availableLabel.gridx = 5;
        c_availableLabel.gridy = 0;
        c_availableLabel.weightx = 1;
        c_availableLabel.fill = GridBagConstraints.HORIZONTAL;
        c_scroll_av.gridx = 5;
        c_scroll_av.gridy = 1;
        c_scroll_av.gridheight = 4;
        c_scroll_av.fill = GridBagConstraints.BOTH;
        c_scroll_av.weightx = 1;
        c_scroll_av.weighty = 1;
        
        add(new JLabel(Translator.getString("STR_classList")), c_classLabel);
        add(new JLabel(Translator.getString("STR_otherClassContent")), c_availableLabel);
        add(new JLabel(Translator.getString("STR_classContent")), c_contentLabel);
        add(getBut_group(), c_bgroup);
        add(getCb_auto(), c_cautoconf);
        add(getContentPanel(), c_scroll_in);
        add(getBut_insert(), c_binsert);
        add(getBut_remove(), c_bremove);
        add(getAvaiblePanel(), c_scroll_av);
        add(getListPanel(), c_scroll_class);
    }
    
    protected GenericListPanel getContentPanel() {
    	if (contentPanel == null) {
    		contentPanel = new GenericListPanel();
    		contentPanel.setList(contentList);
    	}
    	return contentPanel;
    }
    protected GenericListPanel getAvaiblePanel() {
    	if (availablePanel == null) {
    		availablePanel = new GenericListPanel();
    		availablePanel.setList(availableList);
    	}
    	return availablePanel;
    }
    
    private JButton getBut_insert() {
        if (but_insert == null) {
            but_insert = new JButton("<<");
            but_insert.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    insert();
                }
            });
        }
        return but_insert;
    }
    private JButton getBut_remove() {
        if (but_remove == null) {
            but_remove = new JButton(">>");
            but_remove.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    remove();
                }
            });
        }
        return but_remove;
    }

    private JToggleButton getBut_group() {
        if (but_group == null) {
            but_group = new JToggleButton("G");
            but_group.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    groupToggle();
                }
            });
        }
        return but_group;
    }


    private GenericListPanel getListPanel() {
    	if (listPanel == null) {
    		listPanel = new GenericListPanel();
    		listPanel.addSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					classSelectionChanged();
				}
			});
    		classList = new PriorityClassList(v_class, m_elt);
    		listPanel.setList(classList);
    	}
    	return listPanel;
    }
    
    /**
     * toggle the selection grouping:
     *    - if all selected items are part of the same group, it will be "ungrouped"
     *    - if selected items are part of several groups, they will be merged with the first one
     */
    protected void groupToggle() {
        int[] ts = listPanel.getSelection();
        int[][] selExtended = classList.getMovingRows(NONE, ts);
        // if class with different priorities are selected: give them all the same priority
        if (selExtended.length < 1) {
        	return;
        }
        if (selExtended.length > 1) {
            if (ts == null || ts.length < 1) {
                return;
            }
            int pos = selExtended[0][1];
            int pr = ((GsReg2dynPriorityClass)v_class.get(pos)).rank;
            for (int i=1 ; i<selExtended.length ; i++) {
            	for (int j=selExtended[i-1][1]+1 ; j<selExtended[i][0] ; j++) {
            		((GsReg2dynPriorityClass)v_class.get(j)).rank -= i-1;
            	}
            	for (int j=selExtended[i][0] ; j<=selExtended[i][1] ; j++) {
	                pos++;
	                ((GsReg2dynPriorityClass)v_class.get(j)).rank = pr;
                    classList.moveElementAt(j, pos);
            	}
            }
            int l = selExtended.length - 1;
            for (int j=selExtended[l][1]+1 ; j<v_class.size() ; j++) {
            	((GsReg2dynPriorityClass)v_class.get(j)).rank -= l;
            }
            classList.refresh();
            listPanel.getSelectionModel().clearSelection();
            listPanel.getSelectionModel().addSelectionInterval(selExtended[0][0],pos);
        } else {
            if (selExtended[0][0] != selExtended[0][1]) {
                int i = selExtended[0][0];
                int inc = 1;
                for (i++ ; i<selExtended[0][1] ; i++) {
                    ((GsReg2dynPriorityClass)v_class.get(i)).rank += inc;
                    inc++;
                }
                for ( ; i<v_class.size() ; i++) {
                    ((GsReg2dynPriorityClass)v_class.get(i)).rank += inc;
                }
                classList.refresh();
            }
        }
    }
    
    /**
     * add genes to the selected class: they will be removed from their current class.
     */
    protected void insert() {
        int[] t = availablePanel.getSelection();
        for (int i=0 ; i<t.length ; i++) {
        	int index = t[i];
            PriorityMember k = (PriorityMember)v_avaible.get(index);
            if (k.type != NONE) { // +1 and -1 are separated, don't move everything
                Object[] tk = (Object[])m_elt.get(k.vertex);
                if (k.type == UP) {
                    tk[0] = currentClass;
                } else {
                    tk[1] = currentClass;
                }
            } else { // simple case
                m_elt.put(k.vertex, currentClass);
            }
        }
        classSelectionChanged();
    }
    
    /**
     * remove genes from the selected class: they will go back to the default one.
     */
    protected void remove() {
        int[] t = contentPanel.getSelection();
        for (int i=0 ; i<t.length ; i++) {
        	int index = t[i];
            PriorityMember k = (PriorityMember)v_content.get(index);
            Object lastClass = v_class.get(v_class.size()-1);
            if (k.type != NONE) { // +1 and -1 are separated, don't move everything
                Object[] tk = (Object[])m_elt.get(k.vertex);
                if (k.type == UP) {
                    tk[0] = lastClass;
                } else {
                    tk[1] = lastClass;
                }
            } else { // simple case
                m_elt.put(k.vertex, lastClass);
            }
        }
        classSelectionChanged();
    }

    /**
     * call it when the user changes the class selection: update UI to match the selection
     */
    protected void classSelectionChanged() {
        v_content.clear();
        v_avaible.clear();
        
        int[] ti = listPanel.getSelection();
        int[][] selExtended = classList.getMovingRows(NONE, ti);
        if (selExtended.length != 1) {
            but_group.setEnabled(true);
            but_group.setSelected(false);
        } else {
            if (selExtended[0][0] != selExtended[0][1]) {
                but_group.setEnabled(true);
                but_group.setSelected(true);
            } else {
                but_group.setEnabled(false);
                but_group.setSelected(false);
            }
        }
        
        if (ti.length != 1) {
            but_remove.setEnabled(false);
            but_insert.setEnabled(false);
            contentList.refresh();
            availableList.refresh();
            return;
        }
        but_remove.setEnabled(true);
        but_insert.setEnabled(true);
        
        int i = ti[0];
        if (i>=0 && i<v_class.size()) {
            currentClass = (GsReg2dynPriorityClass)v_class.get(i);
        } else {
//            table_class.getSelectionModel().setSelectionInterval(0, 0);
            return;
        }
        if (v_class.size() < 2) {
            but_remove.setEnabled(false);
        } else {
            but_remove.setEnabled(true);
        }
        
        Iterator it = v_nodeOrder.iterator();
        while (it.hasNext()) {
        	GsRegulatoryVertex v = (GsRegulatoryVertex)it.next();
            PriorityMember k = new PriorityMember(v, NONE);
            Object target = m_elt.get(v);
            if (target instanceof Object[]) {
                Object[] t = (Object[])target;
                k.type = DOWN;
                PriorityMember kp = new PriorityMember(v, UP);
                if (t[0] == currentClass) {
                    if (t[1] == currentClass) {
                        v_content.add(k);
                        v_content.add(kp);
                    } else {
                        v_content.add(kp);
                        v_avaible.add(k);
                    }
                } else if (t[1] == currentClass) {
                    v_avaible.add(kp);
                    v_content.add(k);
                } else {
                    v_avaible.add(kp);
                    v_avaible.add(k);
                }
            } else {
                if (target == currentClass) {
                    v_content.add(k);
                } else {
                    v_avaible.add(k);
                }
            }
        }
        contentList.refresh();
        availableList.refresh();
    }
    
    private JComboBox getCb_auto() {
        if (cb_auto == null) {
            cb_auto = new JComboBox();
            for (int i=0 ; i<t_sauto.length ; i++) {
                cb_auto.addItem(t_sauto[i]);
            }
            cb_auto.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    applyAuto();
                }
            });
        }
        return cb_auto;
    }
    
    protected void applyAuto() {
        // first delete all classes
        while (v_class.size() > 1) {
            v_class.remove(1);
        }
        Object lastClass = v_class.get(0);
        ((GsReg2dynPriorityClass)lastClass).setName("new class");
        for (int i=0 ; i<v_nodeOrder.size() ; i++) {
            m_elt.put(v_nodeOrder.get(i), lastClass);
        }
        switch (cb_auto.getSelectedIndex()) {
            case AUTO_MANY:
                // should be equivalent to the old priority system: add one class per node
                v_class.clear();
                m_elt.clear();
                for (int i=0 ; i<v_nodeOrder.size() ; i++) {
                    currentClass = new GsReg2dynPriorityClass();
                    v_class.add(i, currentClass);
                    m_elt.put(v_nodeOrder.get(i), currentClass);
                    currentClass.setName(""+v_nodeOrder.get(i));
                }
                break;
            case AUTO_PLUS_MINUS:
                for (int i=0 ; i<v_nodeOrder.size() ; i++) {
                    Object[] t = {lastClass, lastClass};
                    m_elt.put(v_nodeOrder.get(i), t);
                }
                break;
        }
        classList.refresh();
//        classTableModel.fireTableRowsDeleted(0, size);
//        classTableModel.fireTableRowsInserted(0, v_class.size()-1);
//        table_class.getSelectionModel().setSelectionInterval(0, 0);
    }
}

class PriorityMember {
	GsRegulatoryVertex vertex;
	int type;
	
	public PriorityMember(GsRegulatoryVertex vertex, int type) {
		this.vertex = vertex;
		this.type = type;
	}

	public String toString() {
		return vertex+GsReg2dynPriorityClassConfig.t_typeName[type];
	}
}

class PriorityClassList extends SimpleGenericList {

	Map m_elt;
	
	public PriorityClassList(Vector v_class, Map m_elt) {
		canAdd = true;
		canRemove = true;
		canOrder = true;
		canEdit = true;
		nbcol = 3;
		addWithPosition = true;
		this.m_elt = m_elt;
		Class[] t = {Integer.class, Boolean.class, String.class};
		t_type = t;
		setData(v_class);
	}
	public void moveElementAt(int j, int pos) {
		moveElement(j, pos);
		
	}
	public Object doCreate(String name, int pos) {
        int priority = ((GsReg2dynPriorityClass)v_data.get(pos)).rank;
        for ( ; pos < v_data.size() ; pos++) {
            if (((GsReg2dynPriorityClass)v_data.get(pos)).rank != priority) {
                break;
            }
        }
        GsReg2dynPriorityClass newclass = new GsReg2dynPriorityClass(priority+1);
        for (int i=pos ; i<v_data.size() ; i++) {
            ((GsReg2dynPriorityClass)v_data.get(i)).rank++;
        }
        return newclass;
	}
	
	public boolean remove(String filter, int[] t_index) {
		if (t_index.length >= v_data.size()) {
			return false;
		}
		for (int i = t_index.length - 1 ; i > -1 ; i--) {
			int index = getRealIndex(filter, t_index[i]);
			
            GsReg2dynPriorityClass c = (GsReg2dynPriorityClass) v_data.remove(index);
            if (index < v_data.size()) {
            	// update rank of the next priority classes
            	if ( index == 0 || ((GsReg2dynPriorityClass) v_data.get(index-1)).rank != c.rank) {
            		if (((GsReg2dynPriorityClass) v_data.get(index)).rank != c.rank) {
            			for (int j=index ; j<v_data.size() ; j++) {
            				((GsReg2dynPriorityClass) v_data.get(j)).rank--;
            			}
            		}
            	}
            }
            Iterator it = m_elt.keySet().iterator();
            Object lastClass = v_data.get(v_data.size()-1);
            while (it.hasNext()) {
                Object k = it.next();
                Object cl = m_elt.get(k); 
                if (cl == c) {
                    m_elt.put(k,lastClass);
                } else if (cl instanceof Object[]) {
                    Object[] t = (Object[])cl;
                    for (int j=0 ; j<t.length ; j++) {
                        if (t[j] == c) {
                            t[j] = lastClass;
                        }
                    }
                }
            }
			
			if (v_listeners != null) {
				it = v_listeners.iterator();
				while (it.hasNext()) {
					((GenericListListener)it.next()).itemRemoved(c, t_index[i]);
				}
			}
		}
		return true;
	}

	
    /**
     * move the whole selection up.
     * if some selected class are part of a group, the whole group will move with it.
     */
    protected void doMoveUp(int[] selection, int diff) {
        int[][] index = getMovingRows(GsReg2dynPriorityClassConfig.UP, selection);
        if (index == null) {
            return;
        }
        
        int reselect = 0;
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = start+diff;
            int pr = ((GsReg2dynPriorityClass)v_data.get(start)).rank;
            int prTarget = ((GsReg2dynPriorityClass)v_data.get(target)).rank;
            target--;
            while (target >= 0 && ((GsReg2dynPriorityClass)v_data.get(target)).rank == prTarget) {
                target--;
            }
            target++;
            for (int j=target ; j<start ; j++) {
                ((GsReg2dynPriorityClass)v_data.get(j)).rank = pr;
            }
            for (int j=0 ; j<=stop-start ; j++) {
            	((GsReg2dynPriorityClass)v_data.get(start+j)).rank = prTarget;
            	moveElement(start+j, target+j);
            	if (reselect < selection.length && selection[reselect] == start+j) {
            		selection[reselect++] = target+j;
                }
            }
        }
        refresh();
    }

    /**
     * move the whole selection down
     * if some selected class are part of a group, the whole group will move with it.
     */
    protected void doMoveDown(int[] selection, int diff) {
        int[][] index = getMovingRows(GsReg2dynPriorityClassConfig.DOWN, selection);
        if (index == null) {
            return;
        }
        
        int reselect = 0;
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = stop+diff;
            int pr = ((GsReg2dynPriorityClass)v_data.get(start)).rank;
            int prTarget = ((GsReg2dynPriorityClass)v_data.get(target)).rank;
            target++;
            while (target < v_data.size() && ((GsReg2dynPriorityClass)v_data.get(target)).rank == prTarget) {
                target++;
            }
            target--;
            for (int j=stop+1 ; j<=target ; j++) {
                ((GsReg2dynPriorityClass)v_data.get(j)).rank = pr;
            }
            for (int j=0 ; j<=stop-start ; j++) {
                ((GsReg2dynPriorityClass)v_data.get(start)).rank = prTarget;
                moveElement(start, target);
                if (reselect < selection.length && selection[reselect] == start+j) {
            		selection[reselect++] = target-stop+start+j;
                }
            }
        }
        refresh();
    }
    
    /**
     * when moving a selection of class, they must move with other class of the same priority.
     * this checks the selection and compute a list of all really moving rows as ranges: start-stop for each selected clas
     * @param key
     * @param index 
     * @return moving ranges or null if nothing should move
     */
    int[][] getMovingRows(int key, int[] index) {
        if (index == null) {
        	return null;
        }
        int end = v_data.size();
        int count = 0;
        int lastPriority = -1;
        for (int i=0 ; i<index.length ; i++) {
            int priority = ((GsReg2dynPriorityClass)v_data.get(index[i])).rank;
            if (priority != lastPriority) {
                int start = index[i]-1;
                int stop = index[i]+1;
                while(start >= 0 && ((GsReg2dynPriorityClass)v_data.get(start)).rank == priority) {
                    start--;
                }
                while(stop < end && ((GsReg2dynPriorityClass)v_data.get(stop)).rank == priority) {
                    stop++;
                }
                start++;
                stop--;
                // if moving up and already on top or moving down and already on bottom: don't do anything
                if (key==GsReg2dynPriorityClassConfig.UP && start == 0 || key==GsReg2dynPriorityClassConfig.DOWN && stop == end-1) {
                    return null;
                }
                count++;
                lastPriority = priority;
            }
        }
        
        int[][] ret = new int[count][3];
        lastPriority = -1;
        count = 0;
        for (int i=0 ; i<index.length ; i++) {
            int priority = ((GsReg2dynPriorityClass)v_data.get(index[i])).rank;
            if (priority != lastPriority) {
                int start = index[i]-1;
                int stop = index[i]+1;
                while(start >= 0 && ((GsReg2dynPriorityClass)v_data.get(start)).rank == priority) {
                    start--;
                }
                while(stop < end && ((GsReg2dynPriorityClass)v_data.get(stop)).rank == priority) {
                    stop++;
                }
                start++;
                stop--;
                ret[count][0] = start;
                ret[count][1] = stop;
                lastPriority = priority;
                count++;
            }
        }
        return ret;
    }


}

class ClassTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 7741212000452988183L;

	Vector v;
	
	protected ClassTableModel(Vector v) {
		this.v = v;
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int column) {
		switch(column) {
		case 0: return Translator.getString("STR_rank");
		case 1: return Translator.getString("STR_sync");
		case 2: return Translator.getString("STR_name");
		}
		return super.getColumnName(column);
	}

	public int getRowCount() {
        if (v == null) {
            return 0;
        }
		return v.size();
	}

	public Class getColumnClass(int columnIndex) {
        if (columnIndex == 1) {
            return Boolean.class;
        }
        return super.getColumnClass(columnIndex);
    }

    public Object getValueAt(int row, int column) {
		if (row < v.size()) {
			GsReg2dynPriorityClass pclass = (GsReg2dynPriorityClass)v.get(row);
			switch (column) {
				case 0: return ""+pclass.rank;
                case 1: return pclass.getMode() == GsReg2dynPriorityClass.ASYNCHRONOUS ? Boolean.FALSE : Boolean.TRUE;
				case 2: return pclass.getName();
			}
		}
		return super.getValueAt(row, column);
	}

	public boolean isCellEditable(int row, int column) {
		return column == 1 || column == 2;
	}

	public void setValueAt(Object aValue, int row, int column) {
		if (row < v.size()) {
            if (column == 1 && aValue instanceof Boolean) {
                ((GsReg2dynPriorityClass)v.get(row)).setMode(aValue == Boolean.FALSE ? GsReg2dynPriorityClass.ASYNCHRONOUS : GsReg2dynPriorityClass.SYNCHRONOUS);
            }
            if (column == 2) {
                ((GsReg2dynPriorityClass)v.get(row)).setName(aValue.toString());
            }
		}
	}
    
    protected void moveElementAt(int i, int j) {
        Object obj=v.remove(i);
        v.insertElementAt(obj,j);
        fireTableRowsDeleted(i, i);
        fireTableRowsInserted(j, j);
    }
}

class ClassList extends SimpleGenericList {
	protected ClassList (Vector v) {
		setData(v);
		canEdit = true;
		canAdd = true;
		canRemove = true;
		canOrder = true;
	}
}