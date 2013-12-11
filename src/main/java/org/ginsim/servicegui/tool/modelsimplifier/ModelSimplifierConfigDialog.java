package org.ginsim.servicegui.tool.modelsimplifier;

import java.util.List;

import javax.swing.JOptionPane;

import org.colomoto.common.task.Task;
import org.colomoto.common.task.TaskListener;
import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.SimpleGenericList;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.modelsimplifier.ReductionTask;
import org.ginsim.service.tool.modelsimplifier.ReconstructionTask;
import org.ginsim.service.tool.modelsimplifier.ReductionLauncher;


public class ModelSimplifierConfigDialog extends StackDialog implements ReductionLauncher, TaskListener {
	private static final long	serialVersionUID	= 3618855894072951620L;

	private final RegulatoryGraph graph;
    private final ReductionConfigurationPanel lp;

    ReductionTask simplifier = null;
    ReconstructionTask reconstructionTask = null;

    private boolean isRunning = false;
	
	ModelSimplifierConfigDialog(RegulatoryGraph graph) {
		super(graph, "modelSimplifier", 600, 500);
		this.graph = graph;
		setTitle("select nodes to remove");
		
        lp = new ReductionConfigurationPanel(graph);

		setMainPanel(lp);
		setVisible(true);
	}
	
	protected void run() {
		if (!isRunning && lp.getSelectedItem() != null) {
			simplifier = new ReductionTask(graph, lp.getSelectedItem(), this);
            simplifier.background(this);
            isRunning = true;
            brun.setEnabled(false);
		}
	}
	
	@Override
    public void taskUpdated(Task task) {
        if (task == null) {
            return;
        }

        if (task == simplifier) {
            LogicalModel model = simplifier.getResult();
            simplifier = null;
            reconstructionTask = new ReconstructionTask(model, graph);
            reconstructionTask.background(this);
            return;
        }

        if (task == reconstructionTask) {
    	    isRunning = false;
            RegulatoryGraph newGraph = reconstructionTask.getResult();
            GUIManager.getInstance().whatToDoWithGraph(newGraph, this.graph, false);
            cancel();
        }
    }

	@Override
	public boolean showPartialReduction(List<NodeInfo> l_todo) {

        int choice = JOptionPane.showConfirmDialog(this, "show result of partial reduction?", "Reduction failed",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

		if (choice == JOptionPane.NO_OPTION) {
	    	isRunning = false;
			cancel();
			return false;
		}
		return true;
	}

}


class SimplifierConfigContentList extends SimpleGenericList<RegulatoryNode> {

	SimplifierConfigContentList(List<RegulatoryNode> nodeOrder) {
		super(nodeOrder);
		canAdd = false;
		canOrder = false;
		canEdit = true;
		nbcol = 2;
		t_type = new Class[2];
		t_type[0] = String.class;
		t_type[1] = Boolean.class;
	}
}

