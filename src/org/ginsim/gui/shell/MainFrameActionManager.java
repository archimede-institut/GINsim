package org.ginsim.gui.shell;

import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.ginsim.graph.EditGroup;
import org.ginsim.graph.EditMode;
import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GsExportAction;
import org.ginsim.gui.service.GsGUIServiceFactory;
import org.ginsim.gui.service.GsImportAction;
import org.ginsim.gui.service.GsLayoutAction;

public class MainFrameActionManager implements FrameActions {

	private final JToolBar toolbar;

	private final static JMenu recentMenu = new JMenu("Recent");
	
	private final JMenu importMenu = new JMenu("Import");
	private final JMenu exportMenu = new JMenu("Export");
	private final JMenu layoutMenu = new JMenu("Layout");
	private final JMenu actionMenu = new JMenu("Actions");
	

	public MainFrameActionManager(JMenuBar menubar, JToolBar toolbar) {
		this.toolbar = toolbar;
		
		// fill the menu
		JMenu menu = new JMenu("File");
		menu.add(recentMenu);
		menu.add(importMenu);
		menu.add(exportMenu);
		menubar.add(menu);
		menu = new JMenu("Edit");
		menubar.add(menu);
		menu = new JMenu("View");
		menu.add(layoutMenu);
		menubar.add(menu);
		menubar.add(actionMenu);
		menu = new JMenu("Help");
		menubar.add(menu);
	}
	
	@Override
	public void setCurrentMode(EditMode mode, boolean lock) {
		// TODO Auto-generated method stub

	}

	@Override
	public EditMode getCurrentEditMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditGroup getCurrentGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeModeIfUnlocked() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldAutoAddNewElements() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGraph(Graph<?, ?> graph) {
		
		// TODO: deal with the view and edit menus
		importMenu.removeAll();
		exportMenu.removeAll();
		layoutMenu.removeAll();
		actionMenu.removeAll();
		
		// TODO: reset edit actions
		
		List<Action> actions = GsGUIServiceFactory.getFactory().getAvailableActions(graph);
		for (Action action: actions) {
			System.out.println("should add action: "+ action);
			if (action instanceof GsImportAction) {
				importMenu.add(action);
			}
			else if (action instanceof GsExportAction) {
				exportMenu.add(action);
			}
			else if (action instanceof GsLayoutAction) {
				layoutMenu.add(action);
			}
			else {
				actionMenu.add(action);
			}
		}
	}

}
