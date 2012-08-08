package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationManager;
import org.ginsim.core.graph.regulatorygraph.perturbation.RegulatoryMutantDef;
import org.ginsim.core.graph.regulatorygraph.perturbation.RegulatoryMutants;
import org.ginsim.gui.guihelpers.GUIHelper;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.utils.data.GenericListPanel;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(GUIHelper.class)
@GUIFor(RegulatoryMutants.class)
public class PerturbationGUIHelper implements GUIHelper {

	@Override
	public Component getPanel(Object o) {
		if ( o == null || !(o instanceof RegulatoryMutants)) {
			throw new RuntimeException("Can only edit mutants");
		}
		
        RegulatoryMutants mutants = (RegulatoryMutants) o;
        MutantPanel mpanel = new MutantPanel();
        Map m = new HashMap();
        m.put(RegulatoryMutantDef.class, mpanel);
        GenericListPanel lp = new GenericListPanel(m, "mutantList");
        lp.setList(mutants);
        mpanel.setEditedObject(mutants, lp);
    	return lp;

	}

	@Override
	public Component getSelectionPanel(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supports(Object o) {
		return o instanceof RegulatoryMutants;
	}

}
