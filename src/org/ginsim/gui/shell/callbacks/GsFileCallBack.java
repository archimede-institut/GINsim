package org.ginsim.gui.shell.callbacks;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.ginsim.graph.Graph;
import org.ginsim.gui.shell.FrameActions;

/**
 * Callbacks for the "File" menu
 */
public class GsFileCallBack {
	
	private static JMenu recentMenu = new JMenu("Recent files");
	private static List<String> recentFiles = new ArrayList<String>();
	
	protected static void addRecentFile(String path) {
		// add this file on top of the list
		recentFiles.remove(path);
		recentFiles.add(0, path);
		
		// trim the list size
		while (recentFiles.size() > 10) {
			recentFiles.remove(10);
		}
		
		// rebuild the recent menu
		recentMenu.removeAll();
		for (String recent: recentFiles) {
			// TODO: real recent action with better title
			recentMenu.add(recent);
		}
	}
	
	/**
	 * Create and populate a File menu
	 * 
	 * @param recentMenu
	 * @param importmenu
	 * @param exportmenu
	 * @return
	 */
	public static JMenu getFileMenu(Graph<?, ?> g, JMenu importMenu, JMenu exportMenu) {
		JMenu menu = new JMenu("File");
		
		menu.add(new NewAction());
		menu.add(new OpenAction());
		menu.add(recentMenu);
		menu.add(importMenu);

		menu.add(new JSeparator());
		
		menu.add(new SaveAction(g));
		menu.add(exportMenu);

		menu.add(new JSeparator());
		
		menu.add(new CloseAction(g));
		menu.add(new QuitAction());
		
		return menu;
	}
}

class NewAction extends AbstractAction {
	public NewAction() {
		super("New");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, FrameActions.MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// FIXME: new action
		System.out.println("TODO: create a new graph");
	}
}

class OpenAction extends AbstractAction {
	
	public OpenAction() {
		super("Open");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, FrameActions.MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// FIXME: open action
		System.out.println("TODO: select and open a new graph");
		GsFileCallBack.addRecentFile("test recent");
	}
}

class SaveAction extends AbstractAction {
	
	private final Graph<?,?> g;
	
	public SaveAction(Graph<?,?> g) {
		super("Save");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, FrameActions.MASK));
		this.g = g;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// FIXME: save action
		System.out.println("TODO: save graph "+g);
		GsFileCallBack.addRecentFile("saved recent");
	}
}

class CloseAction extends AbstractAction {
	
	private final Graph<?,?> g;
	
	public CloseAction(Graph<?,?> g) {
		super("Close");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, FrameActions.MASK));
		this.g = g;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// FIXME: close action
		System.out.println("TODO: close graph "+g);
	}
}

class QuitAction extends AbstractAction {
	
	public QuitAction() {
		super("Quit");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, FrameActions.MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// FIXME: quit action
		System.out.println("TODO: quit GINsim");
	}
}
