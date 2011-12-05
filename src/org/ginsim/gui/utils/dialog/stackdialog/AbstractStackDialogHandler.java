package org.ginsim.gui.utils.dialog.stackdialog;

import java.awt.Component;

import javax.swing.JPanel;

import org.ginsim.gui.utils.dialog.DefaultDialogSize;


abstract public class AbstractStackDialogHandler extends JPanel implements StackDialogHandler {

	protected HandledStackDialog stack;

	abstract protected void init();
	
	@Override
	public void setStackDialog(HandledStackDialog stack) {
		this.stack = stack;
		init();
	}

	@Override
	public Component getMainComponent() {
		return this;
	}

	@Override
	public void close() {
	}

	@Override
	public DefaultDialogSize getDefaultSize() {
		return new DefaultDialogSize(getClass().getName(), 400, 400);
	}

}
