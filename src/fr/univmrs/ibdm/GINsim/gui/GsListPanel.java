package fr.univmrs.ibdm.GINsim.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.global.GsEnv;

/**
 * Generic UI to display the content of a list.
 * It offers optionnal UI to reorder and alter the content of the list,
 * using the Glist interface as a backend.
 */
public class GsListPanel extends JPanel {
    private static final long serialVersionUID = -4236977685092639157L;
    
    JScrollPane sp = new JScrollPane();
    GsList list;
    listModel model = new listModel();
    JTable jl = new JTable(model);
    int autohide = -1;
    
    JButton b_up;
    JButton b_down;
    JButton b_add;
    JButton b_del;
    
    /**
     */
    public GsListPanel() {
        sp.setViewportView(jl);
        this .setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 5;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(sp, c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 2;
        b_del = new JButton("X");
        b_del.setForeground(Color.RED);
        b_del.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doRemove();
            }
        });
        this.add(b_del, c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        b_add = new JButton("+");
        b_add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doAdd();
            }
        });
        this.add(b_add, c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 3;
        b_up = new JButton(GsEnv.getIcon("upArrow.gif"));
        b_up.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doMoveUp();
            }
        });
        this.add(b_up, c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 4;
        b_down = new JButton(GsEnv.getIcon("downArrow.gif"));
        b_down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doMoveDown();
            }
        });
        this.add(b_down, c);
    }

    /**
     * set when to autohide most of this widget.
     * (when the list contains <code>autohide</code> elements or less, 
     * only the "add" button will remain visible.
     * default value is -1, so this will never happen, encouraged values are 0 or 1.
     * @param autohide
     */
    public void setAutoHide(int autohide) {
        this.autohide = autohide;
        refreshHide();
    }
    
    /**
     * add a selection listener for this list
     * @param listener
     */
    public void addSelectionListener(ListSelectionListener listener) {
        jl.getSelectionModel().addListSelectionListener(listener);
    }
    
    /**
     * remove a selection listener for this list
     * @param listener
     */
    public void removeSelectionListener(ListSelectionListener listener) {
        jl.getSelectionModel().removeListSelectionListener(listener);
    }
    
    /**
     * set the list to show.
     * @param list
     */
    public void setList(GsList list) {
        this.list = list;
        model.setList(list);
        if (list == null) {
            b_up.setVisible(false);
            b_down.setVisible(false);
            b_add.setVisible(false);
            b_del.setVisible(false);
            return;
        }
        
        b_up.setVisible(list.canOrder());
        b_down.setVisible(list.canOrder());
        b_add.setVisible(list.canAdd());
        b_del.setVisible(list.canRemove());
        if (list.getNbElements() > 0) {
            jl.getSelectionModel().setSelectionInterval(0, 0);
        }
    }
    
    protected void doMoveUp() {
        if (list == null) {
            return;
        }
        int[] index=jl.getSelectedRows();
        for (int i=0;i<index.length;i++) {
            int a = index[i];
            if (a>0) {
                list.moveElement(a, a-1);
                index[i]=a-1;
            } else return;
        }
        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)jl.getSelectionModel();
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
        if (list == null) {
            return;
        }
        int[] index=jl.getSelectedRows();
        for (int i=index.length-1;i>=0;i--) {
            int a = index[i];
            if (a<list.getNbElements()-1) {
                list.moveElement(a, a+1);
                index[i]=a+1;
            } else return;
        }
        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)jl.getSelectionModel();
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
    protected void doAdd() {
        if (list == null) {
            return;
        }
        int n = list.add(jl.getSelectedRow());
        if (n != -1) {
            refresh();
            jl.getSelectionModel().setSelectionInterval(n, n);
        }
    }
    protected void doRemove() {
        if (list == null) {
            return;
        }
        int[] sel = jl.getSelectedRows();
        if (sel.length > 0 && list.remove(sel)) {
            int i = sel[0];
            i = i>=list.getNbElements() ? list.getNbElements()-1 : i;
            refresh();
            jl.getSelectionModel().setSelectionInterval(i,i);
        }
    }

    /**
     * @return the current selection
     */
    public int[] getSelection() {
        return jl.getSelectedRows();
    }

    /**
     * force a refresh of the list
     */
    public void refresh() {
        model.fireTableStructureChanged();
        refreshHide();
    }
    
    private void refreshHide() {
        if (list.getNbElements() <= autohide) {
            if (sp.isVisible()) {
                sp.setVisible(false);
                b_del.setVisible(false);
                b_up.setVisible(false);
                b_down.setVisible(false);
                setSize(30, getHeight());
            }
        } else if (!sp.isVisible()) {
            sp.setVisible(true);
            b_del.setVisible(true);
            b_up.setVisible(true);
            b_down.setVisible(true);
            setSize(100, getHeight());
        }
    }
}

class listModel extends AbstractTableModel {
    private static final long serialVersionUID = 886643323547667463L;
    
    private GsList list;
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0 && list.canEdit();
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        list.edit(rowIndex, aValue);
    }

    public Object getValueAt(int row, int col) {
        if (list == null) {
            return null;
        }
        return list.getElement(row);
    }

    public int getRowCount() {
        if (list == null) {
            return 0;
        }
        return list.getNbElements();
    }
    public int getColumnCount() {
        if (list == null) {
            return 0;
        }
        return 1;
    }
    
    public String getColumnName(int column) {
        return "";
    }

    void setList(GsList list) {
        this.list = list;
        fireTableStructureChanged();
    }
    
    void firechange(int min, int max) {
        fireTableRowsUpdated(min, max);
    }
}
