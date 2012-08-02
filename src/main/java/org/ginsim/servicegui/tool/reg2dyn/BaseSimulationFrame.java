package org.ginsim.servicegui.tool.reg2dyn;

import java.awt.Frame;
import java.awt.Insets;

import org.ginsim.common.callable.ProgressListener;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;



public abstract class BaseSimulationFrame extends LogicalModelActionDialog {
    private static final long serialVersionUID = 8275117764047606650L;

    public BaseSimulationFrame(RegulatoryGraph lrg, Frame parent, String id, int w, int h) {
        super(lrg, parent, id, w, h);
    }

    public void setProgress(int n) {
    	setMessage(""+n);
    }
    public void setProgress(String s) {
    	setMessage(s);
  }
}