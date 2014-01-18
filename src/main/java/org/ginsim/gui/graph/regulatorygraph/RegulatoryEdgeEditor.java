package org.ginsim.gui.graph.regulatorygraph;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.annotation.AnnotationPanel;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUI;


public class RegulatoryEdgeEditor extends JPanel implements GUIEditor<RegulatoryMultiEdge> {

    private static final Insets INSETS = new Insets(3,3,3,3);

    private final SelectButton bSource;
    private final SelectButton bTarget;

    private final AnnotationPanel annotPanel = new AnnotationPanel();
    private final MultiEdgeEditPanel edgeEditPanel;

	public RegulatoryEdgeEditor( RegulatoryGraph graph) {
        super(new GridBagLayout());

		GraphGUI gui = GUIManager.getInstance().getGraphGUI(graph);

        GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 1;
        cst.gridy = 1;
        cst.insets = INSETS;

        // source and target
        bSource = new SelectButton(gui);
        add(bSource, cst);
        cst.gridx++;
        add(new JLabel("\u2192"), cst);
        cst.gridx++;
        bTarget = new SelectButton(gui);
        add(bTarget, cst);

        // main edge setting panel
        cst.fill = GridBagConstraints.BOTH;
        cst.gridy++;
        cst.gridx = 1;
        cst.gridwidth = 5;
        cst.weighty = 1;
        edgeEditPanel = new MultiEdgeEditPanel(graph);
        add(edgeEditPanel, cst);

        // annotation panel
        cst.weightx = 1;
        cst.gridx = 6;
        cst.gridwidth = 1;
        cst.gridx++;
        cst.gridy = 1;
        cst.gridheight = 2;
        add(annotPanel, cst);
	}

    @Override
    public Component getComponent() {
        return this;
    }

	@Override
	public void setEditedItem(RegulatoryMultiEdge medge) {
		if (medge == null) {
			return;
		}

        bSource.setNode(medge.getSource());
        bTarget.setNode(medge.getTarget());

        edgeEditPanel.setEdge(medge);
        annotPanel.setAnnotation(medge.getAnnotation());
    }
}

class SelectButton extends JButton implements ActionListener {

    private final GraphGUI gui;
    private RegulatoryNode node = null;

    public SelectButton(GraphGUI gui) {
        this.gui = gui;
        setBorder(BorderFactory.createEtchedBorder());
        addActionListener(this);
    }

    public void setNode(RegulatoryNode node) {
        this.node = node;
        setText(node.getId());
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (node == null) {
            return;
        }

        gui.getSelection().selectNode(node);
    }
}