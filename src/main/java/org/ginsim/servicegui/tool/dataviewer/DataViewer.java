package org.ginsim.servicegui.tool.dataviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ginsim.commongui.dialog.DefaultDialogSize;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.GraphAssociatedObjectManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.gui.guihelpers.GUIHelper;
import org.ginsim.gui.guihelpers.GUIHelperManager;
import org.ginsim.gui.utils.dialog.stackdialog.HandledStackDialog;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialogHandler;

/**
 * Let the user manage the objects associated to a graph.
 * 
 * This builds a dialog where we can see created objects, delete them or create new ones.
 * 
 * @author Aurelien Naldi
 */
public class DataViewer extends JScrollPane implements StackDialogHandler {
	private static final long serialVersionUID = -7540546129482675257L;
	
	private static final DefaultDialogSize DEFAULTSIZE = new DefaultDialogSize("dataviewer", 400, 400);
	
	private final Graph<?, ?> graph;
	private final JPanel panel = new JPanel(new GridBagLayout());
	private final ObjectAssociationManager objManager = ObjectAssociationManager.getInstance();
	private final Map<String, Object> deleted = new HashMap<String, Object>();

	private HandledStackDialog dialog;
	
	public DataViewer(Graph<?, ?> graph) {
		super();
		this.graph = graph;

		setViewportView(panel);
		
		refresh();
	}
	
	private void refresh() {
		panel.setVisible(false);
		panel.removeAll();
		
		List<String> haveObject = new ArrayList<String>();
		List<String> noObject = new ArrayList<String>();
		
		for (GraphAssociatedObjectManager manager: ObjectAssociationManager.getInstance().getObjectManagerList()) {
			System.out.println(manager);
		}
		
		// collect existing and possible objects
		Collection<Entry<Class, List<GraphAssociatedObjectManager>>> classes = objManager.getManagedClasses();
		for (Entry<Class, List<GraphAssociatedObjectManager>> e: classes) {
			Class cl = e.getKey();
			if (cl.isInstance(graph)) {
				List<GraphAssociatedObjectManager> managers = e.getValue();
				for (GraphAssociatedObjectManager manager: managers) {
					String key = manager.getObjectName();
					Object o = objManager.getObject(graph, key, false);
					if (o == null) {
						noObject.add(manager.getObjectName());
					} else {
						haveObject.add(manager.getObjectName());
					}
				}
				
			}
		}
		
		// build the GUI
		int row = -1;
		GridBagConstraints cst;
		if (haveObject.size() > 0) {
			row++;
			cst = new GridBagConstraints();
			cst.gridx = 0;
			cst.gridy = row;
			cst.gridwidth = 4;
			cst.anchor = GridBagConstraints.WEST;
			JLabel title = new JLabel("Have data:");
			title.setForeground(Color.GREEN);
			panel.add(title, cst);
			for (String key: haveObject) {
				row++;
				cst = new GridBagConstraints();
				cst.gridx = 0;
				cst.gridy = row;
				cst.anchor = GridBagConstraints.WEST;
				panel.add(new JLabel("   "+key), cst);
				
				cst = new GridBagConstraints();
				cst.gridx = 1;
				cst.gridy = row;
				cst.anchor = GridBagConstraints.WEST;
				// lookup a GUIHelper
				Object o = objManager.getObject(graph, key, false);
				GUIHelper helper = GUIHelperManager.getInstance().getHelper(o);
				panel.add(new JButton(new ViewAction(this, key, helper)), cst);

				cst = new GridBagConstraints();
				cst.gridx = 2;
				cst.gridy = row;
				cst.anchor = GridBagConstraints.WEST;
				panel.add(new JButton(new DeleteAction(this, key)), cst);
			}
		}
		
		if (noObject.size() > 0) {
			row++;
			cst = new GridBagConstraints();
			cst.gridx = 0;
			cst.gridy = row;
			cst.gridwidth = 4;
			cst.anchor = GridBagConstraints.WEST;
			JLabel title = new JLabel("Available:");
			title.setForeground(Color.BLUE);
			panel.add(title, cst);
			for (String key: noObject) {
				row++;
				cst = new GridBagConstraints();
				cst.gridx = 0;
				cst.gridy = row;
				cst.anchor = GridBagConstraints.WEST;
				panel.add(new JLabel("   "+key), cst);

				cst = new GridBagConstraints();
				cst.gridx = 1;
				cst.gridy = row;
				cst.anchor = GridBagConstraints.WEST;
				panel.add(new JButton(new CreateAction(this, key)), cst);
			}
		}
		
		if (deleted.size() > 0) {
			row++;
			cst = new GridBagConstraints();
			cst.gridx = 0;
			cst.gridy = row;
			cst.gridwidth = 4;
			cst.anchor = GridBagConstraints.WEST;
			JLabel title = new JLabel("Deleted (will be lost when closing this dialog):");
			title.setForeground(Color.RED);
			panel.add(title, cst);
			for (String key: deleted.keySet()) {
				row++;
				cst = new GridBagConstraints();
				cst.gridx = 0;
				cst.gridy = row;
				cst.anchor = GridBagConstraints.WEST;
				panel.add(new JLabel("   "+key), cst);

				cst = new GridBagConstraints();
				cst.gridx = 1;
				cst.gridy = row;
				cst.anchor = GridBagConstraints.WEST;
				panel.add(new JButton(new RestoreAction(this, key)), cst);
			}
		}
		
		panel.setVisible(true);
	}
	
