package org.ginsim.gui.shell.callbacks;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.ginsim.exception.GsException;
import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.FileSelectionHelper;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.shell.FrameActionManager;

import fr.univmrs.tagc.common.Debugger;

/**
 * Callbacks for the "File" menu
 */
public class GsFileCallBack {
	
	private static JMenu recentMenu = new JMenu("Recent files");
	private static List<String> recentFiles = new ArrayList<String>();
	
	public static void addRecentFile(String path) {
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

	/**
	 * Get the list of recent files.
	 * Note: the recent menu will be managed by this class directly,
	 * this method should only be used to save the list of recent files before closing GINsim
	 * 
	 * @return the list of recent files
	 */
	public static List<String> getRecentFiles() {
		return recentFiles;
	}
}

class NewAction extends AbstractAction {
	public NewAction() {
		super("New");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, FrameActionManager.MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			GUIManager.getInstance().newFrame();
		} catch (Exception e) {
			Debugger.log("error creating the new frame");
		}
	}
}

class OpenAction extends AbstractAction {
	
	public OpenAction() {
		super("Open");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, FrameActionManager.MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// FIXME: open action
		System.out.println("TODO: select and open a new graph");
		String path = FileSelectionHelper.selectOpenFilename(null);
		if (path != null) {
			try {
				Graph g = GraphManager.getInstance().open(path);
				GsFileCallBack.addRecentFile(path);
				GUIManager.getInstance().newFrame(g);
			} catch (GsException e) {
				e.printStackTrace();
			}
		}
	}
}

class SaveAction extends AbstractAction {
	
	private final Graph<?,?> g;
	
	public SaveAction(Graph<?,?> g) {
		super("Save");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, FrameActionManager.MASK));
		this.g = g;
	}
	
	public boolean save() {
		return GUIManager.getInstance().getGraphGUI(g).save();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		save();
	}
}

class CloseAction extends AbstractAction {
	
	private final Graph<?,?> g;
	
	public CloseAction(Graph<?,?> g) {
		super("Close");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, FrameActionManager.MASK));
		this.g = g;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		GUIManager.getInstance().close(g);
	}
}

class QuitAction extends AbstractAction {
	
	public QuitAction() {
		super("Quit");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, FrameActionManager.MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		GUIManager.getInstance().quit();
	}
}
