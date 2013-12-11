package org.ginsim.gui.utils.data;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.ginsim.gui.utils.widgets.SplitPane;

public class ListEditionPanel<T> extends SplitPane {

	private CardLayout cards = new CardLayout();
	private JPanel mainPanel = new JPanel(cards);
	private final ListPanelHelper<T> helper;
	private final Map<String, Component> m_panels = new HashMap<String, Component>();
	private final Map<String, String> m_cardAliases = new HashMap<String, String>();
	private final ListPanel<T> listPanel;

	public ListEditionPanel(ListPanelHelper<T> helper, List<T> list, String title) {
		
		this.helper = helper;
		listPanel = new ListPanel<T>(helper, title);
		listPanel.setList(list);
		setLeftComponent( listPanel);
	}

	public void init() {
		helper.setEditPanel(this);
		setRightComponent(mainPanel);
	}
	
	public void addPanel(Component panel, String name) {
		for (String key: m_panels.keySet()) {
			if (m_panels.get(key) == panel) {
				m_cardAliases.put(name, key);
				return;
			}
		}
		mainPanel.add(panel, name);
	}
	
	public void showPanel(String name) {
		String alias = m_cardAliases.get(name);
		if (alias == null) {
			alias = name;
		}
		cards.show(mainPanel, alias);
		repaint();
	}

    public int[] getSelection() {
        return listPanel.getSelection();
    }

    public T getSelectedItem() {
        return listPanel.getSelectedItem();
    }
}

