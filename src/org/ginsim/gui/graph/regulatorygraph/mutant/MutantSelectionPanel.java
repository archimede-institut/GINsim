package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.ginsim.common.utils.Translator;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.mutant.MutantListManager;
import org.ginsim.core.graph.regulatorygraph.mutant.RegulatoryMutantDef;
import org.ginsim.core.graph.regulatorygraph.mutant.RegulatoryMutants;
import org.ginsim.core.utils.data.GenericList;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphGUIListener;
import org.ginsim.gui.utils.data.GenericListPanel;
import org.ginsim.gui.utils.data.GenericListSelectionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;


public class MutantSelectionPanel extends GenericListSelectionPanel implements GraphGUIListener<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> {
	private static final long serialVersionUID = 1213902700181873169L;
	
    /**
     * edit mutants associated with a graph
     * @param graph
     * @return a panel to configure mutants
     */
    public static JPanel getMutantConfigPanel(RegulatoryGraph graph) {
        RegulatoryMutants mutants = (RegulatoryMutants) ObjectAssociationManager.getInstance().getObject(graph, MutantListManager.KEY, true);
        MutantPanel mpanel = new MutantPanel();
        Map m = new HashMap();
        m.put(RegulatoryMutantDef.class, mpanel);
        GenericListPanel lp = new GenericListPanel(m, "mutantList");
        lp.setList(mutants);
        mpanel.setEditedObject(mutants, lp, graph);
    	return lp;
    }


	
	RegulatoryGraph graph;
	
	public MutantSelectionPanel(StackDialog dialog, RegulatoryGraph graph, ObjectStore store) {
		super(dialog, (GenericList) ObjectAssociationManager.getInstance().getObject( graph, MutantListManager.KEY, false),
				Translator.getString("STR_mutants"), true, Translator.getString("STR_configure_mutants"));
		this.graph = graph;
		setStore(store);
        GraphGUI<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> gui = GUIManager.getInstance().getGraphGUI(graph);
        gui.addGraphGUIListener(this);
	}
	
	protected void configure() {
        dialog.addTempPanel(getMutantConfigPanel(graph));
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
