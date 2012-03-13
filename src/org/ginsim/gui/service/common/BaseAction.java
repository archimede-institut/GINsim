package org.ginsim.gui.service.common;

import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.ginsim.common.utils.Translator;
import org.ginsim.gui.resource.ImageLoader;
import org.ginsim.gui.service.ServiceGUI;


/**
 * this class is used for all actions, it will lookup for an internatiolized string
 * for the name and the tooltip
 */
public abstract class BaseAction extends AbstractAction {
	private static final long	serialVersionUID	= 6937495427962796865L;

	public static ImageIcon getIcon(String name) {
		if (name != null && !"".equals(name)) {
			URL url = ImageLoader.getImagePath(name);
			if (url != null) {
				return new ImageIcon(url);
			}
		}
		return null;
	}

	private int weight = -1;

	/**
     * 
     * @param name name of the action (menu entry)
     * @param icon for menu and toolbar
     * @param tooltip
     * @param accelerator (ie keyboard bytecut)
	 * @param serviceGUI 
     */
	public BaseAction(String name,
			   ImageIcon icon,
			   String tooltip,
			   KeyStroke accelerator,
			   ServiceGUI serviceGUI) {
		this(name, icon, tooltip, accelerator, null, serviceGUI);
	}

	public BaseAction(String name,
			   String icon,
			   String tooltip,
			   KeyStroke accelerator,
			   Integer mnemonic,
			   ServiceGUI serviceGUI) {
		this(name, getIcon(icon), tooltip, accelerator, mnemonic, serviceGUI);
	}
	
	public BaseAction(String name,
			   ImageIcon icon,
			   String tooltip,
			   KeyStroke accelerator,
			   Integer mnemonic,
			   ServiceGUI serviceGUI) {
		
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
		
		if (serviceGUI != null) {
			weight = serviceGUI.getWeight();
		}
	 }
	
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
