package fr.univmrs.tagc.widgets;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import fr.univmrs.tagc.datastore.ValueList;
import fr.univmrs.tagc.datastore.gui.TableActionListener;
import fr.univmrs.tagc.datastore.models.ValueListComboModel;

/**
 * A "better" JTable with some often needed customizations.
 *  - cell value if deleted on keypress.
 *  - JButtons can be added and will get clicks (but not keypress?)
 */
public class EnhancedJTable extends JTable {
    private static final long serialVersionUID = 835349911766025807L;

    private Vector v_actionListeners;
    
    /** 
     */
    public EnhancedJTable() {
        this(null);
    }

    /**
     * @param model
     */
    public EnhancedJTable(TableModel model) {
        super(model);
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        setDefaultEditor(ValueList.class, new ValueInListCellEditor());

        TableCellRenderer defaultRenderer;
        
        defaultRenderer = getDefaultRenderer(JButton.class);
        setDefaultRenderer(JButton.class,
                new JTableButtonRenderer(defaultRenderer));
        setDefaultRenderer(JComponent.class,
                new JTableButtonRenderer(defaultRenderer));
        addMouseListener(new JTableButtonMouseListener(this));
    }

    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
            int condition, boolean pressed) {
        Component editorComponent = getEditorComponent();
        if (editorComponent instanceof JTextField && !editorComponent.hasFocus()) {
            editorComponent.requestFocus();
            ((JTextField)editorComponent).setText("");
        }
        return super.processKeyBinding(ks, e, condition, pressed);
    }

    public void addActionListener(TableActionListener l) {
    	if (v_actionListeners == null) {
    		v_actionListeners = new Vector();
    	}
    	v_actionListeners.add(l);
    }
    public void removeActionListener(TableActionListener l) {
    	if (v_actionListeners == null) {
    		return;
    	}
    	v_actionListeners.remove(l);
    }
	public void click(int row, int col) {
		if (v_actionListeners != null) {
			Iterator it = v_actionListeners.iterator();
			while (it.hasNext()) {
				((TableActionListener)it.next()).actionPerformed(row, col);
			}
		}
	}
}

class ValueInListCellEditor extends AbstractCellEditor implements TableCellEditor {
    private static final long serialVersionUID = -2790803389946873836L;

    ValueListComboModel model = new ValueListComboModel();
    JComboBox combo = new JComboBox(model);
    ValueList data;
    
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }
    
    public Object getCellEditorValue() {
        return data;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof ValueList) {
            model.setData((ValueList)value);
            return combo;
        }
        return new JLabel("not of the good type!");
    }
}

class JTableButtonRenderer implements TableCellRenderer {
    private TableCellRenderer __defaultRenderer;

    /**
     * @param renderer
     */
    public JTableButtonRenderer(TableCellRenderer renderer) {
      __defaultRenderer = renderer;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                           boolean isSelected,
                           boolean hasFocus,
                           int row, int column) {
    	if(value instanceof Component) {
    		return (Component)value;
    	}
    	return __defaultRenderer.getTableCellRendererComponent(
    			table, value, isSelected, hasFocus, row, column);
    }
}


class JTableButtonMouseListener implements MouseListener {
    private EnhancedJTable __table;

    private void __forwardEventToButton(MouseEvent e) {
      TableColumnModel columnModel = __table.getColumnModel();
      int column = columnModel.getColumnIndexAtX(e.getX());
      int row    = e.getY() / __table.getRowHeight();
      Object value;
      JButton button;

      if(row >= __table.getRowCount() || row < 0 ||
         column >= __table.getColumnCount() || column < 0) {
		return;
	}

      value = __table.getValueAt(row, column);

      if(value instanceof JButton) {
          button = (JButton)value;
          if (e.getID() == MouseEvent.MOUSE_CLICKED && e.getButton() == MouseEvent.BUTTON1) {
              button.doClick();
              __table.click(row, column);
          }
      }
      __table.repaint();
    }

    /**
     * @param table
     */
    public JTableButtonMouseListener(EnhancedJTable table) {
      __table = table;
    }

    public void mouseClicked(MouseEvent e) {
      __forwardEventToButton(e);
    }

    public void mouseEntered(MouseEvent e) {
      __forwardEventToButton(e);
    }

    public void mouseExited(MouseEvent e) {
      __forwardEventToButton(e);
    }

    public void mousePressed(MouseEvent e) {
      __forwardEventToButton(e);
    }

    public void mouseReleased(MouseEvent e) {
      __forwardEventToButton(e);
    }
}
