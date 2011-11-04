package org.ginsim.gui.shell;

import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.ginsim.graph.Graph;
import org.ginsim.gui.graph.EditMode;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.service.GsExportAction;
import org.ginsim.gui.service.GsImportAction;
import org.ginsim.gui.service.GsLayoutAction;
import org.ginsim.gui.shell.callbacks.GsFileCallBack;
import org.ginsim.gui.shell.callbacks.GsHelpCallBack;
import org.ginsim.gui.service.GsServiceGUIManager;

public class MainFrameActionManager implements FrameActions {

	private void fillMenu(JMenu menu, List<Action> actions) {
		for (Action action: actions) {
			menu.add(action);
		}
	}
	
	@Override
	public void setCurrentMode(EditAction mode, boolean lock) {
		// TODO Auto-generated method stub

	}

	@Override
	public EditAction getCurrentEditMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditMode getCurrentGroup() {
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
	public void setGraphGUI(GraphGUI<?,?,?> gui, JMenuBar menubar, JToolBar toolbar) {

		Graph<?, ?> graph = gui.getGraph();
		
		// TODO: deal with the tool bar, view and edit menus
		
		// get Service-related actions
		List<Action> actions = GsServiceGUIManager.getManager().getAvailableActions(graph);

		// add them to the right menus
		JMenu importMenu = new JMenu("Import");
		JMenu exportMenu = new JMenu("Export");
		JMenu layoutMenu = new JMenu("Layout");
		JMenu actionMenu = new JMenu("Actions");
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

		// fill the menu bar
		menubar.removeAll();
		menubar.add(GsFileCallBack.getFileMenu(graph, importMenu, exportMenu));
		
		JMenu menu = new JMenu("Edit");
		menubar.add(menu);
		
		
		menubar.add(gui.getViewMenu(layoutMenu));
		
		menubar.add(actionMenu);
		
		menu = new JMenu("Help");
		fillMenu(menu, GsHelpCallBack.getActions());
		menubar.add(menu);
	}

}
