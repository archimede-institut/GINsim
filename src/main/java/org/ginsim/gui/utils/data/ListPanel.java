package org.ginsim.gui.utils.data;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.ginsim.core.utils.data.MultiColObject;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.gui.utils.widgets.SplitPane;
import org.ginsim.gui.utils.widgets.StockButton;


/**
 * Generic UI to display the content of a list.
 * It offers optional UI to reorder and alter the content of the list,
 * using the GenericList interface as a backend.
 */
public class ListPanel<T> extends JPanel 
	implements ListSelectionListener {
    
	JScrollPane sp = new JScrollPane();
    protected List<T> list;
    protected final ListPanelHelper<T> helper;

    protected SimpleListModel<T> model = new SimpleListModel<T>();
    EnhancedJTable jl = new EnhancedJTable(model);
    boolean rendererInstalled = false;
    int autohide = -1;
    
    JButton b_up;
    JButton b_down;
    JButton b_add;
    JButton b_del;
    JLabel l_title = new JLabel();
    
    protected JPanel p_right;
    protected JPanel targetpanel;
    protected CardLayout cards;
    

	// TODO: fix ugly hack for the size of the action column
    int bsize = 25;
    
    public ListPanel(ListPanelHelper<T> helper, String name) {
    	this.helper = helper;
    	jl.addActionListener(model);
    	jl.getSelectionModel().addListSelectionListener(this);
    	targetpanel = this;
    	if (helper.m_right != null) {
    		setLayout(new GridLayout());
    		JSplitPane split = new SplitPane();
    		targetpanel = new JPanel();
    		p_right = new JPanel();
    		cards = new CardLayout();
    		p_right.setLayout(cards);
    		p_right.add(new JPanel(), "empty");
    		Iterator<Entry<Class<?>, Component>> it = helper.m_right.entrySet().iterator();
    		while (it.hasNext()) {
    			Entry<Class<?>, Component> e = it.next();
    			p_right.add(e.getValue(), e.getKey().toString());
    		}
    		cards.show(p_right, "empty");
    		split.setRightComponent(p_right);
    		split.setLeftComponent(targetpanel);
    		split.setDividerLocation( 150);
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
        
        // TODO: use java6 jtable filtering feature
//        t_filter = new StatusTextField("filter", true);
//        t_filter.addKeyListener(this);
//        targetpanel.add(t_filter, c);
		
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
        b_add.addActionListener(new AddAction(helper));
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
    	l_title.setText(title);
		l_title.setVisible(true);
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
    public void setList(List<T> list) {
        this.list = list;
        if (list == null) {
            b_up.setVisible(false);
            b_down.setVisible(false);
            b_add.setVisible(false);
            b_del.setVisible(false);
            // TODO: hide filter label
            jl.setEnabled(false);
            return;
        }
        model.setList(list, this);
        jl.setEnabled(true);
        
        b_up.setVisible(helper.canOrder);
        b_down.setVisible(helper.canOrder);
        b_add.setVisible(helper.canAdd && !helper.doInlineAddRemove);
        
        b_del.setVisible(helper.canRemove && !helper.doInlineAddRemove);
        if (list.size() > 0) {
            jl.getSelectionModel().setSelectionInterval(0, 0);
        }
        for (int i=0 ; i<helper.nbAction ; i++) {
        	jl.getColumnModel().getColumn(i).setMaxWidth(bsize);
        }
    }
    
    private boolean moveData(int[] indices, int qt) {
    	// TODO: implement ordering!
    	return false;
    }
    
	protected void doMoveUp() {
        if (list == null) {
            return;
        }
        int[] index = jl.getSelectedRows();
        if (!moveData(index, -1)) {
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
	
    private void doMoveDown() {
        if (list == null) {
            return;
        }
        int[] index=jl.getSelectedRows();
        if (!moveData(index, 1)) {
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
    
    private void doRemove() {
        if (list == null) {
            return;
        }
        int[] sel = jl.getSelectedRows();
        
        // FIXME: remove entries
//        if (sel.length > 0 && removeData(sel)) {
//            int i = sel[0];
//            i = i>=list.size() ? list.size()-1 : i;
//            refresh();
//            jl.getSelectionModel().setSelectionInterval(i,i);
//        }
    }

    /**
     * force a refresh of the list
     */
    public void refresh() {
        model.fireTableDataChanged();
        refreshHide();
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

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int[] sel = jl.getSelectedRows();
		helper.selectionChanged(sel);
	}
	
}

class AddAction extends AbstractAction {

	private final ListPanelHelper helper;
	
	public AddAction(ListPanelHelper helper) {
		this.helper = helper;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Note: here I would love to hide the menu if it is visible...
		// but menu.isVisible() seems to always returns false! Not worth the extra headache!
		
		Object[] modes = helper.getCreateTypes();
		if (modes == null || modes.length < 1) {
			helper.create(null);
			return;
		}
		
		if (modes.length == 1) {
			helper.create(modes[0]);
			return;
		}
		
		// several possibilities, build a menu for them!
		JPopupMenu menu = new JPopupMenu();
		for (Object o: modes) {
			Action action = null;
			if (o instanceof Action) {
				action = (Action)o;
			} else {
				action = new CreateModeAction(o);
			}
			menu.add(action);
		}
		
		Object src = e.getSource();
		if (src instanceof Component) {
			Component dropdown = (Component)src;
			menu.show(dropdown, 0, dropdown.getHeight());
			return;
		}

	}

}

class CreateModeAction extends AbstractAction {
	private final Object mode;
	
	public CreateModeAction(Object mode) {
		super(mode.toString());
		this.mode = mode;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("TODO: call create for mode: "+mode);
	}
}

class SimpleListModel<T> extends AbstractTableModel implements TableActionListener {
    private static final long serialVersionUID = 886643323547667463L;
    
    private List<T> list;
    private ListPanel<T> panel;

    private int lastLineInc = 0;
    private Map m_button = new HashMap();
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	// TODO: editable cells?
        return false;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    	// TODO: editable cells?
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (list == null) {
            return null;
        }
        if (lastLineInc != 0 && row == list.size()) {
        	return "";
        }
        if (col < panel.helper.nbAction) {
        	Object oa = panel.helper.getAction(row, col);
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
        
        col -= panel.helper.nbAction;
        T o = list.get(row);
        
        if (o instanceof MultiColObject) {
    		return ((MultiColObject)o).getVal(col);
        }
        
    	return o;
    }
	@Override
	public int getRowCount() {
        if (list == null) {
            return 0;
        }
        return list.size()+lastLineInc;
    }
	@Override
    public int getColumnCount() {
    	if (list == null) {
    		return 0;
    	}
        return panel.helper.nbcol;
    }
	@Override
    public Class getColumnClass(int columnIndex) {
    	if (columnIndex < panel.helper.nbAction) {
    		return JButton.class;
    	}
    	return panel.helper.getColumnClass(columnIndex-panel.helper.nbAction);
	}
    @Override
    public String getColumnName(int column) {
    	if (column < panel.helper.nbAction) {
    		return "";
    	}
    	return panel.helper.getColumnName(column- panel.helper.nbAction);
    }
    void setList(List<T> list, ListPanel<T> panel) {
        this.list = list;
        this.panel = panel;
        if (panel.helper.doInlineAddRemove) {
        	lastLineInc = 1;
        } else {
        	lastLineInc = 0;
        }
        fireTableStructureChanged();
    }

    @Override
	public void actionPerformed(int row, int col) {
		panel.helper.runAction(row, col);
	}
}
