package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.datastore.SimpleGenericList;
import fr.univmrs.tagc.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.widgets.StockButton;

/**
 * configure priority classes.
  */
public class GsReg2dynPriorityClassConfig extends JPanel implements ListSelectionListener {

    private static final long serialVersionUID = -3214357334096594239L;

    protected static final int UP = 0;
    protected static final int DOWN = 1;
    protected static final int NONE = 2;
    
    protected static final String[] t_typeName = {" [+]", " [-]", ""};
    
    private JComboBox cb_auto;

    private JButton but_insert;
    private JButton but_remove;
    
    private Vector v_nodeOrder;
    GenericListPanel contentPanel;
    GenericListPanel availablePanel;
    SimpleGenericList contentList = new SimpleGenericList();
    SimpleGenericList availableList = new SimpleGenericList();
    
    private Vector v_content = new Vector();
    private Vector v_avaible = new Vector();
    
    private GsReg2dynPriorityClass currentClass;

    PriorityClassDefinition pcdef;
    
    GenericListPanel listPanel = null;

    private JToggleButton but_group;
	private GenericListPanel pcpanel;
    
    private static final int AUTO_MANY = 1;
    private static final int AUTO_PLUS_MINUS = 2;
    private static final String[] t_sauto = { "One unique class", "One class for each node", "Splitting transitions – one unique class" };
    
    /**
     * @param frame
     * @param nodeOrder
     * @param param
     */
    public GsReg2dynPriorityClassConfig(Vector nodeOrder) {
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
            but_insert = new StockButton("go-previous.png", true);
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
            but_remove = new StockButton("go-next.png", true);
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
    		listPanel.setList(pcdef);
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
        int[][] selExtended = pcdef.getMovingRows(NONE, ts);
        // if class with different priorities are selected: give them all the same priority
        if (selExtended.length < 1) {
        	return;
        }
        if (selExtended.length > 1) {
            if (ts == null || ts.length < 1) {
                return;
            }
            int pos = selExtended[0][1];
            int pr = ((GsReg2dynPriorityClass)pcdef.v_data.get(pos)).rank;
            for (int i=1 ; i<selExtended.length ; i++) {
            	for (int j=selExtended[i-1][1]+1 ; j<selExtended[i][0] ; j++) {
            		((GsReg2dynPriorityClass)pcdef.v_data.get(j)).rank -= i-1;
            	}
            	for (int j=selExtended[i][0] ; j<=selExtended[i][1] ; j++) {
	                pos++;
	                ((GsReg2dynPriorityClass)pcdef.v_data.get(j)).rank = pr;
	                pcdef.moveElementAt(j, pos);
            	}
            }
            int l = selExtended.length - 1;
            for (int j=selExtended[l][1]+1 ; j<pcdef.v_data.size() ; j++) {
            	((GsReg2dynPriorityClass)pcdef.v_data.get(j)).rank -= l;
            }
            pcdef.refresh();
            listPanel.getSelectionModel().clearSelection();
            listPanel.getSelectionModel().addSelectionInterval(selExtended[0][0],pos);
        } else {
            if (selExtended[0][0] != selExtended[0][1]) {
                int i = selExtended[0][0];
                int inc = 1;
                for (i++ ; i<selExtended[0][1] ; i++) {
                    ((GsReg2dynPriorityClass)pcdef.v_data.get(i)).rank += inc;
                    inc++;
                }
                for ( ; i<pcdef.v_data.size() ; i++) {
                    ((GsReg2dynPriorityClass)pcdef.v_data.get(i)).rank += inc;
                }
                pcdef.refresh();
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
                Object[] tk = (Object[])pcdef.m_elt.get(k.vertex);
                if (k.type == UP) {
                    tk[0] = currentClass;
                } else {
                    tk[1] = currentClass;
                }
            } else { // simple case
            	pcdef.m_elt.put(k.vertex, currentClass);
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
            Object lastClass = pcdef.v_data.get(pcdef.v_data.size()-1);
            if (k.type != NONE) { // +1 and -1 are separated, don't move everything
                Object[] tk = (Object[])pcdef.m_elt.get(k.vertex);
                if (k.type == UP) {
                    tk[0] = lastClass;
                } else {
                    tk[1] = lastClass;
                }
            } else { // simple case
            	pcdef.m_elt.put(k.vertex, lastClass);
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
        int[][] selExtended = pcdef.getMovingRows(NONE, ti);
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
        if (i>=0 && i<pcdef.v_data.size()) {
            currentClass = (GsReg2dynPriorityClass)pcdef.v_data.get(i);
        } else {
            listPanel.getSelectionModel().setSelectionInterval(0, 0);
            return;
        }
        if (pcdef.v_data.size() < 2) {
            but_remove.setEnabled(false);
        } else {
            but_remove.setEnabled(true);
        }
        
        Iterator it = v_nodeOrder.iterator();
        while (it.hasNext()) {
        	GsRegulatoryVertex v = (GsRegulatoryVertex)it.next();
            PriorityMember k = new PriorityMember(v, NONE);
            Object target = pcdef.m_elt.get(v);
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
        while (pcdef.v_data.size() > 1) {
        	pcdef.v_data.remove(1);
        }
        Object lastClass = pcdef.v_data.get(0);
        ((GsReg2dynPriorityClass)lastClass).setName("new class");
        for (int i=0 ; i<v_nodeOrder.size() ; i++) {
        	pcdef.m_elt.put(v_nodeOrder.get(i), lastClass);
        }
        switch (cb_auto.getSelectedIndex()) {
            case AUTO_MANY:
                // should be equivalent to the old priority system: add one class per node
            	pcdef.v_data.clear();
            	pcdef.m_elt.clear();
                for (int i=0 ; i<v_nodeOrder.size() ; i++) {
                    currentClass = new GsReg2dynPriorityClass();
                    pcdef.v_data.add(i, currentClass);
                    pcdef.m_elt.put(v_nodeOrder.get(i), currentClass);
                    currentClass.setName(""+v_nodeOrder.get(i));
                }
                break;
            case AUTO_PLUS_MINUS:
                for (int i=0 ; i<v_nodeOrder.size() ; i++) {
                    Object[] t = {lastClass, lastClass};
                    pcdef.m_elt.put(v_nodeOrder.get(i), t);
                }
                break;
        }
        pcdef.refresh();
        listPanel.getSelectionModel().setSelectionInterval(0, 0);
    }

	public void setClassPanel(GenericListPanel pcpanel) {
		this.pcpanel = pcpanel;
		pcpanel.addSelectionListener(this);
	}

	public void valueChanged(ListSelectionEvent e) {
		pcdef = (PriorityClassDefinition)pcpanel.getSelectedItem();
		if (pcdef == null) {
			listPanel.setList(null);
			setEnabled(false);
			return;
		}
		setEnabled(true);
		listPanel.setList(pcdef);
	}
	
	public void setEnabled(boolean b) {
		contentPanel.setEnabled(b);
		availablePanel.setEnabled(b);
		but_group.setEnabled(b);
		but_insert.setEnabled(b);
		but_remove.setEnabled(b);
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

class ClassList extends SimpleGenericList {
	protected ClassList (Vector v) {
		setData(v);
		canEdit = true;
		canAdd = true;
		canRemove = true;
		canOrder = true;
	}
}