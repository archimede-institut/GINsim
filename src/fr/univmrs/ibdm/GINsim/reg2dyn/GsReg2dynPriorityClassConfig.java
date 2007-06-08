package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * configure priority classes.
  */
public class GsReg2dynPriorityClassConfig extends JPanel {

    private static final long serialVersionUID = -3214357334096594239L;

    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int NONE = 2;
    
    private JScrollPane scrollpane;
    private JScrollPane scrollpaneContent;
    private JScrollPane scrollpaneAvaible;
    private JList list_content;
    private JList list_avaible;
    private JButton but_up;
    private JButton but_down;
    private JButton but_new;
    private JButton but_delete;
    
    private JComboBox cb_auto;

    private JButton but_insert;
    private JButton but_remove;
    
    private Vector v_class;
    private Vector v_nodeOrder;
    private ClassListModel contentModel;
    private ClassListModel avaibleModel;
    
    private Vector v_content = new Vector();
    private Vector v_content_comment = new Vector();
    private Vector v_avaible = new Vector();
    private Vector v_avaible_comment = new Vector();
    
    private String S_PLUS = " [+]";
    private String S_MINUS = " [-]";
    
    private Map m_elt;
    private GsReg2dynPriorityClass currentClass;

	private JTable table_class;

	private ClassTableModel classTableModel;

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
        setSize(700, 400);
    }
    
    private void initialize() {
        setLayout(new GridBagLayout());
        
        GridBagConstraints c_bdel = new GridBagConstraints();
        GridBagConstraints c_bnew = new GridBagConstraints();
        GridBagConstraints c_bup = new GridBagConstraints();
        GridBagConstraints c_bdown = new GridBagConstraints();
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
        c_bnew.gridx = 1;
        c_bnew.gridy = 1;
        c_bdel.gridx = 2;
        c_bdel.gridy = 1;
        
        c_bup.gridx = 1;
        c_bup.gridy = 3;
        c_bup.insets.top = 10;
        c_bdown.gridx = 1;
        c_bdown.gridy = 4;
        c_bdown.anchor = GridBagConstraints.NORTH;
        
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
        add(getScrollpaneClass(), c_scroll_class);
        add(getBut_new(), c_bnew);
        add(getBut_delete(), c_bdel);
        add(getBut_up(), c_bup);
        add(getBut_down(), c_bdown);
        add(getCb_auto(), c_cautoconf);
        add(getScrollpaneContent(), c_scroll_in);
        add(getBut_insert(), c_binsert);
        add(getBut_remove(), c_bremove);
        add(getScrollpaneAvaible(), c_scroll_av);
        add(getBut_group(), c_bgroup);
    }
    
    private JButton getBut_delete() {
        if (but_delete == null) {
            but_delete = new JButton("X");
            but_delete.setForeground(Color.RED);
            but_delete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    delete();
                }
            });
        }
        return but_delete;
    }
    private JButton getBut_down() {
        if (but_down == null) {
            but_down = new JButton(GsEnv.getIcon("downArrow.gif"));
            but_down.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    down();
                }
            });
        }
        return but_down;
    }
    private JButton getBut_new() {
        if (but_new == null) {
            but_new = new JButton("+");
            but_new.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    create();
                }
            });
        }
        return but_new;
    }
    private JButton getBut_up() {
        if (but_up == null) {
            but_up = new JButton(GsEnv.getIcon("upArrow.gif"));
            but_up.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    up();
                }
            });
        }
        return but_up;
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

    private JTable getTableClass() {
    	if (table_class == null) {
    		classTableModel = new ClassTableModel(v_class);
    		table_class = new GsJTable(classTableModel);
            table_class.getColumn(table_class.getColumnName(0)).setMinWidth(35);
            table_class.getColumn(table_class.getColumnName(1)).setMinWidth(30);
            table_class.getColumn(table_class.getColumnName(0)).setMaxWidth(35);
            table_class.getColumn(table_class.getColumnName(1)).setMaxWidth(30);
    		table_class.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
    		    public void valueChanged(ListSelectionEvent e) {
    		        classSelectionChanged();
    		    }
    		});
    	}
    	return table_class;
    }
    
    private JList getList_content() {
        if (list_content == null) {
            contentModel = new ClassListModel(v_content, v_content_comment);
            list_content = new JList(contentModel);
        }
        return list_content;
    }
    private JList getList_avaible() {
        if (list_avaible == null) {
            avaibleModel = new ClassListModel(v_avaible, v_avaible_comment);
            list_avaible = new JList(avaibleModel);
        }
        return list_avaible;
    }
    private JScrollPane getScrollpaneClass() {
        if (scrollpane == null) {
            scrollpane = new JScrollPane();
            scrollpane.setViewportView(getTableClass());
            scrollpane.setSize(20, scrollpane.getHeight());
        }
        return scrollpane;
    }

    private JScrollPane getScrollpaneContent() {
        if (scrollpaneContent == null) {
            scrollpaneContent = new JScrollPane();
            scrollpaneContent.setViewportView(getList_content());
            scrollpaneContent.setSize(100, scrollpaneContent.getHeight());
        }
        return scrollpaneContent;
    }

    private JScrollPane getScrollpaneAvaible() {
        if (scrollpaneAvaible == null) {
            scrollpaneAvaible = new JScrollPane();
            scrollpaneAvaible.setViewportView(getList_avaible());
            scrollpaneAvaible.setSize(100, scrollpaneAvaible.getHeight());
        }
        return scrollpaneAvaible;
    }

    protected void delete() {
        int index = table_class.getSelectedRow();
        if (index >= 0 && index < v_class.size() && v_class.size() > 1) {
            GsReg2dynPriorityClass c = (GsReg2dynPriorityClass) v_class.get(index);
            v_class.remove(index);
            if (index < v_class.size()) {
            	// update rank of the next priority classes
            	if ( index == 0 || ((GsReg2dynPriorityClass) v_class.get(index-1)).rank != c.rank) {
            		if (((GsReg2dynPriorityClass) v_class.get(index)).rank != c.rank) {
            			for (int i=index ; i<v_class.size() ; i++) {
            				((GsReg2dynPriorityClass) v_class.get(i)).rank--;
            			}
            		}
            	}
            }
            Iterator it = m_elt.keySet().iterator();
            Object lastClass = v_class.get(v_class.size()-1);
            while (it.hasNext()) {
                Object k = it.next();
                Object cl = m_elt.get(k); 
                if (cl == c) {
                    m_elt.put(k,lastClass);
                } else if (cl instanceof Object[]) {
                    Object[] t = (Object[])cl;
                    for (int i=0 ; i<t.length ; i++) {
                        if (t[i] == c) {
                            t[i] = lastClass;
                        }
                    }
                }
            }

            classTableModel.fireTableRowsDeleted(index, index);
            table_class.getSelectionModel().clearSelection();
            if (index == v_class.size()) {
            	index--;
            }
            table_class.getSelectionModel().addSelectionInterval(index, index);
        }
    }
    
    protected void create() {
        int pos = table_class.getSelectedRow();
        if (pos < 0 || pos >= v_class.size()) {
            pos = 0;
        }
        int priority = ((GsReg2dynPriorityClass)v_class.get(pos)).rank;
        for ( ; pos < v_class.size() ; pos++) {
            if (((GsReg2dynPriorityClass)v_class.get(pos)).rank != priority) {
                break;
            }
        }
        v_class.add(pos++, new GsReg2dynPriorityClass(priority+1));
        for (int i=pos ; i<v_class.size() ; i++) {
            ((GsReg2dynPriorityClass)v_class.get(i)).rank++;
        }
        classTableModel.fireTableRowsInserted(pos, pos);
    }

    /**
     * when moving a selection of class, they must move with other class of the same priority.
     * this checks the selection and compute a list of all really moving rows as ranges: start-stop for each selected clas
     * @param key
     * @param index 
     * @return moving ranges or null if nothing should move
     */
    private int[][] getMovingRows(int key, int[] index) {
        if (index == null) {
        	return null;
        }
        int end = v_class.size();
        int count = 0;
        int lastPriority = -1;
        for (int i=0 ; i<index.length ; i++) {
            int priority = ((GsReg2dynPriorityClass)v_class.get(index[i])).rank;
            if (priority != lastPriority) {
                int start = index[i]-1;
                int stop = index[i]+1;
                while(start >= 0 && ((GsReg2dynPriorityClass)v_class.get(start)).rank == priority) {
                    start--;
                }
                while(stop < end && ((GsReg2dynPriorityClass)v_class.get(stop)).rank == priority) {
                    stop++;
                }
                start++;
                stop--;
                // if moving up and already on top or moving down and already on bottom: don't do anything
                if ((key==UP && start == 0) || (key==DOWN && stop == end-1)) {
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
            int priority = ((GsReg2dynPriorityClass)v_class.get(index[i])).rank;
            if (priority != lastPriority) {
                int start = index[i]-1;
                int stop = index[i]+1;
                while(start >= 0 && ((GsReg2dynPriorityClass)v_class.get(start)).rank == priority) {
                    start--;
                }
                while(stop < end && ((GsReg2dynPriorityClass)v_class.get(stop)).rank == priority) {
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
    
    /**
     * move the whole selection up.
     * if some selected class are part of a group, the whole group will move with it.
     */
    protected void up() {
        int[] ts = table_class.getSelectedRows();
        int[][] index = getMovingRows(UP, ts);
        if (index == null) {
            return;
        }
        
        int reselect = 0;
        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)table_class.getSelectionModel();
        selectionModel.clearSelection();
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = start-1;
            int pr = ((GsReg2dynPriorityClass)v_class.get(start)).rank;
            int prTarget = ((GsReg2dynPriorityClass)v_class.get(target)).rank;
            target--;
            while (target >= 0 && ((GsReg2dynPriorityClass)v_class.get(target)).rank == prTarget) {
                target--;
            }
            target++;
            for (int j=target ; j<start ; j++) {
                ((GsReg2dynPriorityClass)v_class.get(j)).rank = pr;
            }
            for (int j=0 ; j<=stop-start ; j++) {
                ((GsReg2dynPriorityClass)v_class.get(start+j)).rank = prTarget;
                classTableModel.moveElementAt(start+j, target+j);
                if (reselect < ts.length && ts[reselect] == start+j) {
                    	reselect++;
                    	selectionModel.addSelectionInterval(target+j, target+j);
                }
            }
        }
    }

    /**
     * move the whole selection down
     * if some selected class are part of a group, the whole group will move with it.
     */
    protected void down() {
        int[] ts = table_class.getSelectedRows();
        int[][] index = getMovingRows(DOWN, ts);
        if (index == null) {
            return;
        }
        
        int reselect = 0;
        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)table_class.getSelectionModel();
        selectionModel.clearSelection();
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = stop+1;
            int pr = ((GsReg2dynPriorityClass)v_class.get(start)).rank;
            int prTarget = ((GsReg2dynPriorityClass)v_class.get(target)).rank;
            target++;
            
            while (target > v_class.size() && ((GsReg2dynPriorityClass)v_class.get(target)).rank == prTarget) {
                target++;
            }
            target--;
            for (int j=stop+1 ; j<=target ; j++) {
                ((GsReg2dynPriorityClass)v_class.get(j)).rank = pr;
            }
            for (int j=0 ; j<=stop-start ; j++) {
                ((GsReg2dynPriorityClass)v_class.get(start)).rank = prTarget;
                classTableModel.moveElementAt(start, target);
                if (reselect < ts.length && ts[reselect] == start+j) {
                    	reselect++;
                    	selectionModel.addSelectionInterval(target, target);
                }
            }
        }
    }
    /**
     * toggle the selection grouping:
     *    - if all selected items are part of the same group, it will be "ungrouped"
     *    - if selected items are part of several groups, they will be merged with the first one
     */
    protected void groupToggle() {
        int[] ts = table_class.getSelectedRows();
        int[][] selExtended = getMovingRows(NONE, ts);
        // if class with different priorities are selected: give them all the same priority
        if (selExtended.length < 1) {
        	return;
        }
        if (selExtended.length > 1) {
            int[] index = table_class.getSelectedRows();
            if (index == null || index.length < 1) {
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
                    classTableModel.moveElementAt(j, pos);
            	}
            }
            int l = selExtended.length - 1;
            for (int j=selExtended[l][1]+1 ; j<v_class.size() ; j++) {
            	((GsReg2dynPriorityClass)v_class.get(j)).rank -= l;
            }
            classTableModel.fireTableRowsUpdated(selExtended[1][0]+1, selExtended[selExtended.length-1][1]);
            table_class.getSelectionModel().clearSelection();
            table_class.getSelectionModel().addSelectionInterval(selExtended[0][0],pos);
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
                classTableModel.fireTableRowsUpdated(selExtended[0][0], v_class.size()-1);
            }
        }
    }
    
    /**
     * add genes to the selected class: they will be removed from their current class.
     */
    protected void insert() {
        int[] t = list_avaible.getSelectedIndices();
        for (int i=0 ; i<t.length ; i++) {
            Object k = v_avaible.get(t[i]);
            if (v_avaible_comment.size() == v_avaible.size()) { // +1 and -1 are separated, don't move everything
                Object[] tk = (Object[])m_elt.get(k);
                if (v_avaible_comment.get(t[i]) == S_PLUS) {
                    tk[0] = currentClass;
                } else {
                    tk[1] = currentClass;
                }
            } else { // simple case
                m_elt.put(k, currentClass);
            }
        }
        classSelectionChanged();
    }
    
    /**
     * remove genes from the selected class: they will go back to the default one.
     */
    protected void remove() {
        int[] t = list_content.getSelectedIndices();
        for (int i=0 ; i<t.length ; i++) {
            Object k = v_content.get(t[i]);
            Object lastClass = v_class.get(v_class.size()-1);
            if (v_content_comment.size() == v_content.size()) { // +1 and -1 are separated, don't move everything
                Object[] tk = (Object[])m_elt.get(k);
                if (v_content_comment.get(t[i]) == S_PLUS) {
                    tk[0] = lastClass;
                } else {
                    tk[1] = lastClass;
                }
            } else { // simple case
                m_elt.put(k, lastClass);
            }
        }
        classSelectionChanged();
    }

    /**
     * call it when the user changes the class selection: update UI to match the selection
     */
    protected void classSelectionChanged() {
        v_content.clear();
        v_content_comment.clear();
        v_avaible.clear();
        v_avaible_comment.clear();
        
        int[] ts = table_class.getSelectedRows();
        int[][] selExtended = getMovingRows(NONE, ts);
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
        
        int[] ti = table_class.getSelectedRows();
        if (ti.length != 1) {
            but_remove.setEnabled(false);
            but_insert.setEnabled(false);
            but_delete.setEnabled(false);
            contentModel.fireAllChanged();
            avaibleModel.fireAllChanged();
            return;
        }
        but_remove.setEnabled(true);
        but_insert.setEnabled(true);
        
        int i = ti[0];
        if (i>=0 && i<v_class.size()) {
            currentClass = (GsReg2dynPriorityClass)v_class.get(i);
        } else {
            table_class.getSelectionModel().setSelectionInterval(0, 0);
            return;
        }
        if (v_class.size() < 2) {
            but_delete.setEnabled(false);
            but_remove.setEnabled(false);
        } else {
            but_delete.setEnabled(true);
            but_remove.setEnabled(true);
        }
        
        Iterator it = v_nodeOrder.iterator();
        while (it.hasNext()) {
            Object k = it.next();
            Object target = m_elt.get(k);
            if (target instanceof Object[]) {
                Object[] t = (Object[])target;
                if (t[0] == currentClass) {
                    if (t[1] == currentClass) {
                        v_content.add(k);
                        v_content_comment.add(S_PLUS);
                        v_content.add(k);
                        v_content_comment.add(S_MINUS);
                    } else {
                        v_content.add(k);
                        v_content_comment.add(S_PLUS);
                        v_avaible.add(k);
                        v_avaible_comment.add(S_MINUS);
                    }
                } else if (t[1] == currentClass) {
                    v_avaible.add(k);
                    v_avaible_comment.add(S_PLUS);
                    v_content.add(k);
                    v_content_comment.add(S_MINUS);
                } else {
                    v_avaible.add(k);
                    v_avaible_comment.add(S_PLUS);
                    v_avaible.add(k);
                    v_avaible_comment.add(S_MINUS);
                }
            } else {
                if (m_elt.get(k) == currentClass) {
                    v_content.add(k);
                } else {
                    v_avaible.add(k);
                }
            }
        }
        contentModel.fireAllChanged();
        avaibleModel.fireAllChanged();
        
        // reset selection in content and avaible lists
        list_content.setSelectedIndices(new int[0]);
        list_avaible.setSelectedIndices(new int[0]);
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
        int size = v_class.size();
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
        classTableModel.fireTableRowsDeleted(0, size);
        classTableModel.fireTableRowsInserted(0, v_class.size()-1);
        table_class.getSelectionModel().setSelectionInterval(0, 0);
    }
}

class ClassListModel extends DefaultListModel {
    private static final long serialVersionUID = 7741212000452988183L;
    
    private Vector v;
    private Vector v_comment;
    
    protected ClassListModel(Vector v, Vector v_comment) {
        this.v = v;
        this.v_comment = v_comment;
    }
    
    public int getSize() {
        return v.size();
    }

    public Object getElementAt(int index) {
        String s;
        if (index >= 0 && index < getSize()) {
            if (v_comment.size() == v.size()) {
                s = ""+v_comment.get(index);
            } else {
                s = "";
            }
            return v.get(index) + s;
        }
        return null;
    }

    protected void fireAllChanged() {
        fireContentsChanged(this, 0, getSize());
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