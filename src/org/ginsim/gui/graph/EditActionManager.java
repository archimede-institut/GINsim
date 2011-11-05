package org.ginsim.gui.graph;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;


/**
 * Manage the available interactive edition action.
 * 
 * This class provides the GUI logics to select the current edit mode.
 * It always provides a "Select or move" action and will add a "delete" action as well
 * as "add edge" and "add node" actions depending on the selected graph.
 * 
 * @author Aurelien Naldi
 */
public class EditActionManager {

	private static final EditAction EDIT_MODE = new EditAction(EditMode.EDIT, "E");
	private static final EditAction EDGEPOINT_MODE = new EditAction(EditMode.EDGEPOINT, "iP");
	
	private EditAction selectedAction = EDGEPOINT_MODE;
	private final List<EditAction> actions;
	private boolean locked = false;
	
	// hack: remember the edit buttons to make sure to update them
	Map<EditAction, EditActionSelectButton> m_editButtons;
	
	/**
	 * Create an action manager with a list of specific edit actions
	 * 
	 * @param actions available actions or null
	 */
	public EditActionManager(List<EditAction> actions) {
		this.actions = actions;
		if (actions == null) {
			System.out.println("| No edit actions..");
		} else {
			for (EditAction action: actions) {
				System.out.println("| --> "+action);
			}
		}
	}
	
	/**
	 * @return the currently selected edit action
	 */
	public EditAction getSelectedAction() {
		return selectedAction;
	}

	/**
	 * Change the selected edit action.
	 * If the provided action was already selected, it will be locked.
	 * 
	 * @param action
	 */
	public void setSelectedAction(EditAction action) {
		if ( this.selectedAction == action) {
			locked = true;
		} else {
			selectedAction = action;
			locked = false;
		}
		
		// refresh buttons
		if (m_editButtons != null) {
			for (EditActionSelectButton b: m_editButtons.values()) {
				b.setSelectedAction(selectedAction, locked);
			}
		}
	}
	
	/**
	 * Warn the manager that an action was performed.
	 * Depending on the lock state, the selected action will be reverted to the default edit mode.
	 * 
	 * @param action the action that was performed. It should be the same as the selected one
	 */
	public void actionPerformed(EditAction action) {
		if (action != selectedAction) {
			System.err.println("Did we just perform a non-selected edit action?");
			return;
		}
		if (!locked) {
			setSelectedAction(EDIT_MODE);
		}
	}

	/**
	 * Fill the toolbar with buttons matching the available edit actions.
	 * 
	 * @param toolbar
	 */
	public void addEditButtons(JToolBar toolbar) {
		m_editButtons = new HashMap<EditAction, EditActionSelectButton>();

		toolbar.add(getButton(EDIT_MODE));
		if (actions != null) {
			for (EditAction action: actions) {
				toolbar.add(getButton(action));
			}
		}
		toolbar.add(getButton(EDGEPOINT_MODE));
		
		// set the initial status
		setSelectedAction(EDIT_MODE);
	}
	
	private EditActionSelectButton getButton(EditAction action) {
		ChangeEditModeAction chAction = new ChangeEditModeAction(this, action);
		EditActionSelectButton b = new EditActionSelectButton(chAction);
		m_editButtons.put(action, b);
		return b;
	}
}

@SuppressWarnings("serial")
class ChangeEditModeAction extends AbstractAction {

	private final EditActionManager manager;
	protected final EditAction action;
	
	protected ChangeEditModeAction( EditActionManager manager, EditAction action) {
		super(action.getName());
		this.manager = manager;
		this.action = action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		manager.setSelectedAction(action);
	}
}

class EditActionSelectButton extends JToggleButton {
	private final EditAction action;
	
	protected EditActionSelectButton(ChangeEditModeAction action) {
		super(action);
		this.action = action.action;
	}
	
	protected void setSelectedAction(EditAction action, boolean locked) {
		if (this.action == action) {
			setSelected(true);
			if (locked) {
				// TODO: change color for locked buttons
			}
		} else {
			setSelected(false);
		}
	}
}
