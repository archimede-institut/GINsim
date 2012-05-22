package org.ginsim.gui.graph.hierarchicaltransitiongraph;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.hierachicaltransitiongraph.DecisionOnEdge;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.regulatorygraph.RegulatoryGraphOptionPanel;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.widgets.Frame;
import org.mangosdk.spi.ProviderFor;


/**
 * @author spinelli
 *
 */

@ProviderFor( GraphGUIHelper.class)
public class HierarchicalTransitionGraphGUIHelper implements GraphGUIHelper<HierarchicalTransitionGraph, HierarchicalNode, DecisionOnEdge> {

	
	/**
	 * Provide the file filter to apply to a file chooser
	 * 
	 * @return the file filter to apply to a file chooser
	 */
	@Override
	public FileFilter getFileFilter() {
		
	    GsFileFilter ffilter = new GsFileFilter();
	    ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");

		return ffilter;
	}
	
	/**
	 * Create a panel containing the option for graph saving 
	 * 
	 * @param graph the edited graph
	 */
	@Override
	public JPanel getSaveOptionPanel( HierarchicalTransitionGraph graph) {
		
		Frame graph_frame = GUIManager.getInstance().getFrame( graph);
		
		Object[] t_mode = { Translator.getString("STR_saveNone"),
							Translator.getString("STR_savePosition"),
							Translator.getString("STR_saveComplet") };
		JPanel optionPanel = new RegulatoryGraphOptionPanel(t_mode, graph_frame != null ? 2 : 0);
		
		return optionPanel ;
	}


	@Override
	public GUIEditor<HierarchicalTransitionGraph> getMainEditionPanel( HierarchicalTransitionGraph graph) {
		return null;
	}

	@Override
	public String getEditingTabLabel(HierarchicalTransitionGraph graph) {
		return "HTG";
	}

	@Override
	public GUIEditor<HierarchicalNode> getNodeEditionPanel( HierarchicalTransitionGraph graph) {
		return new HierarchicalNodeParameterPanel(graph);
	}

	@Override
	public GUIEditor<DecisionOnEdge> getEdgeEditionPanel( HierarchicalTransitionGraph graph) {
		return new HierarchicalEdgeParameterPanel(graph);
	}

	@Override
	public JPanel getInfoPanel(HierarchicalTransitionGraph graph) {
		// TODO: info panel for HTG?
		return null;
	}

	@Override
	public Class<HierarchicalTransitionGraph> getGraphClass() {
		
		return HierarchicalTransitionGraph.class;
	}

	@Override
	public List<EditAction> getEditActions(HierarchicalTransitionGraph graph) {
		return null;
	}
}
