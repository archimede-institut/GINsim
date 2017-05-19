package org.ginsim.gui.graph.trapspacetree;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.core.graph.trapspacetree.TrapSpaceInclusion;
import org.ginsim.core.graph.trapspacetree.TrapSpaceNode;
import org.ginsim.core.graph.trapspacetree.TrapSpaceTree;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.shell.GsFileFilter;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( GraphGUIHelper.class)
public class TrapSpaceTreeGUIHelper implements GraphGUIHelper<TrapSpaceTree, TrapSpaceNode, TrapSpaceInclusion> {

	@Override
	public String getEditingTabLabel(TrapSpaceTree graph) {
		return "TST";
	}

	@Override
	public Class<TrapSpaceTree> getGraphClass() {
		return TrapSpaceTree.class;
	}

	@Override
	public boolean canCopyPaste(TrapSpaceTree graph) {
		return false;
	}

	@Override
	public GUIEditor<TrapSpaceTree> getMainEditionPanel(TrapSpaceTree tree) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<TrapSpaceNode> getNodeEditionPanel(TrapSpaceTree tree) {
		return new TrapSpaceTreeMainPanel(tree);
	}

	@Override
	public GUIEditor<TrapSpaceInclusion> getEdgeEditionPanel(TrapSpaceTree tree) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getInfoPanel(TrapSpaceTree graph) {

        // just display the number of trap spaces for now
        int n = graph.getNodeCount();
        JPanel pinfo = new JPanel();
        pinfo.add(new JLabel("nb trap spaces: "+n));
        return pinfo;
	}


	@Override
	public List<EditAction> getEditActions(TrapSpaceTree graph) {
		return null;
	}

	@Override
	public FileFilter getFileFilter() {
		GsFileFilter ffilter = new GsFileFilter();
	    ffilter.setExtensionList(new String[] {"ginml"}, "ginml files");

		return ffilter;
	}

	@Override
	public JPanel getSaveOptionPanel(TrapSpaceTree graph) {
		// TODO Auto-generated method stub
		JPanel psave = new JPanel();
		return psave;
	}

}
