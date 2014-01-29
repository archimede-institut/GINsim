package org.ginsim.gui.shell;

import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import org.ginsim.Launcher;
import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.Graph;
import org.ginsim.gui.graph.EditActionManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.ServiceGUIManager;
import org.ginsim.gui.shell.actions.BaseAction;
import org.ginsim.gui.shell.actions.ExportAction;
import org.ginsim.gui.shell.actions.GenericGraphAction;
import org.ginsim.gui.shell.actions.ImportAction;
import org.ginsim.gui.shell.actions.LayoutAction;
import org.ginsim.gui.shell.actions.ToolAction;
import org.ginsim.gui.shell.callbacks.EditCallBack;
import org.ginsim.gui.shell.callbacks.FileCallBack;
import org.ginsim.gui.shell.callbacks.SelectionCallBack;

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
		JMenu importMenu = new JMenu( Txt.t("STR_Import"));
		JMenu exportMenu = new JMenu( Txt.t("STR_Export"));
		JMenu layoutMenu = new JMenu( Txt.t("STR_Layout"));
		JMenu graphMenu = new JMenu( Txt.t("STR_Graph"));
		JMenu toolsMenu = new JMenu( Txt.t("STR_Tools"));
		JMenu toolkitsMenu = null;
		if (Launcher.developer_mode) {
			 toolkitsMenu = new JMenu( Txt.t("STR_Toolkits"));
		}

		EditCallBack.addEditEntries(graphMenu, gui);
		graphMenu.addSeparator();
		SelectionCallBack.fillMenu(graphMenu, graph); //Add all the simple selection actions
		
		int nextSeparator = 0;
		
		for (Action action: actions) {
			if (nextSeparator < ServiceGUI.separators.length && ((BaseAction)action).getWeight() >= ServiceGUI.separators[nextSeparator]) {
				if (action instanceof GenericGraphAction) {
					graphMenu.add( new JSeparator());
				} else if (action instanceof ToolAction) {
					toolsMenu.add( new JSeparator());
				} else if (action instanceof ExportAction) {
					exportMenu.add( new JSeparator());
				}
				nextSeparator++;
			}
			if (action instanceof ImportAction) {
				importMenu.add( action);
			} else if (action instanceof ExportAction) {
				exportMenu.add( action);
			} else if (action instanceof LayoutAction) {
				layoutMenu.add( action);
			} else if (action instanceof GenericGraphAction) {
				graphMenu.add( action);
			} else if (action instanceof ToolAction) {
				toolsMenu.add( action);
			} else {
				toolkitsMenu.add( action);
			}
		}

		// fill the menu bar
		menubar.removeAll();
		toolbar.removeAll();
		menubar.add( FileCallBack.getFileMenu(graph, importMenu, exportMenu));
		// TODO: the file menu should add some stuff to the toolbar as well

		EditActionManager editManager = gui.getEditActionManager();
		editManager.addEditButtons( toolbar);
		
		menubar.add( gui.getViewMenu( layoutMenu));
		
		if (graphMenu.getItemCount() > 0) {
			menubar.add( graphMenu);
		}
		if (toolsMenu.getItemCount() > 0) {
			menubar.add( toolsMenu);
		}
		if (toolkitsMenu != null && toolkitsMenu.getItemCount() > 0) {
			menubar.add( toolkitsMenu);
		}
		
	}

}