	@Override
	public void close() {
	}

	@Override
	public void setStackDialog(HandledStackDialog dialog) {
		this.dialog = dialog;
		dialog.setTitle("Associated data");
	}

	@Override
	public Component getMainComponent() {
		return this;
	}

	@Override
	public boolean run() {
		return false;
	}

	@Override
	public DefaultDialogSize getDefaultSize() {
		return DEFAULTSIZE;
	}

	public void view(String key, GUIHelper helper) {
		dialog.addSecondaryPanel(helper.getPanel(objManager.getObject(graph, key, false)), "HELPER");
		dialog.setVisiblePanel("HELPER");
	}

	public void delete(String key) {
		Object o = objManager.getObject(graph, key, false);
		if (o != null) {
			deleted.put(key, o);
		}
		objManager.removeObject(graph, key);
		refresh();
	}

	public void create(String key) {
		Object o = objManager.getObject(graph, key, true);
		refresh();
		GUIHelper helper = GUIHelperManager.getInstance().getHelper(o);
		if (helper != null) {
			view(key, helper);
		}
	}
	
	public void restore(String key) {
		Object restored = deleted.get(key);
		objManager.addObject(graph, key, restored);
		refresh();
	}
}

class ViewAction extends AbstractAction {

	private final DataViewer viewer;
	private final String key;
	private final GUIHelper helper;

	
	public ViewAction(DataViewer viewer, String key, GUIHelper helper) {
		super("view");
		this.viewer = viewer;
		this.key = key;
		this.helper = helper;
		setEnabled(helper != null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		viewer.view(key, helper);
	}
}

class DeleteAction extends AbstractAction {

	private final DataViewer viewer;
	private final String key;
	
	public DeleteAction(DataViewer viewer, String key) {
		super("Delete");
		this.viewer = viewer;
		this.key = key;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		viewer.delete(key);
	}
}

class CreateAction extends AbstractAction {

	private final DataViewer viewer;
	private final String key;
	
	public CreateAction(DataViewer viewer, String key) {
		super("Create");
		this.viewer = viewer;
		this.key = key;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		viewer.create(key);
	}
}

class RestoreAction extends AbstractAction {

	private final DataViewer viewer;
	private final String key;
	
	public RestoreAction(DataViewer viewer, String key) {
		super("Restore");
		this.viewer = viewer;
		this.key = key;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		viewer.restore(key);
	}
}
