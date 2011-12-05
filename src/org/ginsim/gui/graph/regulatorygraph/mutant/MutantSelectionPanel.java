package org.ginsim.gui.graph.regulatorygraph.mutant;

import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.utils.data.GenericListSelectionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.utils.data.GenericList;
import org.ginsim.utils.data.ObjectStore;


public class MutantSelectionPanel extends GenericListSelectionPanel {
	private static final long serialVersionUID = 1213902700181873169L;
	
	RegulatoryGraph graph;
	
	public MutantSelectionPanel(StackDialog dialog, RegulatoryGraph graph, ObjectStore store) {
		super(dialog, (GenericList) ObjectAssociationManager.getInstance().getObject( graph, MutantListManager.key, false),
				Translator.getString("STR_mutants"), true, Translator.getString("STR_configure_mutants"));
		this.graph = graph;
		setStore(store);
	}
	
	protected void configure() {
        dialog.addTempPanel(RegulatoryMutants.getMutantConfigPanel(graph));
	}
}
