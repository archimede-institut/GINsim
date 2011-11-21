package org.ginsim.gui.graph.dynamicalhierarchicalgraph;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraph;
import org.ginsim.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.regulatorygraph.GsRegulatoryGraphOptionPanel;
import org.ginsim.gui.service.tool.dynamicalhierarchicalsimplifier.DynamicalHierarchicalParameterPanel;
import org.ginsim.gui.shell.GsFileFilter;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.Frame;

@ProviderFor( GraphGUIHelper.class)
public class DynamicalHierarchicalGraphGUIHelper implements
		GraphGUIHelper<DynamicalHierarchicalGraph, DynamicalHierarchicalNode, Edge<DynamicalHierarchicalNode>> {

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
	public JPanel getSaveOptionPanel(DynamicalHierarchicalGraph graph) {
		
		Frame graph_frame = GUIManager.getInstance().getFrame( graph);
		
        Object[] t_mode = { Translator.getString("STR_saveNone"),
                    		Translator.getString("STR_savePosition"),
                    		Translator.getString("STR_saveComplet") };
        JPanel optionPanel = new GsRegulatoryGraphOptionPanel(t_mode, graph_frame != null ? 2 : 0);
		
		return optionPanel ;
	}


	@Override
	public GUIEditor<DynamicalHierarchicalGraph> getMainEditionPanel(
			DynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEditingTabLabel(DynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<DynamicalHierarchicalNode> getNodeEditionPanel(
			DynamicalHierarchicalGraph graph) {
        return new DynamicalHierarchicalParameterPanel(graph);
	}

	@Override
	public GUIEditor<Edge<DynamicalHierarchicalNode>> getEdgeEditionPanel(
			DynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getInfoPanel(DynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<DynamicalHierarchicalGraph> getGraphClass() {
		
		return DynamicalHierarchicalGraph.class;
	}

	@Override
	public List<EditAction> getEditActions(DynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}
}
