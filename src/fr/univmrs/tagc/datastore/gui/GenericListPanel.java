package fr.univmrs.tagc.datastore.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import fr.univmrs.tagc.datastore.*;
import fr.univmrs.tagc.widgets.EnhancedJTable;
import fr.univmrs.tagc.widgets.SplitPane;
import fr.univmrs.tagc.widgets.StatusTextField;
import fr.univmrs.tagc.widgets.StockButton;

/**
 * Generic UI to display the content of a list.
 * It offers optional UI to reorder and alter the content of the list,
 * using the GenericList interface as a backend.
 */
public class GenericListPanel extends JPanel 
	implements KeyListener, ObjectPropertyEditorUI, ListSelectionListener {
    private static final long serialVersionUID = -4236977685092639157L;
    
    JScrollPane sp = new JScrollPane();
    GenericList list;
    listModel model = new listModel();
    EnhancedJTable jl = new EnhancedJTable(model);
    boolean rendererInstalled = false;
    int autohide = -1;
    
    JButton b_up;
    JButton b_down;
    JButton b_add;
    JButton b_del;
    StatusTextField t_filter;
    JLabel l_title = new JLabel();
    
    protected JPanel p_right;
    protected JPanel targetpanel;
    protected CardLayout cards;

	// TODO: fix ugly hack for the size of the action column
    int bsize = 25;
    
	private GenericPropertyInfo	pinfo;
    
    public GenericListPanel() {
    	this(null, null);
    }
    public GenericListPanel(Map m_right, String name) {
    	jl.addActionListener(model);
    	jl.getSelectionModel().addListSelectionListener(this);
    	targetpanel = this;
    	if (m_right != null) {
    		setLayout(new GridLayout());
    		JSplitPane split = new SplitPane();
    		targetpanel = new JPanel();
    		p_right = new JPanel();
    		cards = new CardLayout();
    		p_right.setLayout(cards);
    		p_right.add(new JPanel(), "empty");
    		Iterator it = m_right.entrySet().iterator();
    		while (it.hasNext()) {
    			Entry e = (Entry)it.next();
    			p_right.add((Component)e.getValue(), e.getKey().toString());
    		}
    		cards.show(p_right, "empty");
    		split.setRightComponent(p_right);
    		split.setLeftComponent(targetpanel);
    		this.add(split);
    		split.setName(name+".split");
    	}
        targetpanel .setLayout(new GridBagLayout());
        sp.setViewportView(jl);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(3,5,4, 5);
        targetpanel.add(l_title, c);
		l_title.setVisible(false);
		
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        t_filter = new StatusTextField("filter", true);
        t_filter.addKeyListener(this);
        targetpanel.add(t_filter, c);
		
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.gridheight = 6;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        targetpanel.add(sp, c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 4;
        b_del = new StockButton("list-remove.png", true);
        b_del.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doRemove();
            }
        });
        targetpanel.add(b_del, c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 2;
        b_add = new StockButton("list-add.png", true);
        b_add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doAdd();
            }
        });
        targetpanel.add(b_add, c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 5;
        b_up = new StockButton("go-up.png", true);
        b_up.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doMoveUp();
            }
        });
        targetpanel.add(b_up, c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 6;
        b_down = new StockButton("go-down.png", true);
        b_down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doMoveDown();
            }
        });
        targetpanel.add(b_down, c);
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
    
    public void setTitle(String title) {
    	if (title == null) {
    		l_title.setVisible(false);
    		return;
    	}
		l_title.setVisible(true);
    	l_title.setText(title);
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
    public void setList(GenericList list) {
        this.list = list;
        if (list == null) {
            b_up.setVisible(false);
            b_down.setVisible(false);
            b_add.setVisible(false);
            b_del.setVisible(false);
            t_filter.setVisible(false);
            jl.setEnabled(false);
            return;
        }
        model.setList(list, this);
        jl.setEnabled(true);
        b_up.setVisible(list.canOrder());
        b_down.setVisible(list.canOrder());
        b_add.setVisible(list.canAdd() && !list.doInlineAddRemove());
        b_del.setVisible(list.canRemove() && !list.doInlineAddRemove());
        if (list.getNbElements(null) > 0) {
            jl.getSelectionModel().setSelectionInterval(0, 0);
        }
        for (int i=0 ; i<list.nbAction ; i++) {
        	jl.getColumnModel().getColumn(i).setMaxWidth(bsize);
        }
    }
    
	protected void doMoveUp() {
        if (list == null) {
            return;
        }
        int[] index = jl.getSelectedRows();
        if (!list.move(index, -1)) {
        	return;
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
        if (!list.move(index, 1)) {
        	return;
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
        Point b = b_add.getLocationOnScreen();
        int n = list.add(jl.getSelectedRow(), b.x, b.y);
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
        if (sel.length > 0 && list.remove(null, sel)) {
            int i = sel[0];
            i = i>=list.getNbElements(null) ? list.getNbElements(null)-1 : i;
            refresh();
            jl.getSelectionModel().setSelectionInterval(i,i);
        }
    }

    /**
     * @return the current selection
     */
    public int[] getSelection() {
    	String filter = t_filter.getText();
    	if (filter == null || filter == "") {
    		return jl.getSelectedRows();
    	}
    	int[] t = jl.getSelectedRows();
    	int[] ret = new int[t.length];
    	for (int i=0 ; i<t.length ; i++) {
    		ret[i] = list.getRealIndex(filter, t[i]);
    	}
    	return ret;
    }

    /**
     * force a refresh of the list
     */
    public void refresh() {
        model.fireTableDataChanged();
        refreshHide();
    }
    
    private void refreshHide() {
        if (list.getNbElements(null) <= autohide) {
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
	public void keyTyped(KeyEvent e) {
	}
	public void keyReleased(KeyEvent e) {
		model.setFilter(t_filter.getText());
		if (t_filter.getText().length() > 0) {
			b_down.setEnabled(false);
			b_up.setEnabled(false);
		} else {
			b_down.setEnabled(true);
			b_up.setEnabled(true);
		}
	}
	public void keyPressed(KeyEvent e) {
	}

	public void apply() {
	}

	public void refresh(boolean force) {
		if (force) {
			setList((GenericList)pinfo.getRawValue());
		}
	}

	public void setEditedProperty(GenericPropertyInfo pinfo,
			GenericPropertyHolder panel) {
		this.pinfo = pinfo;
		panel.addField(this, pinfo, 0);
	}

	public void installEditor() {
		if (!rendererInstalled) {
			Map m = list.getCellEditor();
			rendererInstalled = true;
			if (m == null) {
				return;
			}
	        // FIXME: put row height at the right place...
	        jl.setRowHeight(35);
			Iterator it = m.entrySet().iterator();
			while (it.hasNext()) {
				Entry e = (Entry)it.next();
				jl.addCellEditor((Class)e.getKey(), (TableCellEditor)e.getValue());
			}
		}
	}

	public ListSelectionModel getSelectionModel() {
		return jl.getSelectionModel();
	}
	public Object getSelectedItem() {
		int[] t_sel = getSelection();
		if (t_sel.length != 1) {
			return null;
		}
		return list.getElement(null, t_sel[0]);
	}
	public void valueChanged(ListSelectionEvent e) {
    	if (pinfo != null) {
    		pinfo.setValue(jl.getSelectedRow());
    	}
    	if (cards != null) {
	    	int[] t_sel = getSelection();
	    	if (t_sel.length == 1) {
	    		cards.show(p_right, getSelectedItem().getClass().toString());
	    	} else {
	    		cards.show(p_right, "empty");
	    	}
    	}
	}
}

class listModel extends AbstractTableModel implements GenericListListener, TableActionListener {
    private static final long serialVersionUID = 886643323547667463L;
    
    private GenericList list;
    private GenericListPanel panel;
    private int lastLineInc = 0;
    private Map m_button = new HashMap();
    private Class[] t_type;
    
    private String filter = null;

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return list.canEdit() && columnIndex >= list.nbAction;
    }
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        list.edit(filter, rowIndex, columnIndex, aValue);
    }

    public Object getValueAt(int row, int col) {
        if (list == null) {
            return null;
        }
        if (lastLineInc != 0 && row == list.getNbElements(null)) {
        	return "";
        }
        if (col < list.nbAction) {
        	Object oa = list.getAction(filter, row, col);
        	if (oa == null || "".equals(oa)) {
        		return "";
        	}
        	JButton b = (JButton)m_button.get(oa);
        	if (b == null) {
        		if (oa instanceof String) {
        			b = new JButton((String)oa);
        		} else if (oa instanceof Icon) {
        			b = new JButton((Icon)oa);
        		} else {
        			return "";
        		}
        		b.setBorder(BorderFactory.createEmptyBorder());
        		m_button.put(oa, b);
        	}
        	return b;
        }
        Object o = list.getElement(filter, row);
        if (list.mcolHelper != null) {
        	return list.mcolHelper.getVal(o, col-list.nbAction);
        }
        if (o instanceof MultiColObject) {
    		return ((MultiColObject)o).getVal(col-list.nbAction);
        }
        return o;
    }
    public Class getColumnClass(int columnIndex) {
    	if (columnIndex < list.nbAction) {
    		return JButton.class;
    	}
    	if (t_type != null) {
    		panel.installEditor();
    		return t_type[columnIndex-list.nbAction];
    	}
		return super.getColumnClass(columnIndex);
	}
	public int getRowCount() {
        if (list == null) {
            return 0;
        }
        return list.getNbElements(filter)+lastLineInc;
    }
    public int getColumnCount() {
    	if (list == null) {
    		return 0;
    	}
        return list.getNbCol();
    }
    
    public String getColumnName(int column) {
    	if (list != null && column >= list.nbAction) {
    		if (list.nbcol == 1) {
    			return list.getTitle();
    		}
    		return list.getColName(column-list.nbAction);
    	}
        return "";
    }
    public void setFilter(String filter) {
    	if (list == null) {
    		return;
    	}
    	if (filter != null && filter.length() == 0) {
    		this.filter = null;
    	} else {
    		this.filter = filter;
    	}
    	fireTableDataChanged();
    }
    void setList(GenericList list, GenericListPanel panel) {
        this.list = list;
        this.panel = panel;
        this.t_type = list.getObjectType();
        list.addListListener(this);
        if (list.doInlineAddRemove()) {
        	lastLineInc = 1;
        } else {
        	lastLineInc = 0;
        }
        structureChanged();
    }
    void firechange(int min, int max) {
        fireTableRowsUpdated(min, max);
    }
	public void contentChanged() {
		fireTableDataChanged();
	}
	public void itemAdded(Object item, int pos) {
		fireTableRowsInserted(pos, pos);
	}
	public void structureChanged() {
		fireTableStructureChanged();
	}
	public void itemRemoved(Object item, int pos) {
		fireTableRowsDeleted(pos, pos);
	}
	public void actionPerformed(int row, int col) {
		list.run(filter, row, col);
	}
}
