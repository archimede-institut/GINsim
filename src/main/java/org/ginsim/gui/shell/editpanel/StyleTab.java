package org.ginsim.gui.shell.editpanel;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.Collection;

import javax.swing.JPanel;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;

public class StyleTab extends JPanel implements EditTab {

	private static final String ITEMPANEL = "item";
	private static final String MANAGERPANEL = "manager";
	
    private final StyleItemPanel itemPanel;
    private final StyleManagerPanel managerPanel;
    
    private final CardLayout cards;
    
	public StyleTab(GraphGUI<?, ?, ?> gui) {
		this.cards = new CardLayout();
		this.setLayout(cards);

		Graph graph = gui.getGraph();
    	StyleManager manager = graph.getStyleManager();
    	
		this.itemPanel = new StyleItemPanel(gui);
		this.managerPanel = new StyleManagerPanel(gui);

		add(itemPanel, ITEMPANEL);
		add(managerPanel, MANAGERPANEL);
	}

	@Override
	public final Component getComponent() {
		return this;
	}

	@Override
	public String getTitle() {
		return "Style";
	}

	@Override
	public boolean isActive(GraphSelection<?, ?> selection) {

		if (selection.getSelectionType() == SelectionType.SEL_NONE) {
			cards.show(this, MANAGERPANEL);
		} else {
			itemPanel.edit(selection.getSelectedNodes(), (Collection<Edge>)selection.getSelectedEdges());
			cards.show(this, ITEMPANEL);
		}

		return true;
	}
}
