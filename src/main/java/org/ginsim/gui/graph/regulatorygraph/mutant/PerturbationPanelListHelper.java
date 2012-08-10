package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.ginsim.gui.utils.data.ListPanelHelper;

public class PerturbationPanelListHelper extends ListPanelHelper {

	public Object[] getCreateTypes() {
		
		return PerturbationType.values();
	}

	public int create(Object arg) {
		if (arg instanceof PerturbationType) {
			PerturbationType type = (PerturbationType)arg;
			PerturbationType newPerturbation = null;
			switch (type) {
			case FIXED:
				
				break;
			case RANGE:
				
				break;

			}
			
		}
		
		return -1;
	}

}

enum PerturbationType {
	FIXED, RANGE;
}

class AddPerturbationAction extends AbstractAction {

	public AddPerturbationAction(PerturbationType type) {
		super(type.toString());
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}
	
}
