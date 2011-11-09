package fr.univmrs.tagc.GINsim.reg2dyn;

import javax.swing.JFrame;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;

import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.tagc.GINsim.gui.GsActions;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * main method for the reg2dyn plugin
 */
public class Reg2DynPlugin implements GsPlugin, GsActionProvider {

    static {
        if (!GsRegulatoryGraphDescriptor.isObjectManagerRegistred(GsMutantListManager.key)) {
            GsRegulatoryGraphDescriptor.registerObjectManager(new GsMutantListManager());
        }
        GsRegulatoryGraphDescriptor.registerObjectManager(new GsInitialStateManager());
        GsRegulatoryGraphDescriptor.registerObjectManager(new GsSimulationParametersManager());
    }
    
    private GsPluggableActionDescriptor[] t_action = null;

    public void registerPlugin() {
        GsRegulatoryGraphDescriptor.registerActionProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
        if (t_action == null) {
            t_action = new GsPluggableActionDescriptor[1];
            t_action[0] = new GsPluggableActionDescriptor("STR_reg2dyn",
                    "STR_reg2dyn_descr", null, this, ACTION_ACTION, 0);
//            t_action[1] = new GsPluggableActionDescriptor("STR_batchreg2dyn",
//                    "STR_batchreg2dyn_descr", null, this, ACTION_ACTION, 1);
        }
        return t_action;
    }

    public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
        if (actionType != ACTION_ACTION) {
            return;
        }
        if (!(graph instanceof GsRegulatoryGraph) || graph.getNodeOrderSize() < 1) {
            graph.addNotificationMessage(new GsGraphNotificationMessage(graph, 
            		Translator.getString(graph instanceof GsRegulatoryGraph ? "STR_emptyGraph" : "STR_notRegGraph"), 
            		GsGraphNotificationMessage.NOTIFICATION_WARNING));
            return;
        }
		if (ref == 0 || ref == 1) {
//            Map m_params = (Map)graph.getObject("reg2dyn_parameters");
//            if (m_params == null) {
//                m_params = new HashMap();
//                graph.addObject("reg2dyn_parameters", m_params);
//            }
//            new Reg2dynFrame(frame, (GsRegulatoryGraph)graph, m_params).setVisible(true);
            GsMainFrame mainFrame = graph.getGraphManager().getMainFrame();
            if (mainFrame != null) {
            	mainFrame.getActions().setCurrentMode(GsActions.MODE_DEFAULT, 0, false);
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
