package fr.univmrs.tagc.GINsim.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.ginsim.gui.service.BaseAction;

import fr.univmrs.tagc.GINsim.global.GsEnv;

/**
 * Helper action to easily add custom actions (typically layout, export...)
 */
public class GsEditSwitchAction extends BaseAction {

    private static final long serialVersionUID = -8958595762708145152L;


    private int mode;
    private int subMode = GsActions.MODE_DEFAULT;
    private long lastTime = 0;
    private GsActions gsa;
    /**
     * create a new editSwitchAction with default submode.
     * @param name name of the action (menu entry)
     * @param icon for menu and toolbar
     * @param tooltip
     * @param accelerator (ie keyboard bytecut)
     * @param gsa where does it lands
     * @param mode the real info: to which mode should it switch
     */
    public GsEditSwitchAction(String name,
			   ImageIcon icon, String tooltip,
			   KeyStroke accelerator, GsActions gsa,
			   int mode) {
        super(name, icon, tooltip, accelerator);
        this.mode = mode;
        this.gsa = gsa;
    }
    /**
     * create a new editSwitchAction with a submode.
     * @param name name of the action (menu entry)
     * @param icon for menu and toolbar
     * @param tooltip
     * @param accelerator (ie keyboard bytecut)
     * @param gsa where does it lands
     * @param mode the real info: to which mode should it switch
     * @param subMode option for this edit mode
     */
    public GsEditSwitchAction(String name,
			   ImageIcon icon, String tooltip,
			   KeyStroke accelerator, GsActions gsa,
			   int mode, int subMode) {
     super(name, icon, tooltip, accelerator);
     this.mode = mode;
     this.subMode = subMode;
     this.gsa = gsa;
 }
    
    /**
     * @param descriptor
     * @param gsa
     */
    public GsEditSwitchAction(GsEditModeDescriptor descriptor, GsActions gsa) {
        super(descriptor.name, descriptor.icon, descriptor.descr, null);
        this.mode = descriptor.mode;
        this.subMode = descriptor.submode;
        this.gsa = gsa;
        
        if (descriptor.key != KeyEvent.VK_UNDEFINED) {
        	this.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(descriptor.key, GsActions.mask | KeyEvent.ALT_MASK));
        }

    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
		long time;
		time=e.getWhen();

		gsa.setCurrentMode(mode, subMode, (time - lastTime < GsEnv.TIMEOUT));
		lastTime=time;

    }


}
