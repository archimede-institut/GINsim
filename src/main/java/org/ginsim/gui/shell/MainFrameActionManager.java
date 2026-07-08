package org.ginsim.gui.shell;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.graph.EditActionManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.GSServiceGUIManager;
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

	private static final String IMPORT = Txt.t("STR_Import");
	private static final String EXPORT = Txt.t("STR_Export");
	private static final String LAYOUT = Txt.t("STR_Layout");
	private static final String GRAPH  = Txt.t("STR_Graph");
	private static final String TOOLS  = Txt.t("STR_Tools");
	private static final String TOOLKIT = Txt.t("STR_Toolkits");

	Map<String, JMenu> menus = new HashMap<String, JMenu>();
	Map<String, JMenu> submenus = new HashMap<String, JMenu>();

	private void fillMenu(JMenu menu, List<Action> actions) {
		for (Action action: actions) {
			menu.add(action);
		}
	}
	
	@Override
	public void buildActions(GraphGUI<?,?,?> gui, JMenuBar menubar, JToolBar toolbar) {

		Graph<?,?> graph = gui.getGraph();
		
		// get Service-related actions
		List<Action> actions = GSServiceGUIManager.getAvailableActions(graph);

		EditCallBack.addEditEntries(getMenu(GRAPH), gui);
		getMenu(GRAPH).addSeparator();
		SelectionCallBack.fillMenu(getMenu(GRAPH), graph); //Add all the simple selection actions
		
		int nextSeparator = 0;
		
		for (Action action: actions) {
			if (nextSeparator < ServiceGUI.separators.length && ((BaseAction)action).getWeight() >= ServiceGUI.separators[nextSeparator]) {
				if (action instanceof GenericGraphAction) {
					getMenu(GRAPH).add( new JSeparator());
				} else if (action instanceof ToolAction) {
					getMenu(TOOLS).add( new JSeparator());
				} else if (action instanceof ExportAction) {
					getMenu(EXPORT).add( new JSeparator());
				}
				nextSeparator++;
			}
			if (action instanceof ImportAction) {
				addAction(IMPORT, action);
			} else if (action instanceof ExportAction) {
				addAction(EXPORT, action);
			} else if (action instanceof LayoutAction) {
				addAction(LAYOUT, action);
			} else if (action instanceof GenericGraphAction) {
				addAction(GRAPH, action);
			} else if (action instanceof ToolAction) {
				addAction(TOOLS, action);
			} else {
				addAction(TOOLKIT, action);
			}
		}

		// fill the menu bar
		menubar.removeAll();
		toolbar.removeAll();
        menubar.add(FileCallBack.getMainMenu());
		menubar.add( FileCallBack.getFileMenu(graph, getMenu(IMPORT), getMenu(EXPORT)));
		// TODO: the file menu should add some stuff to the toolbar as well

		EditActionManager editManager = gui.getEditActionManager();
		editManager.addEditButtons( toolbar);
		
		menubar.add( gui.getViewMenu( getMenu(LAYOUT)));
		
		menubar.add( getMenu(GRAPH));
		menubar.add( getMenu(TOOLS));
		if (menus.containsKey(TOOLKIT)) {
			menubar.add( getMenu(TOOLKIT));
		}
		
	}
	
	private JMenu getMenu(String key) {
		if (!menus.containsKey(key)) {
			menus.put(key, new JMenu(key));
		}
		
		return menus.get(key);
	}
	
	private JMenu getSubmenu(String key, String name) {
		String s = key+"/"+name;
		if (!submenus.containsKey(s)) {
			JMenu parent = getMenu(key);
			JMenu newmenu = new JMenu(name);
			parent.add(newmenu);
			submenus.put(s, newmenu);
		}
		return submenus.get(s);
	}

	private void addAction(String s_menu, Action action) {
		JMenu menu = getMenu(s_menu);
		String category = (String)action.getValue("category");
		if (category != null) {
			menu = getSubmenu(s_menu, category);
		}
		menu.add(action);
	}
}
