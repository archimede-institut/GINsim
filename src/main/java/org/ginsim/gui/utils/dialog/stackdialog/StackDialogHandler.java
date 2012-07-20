package org.ginsim.gui.utils.dialog.stackdialog;

import java.awt.Component;

import org.ginsim.commongui.dialog.DefaultDialogSize;


public interface StackDialogHandler {

	void setStackDialog( HandledStackDialog dialog);
	
	Component getMainComponent();
	
	boolean run();
	
	void close();

	DefaultDialogSize getDefaultSize();
}
