package fr.univmrs.tagc.GINsim.reg2dyn;

import java.awt.Insets;

import javax.swing.JFrame;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.common.widgets.StackDialog;


public abstract class BaseReg2DynFrame extends StackDialog implements SimulationManager {
    /**  */
    private static final long serialVersionUID = 8275117764047606650L;

    Insets indentInset = new Insets(0, 30, 0, 0);
//    protected boolean isrunning = false;

    public abstract void endSimu(GsGraph graph);


    public BaseReg2DynFrame(JFrame parent, String id, int w, int h) {
        super(parent, id, w, h);
    }

    public void setProgress(int n) {
//        if (isrunning) {
            setMessage(""+n);
//        }
    }

    public void addStableState(SimulationQueuedState item) {
        System.out.print("stable (depth "+item.depth+"): ");
    }
}