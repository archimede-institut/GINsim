package fr.univmrs.tagc.GINsim.regulatoryGraph.mutant;

import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.tagc.common.datastore.GenericList;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.datastore.gui.GenericListSelectionPanel;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.StackDialog;
import fr.univmrs.tagc.common.managerresources.Translator;

public class MutantSelectionPanel extends GenericListSelectionPanel {
	private static final long serialVersionUID = 1213902700181873169L;
	
	GsRegulatoryGraph graph;
	
	public MutantSelectionPanel(StackDialog dialog, GsRegulatoryGraph graph, ObjectStore store) {
		super(dialog, (GenericList) ObjectAssociationManager.getInstance().getObject( graph, GsMutantListManager.key, false),
				Translator.getString("STR_mutants"), true, Translator.getString("STR_configure_mutants"));
		this.graph = graph;
		setStore(store);
	}
	
	protected void configure() {
        dialog.addTempPanel(GsRegulatoryMutants.getMutantConfigPanel(graph));
	}
}
