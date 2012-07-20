package org.ginsim.commongui.dialog;

/**
 * Simple structure to store and remember the default size of a dialog.
 * 
 * @author Aurelien Naldi
 */
public class DefaultDialogSize {

	public final String ID;
	public final int width;
	public final int height;
	
	public DefaultDialogSize(String ID, int w, int h) {
		this.ID = ID;
		this.width = w;
		this.height = h;
	}
}
