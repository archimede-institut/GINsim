package org.ginsim.gui.shell.editpanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.ginsim.core.graph.view.style.Style;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProvider;
import org.ginsim.gui.graph.GraphGUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;


public class StyleManagerPanel extends JPanel implements ItemListener, ListSelectionListener, ActionListener {

	private final StyleManager manager;
    private final ListTableModel nStyleModel;
    private final ListTableModel eStyleModel;
    private final JList styleTable;
    private final StyleEditionPanel stylePanel;
    private final JButton b_removeProvider;

    private boolean providerMode = false;
	
	public StyleManagerPanel(GraphGUI gui) {
        super(new GridBagLayout());
		this.manager = gui.getGraph().getStyleManager();
        nStyleModel = new ListTableModel(manager.getNodeStyles());
        eStyleModel = new ListTableModel(manager.getEdgeStyles());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;

        // select node or edge styles
        ButtonGroup group = new ButtonGroup();
        JToggleButton bNodes = new JToggleButton("Nodes");
        group.add(bNodes);
        JToggleButton bEdges = new JToggleButton("Edges");
        group.add(bEdges);
        bNodes.setSelected(true);
        bNodes.addItemListener(this);

        add(bNodes, c);
        c.gridx = 1;
        add(bEdges, c);

        // list of styles
        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        styleTable = new JList(nStyleModel);
        add(styleTable, c);
        styleTable.getSelectionModel().addListSelectionListener(this);

        // style provider reset button
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        b_removeProvider = new JButton("Remove style provider");
        b_removeProvider.setVisible(false);
        b_removeProvider.addActionListener(this);
        add(b_removeProvider, c);

        // style edition panel itself
        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 1;
        stylePanel = new StyleEditionPanel(gui, manager);
        add(stylePanel, c);


        styleTable.setSelectedIndex(0);
	}


    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            styleTable.setModel(nStyleModel);
        } else {
            styleTable.setModel(eStyleModel);
        }
        updated();
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        Style style = (Style)styleTable.getSelectedValue();
        stylePanel.setStyle(style);

        if (style == null) {
            styleTable.setSelectedIndex(0);
        }

        updated();
    }

    private void updated() {
        StyleProvider styleProvider = manager.getStyleProvider();
        if (providerMode && styleProvider == null) {
            providerMode = false;
            b_removeProvider.setVisible(false);
        } else if (!providerMode && styleProvider != null) {
            providerMode = true;
            b_removeProvider.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        manager.setStyleProvider(null);
        updated();
    }
}

class ListTableModel extends AbstractListModel {

    private final List data;

    ListTableModel(List data) {
        this.data = data;
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public Object getElementAt(int i) {
        return data.get(i);
    }
}
