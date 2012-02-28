package org.ginsim.gui.shell;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.ginsim.common.OptionStore;
import org.ginsim.gui.shell.callbacks.FileCallBack;
import org.ginsim.gui.utils.widgets.Frame;

public class StartupDialog extends Frame {

	public StartupDialog() {
		super("startup", 300, 200);
		JPanel panel = new JPanel(new GridBagLayout());
		setContentPane(panel);
		
		GridBagConstraints cst = new GridBagConstraints();
		add(new JButton(FileCallBack.getActionNew()), cst);
		cst = new GridBagConstraints();
		cst.gridx = 1;
		add(new JButton(FileCallBack.getActionOpen()), cst);
		
		int gridy = 1;
		cst.gridx = 0;
		for (Action action: FileCallBack.getActionsRecent()) {
			cst = new GridBagConstraints();
			cst.gridy = gridy++;
			add(new JButton(action), cst);
		}
		
		setVisible(true);
	}

	@Override
	public void close() {
		System.out.println("Closing the startup dialog");
		OptionStore.saveOptions();
		System.exit(0);
	}

}

class WrappedAction extends AbstractAction {
	private final Action action;
	private final StartupDialog dialog;
	
	public WrappedAction(Action action, StartupDialog dialog) {
		this.action = action;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dialog.dispose();
		action.actionPerformed(e);
	}
}
