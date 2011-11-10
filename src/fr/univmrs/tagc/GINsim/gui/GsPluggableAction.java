package fr.univmrs.tagc.GINsim.gui;

import java.awt.event.ActionEvent;

import org.ginsim.exception.GsException;
import org.ginsim.gui.service.BaseAction;

import fr.univmrs.tagc.GINsim.global.GsEnv;

/**
 * Helper action to easily add custom mode (as several kind of vertex or edge to add)
 */
public class GsPluggableAction extends BaseAction {

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
