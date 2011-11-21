package org.ginsim.gui.graph.tree;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.tree.Tree;
import org.ginsim.graph.tree.GsTreeNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.regulatorygraph.GsRegulatoryGraphOptionPanel;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.Frame;

@ProviderFor( GraphGUIHelper.class)
public class TreeGUIHelper implements GraphGUIHelper<Tree, GsTreeNode, Edge<GsTreeNode>> {

	
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
        JPanel optionPanel = new GsRegulatoryGraphOptionPanel(t_mode, graph_frame != null ? 2 : 0);
		
		return optionPanel ;
	}
	
	protected FileFilter doGetFileFilter() {
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
		return ffilter;
	}

	@Override
	public GUIEditor<Tree> getMainEditionPanel(Tree graph) {
		// TODO update this edition panel
		// return new TreeActionPanel(graph, parser);
		return null;
	}

	@Override
	public String getEditingTabLabel(Tree graph) {
		return "Tree";
	}

	@Override
	public GUIEditor<GsTreeNode> getNodeEditionPanel(Tree graph) {
		return null;
	}

	@Override
	public GUIEditor<Edge<GsTreeNode>> getEdgeEditionPanel(Tree graph) {
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
}
