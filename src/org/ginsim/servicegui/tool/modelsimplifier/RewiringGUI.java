package org.ginsim.servicegui.tool.modelsimplifier;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ginsim.commongui.dialog.DefaultDialogSize;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.utils.dialog.stackdialog.HandledStackDialog;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialogHandler;
import org.ginsim.service.tool.modelsimplifier.ModelRewiring;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierService;
import org.ginsim.service.tool.modelsimplifier.RewiringAction;

public class RewiringGUI extends JPanel implements StackDialogHandler {

	private static DefaultDialogSize SIZE = new DefaultDialogSize("Rewiring", 200, 200);

	private final ModelRewiring rewirer;
	private final RegulatoryGraph graph;
	
	public RewiringGUI( RegulatoryGraph graph) {
		this.graph = graph;
		this.rewirer = ServiceManager.get( ModelSimplifierService.class).getRewirer( graph);
		Collection<RegulatoryNode> targets = rewirer.lookupTargets();
		Collection<RegulatoryNode> rewired = rewirer.lookupRewired();

//		GraphGUI<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> gui = GUIManager.getInstance().getGraphGUI(graph);
//		GraphSelection<RegulatoryNode, RegulatoryMultiEdge> selection = gui.getSelection();
//		selection.unselectAll();
//		selection.setSelectedNodes(rewired);

		JTextField tf = new JTextField();
		tf.setText("Pure targets: "+targets.size()+"\nRewired: "+rewired.size());
		add(tf);
	}

	@Override
	public void setStackDialog(HandledStackDialog dialog) {
		// TODO Auto-generated method stub
	}

	@Override
	public Component getMainComponent() {
		return this;
	}

	@Override
	public boolean run() {
		System.out.println("run rewiring");
		
		rewirer.setRewiringAction(RewiringAction.LowPriority);
		rewirer.call();
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public DefaultDialogSize getDefaultSize() {
		return SIZE;
	}
	
}
