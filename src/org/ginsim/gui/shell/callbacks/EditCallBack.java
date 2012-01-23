package org.ginsim.gui.shell.callbacks;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.ginsim.common.utils.Translator;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.shell.FrameActionManager;
import org.ginsim.gui.shell.SearchFrame;

import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;


/**
 * Callbacks for the "File" menu
 */
public class EditCallBack {
	
	protected static Graph copiedSubGraph = null;
	
	/**
	 * Create and populate an Edit menu
	 * 
	 * @param graph a graph
	 * @return
	 */
	public static JMenu getEditMenu(Graph<?, ?> graph) {
		JMenu menu = new JMenu( Translator.getString( "STR_Edit"));
		
		menu.add(new CopyAction(graph));
		menu.add(new PasteAction(graph));

		menu.add(new JSeparator());
//
//		menu.add(new UndoAction(graph));
//		menu.add(new RedoAction(graph));
//
//		menu.add(new JSeparator());
		
		menu.add(new SearchNodeAction(graph));

		menu.add(new JSeparator());
		JMenu smenu = new JMenu(Translator.getString( "STR_SelectAll"));
		smenu.add(new SelectAllAction(graph));
		smenu.add(new SelectAllNodesAction(graph));
		smenu.add(new SelectAllEdgesAction(graph));
		menu.add(smenu);
		
		smenu = new JMenu(Translator.getString( "STR_InvertSelection"));
		smenu.add(new InvertSelectionAction(graph));
		smenu.add(new InvertNodesSelectionAction(graph));
		smenu.add(new InvertEdgesSelectionAction(graph));
		menu.add(smenu);
		
		smenu = new JMenu(Translator.getString( "STR_ExtendSelection"));
		smenu.add(new ExtendSelectionToOutgoingNodesAction(graph));
		smenu.add(new ExtendSelectionToOutgoingEdgesAction(graph));
		smenu.add(new ExtendSelectionToIncomingNodesAction(graph));
		smenu.add(new ExtendSelectionToIncomingEdgesAction(graph));
		menu.add(smenu);
		
		return menu;
	}
}


class CopyAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public CopyAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_Copy"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_Copy_descr"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, FrameActionManager.MASK));
		this.graph = graph;
	}
	
	public void copy() {
		GraphSelection graphSelection = GUIManager.getInstance().getGraphGUI(graph).getSelection();
		Collection nodes, edges;
		if (graphSelection.getSelectedNodes() == null) {
			nodes = new ArrayList();
		} else {
			nodes = new ArrayList(graphSelection.getSelectedNodes());
		}
		if (graphSelection.getSelectedEdges() == null) {
			edges = new ArrayList();
			EditCallBack.copiedSubGraph = graph.getSubgraph(nodes, edges);
			return;
		}
		edges = new ArrayList(graphSelection.getSelectedEdges());
		Collection selectedEdges = graphSelection.getSelectedEdges();
		int questionHasBeenAsked = -1; //-1 = Not asked, 0 = asked and extend, 1 = asked and restraint
		for (Iterator it = selectedEdges.iterator(); it.hasNext();) {
			Edge edge = (Edge) it.next();
			questionHasBeenAsked = nodeFound(nodes, edges, edge, edge.getSource(), questionHasBeenAsked);
			questionHasBeenAsked = nodeFound(nodes, edges, edge, edge.getTarget(), questionHasBeenAsked);
		}
		
		EditCallBack.copiedSubGraph = graph.getSubgraph(nodes, edges);
	}
	
	private int nodeFound(Collection nodes, Collection edges, Edge edge, Object node, int questionHasBeenAsked) {
		if (! nodes.contains(node)) {
			if (questionHasBeenAsked == -1) {//not asked
				questionHasBeenAsked = JOptionPane.showConfirmDialog(null, Translator.getString( "STR_Copy_shouldExtend"),"",JOptionPane.YES_NO_OPTION);
			}
			if (questionHasBeenAsked == 0) { //extend
				nodes.add(node);
			} else if (questionHasBeenAsked == 1){ //restraint
				edges.remove(edge);
			}
		}
		return questionHasBeenAsked;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		copy();
	}
}

class PasteAction<V, E extends Edge<V>> extends AbstractAction {
	
	private final Graph<V,E> graph;
	
