package org.ginsim.gui.shell;

import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.ginsim.graph.Graph;
import org.ginsim.gui.graph.EditActionManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.service.GsServiceGUIManager;
import org.ginsim.gui.service.common.GsExportAction;
import org.ginsim.gui.service.common.GsImportAction;
import org.ginsim.gui.service.common.GsLayoutAction;
import org.ginsim.gui.shell.callbacks.GsFileCallBack;
import org.ginsim.gui.shell.callbacks.GsHelpCallBack;

public class MainFrameActionManager implements FrameActionManager {

	private void fillMenu(JMenu menu, List<Action> actions) {
		for (Action action: actions) {
			menu.add(action);
		}
	}
	
	@Override
	public void buildActions(GraphGUI<?,?,?> gui, JMenuBar menubar, JToolBar toolbar) {

		Graph<?,?> graph = gui.getGraph();
		
		// get Service-related actions
		List<Action> actions = GsServiceGUIManager.getManager().getAvailableActions(graph);

		// add them to the right menus
		JMenu importMenu = new JMenu( "Import");
		JMenu exportMenu = new JMenu( "Export");
		JMenu layoutMenu = new JMenu( "Layout");
		JMenu actionMenu = new JMenu( "Actions");
		for (Action action: actions) {
			System.out.println( "should add action: "+ action);
			if (action instanceof GsImportAction) {
				importMenu.add( action);
			}
			else if (action instanceof GsExportAction) {
				exportMenu.add( action);
			}
			else if (action instanceof GsLayoutAction) {
				layoutMenu.add( action);
			}
			else {
				actionMenu.add( action);
			}
		}

		// fill the menu bar
		menubar.removeAll();
		toolbar.removeAll();
		menubar.add( GsFileCallBack.getFileMenu(graph, importMenu, exportMenu));
		// TODO: the file menu should add some stuff to the toolbar as well
		
		EditActionManager editManager = gui.getEditActionManager();
		editManager.addEditButtons( toolbar);
		
		JMenu menu = new JMenu( "Edit");
		// TODO: edit menu
		menubar.add( menu);
		
		menubar.add( gui.getViewMenu( layoutMenu));
		
		menubar.add( actionMenu);
		
		menu = new JMenu("Help");
		fillMenu(menu, GsHelpCallBack.getActions());
		menubar.add(menu);
	}

}
