package org.ginsim.gui.utils.data;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.gui.utils.widgets.SplitPane;

public class ListEditionPanel<T,L extends List<T>> extends SplitPane {

    private final Object associated;
	private final CardLayout cards = new CardLayout();
	private final JPanel mainPanel = new JPanel(cards);
	private final Map<String, Component> m_panels = new HashMap<String, Component>();
	private final Map<String, String> m_cardAliases = new HashMap<String, String>();
	private final ListPanel<T,L> listPanel;
    private final ListPanelCompanion companion;
    private final StackDialog dialog;

	public ListEditionPanel(ListPanelHelper<T,L> helper, L list, String title, StackDialog dialog, Object associated) {
		this.associated = associated;
        this.dialog = dialog;
		listPanel = new ListPanel<T,L>(helper, title, this);
		listPanel.setList(list);
		setLeftComponent( listPanel);

        companion = helper.getCompanion(this);
        if (companion != null) {
            companion.setParentList(list);
        	setRightComponent(mainPanel);
            companion.selectionUpdated(getSelection());
        }
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

    public L getList() {
        return listPanel.getList();
    }

    public T getSelectedItem() {
        return listPanel.getSelectedItem();
    }

    public void selectionUpdated(int[] sel) {
        if (companion != null) {
            companion.selectionUpdated(sel);
        }
    }

    public void refresh() {
        listPanel.refresh();
    }

    public Object retrieveAssociated() {
        return associated;
    }

    public StackDialog getDialog() {
        return dialog;
    }
}
