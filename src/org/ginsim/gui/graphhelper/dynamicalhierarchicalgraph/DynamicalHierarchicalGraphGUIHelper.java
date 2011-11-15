package org.ginsim.gui.graphhelper.dynamicalhierarchicalgraph;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.dynamicalhierarchicalgraph.GsDynamicalHierarchicalGraph;
import org.ginsim.graph.dynamicalhierarchicalgraph.GsDynamicalHierarchicalNode;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graphhelper.GraphGUIHelper;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;

@ProviderFor( GraphGUIHelper.class)
public class DynamicalHierarchicalGraphGUIHelper implements
		GraphGUIHelper<GsDynamicalHierarchicalGraph, GsDynamicalHierarchicalNode, Edge<GsDynamicalHierarchicalNode>> {

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

	@Override
	public GUIEditor<GsDynamicalHierarchicalGraph> getMainEditionPanel(
			GsDynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEditingTabLabel(GsDynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<GsDynamicalHierarchicalNode> getNodeEditionPanel(
			GsDynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<Edge<GsDynamicalHierarchicalNode>> getEdgeEditionPanel(
			GsDynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getInfoPanel(GsDynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<GsDynamicalHierarchicalGraph> getGraphClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EditAction> getEditActions(GsDynamicalHierarchicalGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}
}
