package fr.univmrs.tagc.widgets;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import fr.univmrs.tagc.datastore.ValueList;

/**
 * A "better" JTable with some often needed customizations.
 *  - cell value if deleted on keypress.
 *  - JButtons can be added and will get clicks (but not keypress?)
 */
public class EnhancedJTable extends JTable {
    private static final long serialVersionUID = 835349911766025807L;

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
        setDefaultEditor(Boolean.class, new BooleanCellEditor());
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
}

class BooleanCellEditor extends DefaultCellEditor {
    private static final long serialVersionUID = -2790803389946873836L;

    protected BooleanCellEditor() {
        super(new JCheckBox());
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }
}

class ValueInListCellEditor extends AbstractCellEditor implements TableCellEditor {
    private static final long serialVersionUID = -2790803389946873836L;

    GsComboModel model = new GsComboModel();
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
                           int row, int column)
    {
      if(value instanceof Component) {
		return (Component)value;
	}
      return __defaultRenderer.getTableCellRendererComponent(
         table, value, isSelected, hasFocus, row, column);
    }
}

//class JTableValueInListRenderer implements TableCellRenderer {
//    private TableCellRenderer __defaultRenderer;
//
//    /**
//     * @param renderer
//     */
//    public JTableValueInListRenderer(TableCellRenderer renderer) {
//      __defaultRenderer = renderer;
//    }
//
//    public Component getTableCellRendererComponent(JTable table, Object value,
//                           boolean isSelected,
//                           boolean hasFocus,
//                           int row, int column)
//    {
//      if(value instanceof GsValueList)
//        return new JComboBox(new GsComboModel((GsValueList)value));
//      return __defaultRenderer.getTableCellRendererComponent(
//         table, value, isSelected, hasFocus, row, column);
//    }
//}

class JTableButtonMouseListener implements MouseListener {
    private JTable __table;

    private void __forwardEventToButton(MouseEvent e) {
      TableColumnModel columnModel = __table.getColumnModel();
      int column = columnModel.getColumnIndexAtX(e.getX());
      int row    = e.getY() / __table.getRowHeight();
      Object value;
      JButton button;
//      MouseEvent buttonEvent;

      if(row >= __table.getRowCount() || row < 0 ||
         column >= __table.getColumnCount() || column < 0) {
		return;
	}

      value = __table.getValueAt(row, column);

      if(value instanceof JButton) {
          button = (JButton)value;
          if (e.getID() == MouseEvent.MOUSE_CLICKED && e.getButton() == MouseEvent.BUTTON1) {
              button.doClick();
          }
      }
//      buttonEvent = SwingUtilities.convertMouseEvent(__table, e, button);
//      button.dispatchEvent(buttonEvent);
      // This is necessary so that when a button is pressed and released
      // it gets rendered properly.  Otherwise, the button may still appear
      // pressed down when it has been released.
      __table.repaint();
    }

    /**
     * @param table
     */
    public JTableButtonMouseListener(JTable table) {
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

class GsComboModel extends DefaultComboBoxModel {
    private static final long serialVersionUID = -8553547226168566527L;
    
    ValueList data;

    GsComboModel() {
    }
    
    GsComboModel(ValueList data) {
        this.data = data;
    }
    
    void setData(ValueList data) {
        this.data = data;
        fireContentsChanged(this, 0, getSize());
    }
    
    public Object getElementAt(int index) {
        if (data == null) {
            return null;
        }
        return data.get(index);
    }

    public int getIndexOf(Object anObject) {
        if (data == null) {
            return -1;
        }
        return data.indexOf(anObject);
    }

    public Object getSelectedItem() {
        if (data == null) {
            return null;
        }
        int sel = data.getSelectedIndex();
        sel = sel == -1 ? 0 : sel;
        return data.get(data.getSelectedIndex());
    }

    public int getSize() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public void setSelectedItem(Object anObject) {
        if (data == null) {
            return;
        }
        data.setSelectedIndex(getIndexOf(anObject));
    }
}  