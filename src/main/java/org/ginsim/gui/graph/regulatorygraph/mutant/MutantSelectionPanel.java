package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.util.List;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationHolder;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphGUIListener;
import org.ginsim.gui.utils.data.ListSelectionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;


public class MutantSelectionPanel extends ListSelectionPanel<Perturbation> implements GraphGUIListener<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> {
	private static final long serialVersionUID = 1213902700181873169L;
	
	private final RegulatoryGraph graph;
	private final PerturbationHolder holder;
	
	public MutantSelectionPanel(StackDialog dialog, RegulatoryGraph graph, PerturbationHolder holder) {
		this.graph = graph;
		this.holder = holder;
		
        GraphGUI<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> gui = GUIManager.getInstance().getGraphGUI(graph);
        gui.addGraphGUIListener(this);

        initialize(dialog, (List<Perturbation>) ObjectAssociationManager.getInstance().getObject( graph, PerturbationManager.KEY, false),
				Translator.getString("STR_mutants"), true);
	}
	
	@Override
	public void configure() {
		// FIXME: use gui helper to get a configuration panel!
        // dialog.addTempPanel(getMutantConfigPanel(graph));

		System.err.println("TODO: new perturbation config panel!!");
	}

	public Perturbation getSelected() {
		return holder.getPerturbation();
	}
	
	public void setSelected(Perturbation p) {
		holder.setPerturbation(p);
	}

	@Override
	public void graphSelectionChanged(
			GraphGUI<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> gui) {
	}

	@Override
	public void graphGUIClosed(
			GraphGUI<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> gui) {
	}

	@Override
	public void graphChanged(RegulatoryGraph g, GraphChangeType type, Object data) {
		if (type == GraphChangeType.ASSOCIATEDADDED || type == GraphChangeType.ASSOCIATEDUPDATED) {
			refresh();
		}
	}
}
