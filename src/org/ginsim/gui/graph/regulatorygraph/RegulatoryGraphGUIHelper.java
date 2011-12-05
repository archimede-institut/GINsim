package org.ginsim.gui.graph.regulatorygraph;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.gui.graph.AddEdgeAction;
import org.ginsim.gui.graph.AddNodeAction;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.shell.GsFileFilter;
import org.mangosdk.spi.ProviderFor;


/**
 * GUI helper for the regulatory graph.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor( GraphGUIHelper.class)
public class RegulatoryGraphGUIHelper implements GraphGUIHelper<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> {

	@Override
	public Class getGraphClass() {
		return RegulatoryGraph.class;
	}

	@Override
	public List<EditAction> getEditActions(RegulatoryGraph graph) {
		List<EditAction> actions = new ArrayList<EditAction>();
		NodeAttributesReader reader = graph.getNodeAttributeReader();
		actions.add(new AddRegulatoryNodeAction(graph, "Add components", reader));
		actions.add(new AddRegulatoryEdgeAction(graph, "Add positive regulations", 1));
		actions.add(new AddRegulatoryEdgeAction(graph, "Add negative regulations", -1));
		actions.add(new AddRegulatoryEdgeAction(graph, "Add unknown regulations", 0));
		return actions;
	}

	@Override
	public GUIEditor<RegulatoryGraph> getMainEditionPanel( RegulatoryGraph graph) {
		RegulatoryGraphEditor editor = new RegulatoryGraphEditor();
		editor.setEditedObject(graph);
		return editor;
	}

	@Override
	public String getEditingTabLabel( RegulatoryGraph graph) {
		return "STR_modelAttribute";
	}

	@Override
	public GUIEditor<RegulatoryNode> getNodeEditionPanel( RegulatoryGraph graph) {
		return new RegulatoryNodeEditor(graph);
	}

	@Override
	public GUIEditor<RegulatoryMultiEdge> getEdgeEditionPanel( RegulatoryGraph graph) {
		return new RegulatoryEdgeEditor(graph);
	}

	@Override
	public JPanel getInfoPanel(RegulatoryGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public FileFilter getFileFilter() {
	    
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");

		return ffilter;
	}

	@Override
	public JPanel getSaveOptionPanel(RegulatoryGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// FIXME: option panel?
//	private JPanel getOptionPanel() {
//		RegulatoryGraphOptionPanel optionPanel;
//        Object[] t_mode = { Translator.getString("STR_saveNone"),
//                Translator.getString("STR_savePosition"),
//                Translator.getString("STR_saveComplet") };
//		optionPanel = new RegulatoryGraphOptionPanel(t_mode, this.saveMode);
//		return optionPanel;
//	}

}

class AddRegulatoryNodeAction extends AddNodeAction<RegulatoryNode> {

	private final RegulatoryGraph graph;
	public AddRegulatoryNodeAction(RegulatoryGraph graph, String name, NodeAttributesReader reader) {
		super(name, reader, "insertsquare.gif");
		this.graph = graph;
	}

	@Override
	protected RegulatoryNode getNewNode() {
		return graph.addNode();
	}
}

class AddRegulatoryEdgeAction extends AddEdgeAction<RegulatoryNode, RegulatoryMultiEdge> {

	private final RegulatoryGraph graph;
	private final int sign;

	private static String getIcon(int sign) {
		if (sign < 0) {
			return "insertnegativeedge.gif";
		}
		if (sign > 0) {
			return "insertpositiveedge.gif";
		}
		return "insertunknownedge.gif";
	}
	
	public AddRegulatoryEdgeAction(RegulatoryGraph graph, String name, int sign) {
		super(name, getIcon(sign));
		this.graph = graph;
		this.sign = sign;
	}

	@Override
	protected RegulatoryMultiEdge getNewEdge(RegulatoryNode source, RegulatoryNode target) {
		return graph.addEdge(source, target, sign);
	}
}