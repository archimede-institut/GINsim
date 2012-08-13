package org.ginsim.gui.utils.data;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JPanel;

import org.ginsim.gui.utils.widgets.SplitPane;

public class ListEditionPanel<T> extends SplitPane {

	private CardLayout cards = new CardLayout();
	private JPanel mainPanel = new JPanel(cards);
	private final ListPanelHelper<T> helper;
	
	public ListEditionPanel(ListPanelHelper<T> helper, List<T> list, String title) {
		
		this.helper = helper;
		ListPanel<T> listPanel = new ListPanel<T>(helper, title);
		listPanel.setList(list);
		setLeftComponent( listPanel);
	}

	protected void init() {
		helper.setEditPanel(this);
		setRightComponent(mainPanel);
	}
	
	public void addPanel(Component panel, String name) {
		mainPanel.add(panel, name);
	}
	
	public void showPanel(String name) {
		cards.show(mainPanel, name);
	}
}

