package org.ginsim.servicegui.tool.modelsimplifier;

import java.util.List;

import javax.swing.JOptionPane;

import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.SimpleGenericList;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifier;
import org.ginsim.service.tool.modelsimplifier.ReductionLauncher;
import org.ginsim.service.tool.modelsimplifier.RemovedInfo;



public class ModelSimplifierConfigDialog extends StackDialog implements ReductionLauncher {
	private static final long	serialVersionUID	= 3618855894072951620L;

	private final RegulatoryGraph graph;
    private final ReductionConfigurationPanel lp;

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
			isRunning = true;
			new ModelSimplifier(graph, lp.getSelectedItem(), this, true);
	        brun.setEnabled(false);
		}
	}
	
	@Override
	public void endSimu( Graph graph, Exception e) {
    	isRunning = false;
        if (null == graph) {
            GUIMessageUtils.openErrorDialog(e.getMessage(), GUIManager.getInstance().getFrame(graph));
            brun.setEnabled(true);
        } else {
            GUIManager.getInstance().whatToDoWithGraph(graph, this.graph, false);
            cancel();
        }
    }

	@Override
	public boolean showPartialReduction(List<RemovedInfo> l_todo) {

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

