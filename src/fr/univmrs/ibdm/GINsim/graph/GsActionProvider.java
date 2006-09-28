package fr.univmrs.ibdm.GINsim.graph;

import javax.swing.JFrame;

import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;

/**
 * how layout/action/export providers should annonce their capabilities.
 * 
 */
public interface GsActionProvider {
    /**  */
    public static final int ACTION_LAYOUT = 0;
    /**  */
    public static final int ACTION_EXPORT = 1;
    /**  */
    public static final int ACTION_ACTION = 2;
    
	/**
     * get actions avaible within this provider.
     * 
	 * @param actionType the type of action (one of ACTION_ACTION, ACTION_EXPORT or ACTION_LAYOUT)
	 * @param graph the graph for which we are adding it
	 * @return the actions that this actionDescriptor can handle or null if none
	 */
	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph);
	
	/**
	 * run a given action on the graph.
	 * @param actionType
	 * @param ref
	 * @param graph
	 * @param frame
     * 
	 * @throws GsException if error (like invalid parameters)
	 */
	public void runAction (int actionType, int ref, GsGraph graph, JFrame frame) throws GsException;
}
