package org.ginsim.gui.graphhelper.regulatorygraph;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.ginsim.gui.graph.AddEdgeAction;
import org.ginsim.gui.graph.AddVertexAction;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graphhelper.GraphGUIHelper;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.RegulatoryEdgeEditor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.RegulatoryGraphEditor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.RegulatoryVertexEditor;

/**
 * GUI helper for the regulatory graph.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor( GraphGUIHelper.class)
public class RegulatoryGraphGUIHelper implements GraphGUIHelper<GsRegulatoryGraph, GsRegulatoryVertex, GsRegulatoryMultiEdge> {

	@Override
	public Class getGraphClass() {
		return GsRegulatoryGraph.class;
	}

	@Override
	public List<EditAction> getEditActions(GsRegulatoryGraph graph) {
		List<EditAction> actions = new ArrayList<EditAction>();
		GsVertexAttributesReader reader = graph.getVertexAttributeReader();
		actions.add(new AddRegulatoryVertexAction(graph, "+ TV", reader));
		actions.add(new AddTestEdgeAction(graph, "E +", 1));
		actions.add(new AddTestEdgeAction(graph, "E -", -1));
		actions.add(new AddTestEdgeAction(graph, "E ?", 0));
		return actions;
	}

	@Override
	public GUIEditor<GsRegulatoryGraph> getMainEditionPanel( GsRegulatoryGraph graph) {
		RegulatoryGraphEditor editor = new RegulatoryGraphEditor();
		editor.setEditedObject(graph);
		return editor;
	}

	@Override
	public String getEditingTabLabel( GsRegulatoryGraph graph) {
		return "STR_modelAttribute";
	}

	@Override
	public GUIEditor<GsRegulatoryVertex> getNodeEditionPanel( GsRegulatoryGraph graph) {
		return new RegulatoryVertexEditor(graph);
	}

	@Override
	public GUIEditor<GsRegulatoryMultiEdge> getEdgeEditionPanel( GsRegulatoryGraph graph) {
		return new RegulatoryEdgeEditor(graph);
	}

	@Override
	public JPanel getInfoPanel(GsRegulatoryGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// FIXME: option panel?
//	private JPanel getOptionPanel() {
//		GsRegulatoryGraphOptionPanel optionPanel;
//        Object[] t_mode = { Translator.getString("STR_saveNone"),
//                Translator.getString("STR_savePosition"),
//                Translator.getString("STR_saveComplet") };
//		optionPanel = new GsRegulatoryGraphOptionPanel(t_mode, this.saveMode);
//		return optionPanel;
//	}

}

class AddRegulatoryVertexAction extends AddVertexAction<GsRegulatoryVertex> {

	private final GsRegulatoryGraph graph;
	public AddRegulatoryVertexAction(GsRegulatoryGraph graph, String name, GsVertexAttributesReader reader) {
		super(name, reader);
		this.graph = graph;
	}

	@Override
	protected GsRegulatoryVertex getNewVertex() {
		return graph.addVertex();
	}
}

class AddTestEdgeAction extends AddEdgeAction<GsRegulatoryVertex, GsRegulatoryMultiEdge> {

	private final GsRegulatoryGraph graph;
	private final int sign;
	
	public AddTestEdgeAction(GsRegulatoryGraph graph, String name, int sign) {
		super(name);
		this.graph = graph;
		this.sign = sign;
	}

	@Override
	protected GsRegulatoryMultiEdge getNewEdge(GsRegulatoryVertex source, GsRegulatoryVertex target) {
		return graph.addEdge(source, target, sign);
	}
}