package org.ginsim.servicegui.tool.reg2dyn;

import java.awt.Frame;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;


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