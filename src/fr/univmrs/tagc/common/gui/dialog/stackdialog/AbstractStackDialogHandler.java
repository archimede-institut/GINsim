package fr.univmrs.tagc.common.gui.dialog.stackdialog;

import java.awt.Component;

import javax.swing.JPanel;

abstract public class AbstractStackDialogHandler extends JPanel implements StackDialogHandler {

	protected StackDialog stack;

	abstract protected void init();
	
	@Override
	public void setStackDialog(StackDialog stack) {
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

}
