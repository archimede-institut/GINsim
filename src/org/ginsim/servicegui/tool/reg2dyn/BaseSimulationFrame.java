package org.ginsim.servicegui.tool.reg2dyn;

import java.awt.Frame;
import java.awt.Insets;

import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.reg2dyn.SimulationManager;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;



public abstract class BaseSimulationFrame extends StackDialog implements SimulationManager {
    /**  */
    private static final long serialVersionUID = 8275117764047606650L;

    Insets indentInset = new Insets(0, 30, 0, 0);
//    protected boolean isrunning = false;

    /**
     * Callback when the simulation is complete.
     * @param graph the simulated graph
     */
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

    /**
     * Callback when a stable state is encountered during the simulation.
     * @param item the stable state
     */
    public abstract void addStableState(SimulationQueuedState item);
}