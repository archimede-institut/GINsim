package org.ginsim.gui.shell.callbacks;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.ginsim.graph.Graph;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.graph.testGraph.TestGraphImpl;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.shell.FrameActionManager;

import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.managerresources.Translator;

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
}

class NewAction extends AbstractAction {
	public NewAction() {
		super("New");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, FrameActionManager.MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// FIXME: change the default graph type
		TestGraph lrg = new TestGraphImpl();
		try {
			GUIManager.getInstance().newFrame( lrg);
		} catch (Exception e) {
			System.err.println("error creating the new frame");
		}
		System.out.println("TODO: change new graph type");
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
		GsFileCallBack.addRecentFile("test recent");
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
