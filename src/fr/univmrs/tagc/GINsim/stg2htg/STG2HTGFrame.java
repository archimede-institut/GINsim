package fr.univmrs.tagc.GINsim.stg2htg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class STG2HTGFrame extends StackDialog implements ActionListener {
	private static final long serialVersionUID = -7430762236435581864L;
	private Graph graph;
	private JFrame frame;
	
	public STG2HTGFrame(JFrame frame, Graph graph) {
		
		super(frame, "STR_STG2HTG", 420, 260);
		this.graph = graph;
   }

	protected void run() {
		Thread thread = new STG2HTG(frame, graph);
		thread.start();
	}
	
	public void actionPerformed(ActionEvent e) {
	}

	public Graph getGraph() {
		
		return graph;
	}
	
	public void cancel() {
		super.cancel();
	}
}
