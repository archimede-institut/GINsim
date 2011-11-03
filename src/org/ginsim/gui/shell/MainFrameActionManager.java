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
	private final JMenu recentMenu = new JMenu("Recent");
	

	public MainFrameActionManager(JMenuBar menubar, JToolBar toolbar) {
		this.toolbar = toolbar;
		
		// fill the menu
		JMenu menu = new JMenu("File");
		menubar.add(menu);
		menu = new JMenu("Edit");
		menubar.add(menu);
	}
	
	@Override
	public void updateRecentMenu() {
		// TODO Auto-generated method stub
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
		
		// TODO: clean dynamic menus
		
		// TODO: reset edit actions
		
		List<Action> actions = GsGUIServiceFactory.getFactory().getAvailableActions(graph);
		for (Action action: actions) {
			System.out.println("should add action: "+ action);
			if (action instanceof GsImportAction) {
				// TODO: add to import menu
			}
			else if (action instanceof GsExportAction) {
				// TODO: add to export menu
			}
			else if (action instanceof GsLayoutAction) {
				// TODO: add to layout menu
			}
			else {
				// TODO: add to action menu
			}
		}
	}

}
