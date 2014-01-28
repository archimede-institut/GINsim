package org.ginsim.servicegui.tool.reg2dyn.priorityclass;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.utils.data.ListPanel;
import org.ginsim.gui.utils.data.ListPanelCompanion;
import org.ginsim.gui.utils.data.ListPanelHelper;
import org.ginsim.gui.utils.widgets.StockButton;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClass;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;

import java.util.List;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A panel to edit the content of a PriorityClass
 *
 * @author Aurelien Naldi
 */
public class PriorityClassContentEditor extends JPanel implements ListPanelCompanion {

    private static final ListPanelHelper LISTING_HELPER = new ListingHelper();

    protected static final String[] t_typeName = {" [+]", " [-]", ""};

    private static final int UP = PrioritySetDefinition.UP;
    private static final int DOWN = PrioritySetDefinition.DOWN;
    private static final int NONE = PrioritySetDefinition.NONE;


    private final JButton but_insert;
    private final JButton but_remove;

    private final ListPanel contentPanel;
    private final ListPanel availablePanel;

    private final PriorityDefinitionPanel parentPanel;

    private final List<PriorityMember> l_content = new ArrayList<PriorityMember>();
    private final List<PriorityMember> l_available = new ArrayList<PriorityMember>();


    private PrioritySetDefinition pcdef;
    private PriorityClass currentClass;


    public PriorityClassContentEditor(PriorityDefinitionPanel parentPanel) {
        super(new GridBagLayout());
        this.parentPanel = parentPanel;

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 1;
        but_insert = new StockButton("go-previous.png", true);
        but_insert.setToolTipText(Translator.getString("STR_pclass_insert"));
        but_insert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insert();
            }
        });
        add(but_insert, c);
        c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 2;
        but_remove = new StockButton("go-next.png", true);
        but_remove.setToolTipText(Translator.getString("STR_pclass_remove"));
        but_remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                remove();
            }
        });
        add(but_remove, c);


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


        add(new JLabel(Translator.getString("STR_otherClassContent")), c_availableLabel);
        add(new JLabel(Translator.getString("STR_classContent")), c_contentLabel);
        contentPanel = new ListPanel(LISTING_HELPER, "");
        contentPanel.setList(l_content);
        add(contentPanel, c_scroll_in);
        availablePanel = new ListPanel(LISTING_HELPER, "");
        availablePanel.setList(l_available);
        add(availablePanel, c_scroll_av);

        if (parentPanel != null) {
            parentPanel.addPanel(this, "CONTENT");
            parentPanel.showPanel("CONTENT");
        }
    }

    /**
     * add genes to the selected class: they will be removed from their current class.
     */
    protected void insert() {
        int[] t = availablePanel.getSelection();
        for (int i=0 ; i<t.length ; i++) {
            int index = t[i];
            PriorityMember k = l_available.get(index);
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
        refresh();
    }

    /**
     * remove genes from the selected class: they will go back to the default one.
     */
    protected void remove() {
        int[] t = contentPanel.getSelection();
        for (int i=0 ; i<t.length ; i++) {
            int index = t[i];
            PriorityMember k = l_content.get(index);
            Object lastClass = pcdef.get(pcdef.size()-1);
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
        refresh();
    }

    @Override
    public void setParentList(List list) {
        // TODO: implement setParent
    }

    @Override
    public void selectionUpdated(int[] ti) {
        pcdef = parentPanel.getCurrentDefinition();
        currentClass = null;

        if (pcdef == null || ti.length != 1) {
            but_remove.setEnabled(false);
            but_insert.setEnabled(false);
        } else {
            int i = ti[0];
            if (i>=0 && i<pcdef.size()) {
                currentClass = pcdef.get(i);
            }
        }
        refresh();
    }

    private void refresh() {

        l_content.clear();
        l_available.clear();

        if (currentClass == null) {
            but_remove.setEnabled(false);
            but_insert.setEnabled(false);
            return;
        }

        if (pcdef.size() < 2) {
            but_remove.setEnabled(false);
            but_insert.setEnabled(false);
        } else {
            but_remove.setEnabled(true);
            but_insert.setEnabled(true);
        }

        for (RegulatoryNode v: parentPanel.getNodeOrder()) {
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
                        l_available.add(k);
                    }
                } else if (t[1] == currentClass) {
                    l_available.add(kp);
                    l_content.add(k);
                } else {
                    l_available.add(kp);
                    l_available.add(k);
                }
            } else {
                if (target == currentClass) {
                    l_content.add(k);
                } else {
                    l_available.add(k);
                }
            }
        }
        contentPanel.refresh();
        availablePanel.refresh();
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
        return vertex+PriorityClassContentEditor.t_typeName[type];
    }
}

class ListingHelper extends ListPanelHelper {

    public ListingHelper() {
        canOrder = false;
    }
}
