package org.ginsim.gui.graphhelper.hierarchicaltransitiongraph;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.hierachicaltransitiongraph.GsHierarchicalNode;
import org.ginsim.graph.hierachicaltransitiongraph.GsHierarchicalTransitionGraph;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graphhelper.GraphGUIHelper;
import org.ginsim.gui.service.tools.decisionanalysis.GsDecisionOnEdge;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;

/**
 * @author spinelli
 *
 */

@ProviderFor( GraphGUIHelper.class)
public class HierarchicalTransitionGraphGUIHelper implements GraphGUIHelper<GsHierarchicalTransitionGraph, GsHierarchicalNode, GsDecisionOnEdge> {

	
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
	public GUIEditor<GsHierarchicalTransitionGraph> getMainEditionPanel( GsHierarchicalTransitionGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEditingTabLabel(GsHierarchicalTransitionGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<GsHierarchicalNode> getNodeEditionPanel( GsHierarchicalTransitionGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<GsDecisionOnEdge> getEdgeEditionPanel( GsHierarchicalTransitionGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getInfoPanel(GsHierarchicalTransitionGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<GsHierarchicalTransitionGraph> getGraphClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EditAction> getEditActions(GsHierarchicalTransitionGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}
}
