package fr.univmrs.tagc.common.gui.dialog.stackdialog;

import java.awt.Component;

import fr.univmrs.tagc.common.gui.dialog.DefaultDialogSize;

public interface StackDialogHandler {

	void setStackDialog( StackDialog dialog);
	
	Component getMainComponent();
	
	void run();
	
	void close();

	DefaultDialogSize getDefaultSize();
}