	public PasteAction(Graph<V,E> graph) {
		super( Translator.getString( "STR_Paste"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_Paste_descr"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, FrameActionManager.MASK));
		this.graph = graph;
	}
	
	public void paste() {
		graph.merge(EditCallBack.copiedSubGraph);
		GraphSelection<V,E> graphSelection = GUIManager.getInstance().getGraphGUI(graph).getSelection();
		Collection<E> edges = new ArrayList<E>();
		HashMap<V, V> old_to_new_nodes = new HashMap<V, V>();
		for (Object oldNode : EditCallBack.copiedSubGraph.getNodes()) {
			for (Object newNode : graph.getNodes()) {
				if (newNode.toString().startsWith(oldNode.toString())) {
					Object mappedNode = old_to_new_nodes.get(oldNode);
					if (mappedNode == null || mappedNode.toString().compareTo(newNode.toString()) < 0) {
						old_to_new_nodes.put((V) oldNode, (V) newNode);
					}
				}
			}
		}
		Collection<Edge> oldEdges = EditCallBack.copiedSubGraph.getEdges();
		for (Edge edge : oldEdges) {
			edges.add(graph.getEdge(old_to_new_nodes.get(edge.getSource()), old_to_new_nodes.get(edge.getTarget())));
		}
		
		graphSelection.setSelectedNodes(old_to_new_nodes.values());
		graphSelection.setSelectedEdges(edges);

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		paste();
	}
}

class UndoAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public UndoAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_Undo"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_Undo_descr"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, FrameActionManager.MASK));
		this.graph = graph;
	}
	
	public void undo() {
		//TODO Recfactoring action
		//FIXME : find where to undo
		LogManager.error("Unimplemented menu : undo");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		undo();
	}
}
class RedoAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public RedoAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_Redo"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_Redo_descr"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, FrameActionManager.MASK | KeyEvent.SHIFT_MASK));
		this.graph = graph;
	}
	
	public void redo() {
		//TODO Recfactoring action
		//FIXME : find where to redo
		LogManager.error("Unimplemented menu : redo");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		redo();
	}
}
class SearchNodeAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public SearchNodeAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_SearchNode"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_SearchNode_descr"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, FrameActionManager.MASK));
		this.graph = graph;
	}
	
	public void searchNode() {
		new SearchFrame(GUIManager.getInstance().getGraphGUI(graph));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		searchNode();
	}
}

class SelectAllAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public SelectAllAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_SelectAll"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_SelectAll_descr"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, FrameActionManager.MASK));
		this.graph = graph;
	}
	
	public void selectAll() {
		GUIManager.getInstance().getGraphGUI(graph).getSelection().selectAll();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		selectAll();
	}
}

class SelectAllNodesAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public SelectAllNodesAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_SelectAllNodes"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_SelectAllNodes_descr"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, FrameActionManager.MASK | KeyEvent.SHIFT_MASK));
		this.graph = graph;
	}
	
	public void selectAllNodes() {
		GUIManager.getInstance().getGraphGUI(graph).getSelection().selectAllNodes();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		selectAllNodes();
	}
}

class SelectAllEdgesAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public SelectAllEdgesAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_SelectAllEdges"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_SelectAllEdges_descr"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, FrameActionManager.MASK | KeyEvent.SHIFT_MASK | KeyEvent.ALT_MASK));
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		GUIManager.getInstance().getGraphGUI(graph).getSelection().selectAllEdges();
	}
}

class InvertSelectionAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public InvertSelectionAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_InvertSelection"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_InvertSelection_descr"));
		//putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, FrameActionManager.MASK | KeyEvent.SHIFT_MASK));
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		GUIManager.getInstance().getGraphGUI(graph).getSelection().invertSelection();
	}
}

class InvertNodesSelectionAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public InvertNodesSelectionAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_InvertNodesSelection"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_InvertNodesSelection_descr"));
		//putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, FrameActionManager.MASK | KeyEvent.SHIFT_MASK));
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		GUIManager.getInstance().getGraphGUI(graph).getSelection().invertNodesSelection();
	}
}

class InvertEdgesSelectionAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public InvertEdgesSelectionAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_InvertEdgesSelection"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_InvertEdgesSelection_descr"));
		//putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, FrameActionManager.MASK | KeyEvent.SHIFT_MASK));
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		GUIManager.getInstance().getGraphGUI(graph).getSelection().invertEdgesSelection();
	}
}

class ExtendSelectionToOutgoingNodesAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public ExtendSelectionToOutgoingNodesAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_ExtendSelectionToOutgoingNodes"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_ExtendSelectionToOutgoingNodes_descr"));
		//putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, FrameActionManager.MASK | KeyEvent.SHIFT_MASK));
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		GUIManager.getInstance().getGraphGUI(graph).getSelection().extendSelectionToOutgoingNodes();
	}
}

class ExtendSelectionToOutgoingEdgesAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public ExtendSelectionToOutgoingEdgesAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_ExtendSelectionToOutgoingEdges"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_ExtendSelectionToOutgoingEdges_descr"));
		//putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, FrameActionManager.MASK | KeyEvent.SHIFT_MASK));
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		GUIManager.getInstance().getGraphGUI(graph).getSelection().extendSelectionToOutgoingEdges();
	}
}

class ExtendSelectionToIncomingNodesAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public ExtendSelectionToIncomingNodesAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_ExtendSelectionToIncomingNodes"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_ExtendSelectionToIncomingNodes_descr"));
		//putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, FrameActionManager.MASK | KeyEvent.SHIFT_MASK));
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		GUIManager.getInstance().getGraphGUI(graph).getSelection().extendSelectionToIncomingNodes();
	}
}

class ExtendSelectionToIncomingEdgesAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public ExtendSelectionToIncomingEdgesAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_ExtendSelectionToIncomingEdges"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_ExtendSelectionToIncomingEdges_descr"));
		//putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, FrameActionManager.MASK | KeyEvent.SHIFT_MASK));
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		GUIManager.getInstance().getGraphGUI(graph).getSelection().extendSelectionToIncomingEdges();
	}
}

