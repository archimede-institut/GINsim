package org.ginsim.gui.utils.data.models;

import javax.swing.AbstractSpinnerModel;


/**
 * model for a max spinbutton, depends on a GsMinMaxSpinModel
 */
abstract public class SpinModel extends AbstractSpinnerModel {

	protected String name = "";
	
	abstract public void setEditedObject(Object rawValue);

	public String getName() {
		return name;
	}
}
