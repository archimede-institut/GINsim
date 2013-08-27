package org.ginsim.gui.graph.tree;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.tree.Tree;
import org.ginsim.core.graph.tree.TreeEdge;
import org.ginsim.core.graph.tree.TreeNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.regulatorygraph.RegulatoryGraphOptionPanel;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.widgets.Frame;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( GraphGUIHelper.class)
public class TreeGUIHelper implements GraphGUIHelper<Tree, TreeNode, TreeEdge> {

	
	/**
	 * Provide the file filter to apply to a file chooser
	 * 
	 * @return the file filter to apply to a file chooser
	 */
	@Override
	public FileFilter getFileFilter() {
		
		GsFileFilter ffilter = new GsFileFilter();
	    //ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
		return ffilter;
	}
	
	/**
	 * Create a panel containing the option for graph saving 
	 * 
	 * @param graph the edited graph
	 */
	@Override
	public JPanel getSaveOptionPanel(Tree graph) {
		
		Frame graph_frame = GUIManager.getInstance().getFrame( graph);
		
		Object[] t_mode = { Translator.getString("STR_saveNone"),
							Translator.getString("STR_savePosition"),
							Translator.getString("STR_saveComplet") };
        JPanel optionPanel = new RegulatoryGraphOptionPanel(t_mode, graph_frame != null ? 2 : 0);
		
		return optionPanel ;
	}
	
	protected FileFilter doGetFileFilter() {
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
		return ffilter;
	}

	@Override
	public GUIEditor<Tree> getMainEditionPanel(Tree tree) {
		TreeActionPanel treeActionPanel = new TreeActionPanel();
		treeActionPanel.setEditedItem(tree);
		return treeActionPanel;//tree, tree.getParser());
	}

	@Override
	public String getEditingTabLabel(Tree graph) {
		return "Tree";
	}

	@Override
	public GUIEditor<TreeNode> getNodeEditionPanel(Tree graph) {
		return null;
	}

	@Override
	public GUIEditor<TreeEdge> getEdgeEditionPanel(Tree graph) {
		return null;
	}

	@Override
	public JPanel getInfoPanel(Tree graph) {
		return null;
	}

	@Override
	public Class<Tree> getGraphClass() {
		
		return Tree.class;
	}

	@Override
	public List<EditAction> getEditActions(Tree graph) {
		return null;
	}

	@Override
	public boolean canCopyPaste(Tree graph) {
		return false;
	}
}
