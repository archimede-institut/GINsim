package org.ginsim.servicegui.tool.simulation;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.simulation.MultipleSuccessorsUpdater;
import org.colomoto.biolqm.tool.simulation.updater.AsynchronousUpdater;
import org.colomoto.biolqm.tool.simulation.updater.CompleteUpdater;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.tool.simulation.STGSimulation;

import javax.swing.*;
import java.awt.*;


/**
 * Select the settings and start the simulation.
 *
 * @author Aurelien Naldi
 */
public class SimulationFrame extends LogicalModelActionDialog {

    private static final int W = 600;
    private static final int H = 400;
    private static final String ID = "display.simulation";

    private JPanel panel = new JPanel();
    private JCheckBox isComplete = new JCheckBox("\"Complete\" simulation");

    public SimulationFrame(RegulatoryGraph lrg, Frame parent) {
        super(lrg, parent, ID, W, H);
        panel.add(new JLabel("Warning: this is an experiment for a new simulation backend"));

        panel.add(isComplete);

        setMainPanel(panel);
    }

    @Override
    public void run(LogicalModel model) {

        MultipleSuccessorsUpdater updater;
        if (isComplete.isSelected()) {
            updater = new CompleteUpdater(model);
        } else {
            updater = new AsynchronousUpdater(model);
        }

        STGSimulation simulation = new STGSimulation(model, updater);
        simulation.runSimulation();
        this.doClose();

        DynamicGraph graph = simulation.getGraph();
        GUIManager.getInstance().whatToDoWithGraph(graph, true);
    }

}
