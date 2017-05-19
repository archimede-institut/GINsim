package org.ginsim.gui.graph.trapspacetree;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.trapspacetree.TrapSpaceNode;
import org.ginsim.core.graph.trapspacetree.TrapSpaceTree;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.servicegui.tool.lrgcolorizer.LRGPatternStyleProvider;

public class TrapSpaceTreeMainPanel extends JPanel implements GUIEditor<TrapSpaceNode> {

	private TrapSpaceTree tree = null;
	private RegulatoryGraph lrg = null;
	
    private StyleManager<RegulatoryNode, RegulatoryMultiEdge> styleManager;
    private LRGPatternStyleProvider styleProvider;
	
	public TrapSpaceTreeMainPanel(TrapSpaceTree tree) {
		this.tree = tree;
		try {
			this.lrg = tree.getAssociatedGraph();
		} catch (GsException e) {
			System.out.println("ERROR finding LRG");
			e.printStackTrace();
			this.lrg = null;
			this.styleManager = null;
			this.styleProvider = null;
		}
		
		add( new JLabel("edit "+tree +". LRG: "+lrg));
		
		if (lrg != null) {
			styleProvider = new LRGPatternStyleProvider(lrg);
			styleManager = lrg.getStyleManager();
			styleManager.setStyleProvider( styleProvider);
		}
	}

	@Override
	public void setEditedItem(TrapSpaceNode node) {
		if (styleProvider == null) {
			return;
		}
		if (node == null) {
			styleProvider.setPattern(null, null);
			return;
		}
		styleProvider.setPattern(node.trapspace.pattern, node.trapspace.percolated);
	}

	@Override
	public Component getComponent() {
		return this;
	}
	
}
