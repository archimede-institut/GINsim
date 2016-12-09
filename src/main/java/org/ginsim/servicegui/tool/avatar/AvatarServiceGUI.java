package org.ginsim.servicegui.tool.avatar;

import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.shell.actions.ToolAction;
import org.ginsim.gui.utils.widgets.Frame;
import org.ginsim.service.tool.avatar.service.AvatarService;
import org.mangosdk.spi.ProviderFor;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for the avatar simulation services
 * @author Pedro Monteiro, Rui Henriques
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(AvatarService.class)
@ServiceStatus(EStatus.DEVELOPMENT)
public class AvatarServiceGUI extends AbstractServiceGUI {

    @Override
    public List<Action> getAvailableActions(Graph<?, ?> graph) {
        if(graph instanceof RegulatoryGraph){
            List<Action> actions = new ArrayList<Action>();
            actions.add(new AvatarAction((RegulatoryGraph) graph, this));
            return actions;
        }
        return null;
    }

    @Override
    public int getInitialWeight() {
        return W_UNDER_DEVELOPMENT + 10;
    }
}

/**
 * Action to enable the presence Avatar simulations within the menu with the ginsim services
 * @author Pedro Monteiro, Rui Henriques
 */
class AvatarAction extends ToolAction {

	private static final long serialVersionUID = 3616390928403590166L;
	private final RegulatoryGraph graph;

    /**
     * Creates the action to provide avatar simulations
     * @param graph the regulatory graph
     * @param serviceGUI the avatar simulation service GUI
     */
    public AvatarAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
        super("STR_avatar", "STR_avatar_descr", serviceGUI);
        this.graph = graph;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(graph.getNodeOrderSize()<1){
            NotificationManager.publishWarning(graph, Txt.t("STR_emptyGraph"));
            return;
        }
        Frame mainFrame = GUIManager.getInstance().getFrame(graph);
        AvatarConfigFrame frame = new AvatarConfigFrame(graph, mainFrame);
        frame.setVisible(true);
    }
}
