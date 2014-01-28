package org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeSelectionModel;


public class DecoTreeTable extends JTable {
	private static final long serialVersionUID = -6092467728304640800L;
    private DTreeTableCellRenderer cellRenderer;
	private DTreeTableCellEditor cellEditor;
	private DecoTreeTableModel model;
	private DTreeModel treeModel;
	private TableDecoTree tree;

	public DecoTreeTable(TableDecoTree t) {
		super();
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		treeModel = (DTreeModel)t.getModel();
		model = new DecoTreeTableModel(treeModel);
		tree = t;
		tree.setShowsRootHandles(true);
		setModel(model);
		setRowMargin(1);
		setRowHeight(t.getRowHeight());
		cellRenderer = new DTreeTableCellRenderer(tree);
		tree.setSelectionModel(new DefaultTreeSelectionModel() {
			private static final long serialVersionUID = 7360828868695237574L;
			{
				setSelectionModel(listSelectionModel);
			}
		});
		tree.setEditable(false);
		tree.expandPath(tree.getPathForRow(0));
		cellEditor = new DTreeTableCellEditor(cellRenderer);
		setSelectionBackground(Color.yellow);
		setDefaultRenderer(AbstractDTreeElement.class, cellRenderer);
		setDefaultEditor(AbstractDTreeElement.class, cellEditor);
	}
	public void initModel(AbstractDTreeElement root, Vector columns, Vector colWidth) {
		model.init(columns, tree);
		treeModel.init(root, tree);
		treeModel.fireTreeStructureChanged(root);
		model.fireTableStructureChanged();
		int nbcol = getColumnModel().getColumnCount();
		int[] w = new int[nbcol];
		if (nbcol > 0) for (int i = 0; i < nbcol; i++) w[i] = getColumnModel().getColumn(i).getWidth();
		createDefaultColumnsFromModel();
		if (nbcol > 0) for (int i = 0; i < nbcol; i++) getColumnModel().getColumn(i).setPreferredWidth(w[i]);
	}
	public int getEditingRow() {
		//return editingRow;
		return (getColumnClass(editingColumn) == AbstractDTreeElement.class) ?  -1 : editingRow;
	}
	public JTree getTree() {
		return tree;
	}
	public void setRowHeight(int rowHeight) {
    super.setRowHeight(rowHeight);
		if (cellRenderer != null && tree.getRowHeight() != rowHeight) {
			tree.setRowHeight(getRowHeight());
		}
  }
}
