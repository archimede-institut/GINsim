package fr.univmrs.ibdm.GINsim.gui;

import javax.swing.ImageIcon;

import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * all info needed to add a custom action in the menus or toolbar without any headache.
 */
public class GsPluggableActionDescriptor {

    /** name of the action (ie menu text) */
	public final String name;
	/** description of the action (ie tooltip) */
	public final String descr;
	/** icon for menu/toolbar */
	public final ImageIcon icon;
	/** the object which will actually run the action */
	public final GsActionProvider ap;
    /** access number for the action provider */
    public final int param;
    /** type of action (one of GsActionProvider;ACTION_*) */
    public final int type;
	
	/**
	 * @param name name of the action (ie menu text)
	 * @param descr description of the action (ie tooltip)
	 * @param icon icon to use
	 * @param ap the object which will actually run the action
	 * @param type the type of action to run
	 * @param param actionProvider can provide several action: this is the access number for the accurate one
	 */
	public GsPluggableActionDescriptor(String name, String descr, ImageIcon icon, GsActionProvider ap, int type, int param) {
		this.name = name;
		this.descr = descr;
		this.icon = icon;
		this.ap = ap;
        this.type = type;
		this.param = param;
	}
	
	public String toString() {
		return Translator.getString(name);
	}
}
