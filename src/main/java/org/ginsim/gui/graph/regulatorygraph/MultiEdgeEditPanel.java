package org.ginsim.gui.graph.regulatorygraph;

import org.ginsim.commongui.utils.ImageLoader;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Panel to edit a regulation, and all included edges.
 *
 * @author Aurelien Naldi
 */
public class MultiEdgeEditPanel extends JPanel {

    private static Insets INSETS = new Insets(3,5,3,5);

    private final RegulatoryGraph graph;

    private RegulatoryMultiEdge medge = null;

    public MultiEdgeEditPanel(RegulatoryGraph graph) {
        super(new GridBagLayout());
        Dimension dim = new Dimension(170, 150);
        setMinimumSize(dim);
        setPreferredSize(dim);
        setBackground(Color.WHITE);
        this.graph = graph;
    }

    public void setEdge(RegulatoryMultiEdge medge) {
        this.medge = medge;
        reload();
    }

    private void reload() {
        removeAll();
        if (medge == null) {
            return;
        }

        boolean isMultiple = medge.getEdgeCount() > 1;

        GridBagConstraints cst = new GridBagConstraints();
        cst.anchor = GridBagConstraints.NORTHWEST;
        cst.insets = INSETS;
        int edgeCount = medge.getEdgeCount();
        for (int edgeIndex=0 ; edgeIndex<edgeCount ; edgeIndex++) {

            cst.gridy++;
            cst.gridx = 1;
            cst.weightx = 0;
            cst.fill = GridBagConstraints.NONE;

            RegulatoryEdge edge = medge.getEdge(edgeIndex);

            EdgeThresholdModel thmodel = new EdgeThresholdModel(graph, edge, this);
            cst.gridwidth = 1;
            add(new JSpinner(thmodel), cst);

            cst.gridx++;
            add(new SignCombobox(graph, edge), cst);


            if (isMultiple) {
                cst.weightx = 0;
                cst.fill = GridBagConstraints.NONE;
                cst.gridx++;
                cst.anchor = GridBagConstraints.EAST;
                JButton bDel = new JButton(new ActionDelete(this, edgeIndex));
                bDel.setToolTipText("Remove effect "+edge);
                bDel.setBorder(BorderFactory.createEtchedBorder());
                add(bDel, cst);
                cst.anchor = GridBagConstraints.NORTHWEST;
            }
        }

        // on bottom, a panel allows to add missing effects
        cst.gridy++;
        cst.gridx = 1;
        cst.gridwidth = 3;
        cst.fill = GridBagConstraints.BOTH;
        cst.weightx = 1;
        cst.weighty = 1;
        JPanel addPanel = new JPanel(new GridBagLayout());
        add(addPanel, cst);

        cst = new GridBagConstraints();
        cst.insets = INSETS;
        cst.anchor = GridBagConstraints.SOUTHWEST;
        for (int th: medge.getFreeValues()) {
            cst.gridx++;
            JButton bAdd = new JButton(new ActionAdd(this, th));
            bAdd.setToolTipText("Add an effect at threshold "+th);
            bAdd.setBorder(BorderFactory.createEtchedBorder());
            addPanel.add(bAdd, cst);
        }

        validate();
        repaint();
    }

    public void addRegulation(int threshold) {
        medge.addEdge(RegulatoryEdgeSign.POSITIVE, threshold, graph);
        reload();
    }

    public void removeRegulation(int idx) {
        medge.removeEdge(idx, graph);
        reload();
    }

    public void setThreshold(RegulatoryEdge edge, int th) {
        edge.me.setMin(edge.index, (byte)th, graph);
        reload();
    }
}

class ActionAdd extends AbstractAction {

    private static final Icon icon = ImageLoader.getImageIcon("list-add.png");

    private final MultiEdgeEditPanel panel;
    private final int threshold;

    public ActionAdd(MultiEdgeEditPanel panel, int threshold) {
        super(""+threshold, icon);
        this.panel = panel;
        this.threshold = threshold;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        panel.addRegulation(threshold);
    }
}

class ActionDelete extends AbstractAction {

    private static final Icon icon = ImageLoader.getImageIcon("list-remove.png");

    private final MultiEdgeEditPanel panel;
    private final int idx;

    public ActionDelete(MultiEdgeEditPanel panel, int idx) {
        super("", icon);
        this.panel = panel;
        this.idx = idx;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        panel.removeRegulation(idx);
    }
}

class SignCombobox extends JComboBox {

    private final RegulatoryGraph graph;
    private final RegulatoryEdge edge;

    public SignCombobox(RegulatoryGraph graph, RegulatoryEdge edge) {
        super(RegulatoryEdgeSign.getShortDescForGUI());
        setSelectedIndex(edge.getSign().getIndexForGUI());
        addActionListener(this);
        this.graph = graph;
        this.edge = edge;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        byte s = (byte)getSelectedIndex();
        if (s != edge.getSign().getIndexForGUI() && s >= 0 && s<RegulatoryEdgeSign.values().length) {
            edge.me.setSign(edge.index, RegulatoryEdgeSign.getFromPos(s), graph);
        }
    }

}

class EdgeThresholdModel extends AbstractSpinnerModel {

    private final MultiEdgeEditPanel panel;
    private final RegulatoryGraph graph;
    private final RegulatoryEdge edge;

    public EdgeThresholdModel(RegulatoryGraph graph, RegulatoryEdge edge, MultiEdgeEditPanel panel) {
        this.graph = graph;
        this.edge = edge;
        this.panel = panel;
    }

    public Object getNextValue() {
        panel.setThreshold(edge, edge.getMin()+1);
        return getValue();
    }

    public Object getPreviousValue() {
        panel.setThreshold(edge, edge.getMin()-1);
        return getValue();
    }

    public Object getValue() {
        return new Integer(edge.getMin());
    }

    public void setValue(Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Integer) {
            panel.setThreshold(edge, (Integer)value);
        }
    }
}
