package org.ginsim.gui.graph.regulatorygraph.perturbation;

import java.awt.Component;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.perturbation.ListOfPerturbations;
import org.ginsim.gui.guihelpers.GUIHelper;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(GUIHelper.class)
@GUIFor(ListOfPerturbations.class)
public class PerturbationGUIHelper implements GUIHelper<ListOfPerturbations> {

	public static Component getPerturbationPanel(ListOfPerturbations perturbations, StackDialog dialog) {
        return new PerturbationPanel(perturbations);
	}
	
	@Override
	public Component getPanel(ListOfPerturbations o, StackDialog dialog) {
		return getPerturbationPanel(o, dialog);
	}

	@Override
	public Component getSelectionPanel(ListOfPerturbations o, StackDialog dialog) {
		return null;
	}

	@Override
	public boolean supports(Object o) {
		return o instanceof ListOfPerturbations;
	}

}
