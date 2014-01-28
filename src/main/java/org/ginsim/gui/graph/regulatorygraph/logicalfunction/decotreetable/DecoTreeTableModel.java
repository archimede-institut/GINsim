package org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


public class DecoTreeTableModel extends DefaultTableModel implements TreeModel  {
	private static final long serialVersionUID = 2271557040284252693L;
	private Vector tableModelListeners;
	private JTree tree;
	private Vector columnNames;
	protected DTreeModel treeModel;

	public DecoTreeTableModel(DTreeModel tmodel) {
		super();
		tree = null;
		tableModelListeners = new Vector();
		columnNames = new Vector();
		treeModel = tmodel;
	}
	public void init(Vector columns, JTree t) {
		columnNames = columns;
		tree = t;
		tree.setModel(treeModel);
		tree.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				fireTableStructureChanged();
			}
			public void treeCollapsed(TreeExpansionEvent event) {
				fireTableStructureChanged();
			}
		});
	}
	public void fireTreeStructureChanged(AbstractDTreeElement element) {
		(treeModel).fireTreeStructureChanged(element);
	}
	public void fireTreeNodesChanged(AbstractDTreeElement element) {
		(treeModel).fireTreeNodesChanged(element);
	}
	public void fireTableStructureChanged() {
		super.fireTableStructureChanged();
		if (tableModelListeners != null) {
			TableModelEvent e = new TableModelEvent(this);
			for (Iterator it = tableModelListeners.iterator(); it.hasNext(); )
				((TableModelListener) it.next()).tableChanged(e);
		}
	}

	// TreeModel interface
	public void addTreeModelListener(TreeModelListener treeModelListener) {
		treeModel.addTreeModelListener(treeModelListener);
	}
	public Object getChild(Object parent, int index) {
		return treeModel.getChild(parent, index);
	}
	public int getChildCount(Object parent) {
		return treeModel.getChildCount(parent);
	}
	public int getIndexOfChild(Object parent, Object child) {
		return treeModel.getIndexOfChild(parent, child);
	}
	public Object getRoot() {
		return treeModel.getRoot();
	}
	public boolean isLeaf(Object node) {
		return treeModel.isLeaf(node);
	}
	public void removeTreeModelListener(TreeModelListener treeModelListener) {
		treeModel.removeTreeModelListener(treeModelListener);
	}
	public void valueForPathChanged(TreePath path, Object newValue) {
		treeModel.valueForPathChanged(path, newValue);
	}

	// TableModel interface
  public int getColumnCount() {
		return columnNames.size();
	}
  public int getRowCount() {
		if (tree != null) return tree.getRowCount();
		return 0;
  }
  public AbstractDTreeElement getTreeElement(int row) {
  	if (tree != null)	return (AbstractDTreeElement)tree.getPathForRow(row).getLastPathComponent();
  	return null;
  }
	public void setTreeElement(int row, AbstractDTreeElement e) {
		AbstractDTreeElement oldElement = (AbstractDTreeElement)tree.getPathForRow(row).getLastPathComponent();
		AbstractDTreeElement parent = (AbstractDTreeElement)tree.getPathForRow(row).getPathComponent(tree.getPathForRow(row).getPathCount() - 2);
		for (int i = 0; i < parent.getChildCount(); i++)
			if (parent.getChild(i).equals(oldElement)) {
				parent.setElementAt(i, e);
				break;
			}
	}
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return true;
		if (getTreeElement(rowIndex).getValues() != null)
			return getTreeElement(rowIndex).getValues().isEditable(columnIndex - 1);
		return false;
	}
	public Class getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return AbstractDTreeElement.class;
		if (((AbstractDTreeElement)treeModel.getRoot()).getValues() != null)
			return ((AbstractDTreeElement)treeModel.getRoot()).getValues().getClass(columnIndex - 1);
		return String.class;
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return getTreeElement(rowIndex);
		else if (getTreeElement(rowIndex).getValues() != null)
			return getTreeElement(rowIndex).getValues().getValueAt(columnIndex - 1);
		return null;
	}
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex > 0)
			getTreeElement(rowIndex).getValues().setValueAt(columnIndex - 1, aValue, false);
	}
	public String getColumnName(int columnIndex) {
		return (String)columnNames.elementAt(columnIndex);
	}
	public Vector getColumnNames() {
		return columnNames;
	}
	public void addTableModelListener(TableModelListener l) {
		tableModelListeners.addElement(l);
	}
	public void removeTableModelListener(TableModelListener l) {
		tableModelListeners.removeElement(l);
	}
}
