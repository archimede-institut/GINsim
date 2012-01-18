package org.ginsim.gui.shell.callbacks;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.ginsim.common.utils.Translator;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.shell.FrameActionManager;
import org.ginsim.gui.shell.SearchFrame;


/**
 * Callbacks for the "File" menu
 */
public class EditCallBack {
	
	private static JMenu historyMenu = new JMenu( Translator.getString( "STR_RecentFiles"));
	
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

		menu.add(new UndoAction(graph));
		menu.add(new RedoAction(graph));

		menu.add(new JSeparator());
		
		// TODO: the edit menu should add some stuff from the toolbar as well

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
		//TODO Recfactoring action
		//FIXME : find where to copy
		LogManager.error("Unimplemented menu : paste");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		copy();
	}
}

class PasteAction extends AbstractAction {
	
	private final Graph<?,?> graph;
	
	public PasteAction(Graph<?,?> graph) {
		super( Translator.getString( "STR_Paste"));
		putValue(SHORT_DESCRIPTION,  Translator.getString( "STR_Paste_descr"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, FrameActionManager.MASK));
		this.graph = graph;
	}
	
	public void paste() {
		//TODO Recfactoring action
		//FIXME : find where to paste
		LogManager.error("Unimplemented menu : paste");
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

