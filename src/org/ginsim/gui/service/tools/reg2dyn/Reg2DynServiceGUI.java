package org.ginsim.gui.service.tools.reg2dyn;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.gui.service.tools.pathfinding.PathFindingFrame;
import org.ginsim.service.action.reg2dyn.Reg2DynService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.Frame;

/**
 * main method for the reg2dyn plugin
 */
@ProviderFor( GsServiceGUI.class)
@GUIFor( Reg2DynService.class)
public class Reg2DynServiceGUI implements GsServiceGUI {

    static {
        if (!GsRegulatoryGraphDescriptor.isObjectManagerRegistred(GsMutantListManager.key)) {
            GsRegulatoryGraphDescriptor.registerObjectManager(new GsMutantListManager());
        }
        GsRegulatoryGraphDescriptor.registerObjectManager(new GsInitialStateManager());
        GsRegulatoryGraphDescriptor.registerObjectManager(new GsSimulationParametersManager());
    }
    

//    public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
//        if (actionType != ACTION_ACTION) {
//            return;
//        }
//        if (!(graph instanceof GsRegulatoryGraph) || graph.getNodeOrderSize() < 1) {
//            graph.addNotificationMessage(new NotificationMessage(graph, 
//            		Translator.getString(graph instanceof GsRegulatoryGraph ? "STR_emptyGraph" : "STR_notRegGraph"), 
//            		NotificationMessage.NOTIFICATION_WARNING));
//            return;
//        }
//		if (ref == 0 || ref == 1) {
////            Map m_params = (Map)graph.getObject("reg2dyn_parameters");
////            if (m_params == null) {
////                m_params = new HashMap();
////                graph.addObject("reg2dyn_parameters", m_params);
////            }
////            new Reg2dynFrame(frame, (GsRegulatoryGraph)graph, m_params).setVisible(true);
//            GsMainFrame mainFrame = graph.getGraphManager().getMainFrame();
//            if (mainFrame != null) {
//            	mainFrame.getActions().setCurrentMode(GsActions.MODE_DEFAULT, 0, false);
//            }
//
//            GsSimulationParameterList paramList = (GsSimulationParameterList)graph.getObject(GsSimulationParametersManager.key, true);
//            if (ref == 0) {
//                new GsSingleSimulationFrame(frame, paramList).setVisible(true);
//            } else {
//                new GsBatchSimulationFrame(frame, paramList).setVisible(true);
//            }
//		}
//	}
    
	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		
		if( graph instanceof GsRegulatoryGraph){
			List<Action> actions = new ArrayList<Action>();
			actions.add(new Reg2DynAction( (GsRegulatoryGraph) graph));
			return actions;
		}
		return null;
	}
}


class Reg2DynAction extends GsToolsAction {

	private final GsRegulatoryGraph graph;
	
	public Reg2DynAction( GsRegulatoryGraph graph) {
		super( "STR_reg2dyn", "STR_reg2dyn_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        
		if ( graph.getNodeOrderSize() < 1) {
            new NotificationMessage( (NotificationMessageHolder) graph, Translator.getString("STR_emptyGraph"), NotificationMessage.NOTIFICATION_WARNING);

            return;
        }
		
		// TODO : Refactoring ACTION
		// TODO : What is frame? what is mainFrame?
		// TODO : What do to with this test on ref?
		//if (ref == 0 || ref == 1) {
			
          Frame mainFrame = GUIManager.getInstance().getFrame( graph);
          if (mainFrame != null) {
          	mainFrame.getActions().setCurrentMode( GsActions.MODE_DEFAULT, 0, false);
          }

          GsSimulationParameterList paramList = (GsSimulationParameterList)graph.getObject(GsSimulationParametersManager.key, true);
          if (ref == 0) {
              new GsSingleSimulationFrame(frame, paramList).setVisible(true);
          } else {
              new GsBatchSimulationFrame(frame, paramList).setVisible(true);
          }
		}
		
	}
}
