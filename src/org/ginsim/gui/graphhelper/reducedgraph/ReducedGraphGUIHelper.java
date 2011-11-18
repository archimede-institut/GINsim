package org.ginsim.gui.graphhelper.reducedgraph;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.reducedgraph.GsNodeReducedData;
import org.ginsim.graph.reducedgraph.GsReducedGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graphhelper.GraphGUIHelper;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphOptionPanel;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.Frame;

@ProviderFor( GraphGUIHelper.class)
public class ReducedGraphGUIHelper implements GraphGUIHelper<GsReducedGraph, GsNodeReducedData, Edge<GsNodeReducedData>> {

	
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
	public JPanel getSaveOptionPanel(GsReducedGraph graph) {
		
		Frame graph_frame = GUIManager.getInstance().getFrame( graph);
		
		Object[] t_mode = { Translator.getString("STR_saveNone"), 
                    Translator.getString("STR_savePosition"), 
                    Translator.getString("STR_saveComplet") };
        JPanel optionPanel = new GsRegulatoryGraphOptionPanel(t_mode, graph_frame != null ? 2 : 0);
        
		return optionPanel;
	}

	@Override
	public GUIEditor<GsReducedGraph> getMainEditionPanel(GsReducedGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEditingTabLabel(GsReducedGraph graph) {
		return "SCC";
	}

	@Override
	public GUIEditor<GsNodeReducedData> getNodeEditionPanel(GsReducedGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<Edge<GsNodeReducedData>> getEdgeEditionPanel(
			GsReducedGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getInfoPanel(GsReducedGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<GsReducedGraph> getGraphClass() {
		
		return GsReducedGraph.class;
	}

	@Override
	public List<EditAction> getEditActions(GsReducedGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}
}
