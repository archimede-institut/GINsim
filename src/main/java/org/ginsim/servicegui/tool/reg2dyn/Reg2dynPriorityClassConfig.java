package org.ginsim.servicegui.tool.reg2dyn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.utils.data.*;
import org.ginsim.gui.utils.widgets.StockButton;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.Reg2dynPriorityClass;


/**
 * configure priority classes.
  */
public class Reg2dynPriorityClassConfig extends GenericListPanel implements ListPanelCompanion, ListSelectionListener {

    private static final long serialVersionUID = -3214357334096594239L;

    private static final ListPanelHelper LISTING_HELPER = new ListingHelper();

    private static final int UP = PriorityClassDefinition.UP;
    private static final int DOWN = PriorityClassDefinition.DOWN;
    private static final int NONE = PriorityClassDefinition.NONE;
    
    protected static final String[] t_typeName = {" [+]", " [-]", ""};
    
    private JButton but_insert;
    private JButton but_remove;
    
    private List<RegulatoryNode> nodeOrder;
    ListPanel contentPanel;
    ListPanel availablePanel;

    private List<PriorityMember> l_content = new ArrayList<PriorityMember>();
    private List<PriorityMember> l_avaible = new ArrayList<PriorityMember>();
    
    private Reg2dynPriorityClass currentClass;

    PriorityClassDefinition pcdef;
    
    private StockButton but_group;
	private ListEditionPanel<PriorityClassDefinition, PriorityClassManager> parentPanel;
    
    /**
     * @param nodeOrder
     */
    public Reg2dynPriorityClassConfig(ListEditionPanel editPanel, List<RegulatoryNode> nodeOrder) {
    	super(new HashMap<Class<?>, Component>(), "pclassConfig");
        this.parentPanel = editPanel;
        this.nodeOrder = nodeOrder;
        initialize();

        if (parentPanel != null) {
            parentPanel.addPanel(this, "PCLASS");
            parentPanel.showPanel("PCLASS");
        }
    }
    
    private void initialize() {
        JPanel p_edit = new JPanel();
        p_edit.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 1;
        p_edit.add(getBut_insert(), c);
        c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 2;
        p_edit.add(getBut_remove(), c);


        GridBagConstraints c_scroll_in = new GridBagConstraints();
        GridBagConstraints c_scroll_av = new GridBagConstraints();
        GridBagConstraints c_availableLabel = new GridBagConstraints();
        GridBagConstraints c_contentLabel = new GridBagConstraints();

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
        

        p_edit.add(new JLabel(Translator.getString("STR_otherClassContent")), c_availableLabel);
        p_edit.add(new JLabel(Translator.getString("STR_classContent")), c_contentLabel);
        p_edit.add(getContentPanel(), c_scroll_in);
        p_edit.add(getAvaiblePanel(), c_scroll_av);
        
        p_right.add(p_edit, Reg2dynPriorityClass.class.toString());

        // customize the list panel
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 7;
        c.anchor = GridBagConstraints.NORTH;
        targetpanel.add(getBut_group(), c);
    }
    
    protected ListPanel getContentPanel() {
    	if (contentPanel == null) {
    		contentPanel = new ListPanel(LISTING_HELPER, "");
    		contentPanel.setList(l_content);
    	}
    	return contentPanel;
    }
    protected ListPanel getAvaiblePanel() {
    	if (availablePanel == null) {
    		availablePanel = new ListPanel(LISTING_HELPER, "");
    		availablePanel.setList(l_avaible);
    	}
    	return availablePanel;
    }
    
