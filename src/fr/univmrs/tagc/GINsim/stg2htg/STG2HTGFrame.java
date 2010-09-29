package fr.univmrs.tagc.GINsim.stg2htg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class STG2HTGFrame extends StackDialog implements ActionListener {
	private static final long serialVersionUID = -7430762236435581864L;
	private GsGraph graph;
	
	public STG2HTGFrame(JFrame frame, GsGraph graph) {
		super(frame, "stg2htg", 420, 260);
		this.graph = graph;
		System.out.println("frame created");
   }

	protected void run() {
		Thread thread = new STG2HTG(graph);
		thread.start();
	}
	
	public void actionPerformed(ActionEvent e) {
	}

	public GsGraph getGraph() {
		return graph;
	}
	
	public void cancel() {
		super.cancel();
	}
}
