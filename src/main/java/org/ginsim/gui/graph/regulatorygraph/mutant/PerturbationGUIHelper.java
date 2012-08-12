package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.awt.Component;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.perturbation.RegulatoryMutants;
import org.ginsim.gui.guihelpers.GUIHelper;
import org.ginsim.gui.service.common.GUIFor;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(GUIHelper.class)
@GUIFor(RegulatoryMutants.class)
public class PerturbationGUIHelper implements GUIHelper<RegulatoryMutants> {

	public static Component getPerturbationPanel(RegulatoryMutants o) {
        RegulatoryMutants mutants = (RegulatoryMutants) o;
        MutantPanel panel = new MutantPanel(o);
        return panel;
	}
	
	@Override
	public Component getPanel(RegulatoryMutants o) {
		return getPerturbationPanel(o);
	}

	@Override
	public Component getSelectionPanel(RegulatoryMutants o) {
		return null;
	}

	@Override
	public boolean supports(Object o) {
		return o instanceof RegulatoryMutants;
	}

}
