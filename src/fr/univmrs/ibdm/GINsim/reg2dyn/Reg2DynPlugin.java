package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsActions;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.plugin.GsPlugin;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;

/**
 * main method for the reg2dyn plugin
 * 
 * @author aurelien
 */
public class Reg2DynPlugin implements GsPlugin, GsActionProvider {

    private GsPluggableActionDescriptor[] t_action = null;

    public void registerPlugin() {
        GsRegulatoryGraphDescriptor.registerActionProvider(this);
        Translator
                .pushBundle("fr.univmrs.ibdm.GINsim.ressources.messagesReg2dyn");
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType,
            GsGraph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
        if (t_action == null) {
            t_action = new GsPluggableActionDescriptor[1];
            t_action[0] = new GsPluggableActionDescriptor("STR_reg2dyn",
                    "STR_reg2dyn_descr", null, this, ACTION_ACTION, 0);
        }
        return t_action;
    }

    public void runAction(int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
        if (actionType != ACTION_ACTION) {
            return;
        }
        if (graph.getNodeOrder().size() < 1) {
            return;
        }
		if (ref == 0) {
		    Reg2dynFrame theframe = (Reg2dynFrame)graph.getObject("reg2dyn");
            Map m_params = (Map)graph.getObject("reg2dyn_parameters");
            if (m_params == null) {
                m_params = new HashMap();
                graph.addObject("reg2dyn_parameters", m_params);
            }
		    if (theframe == null) {
		        theframe = new Reg2dynFrame(frame, (GsRegulatoryGraph)graph, m_params);
		         graph.addObject("reg2dyn", theframe);
		    } else {
		        theframe.refreshGraph();
		    }
            graph.getGraphManager().getMainFrame().getGsAction().setCurrentMode(GsActions.MODE_DEFAULT, 0, false);
	        theframe.setVisible(true);
		}
	}
}
