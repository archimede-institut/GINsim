package fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.datastore.GenericList;
import fr.univmrs.tagc.datastore.ObjectStore;
import fr.univmrs.tagc.datastore.gui.GenericListSelectionPanel;
import fr.univmrs.tagc.widgets.StackDialog;

public class MutantSelectionPanel extends GenericListSelectionPanel {
	private static final long serialVersionUID = 1213902700181873169L;
	
	GsRegulatoryGraph graph;
	
	public MutantSelectionPanel(StackDialog dialog, GsRegulatoryGraph graph, ObjectStore store) {
		super(dialog, (GenericList)graph.getObject(GsMutantListManager.key, false), Translator.getString("STR_mutants"));
		this.graph = graph;
		setStore(store);
	}
	
	protected void configure() {
        dialog.addTempPanel(GsRegulatoryMutants.getMutantConfigPanel(graph));
	}
}
