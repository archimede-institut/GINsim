package org.ginsim.gui.graphhelper.dynamicgraph;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.dynamicgraph.GsDynamicGraph;
import org.ginsim.graph.dynamicgraph.GsDynamicNode;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graphhelper.GraphGUIHelper;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;

@ProviderFor( GraphGUIHelper.class)
public class DynamicGraphGUIHelper implements GraphGUIHelper<GsDynamicGraph, GsDynamicNode, Edge<GsDynamicNode>> {

	/**
	 * Provide the file filter to apply to a file chooser
	 * 
	 * @return the file filter to apply to a file chooser
	 */
	@Override
	public FileFilter getFileFilter() {
		
		GsFileFilter ffilter = new GsFileFilter();
	    ffilter.setExtensionList(new String[] {"ginml"}, "ginml files");

		return ffilter;
	}

	@Override
	public GUIEditor<GsDynamicGraph> getMainEditionPanel(GsDynamicGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEditingTabLabel(GsDynamicGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<GsDynamicNode> getNodeEditionPanel(GsDynamicGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<Edge<GsDynamicNode>> getEdgeEditionPanel(
			GsDynamicGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getInfoPanel(GsDynamicGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<GsDynamicGraph> getGraphClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EditAction> getEditActions(GsDynamicGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}
}