    private JButton getBut_insert() {
        if (but_insert == null) {
            but_insert = new StockButton("go-previous.png", true);
            but_insert.setToolTipText(Translator.getString("STR_pclass_insert"));
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
            but_remove.setToolTipText(Translator.getString("STR_pclass_remove"));
            but_remove.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    remove();
                }
            });
        }
        return but_remove;
    }

    private JButton getBut_group() {
        if (but_group == null) {
            but_group = new StockButton("group.png",true);
            but_group.setMinimumSize(new Dimension(35,25));
            but_group.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    groupToggle();
                }
            });
        }
        return but_group;
    }

    /**
     * toggle the selection grouping:
     *    - if all selected items are part of the same group, it will be "ungrouped"
     *    - if selected items are part of several groups, they will be merged with the first one
     */
    protected void groupToggle() {
        int[] ts = getSelection();
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
            int pr = ((Reg2dynPriorityClass)pcdef.v_data.get(pos)).rank;
            for (int i=1 ; i<selExtended.length ; i++) {
            	for (int j=selExtended[i-1][1]+1 ; j<selExtended[i][0] ; j++) {
            		((Reg2dynPriorityClass)pcdef.v_data.get(j)).rank -= i-1;
            	}
            	for (int j=selExtended[i][0] ; j<=selExtended[i][1] ; j++) {
	                pos++;
	                ((Reg2dynPriorityClass)pcdef.v_data.get(j)).rank = pr;
	                pcdef.moveElementAt(j, pos);
            	}
            }
            int l = selExtended.length - 1;
            for (int j=selExtended[l][1]+1 ; j<pcdef.v_data.size() ; j++) {
            	((Reg2dynPriorityClass)pcdef.v_data.get(j)).rank -= l;
            }
            pcdef.refresh();
            getSelectionModel().clearSelection();
            getSelectionModel().addSelectionInterval(selExtended[0][0],pos);
        } else {
            if (selExtended[0][0] != selExtended[0][1]) {
                int i = selExtended[0][0];
                int inc = 1;
                for (i++ ; i<selExtended[0][1] ; i++) {
                    ((Reg2dynPriorityClass)pcdef.v_data.get(i)).rank += inc;
                    inc++;
                }
                for ( ; i<pcdef.v_data.size() ; i++) {
                    ((Reg2dynPriorityClass)pcdef.v_data.get(i)).rank += inc;
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
            PriorityMember k = (PriorityMember)l_avaible.get(index);
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
            PriorityMember k = (PriorityMember)l_content.get(index);
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
        l_content.clear();
        l_avaible.clear();
        
        int[] ti = getSelection();
        int[][] selExtended = pcdef.getMovingRows(NONE, ti);
        if (selExtended.length > 1) {
            but_group.setEnabled(true);
            but_group.setStockIcon("group.png");
            but_group.setToolTipText(Translator.getString("STR_group_descr"));
        } else {
            if (selExtended.length == 0 || selExtended[0][0] == selExtended[0][1]) {
                but_group.setEnabled(false);
                but_group.setStockIcon("group.png");
                but_group.setToolTipText(Translator.getString("STR_group_select_descr"));
            } else {
                but_group.setEnabled(true);
                but_group.setStockIcon("ungroup.png");
                but_group.setToolTipText(Translator.getString("STR_ungroup_descr"));
            }
        }
        
        if (ti.length != 1) {
            but_remove.setEnabled(false);
            but_insert.setEnabled(false);
            contentPanel.refresh();
            availablePanel.refresh();
            return;
        }
        but_remove.setEnabled(true);
        but_insert.setEnabled(true);
        
        int i = ti[0];
        if (i>=0 && i<pcdef.v_data.size()) {
            currentClass = (Reg2dynPriorityClass)pcdef.v_data.get(i);
        } else {
            getSelectionModel().setSelectionInterval(0, 0);
            return;
        }
        if (pcdef.v_data.size() < 2) {
            but_remove.setEnabled(false);
        } else {
            but_remove.setEnabled(true);
        }
        
        for (RegulatoryNode v: nodeOrder) {
            PriorityMember k = new PriorityMember(v, NONE);
            Object target = pcdef.m_elt.get(v);
            if (target instanceof Object[]) {
                Object[] t = (Object[])target;
                k.type = DOWN;
                PriorityMember kp = new PriorityMember(v, UP);
                if (t[0] == currentClass) {
                    if (t[1] == currentClass) {
                        l_content.add(k);
                        l_content.add(kp);
                    } else {
                        l_content.add(kp);
                        l_avaible.add(k);
                    }
                } else if (t[1] == currentClass) {
                    l_avaible.add(kp);
                    l_content.add(k);
                } else {
                    l_avaible.add(kp);
                    l_avaible.add(k);
                }
            } else {
                if (target == currentClass) {
                    l_content.add(k);
                } else {
                    l_avaible.add(k);
                }
            }
        }
        contentPanel.refresh();
        availablePanel.refresh();
    }
    
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == getSelectionModel()) {
			super.valueChanged(e);
			classSelectionChanged();
		}
	}
	
	public void setEnabled(boolean b) {
		contentPanel.setEnabled(b);
		availablePanel.setEnabled(b);
		but_group.setEnabled(b);
		but_insert.setEnabled(b);
		but_remove.setEnabled(b);
	}

    @Override
    public void setParentList(List list) {
        // nothing to do
    }

    @Override
    public void selectionUpdated(int[] selection) {
        pcdef = parentPanel.getSelectedItem();
        if (pcdef == null) {
            setList(null);
            setEnabled(false);
            return;
        }
        setEnabled(true);
        setList(pcdef);
    }
}

class PriorityMember {
	RegulatoryNode vertex;
	int type;
	
	public PriorityMember(RegulatoryNode vertex, int type) {
		this.vertex = vertex;
		this.type = type;
	}

	public String toString() {
		return vertex+Reg2dynPriorityClassConfig.t_typeName[type];
	}
}

class ListingHelper extends ListPanelHelper {

    public ListingHelper() {
        canOrder = false;
    }
}