package fr.univmrs.tagc.common.widgets;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import fr.univmrs.tagc.common.manageressources.Translator;

/**
 * this class is used for all actions, it will lookup for an internatiolized string
 * for the name and the tooltip
 */
public abstract class BaseAction extends AbstractAction {

    /**
     * 
     * @param name name of the action (menu entry)
     * @param icon for menu and toolbar
     * @param tooltip
     * @param accelerator (ie keyboard shortcut)
     */
	public BaseAction(String name,
			   ImageIcon icon,
			   String tooltip,
			   KeyStroke accelerator) {
		this(name, icon, tooltip, accelerator, null);
	}

	public BaseAction(String name,
			   ImageIcon icon,
			   String tooltip,
			   KeyStroke accelerator,
			   Integer mnemonic) {
		
		if (mnemonic != null) {
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}
		this.putValue( Action.NAME, Translator.getString(name));
		if( icon != null ) {
			this.putValue( Action.SMALL_ICON, icon );
		}
		if (tooltip != null) {
			this.putValue( Action.SHORT_DESCRIPTION, Translator.getString(tooltip) );
		}	   		 
		if (accelerator != null) {
			this.putValue( Action.ACCELERATOR_KEY, accelerator );
		}	 
	 }
}