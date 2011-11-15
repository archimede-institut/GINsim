package org.ginsim.gui.graphhelper.tree;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.tree.GsTree;
import org.ginsim.graph.tree.GsTreeNode;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graphhelper.GraphGUIHelper;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;

@ProviderFor( GraphGUIHelper.class)
public class TreeGUIHelper implements GraphGUIHelper<GsTree, GsTreeNode, Edge<GsTreeNode>> {


	public FileFilter getFileFilter() {
		
		GsFileFilter ffilter = new GsFileFilter();
	    //ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
		return ffilter;
	}

	@Override
	public GUIEditor<GsTree> getMainEditionPanel(GsTree graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEditingTabLabel(GsTree graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<GsTreeNode> getNodeEditionPanel(GsTree graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<Edge<GsTreeNode>> getEdgeEditionPanel(GsTree graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getInfoPanel(GsTree graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<GsTree> getGraphClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EditAction> getEditActions(GsTree graph) {
		// TODO Auto-generated method stub
		return null;
	}
}
