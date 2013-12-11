package org.ginsim.servicegui.tool.modelsimplifier;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphGUIListener;
import org.ginsim.gui.utils.data.ListSelectionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfig;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfigList;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfigManager;
import org.ginsim.service.tool.modelsimplifier.ReductionHolder;

import java.awt.*;
import java.util.List;


public class ReductionSelectionPanel extends ListSelectionPanel<ModelSimplifierConfig> implements GraphGUIListener<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> {
	private static final long serialVersionUID = 1213902700181873169L;

	private final RegulatoryGraph graph;
    private final ReductionHolder holder;

	public ReductionSelectionPanel(StackDialog dialog, RegulatoryGraph graph, ReductionHolder holder) {
		super(dialog, "Select a reduction");
		
		this.graph = graph;
		this.holder = holder;
		
        GraphGUI<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> gui = GUIManager.getInstance().getGraphGUI(graph);
        gui.addGraphGUIListener(this);

        initialize("Select a reduction", true);
	}
	
	@Override
	public void configure() {
		Component panel = ReductionGUIHelper.getReductionPanel(getPerturbationsObject(true));
        dialog.addTempPanel(panel);
	}

	public ModelSimplifierConfig getSelected() {
		return holder.getReduction();
	}
	
	public void setSelected(ModelSimplifierConfig r) {
		holder.setReduction(r);
	}

	@Override
	public void graphSelectionChanged(GraphGUI<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> gui) {
	}

	@Override
	public void graphGUIClosed(GraphGUI<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> gui) {
	}

	@Override
	public void graphChanged(RegulatoryGraph g, GraphChangeType type, Object data) {
		if (type == GraphChangeType.ASSOCIATEDADDED || type == GraphChangeType.ASSOCIATEDUPDATED) {
			refresh();
		}
	}

	private ModelSimplifierConfigList getPerturbationsObject(boolean force) {
		return (ModelSimplifierConfigList) ObjectAssociationManager.getInstance().getObject( graph, ModelSimplifierConfigManager.KEY, force);
	}
	
	@Override
	protected List<ModelSimplifierConfig> getList() {
        return getPerturbationsObject(false);
	}
}
