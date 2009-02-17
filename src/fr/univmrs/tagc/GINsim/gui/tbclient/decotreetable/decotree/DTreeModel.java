package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


public class DTreeModel implements TreeModel {
	protected AbstractDTreeElement root;
	private Vector treeModelListeners;
	protected DecoTree tree;

	public DTreeModel() {
		super();
		root = null;
		tree = null;
		treeModelListeners = new Vector();
	}
	public void init(AbstractDTreeElement e, DecoTree t) {
		root = e;
		tree = t;
		tree.setModel(this);
	}

	public JTree getTree() {
		return tree;
	}

	public Object getRoot() {
		return root;
	}

	public int getChildCount(Object parent) {
		return ((AbstractDTreeElement)parent).getChildCount();
	}

	public boolean isLeaf(Object node) {
		return ((AbstractDTreeElement)node).isLeaf();
	}

	public Object getChild(Object parent, int index) {
		return ((AbstractDTreeElement)parent).getChild(index);
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ((AbstractDTreeElement)parent).indexOfChild(child);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		fireTreeStructureChanged((AbstractDTreeElement)path.getLastPathComponent());
	}
	public void fireTreeStructureChanged(AbstractDTreeElement element) {
		TreeModelEvent e = new TreeModelEvent(this, new Object[] {element});
		for (Iterator it = treeModelListeners.iterator(); it.hasNext(); )
			((TreeModelListener)it.next()).treeStructureChanged(e);
	}

	public void fireTreeNodesChanged(AbstractDTreeElement element) {
		TreeModelEvent e = new TreeModelEvent(this, new Object[] {element});
		for (Iterator it = treeModelListeners.iterator(); it.hasNext(); )
			((TreeModelListener)it.next()).treeNodesChanged(e);
	}

	public void addTreeModelListener(TreeModelListener treeModelListener) {
		treeModelListeners.addElement(treeModelListener);
	}

	public void removeTreeModelListener(TreeModelListener treeModelListener) {
		treeModelListeners.removeElement(treeModelListener);
	}


}
