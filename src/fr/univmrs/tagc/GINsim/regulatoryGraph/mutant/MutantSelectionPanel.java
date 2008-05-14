package fr.univmrs.tagc.GINsim.regulatoryGraph.mutant;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.datastore.GenericList;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.datastore.gui.GenericListSelectionPanel;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class MutantSelectionPanel extends GenericListSelectionPanel {
	private static final long serialVersionUID = 1213902700181873169L;
	
	GsRegulatoryGraph graph;
	
	public MutantSelectionPanel(StackDialog dialog, GsRegulatoryGraph graph, ObjectStore store) {
		super(dialog, (GenericList)graph.getObject(GsMutantListManager.key, false), Translator.getString("STR_mutants"), true);
		this.graph = graph;
		setStore(store);
	}
	
	protected void configure() {
        dialog.addTempPanel(GsRegulatoryMutants.getMutantConfigPanel(graph));
	}
}
