package fr.univmrs.ibdm.GINsim.gui;

import java.awt.event.ActionEvent;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;

/**
 * Helper action to easily add custom mode (as several kind of vertex or edge to add)
 */
public class GsPluggableAction extends GsBaseAction {

    private static final long serialVersionUID = 5364117207428709800L;
	private GsMainFrame main;
    private int type;
    private int mode;
    private GsActionProvider ap;
    
    /**
     * 
     * @param ad descriptor for this action
     * @param main the mainFrame
     */
    public GsPluggableAction(GsPluggableActionDescriptor ad, GsMainFrame main) {
        super(ad.name, ad.icon,ad.descr, null);
        this.main = main;
        this.mode = ad.param;
        this.type = ad.type;
        this.ap = ad.ap;
    }
    
    public void actionPerformed(ActionEvent e) {
    	try {
			ap.runAction(type, mode, main.getGraph(), main);
		} catch (GsException e1) {
			GsEnv.error(e1, null);
		}
    }
}
