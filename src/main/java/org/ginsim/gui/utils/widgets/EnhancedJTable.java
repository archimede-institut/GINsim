package org.ginsim.gui.utils.widgets;

import org.ginsim.core.utils.data.ValueList;
import org.ginsim.gui.utils.data.TableActionListener;
import org.ginsim.gui.utils.data.models.ValueListComboModel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * A "better" JTable with some often needed customizations. - cell value if
 * deleted on keypress. - JButtons can be added and will get clicks (but not
 * keypress?)
 */
public class EnhancedJTable extends JTable {
	private static final long serialVersionUID = 835349911766025807L;

	private List<TableActionListener> v_actionListeners;

	public void addCellEditor(Class theClass, TableCellEditor theEditor) {
		setDefaultEditor(theClass, theEditor);
	}

	public void addCellRenderer(Class theClass, Class theRendererClass) {
		TableCellRenderer defaultRenderer;
		Class[] constructors = { TableCellRenderer.class };
		Object[] arguments = new Object[1];
		defaultRenderer = getDefaultRenderer(theClass);
		TableCellRenderer rendererInstance;
		try {
			Constructor constructor = theRendererClass.getConstructor(constructors);
			arguments[0] = defaultRenderer;
			rendererInstance = (TableCellRenderer) constructor.newInstance(arguments);
			setDefaultRenderer(theClass, rendererInstance);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	static {
	}

	/** 
     */
	public EnhancedJTable() {
		this(null);
	}

	public void setCopyHeaders() {
			setTransferHandler( new TableHeaderTransferHandler());
	}

	/**
	 * @param model
	 */
	public EnhancedJTable(TableModel model) {
		super(model);
		// add some custom stuff
		addCellRenderer(JComponent.class, JTableButtonRenderer.class);
		addCellEditor(ValueList.class, new ValueInListCellEditor());
		addMouseListener(new JTableButtonMouseListener(this));
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}

	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
			int condition, boolean pressed) {
		Component editorComponent = getEditorComponent();
		if (editorComponent instanceof JTextField
				&& !editorComponent.hasFocus()) {
			editorComponent.requestFocus();
			((JTextField) editorComponent).setText("");
		}
		return super.processKeyBinding(ks, e, condition, pressed);
	}

	public void addActionListener(TableActionListener l) {
		if (v_actionListeners == null) {
			v_actionListeners = new ArrayList<TableActionListener>();
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
		if (v_actionListeners == null) {
			return;
		}
		for (TableActionListener listener: v_actionListeners) {
			listener.actionPerformed(row, col);
		}
	}

	public void setMaxCols(int[] maxcols) {
		TableColumnModel colModel = getColumnModel();
		for (int i = 0; i < maxcols.length; i += 2) {
			colModel.getColumn(maxcols[i]).setMaxWidth(maxcols[i + 1]);
		}
	}
}

class ValueInListCellEditor extends AbstractCellEditor implements
		TableCellEditor {
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

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value instanceof ValueList) {
			model.setData((ValueList) value);
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
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Component) {
			return (Component) value;
		}
		return __defaultRenderer.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
	}
}

class JTableButtonMouseListener implements MouseListener {
	private EnhancedJTable __table;

	private void __forwardEventToButton(MouseEvent e) {
		TableColumnModel columnModel = __table.getColumnModel();
		int column = columnModel.getColumnIndexAtX(e.getX());
		int row = e.getY() / __table.getRowHeight();
		Object value;
		JButton button;

		if (row >= __table.getRowCount() || row < 0
				|| column >= __table.getColumnCount() || column < 0) {
			return;
		}

		value = __table.getValueAt(row, column);

		if (value instanceof JButton) {
			button = (JButton) value;
			if (e.getID() == MouseEvent.MOUSE_CLICKED
					&& e.getButton() == MouseEvent.BUTTON1) {
				button.doClick();
				__table.click(row, column);
			}
		}

		if (value instanceof Component) {
			// TODO: forward click!
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
