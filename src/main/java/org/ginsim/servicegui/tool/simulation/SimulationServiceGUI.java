package org.ginsim.servicegui.tool.simulation;

import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.shell.actions.ToolAction;
import org.ginsim.gui.utils.widgets.Frame;
import org.ginsim.service.tool.reg2dyn.SimulationParameterList;
import org.ginsim.service.tool.reg2dyn.SimulationParametersManager;
import org.ginsim.service.tool.simulation.SimulationService;
import org.ginsim.servicegui.tool.reg2dyn.SingleSimulationFrame;
import org.mangosdk.spi.ProviderFor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for the simulation service
 *
 * @author Aurelien Naldi
 */
@ProviderFor( ServiceGUI.class)
@GUIFor(SimulationService.class)
@ServiceStatus( EStatus.DEVELOPMENT)
public class SimulationServiceGUI extends AbstractServiceGUI {

    @Override
    public List<Action> getAvailableActions( Graph<?, ?> graph) {

        if( graph instanceof RegulatoryGraph){
            List<Action> actions = new ArrayList<Action>();
            actions.add( new SimulationAction( (RegulatoryGraph) graph, this));
            return actions;
        }
        return null;
    }

    @Override
    public int getInitialWeight() {
        return W_UNDER_DEVELOPMENT + 10;
    }

    class SimulationAction extends ToolAction {

        private final RegulatoryGraph graph;

        private SimulationAction( RegulatoryGraph graph, ServiceGUI serviceGUI) {
            super( "STR_simulation", "STR_simulation_descr", serviceGUI);
            this.graph = graph;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if ( graph.getNodeOrderSize() < 1) {
                NotificationManager.publishWarning(graph, Txt.t("STR_emptyGraph"));
                return;
            }

            Frame mainFrame = GUIManager.getInstance().getFrame( graph);
            GraphGUI<?, ?, ?> gui = null;
            if (mainFrame != null) {
                gui = GUIManager.getInstance().getGraphGUI( graph);
                // TODO: replace this with a mode set on the gui
                // mainFrame.getActions().setCurrentMode( GsActions.MODE_DEFAULT, 0, false);
            }

            // Show the actual simulation GUI
            SimulationFrame frame = new SimulationFrame(graph, mainFrame);
            frame.setVisible(true);
        }
    }

}
