package org.ginsim.servicegui.tool.composition;





import org.ginsim.common.application.GsException;
import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;

import org.ginsim.gui.GUIManager;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.composition.CompositionService;
import org.ginsim.servicegui.tool.composition.CompositionPanel;

/*
 * The composition dialog
 * 
 * @author Nuno D. Mendes
 */

public class CompositionConfigDialog extends StackDialog {

	// TODO: Replace all strings by token in messages.properties

	private static final long serialVersionUID = 8046844091168372569L;
	RegulatoryGraph graph = null;
	CompositionPanel dialog = null;
	boolean isRunning = false;

	CompositionConfigDialog(RegulatoryGraph graph) {
		super(graph, "modelComposer", 700, 300);
		this.graph = graph;
		setTitle("Specify Composition parameters");

		CompositionPanel panel = new CompositionPanel(graph);
		dialog = panel;
		brun.setText("Compose instances");
		brun.setToolTipText("Compose");
		setMainPanel(panel.getMainPanel());
		setVisible(true);
		setSize(getPreferredSize());

	}

	protected void run() throws GsException {
		setRunning(true);
		brun.setEnabled(false);

		CompositionService service = ServiceManager.getManager().getService(
				CompositionService.class);

		// TODO: Deal here with invalid integration functions w.r.t. to given
		// input
		// and proper components using NotificationManager
		RegulatoryGraph composedGraph = service.run(graph, dialog.getConfig());
		GUIManager.getInstance().whatToDoWithGraph(composedGraph, true);

		cancel();
	}


}
