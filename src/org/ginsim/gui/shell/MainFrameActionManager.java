package org.ginsim.gui.shell;

import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.graph.EditActionManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.service.ServiceGUIManager;
import org.ginsim.gui.service.common.GenericGraphAction;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.ImportAction;
import org.ginsim.gui.service.common.LayoutAction;
import org.ginsim.gui.shell.callbacks.FileCallBack;
import org.ginsim.gui.shell.callbacks.HelpCallBack;

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
		List<Action> actions = ServiceGUIManager.getManager().getAvailableActions(graph);

		// add them to the right menus
		JMenu importMenu = new JMenu( "Import");
		JMenu exportMenu = new JMenu( "Export");
		JMenu layoutMenu = new JMenu( "Layout");
		JMenu graphMenu = new JMenu( "Graph");
		JMenu toolsMenu = new JMenu( "Tools");
		for (Action action: actions) {
			if (action instanceof ImportAction) {
				importMenu.add( action);
			}
			else if (action instanceof ExportAction) {
				exportMenu.add( action);
			}
			else if (action instanceof LayoutAction) {
				layoutMenu.add( action);
			}
			else if (action instanceof GenericGraphAction) {
				graphMenu.add( action);
			}
			else {
				toolsMenu.add( action);
			}
		}

		// fill the menu bar
		menubar.removeAll();
		toolbar.removeAll();
		menubar.add( FileCallBack.getFileMenu(graph, importMenu, exportMenu));
		// TODO: the file menu should add some stuff to the toolbar as well
		
		EditActionManager editManager = gui.getEditActionManager();
		editManager.addEditButtons( toolbar);
		
		JMenu menu = new JMenu( "Edit");
		// TODO: edit menu
		// menubar.add( menu);
		
		menubar.add( gui.getViewMenu( layoutMenu));
		
		if (graphMenu.getItemCount() > 0) {
			menubar.add( graphMenu);
		}
		if (toolsMenu.getItemCount() > 0) {
			menubar.add( toolsMenu);
		}
		
		menu = new JMenu("Help");
		fillMenu(menu, HelpCallBack.getActions());
		menubar.add(menu);
	}

}
