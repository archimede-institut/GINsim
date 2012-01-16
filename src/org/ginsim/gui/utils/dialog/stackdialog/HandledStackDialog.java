package org.ginsim.gui.utils.dialog.stackdialog;

import java.awt.Frame;

import org.ginsim.common.exception.GsException;

public class HandledStackDialog extends StackDialog {

	private final StackDialogHandler handler;
	
	public HandledStackDialog(StackDialogHandler handler) {
		this(handler, null);
		handler.setStackDialog(this);
		setMainPanel(handler.getMainComponent());
	}
	
	public HandledStackDialog(StackDialogHandler handler, Frame parent) {
		super(parent, handler.getDefaultSize());
		this.handler = handler;
	}
	
	@Override
	protected void run() throws GsException {
		if (handler.run()) {
			doClose();
		}
	}

}
