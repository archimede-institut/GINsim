package fr.univmrs.tagc.common.datastore.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import fr.univmrs.tagc.common.datastore.GenericListListener;
import fr.univmrs.tagc.common.datastore.GenericNamedList;
import fr.univmrs.tagc.common.datastore.GenericNamedListCapabilities;
import fr.univmrs.tagc.common.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.common.datastore.MultiColObject;
import fr.univmrs.tagc.common.datastore.ObjectPropertyEditorUI;
import fr.univmrs.tagc.common.widgets.ButtonPopup;
import fr.univmrs.tagc.common.widgets.EnhancedJTable;
import fr.univmrs.tagc.common.widgets.SplitPane;
import fr.univmrs.tagc.common.widgets.StatusTextField;
import fr.univmrs.tagc.common.widgets.StockButton;

/**
 * Generic UI to display the content of a list.
 * It offers optional UI to reorder and alter the content of the list,
 * using the GenericNamedList interface as a backend.
 */
public class GenericNamedListPanel<T> extends JPanel 
	implements ObjectPropertyEditorUI, ListSelectionListener, MultiActionListener {
    private static final long serialVersionUID = -4236977685092639157L;
    
    JScrollPane sp = new JScrollPane();
    protected GenericNamedList<T> list;
    protected NamedListModel<T> model = new NamedListModel<T>();
    EnhancedJTable jl = new EnhancedJTable(model);
    boolean rendererInstalled = false;
    int autohide = -1;
    
    JButton b_up;
    JButton b_down;
    ButtonPopup b_add;
    JButton b_del;
    StatusTextField t_filter;
    JLabel l_title = new JLabel();
    
    protected JPanel p_right;
    protected JPanel targetpanel;
    protected CardLayout cards;

	// TODO: fix ugly hack for the size of the action column
    int bsize = 25;
    
	private GenericPropertyInfo	pinfo;
    
    public GenericNamedListPanel() {
    	this(null, null);
    }
    public GenericNamedListPanel(Map<Class<?>, Component> m_right, String name) {
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
    		Iterator<Entry<Class<?>, Component>> it = m_right.entrySet().iterator();
    		while (it.hasNext()) {
    			Entry<Class<?>, Component> e = it.next();
    			p_right.add(e.getValue(), e.getKey().toString());
    		}
    		cards.show(p_right, "empty");
    		split.setRightComponent(p_right);
    		split.setLeftComponent(targetpanel);
    		this.add(split);
    		split.setName(name+".split");
    	}
        targetpanel .setLayout(new GridBagLayout());
        sp.setViewportView(jl);

        RS sorter = new RS(this);
        jl.setRowSorter(sorter);
        
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
        t_filter.addKeyListener(sorter);
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
        b_add = new ButtonPopup("list-add.png", true, null);
        b_add.addActionListener(this);
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
    public void setList(GenericNamedList<T> list) {
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
        
        GenericNamedListCapabilities capabilities = list.getCapabilities();
        
        b_up.setVisible(capabilities.order);
        b_down.setVisible(capabilities.order);
        b_add.setVisible(capabilities.add && !capabilities.inline);
        if (b_add.isVisible()) {
        	b_add.setOptions(list.getAddModes());
        }
        b_del.setVisible(capabilities.remove && !capabilities.inline);
        if (list.size() > 0) {
            jl.getSelectionModel().setSelectionInterval(0, 0);
        }
        for (int i=0 ; i<list.getActionCount() ; i++) {
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
    
    protected void doAdd(int mode) {
        if (list == null) {
            return;
        }
        int pos = jl.getSelectedRow();
        int n = list.add(pos, mode);
        if (n >= 0) {
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
            i = i>=list.size() ? list.size()-1 : i;
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
    	return t;
    }

    /**
     * force a refresh of the list
     */
    public void refresh() {
        model.fireTableDataChanged();
        refreshHide();
        b_add.refresh();
    }
    
    private void refreshHide() {
        if (list.size() <= autohide) {
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
	public void apply() {
	}

	public void refresh(boolean force) {
		if (force) {
			setList((GenericNamedList)pinfo.getRawValue());
		}
	}

	public void setEditedProperty(GenericPropertyInfo pinfo,
			GenericPropertyHolder panel) {
		this.pinfo = pinfo;
		panel.addField(this, pinfo, 0);
	}

	public ListSelectionModel getSelectionModel() {
		return jl.getSelectionModel();
	}
	public Object getSelectedItem() {
		int[] t_sel = getSelection();
		if (t_sel.length == 1 && t_sel[0] < list.size()) {
			return list.get(t_sel[0]);
		}
		return null;
	}
	public void valueChanged(ListSelectionEvent e) {
    	if (pinfo != null) {
    		pinfo.setValue(jl.getSelectedRow());
    	}
    	if (cards != null) {
	    	Object o = getSelectedItem();
	    	if (o != null) {
	    		cards.show(p_right, o.getClass().toString());
	    	} else {
	    		cards.show(p_right, "empty");
	    	}
    	}
	}
	public void actionPerformed(ActionEvent e, int mode) {
		doAdd(mode);
	}
}

class SimpleRowFilter<M,I> extends RowFilter<M, I> {

	private String filter = null;
	
	public void setFilter(String filter, TableRowSorter sorter) {
		if (filter == null || filter.equals("")) {
			this.filter = null;
		} else {
			this.filter = filter.toLowerCase();
		}
		sorter.sort();
	}
	
	@Override
	public boolean include(Entry entry) {
		if (filter == null) {
			return true;
		}
		String v = entry.getStringValue(0).toLowerCase();
		return v.contains(filter);
	}
}


class RS extends TableRowSorter<NamedListModel> implements KeyListener {

	private final SimpleRowFilter rf;
	private final GenericNamedListPanel panel;
	
	public RS(GenericNamedListPanel panel) {
		super(panel.model);
		this.panel = panel;
		rf = new SimpleRowFilter();
		setRowFilter(rf);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		JTextField tf = (JTextField) e.getComponent();
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			tf.setText("");
		}
		
		String t = tf.getText();
		try {
			rf.setFilter(t,this);
			//setRowFilter(RowFilter.regexFilter(t));
			if (t.equals("")) {
				tf.setBackground(Color.WHITE);
				panel.b_down.setEnabled(true);
				panel.b_up.setEnabled(true);
			} else {
				tf.setBackground(Color.YELLOW);
				panel.b_down.setEnabled(false);
				panel.b_up.setEnabled(false);
			}
		} catch (Exception ex) {
			setRowFilter(null);
			tf.setBackground(Color.RED);
		}
	}
}



class NamedListModel<T> extends AbstractTableModel implements GenericListListener, TableActionListener {
    private static final long serialVersionUID = 886643323547667463L;
    
    private GenericNamedList<T> list;
    private GenericNamedListPanel<T> panel;
    private int lastLineInc = 0;
    private Map m_button = new HashMap();
    private Class[] t_type;
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return list.getCapabilities().edit && columnIndex >= list.getActionCount();
    }
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        list.edit(rowIndex, columnIndex, aValue);
    }

    public Object getValueAt(int row, int col) {
        if (list == null) {
            return null;
        }
        if (lastLineInc != 0 && row == list.size()) {
        	return "";
        }
        if (col < list.getActionCount()) {
        	Object oa = list.getAction(row, col);
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
        T o = list.get(row);
        if (o instanceof MultiColObject) {
    		return ((MultiColObject)o).getVal(col-list.getActionCount());
        }
        return o;
    }
    
    public Class getColumnClass(int columnIndex) {
    	if (columnIndex < list.getActionCount()) {
    		return JButton.class;
    	}
    	if (t_type != null) {
    		return t_type[columnIndex-list.getActionCount()];
    	}
		return super.getColumnClass(columnIndex);
	}
	public int getRowCount() {
        if (list == null) {
            return 0;
        }
        return list.size()+lastLineInc;
    }
    public int getColumnCount() {
    	if (list == null) {
    		return 0;
    	}
        return list.getNbCol();
    }
    
    public String getColumnName(int column) {
    	if (list != null && column >= list.getActionCount()) {
    		return list.getColName(column-list.getActionCount());
    	}
        return "";
    }
    void setList(GenericNamedList<T> list, GenericNamedListPanel<T> panel) {
        this.list = list;
        this.panel = panel;
        // FIXME: listeners
        //        list.addListListener(this);
        if (list.getCapabilities().inline) {
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
		list.run(row, col);
	}
}
