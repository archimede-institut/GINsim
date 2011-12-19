package org.ginsim.servicegui.tool.reg2dyn;

import java.awt.Frame;
import java.awt.Insets;

import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;



public abstract class BaseSimulationFrame extends StackDialog implements SimulationManager {
    /**  */
    private static final long serialVersionUID = 8275117764047606650L;

    Insets indentInset = new Insets(0, 30, 0, 0);
//    protected boolean isrunning = false;

    public abstract void endSimu( Graph graph);


    public BaseSimulationFrame(Frame parent, String id, int w, int h) {
        super(parent, id, w, h);
    }

    public void setProgress(int n) {
//        if (isrunning) {
            setMessage(""+n);
//        }
    }
    public void setProgress(String s) {
//      if (isrunning) {
          setMessage(s);
//      }
  }

    public void addStableState(SimulationQueuedState item) {
    	LogManager.trace("stable (depth "+item.depth+"): ");
    }
}