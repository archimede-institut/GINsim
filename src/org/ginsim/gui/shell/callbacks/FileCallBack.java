package org.ginsim.gui.shell.callbacks;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.ginsim.common.OptionStore;
import org.ginsim.common.utils.Translator;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.shell.FrameActionManager;
import org.ginsim.gui.shell.GsFileFilter;


/**
 * Callbacks for the "File" menu
 */
public class FileCallBack {
	
	private static JMenu recentMenu = new JMenu( Translator.getString( "STR_RecentFiles"));
	
	/**
	 * Create and populate a File menu
	 * 
	 * @param recentMenu
	 * @param importmenu
	 * @param exportmenu
	 * @return
	 */
	public static JMenu getFileMenu(Graph<?, ?> g, JMenu importMenu, JMenu exportMenu) {
		JMenu menu = new JMenu( Translator.getString( "STR_File"));
		
		menu.add(new NewAction());
		menu.add(new OpenAction());
		menu.add(recentMenu);
		FileCallBack.updateRecentFiles();
		menu.add(importMenu);

		menu.add(new JSeparator());
		
		menu.add(new SaveAction(g));
		menu.add(new SaveAsAction(g));
		menu.add(exportMenu);

		menu.add(new JSeparator());
		
		menu.add(new CloseAction(g));
		menu.add(new QuitAction());
		
		return menu;
	}
	
	public static void updateRecentFiles(){
		
		 List<String> recentFiles = OptionStore.getRecentFiles();
		// rebuild the recent menu
		recentMenu.removeAll();
		for (String recent: recentFiles) {
			// TODO: real recent action with better title
			recentMenu.add( new OpenAction(recent));
		}
	}
	

}

class NewAction extends AbstractAction {
	public NewAction() {
		super( Translator.getString( "STR_New"));
		putValue( SHORT_DESCRIPTION, Translator.getString( "STR_New_descr"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, FrameActionManager.MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			GUIManager.getInstance().newFrame();
		} catch (Exception e) {
			LogManager.error(" Error creating the new frame");
			LogManager.error( e);
		}
	}
}

class OpenAction extends AbstractAction {
	
	private final String filename;
	
	public OpenAction() {
		super( Translator.getString(  "STR_Open"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, FrameActionManager.MASK));
		this.filename = null;
	}
	
	public OpenAction(String filename) {
		super(filename);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, FrameActionManager.MASK));
		this.filename = filename;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String path = filename;
		if (path == null) {
			GsFileFilter ffilter = new GsFileFilter();
			ffilter.setExtensionList(new String[] { "ginml", "zginml" }, "GINsim files");
			path = FileSelectionHelper.selectOpenFilename( null, ffilter);
		}
		if (path != null) {
			try {
				Graph g = GraphManager.getInstance().open(path);
				OptionStore.addRecentFile(path);
				FileCallBack.updateRecentFiles();
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
		super( Translator.getString( "STR_Save"));
		putValue( SHORT_DESCRIPTION,  Translator.getString( "STR_Save_descr"));
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


class SaveAsAction extends AbstractAction {
	
	private final Graph<?,?> g;
	
	public SaveAsAction(Graph<?,?> g) {
		super( Translator.getString( "STR_SaveAs"));
		putValue( SHORT_DESCRIPTION,  Translator.getString( "STR_SaveAs_descr"));
		//putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, FrameActionManager.MASK));
		this.g = g;
	}
	
	public boolean saveAs() {
		return GUIManager.getInstance().getGraphGUI(g).saveAs();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		saveAs();
	}
}

class CloseAction extends AbstractAction {
	
	private final Graph<?,?> g;
	
	public CloseAction(Graph<?,?> g) {
		super( Translator.getString( "STR_Close"));
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
