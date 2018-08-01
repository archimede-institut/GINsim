package org.ginsim.gui.graph.trapspacetree;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.core.graph.trapspacetree.TrapSpaceInclusion;
import org.ginsim.core.graph.trapspacetree.TrapSpaceNode;
import org.ginsim.core.graph.trapspacetree.TrapSpaceInclusionDiagram;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.shell.GsFileFilter;
import org.kohsuke.MetaInfServices;


@MetaInfServices( GraphGUIHelper.class)
public class TrapSpaceTreeGUIHelper implements GraphGUIHelper<TrapSpaceInclusionDiagram, TrapSpaceNode, TrapSpaceInclusion> {

	@Override
	public String getEditingTabLabel(TrapSpaceInclusionDiagram graph) {
		return "TST";
	}

	@Override
	public Class<TrapSpaceInclusionDiagram> getGraphClass() {
		return TrapSpaceInclusionDiagram.class;
	}

	@Override
	public boolean canCopyPaste(TrapSpaceInclusionDiagram graph) {
		return false;
	}

	@Override
	public GUIEditor<TrapSpaceInclusionDiagram> getMainEditionPanel(TrapSpaceInclusionDiagram tree) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIEditor<TrapSpaceNode> getNodeEditionPanel(TrapSpaceInclusionDiagram tree) {
		return new TrapSpaceTreeMainPanel(tree);
	}

	@Override
	public GUIEditor<TrapSpaceInclusion> getEdgeEditionPanel(TrapSpaceInclusionDiagram tree) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getInfoPanel(TrapSpaceInclusionDiagram graph) {

        // just display the number of trap spaces for now
        int n = graph.getNodeCount();
        JPanel pinfo = new JPanel();
        pinfo.add(new JLabel("nb trap spaces: "+n));
        return pinfo;
	}


	@Override
	public List<EditAction> getEditActions(TrapSpaceInclusionDiagram graph) {
		return null;
	}

	@Override
	public FileFilter getFileFilter() {
		GsFileFilter ffilter = new GsFileFilter();
	    ffilter.setExtensionList(new String[] {"ginml"}, "ginml files");

		return ffilter;
	}

	@Override
	public JPanel getSaveOptionPanel(TrapSpaceInclusionDiagram graph) {
		// TODO Auto-generated method stub
		JPanel psave = new JPanel();
		return psave;
	}

}
