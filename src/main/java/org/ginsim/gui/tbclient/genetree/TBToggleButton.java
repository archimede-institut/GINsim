package org.ginsim.gui.tbclient.genetree;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

/**
 * Convenience class to remove the dependency on the tbrowser jar.
 * Not sure if it should do more things, but it is probably only used in
 * the experimental function editor.
 * 
 * @author Aurelien Naldi
 */
public class TBToggleButton extends JToggleButton {

	public TBToggleButton(ImageIcon imageIcon) {
		setIcon(imageIcon);
	}

	public void setInsets(int i, int j, int k, int l) {
		// FIXME
	}

}
