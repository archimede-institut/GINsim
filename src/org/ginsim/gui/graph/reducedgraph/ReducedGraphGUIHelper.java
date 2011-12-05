package org.ginsim.gui.graph.reducedgraph;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.reducedgraph.NodeReducedData;
import org.ginsim.graph.reducedgraph.ReducedGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.regulatorygraph.RegulatoryGraphOptionPanel;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.service.tool.connectivity.ReducedParameterPanel;
import org.ginsim.gui.utils.widgets.Frame;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( GraphGUIHelper.class)
public class ReducedGraphGUIHelper implements GraphGUIHelper<ReducedGraph, NodeReducedData, Edge<NodeReducedData>> {

	
	/**
	 * Provide the file filter to apply to a file chooser
	 * 
	 * @return the file filter to apply to a file chooser
	 */
	@Override
	public FileFilter getFileFilter() {
		
		return null;
	}

	/**
	 * Create a panel containing the option for graph saving 
	 * 
	 * @param graph the edited graph
	 */
	@Override
	public JPanel getSaveOptionPanel(ReducedGraph graph) {
		
		Frame graph_frame = GUIManager.getInstance().getFrame( graph);
		
		Object[] t_mode = { Translator.getString("STR_saveNone"), 
                    Translator.getString("STR_savePosition"), 
                    Translator.getString("STR_saveComplet") };
        JPanel optionPanel = new RegulatoryGraphOptionPanel(t_mode, graph_frame != null ? 2 : 0);
        
		return optionPanel;
	}

	@Override
	public GUIEditor<ReducedGraph> getMainEditionPanel(ReducedGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEditingTabLabel(ReducedGraph graph) {
		return "SCC";
	}

	@Override
	public GUIEditor<NodeReducedData> getNodeEditionPanel(ReducedGraph graph) {
		return new ReducedParameterPanel(graph);
	}

	@Override
	public GUIEditor<Edge<NodeReducedData>> getEdgeEditionPanel(
			ReducedGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getInfoPanel(ReducedGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<ReducedGraph> getGraphClass() {
		
		return ReducedGraph.class;
	}

	@Override
	public List<EditAction> getEditActions(ReducedGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}
}
