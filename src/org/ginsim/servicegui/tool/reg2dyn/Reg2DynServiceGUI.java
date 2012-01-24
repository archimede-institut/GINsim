package org.ginsim.servicegui.tool.reg2dyn;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.Translator;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.gui.utils.widgets.Frame;
import org.ginsim.service.tool.reg2dyn.Reg2DynService;
import org.ginsim.service.tool.reg2dyn.SimulationParameterList;
import org.ginsim.service.tool.reg2dyn.SimulationParametersManager;
import org.mangosdk.spi.ProviderFor;


/**
 * main method for the reg2dyn plugin
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( Reg2DynService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class Reg2DynServiceGUI implements ServiceGUI {

//    public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
//        if (actionType != ACTION_ACTION) {
//            return;
//        }
//        if (!(graph instanceof RegulatoryGraph) || graph.getNodeOrderSize() < 1) {
//            graph.addNotificationMessage(new Notification(graph, 
//            		Translator.getString(graph instanceof RegulatoryGraph ? "STR_emptyGraph" : "STR_notRegGraph"), 
//            		Notification.NOTIFICATION_WARNING));
//            return;
//        }
//		if (ref == 0 || ref == 1) {
////            Map m_params = (Map)graph.getObject("reg2dyn_parameters");
////            if (m_params == null) {
////                m_params = new HashMap();
////                graph.addObject("reg2dyn_parameters", m_params);
////            }
////            new Reg2dynFrame(frame, (RegulatoryGraph)graph, m_params).setVisible(true);
//            GsMainFrame mainFrame = graph.getGraphManager().getMainFrame();
//            if (mainFrame != null) {
//            	mainFrame.getActions().setCurrentMode(GsActions.MODE_DEFAULT, 0, false);
//            }
//
//            SimulationParameterList paramList = (SimulationParameterList)graph.getObject(SimulationParametersManager.key, true);
//            if (ref == 0) {
//                new SingleSimulationFrame(frame, paramList).setVisible(true);
//            } else {
//                new BatchSimulationFrame(frame, paramList).setVisible(true);
//            }
//		}
//	}
    
	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		
		if( graph instanceof RegulatoryGraph){
			List<Action> actions = new ArrayList<Action>();
			actions.add( new Reg2DynAction( (RegulatoryGraph) graph));
			return actions;
		}
		return null;
	}

	@Override
	public int getWeight() {
		return W_MAIN + 1;
	}
}


class Reg2DynAction extends ToolAction {

	private final RegulatoryGraph graph;
	
	public Reg2DynAction( RegulatoryGraph graph) {
		super( "STR_reg2dyn", "STR_reg2dyn_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        
		if ( graph.getNodeOrderSize() < 1) {
            NotificationManager.publishWarning( graph, Translator.getString("STR_emptyGraph"));

            return;
        }
		
		Frame mainFrame = GUIManager.getInstance().getFrame( graph);
		if (mainFrame != null) {
			GraphGUI<?, ?, ?> gui = GUIManager.getInstance().getGraphGUI( graph);
			// TODO: replace this with a mode set on the gui
			// mainFrame.getActions().setCurrentMode( GsActions.MODE_DEFAULT, 0, false);
		}

		SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager.getInstance().getObject( graph, SimulationParametersManager.key, true);
		new SingleSimulationFrame(mainFrame, paramList).setVisible(true);

	}
		
}
