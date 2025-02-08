package org.ginsim.gui.annotation;

import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.shell.editpanel.EditTab;

import javax.swing.*;
import java.awt.*;

/**
 * class AnnotationTab extends JPanel
 */
public class AnnotationTab extends JPanel implements EditTab {

    public static AnnotationTab prepareTab(GraphGUI<?,?,?> gui) {
        Graph graph = gui.getGraph();

        // TODO: create the panel only of the graph supports annotations
        if (graph instanceof RegulatoryGraph) {
            return new AnnotationTab();
        }

        return null;
    }

    @Override
    public String getTitle() {
        return "Annotation";
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public boolean isActive(GraphSelection<?, ?> selection) {
        if (selection == null) {
            // No selection: annotate the graph
            return true;
        }

        switch (selection.getSelectionType()) {
            case SEL_NONE:
            case SEL_NODE:
                return true;
            case SEL_EDGE:
            case SEL_MULTIPLE:
                return false;
        }
        return false;
    }
}
