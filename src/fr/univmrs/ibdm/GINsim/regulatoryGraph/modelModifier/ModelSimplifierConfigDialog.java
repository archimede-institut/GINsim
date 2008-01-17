package fr.univmrs.ibdm.GINsim.regulatoryGraph.modelModifier;

import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.widgets.StackDialog;


public class ModelSimplifierConfigDialog extends StackDialog {
	private static final long	serialVersionUID	= 3618855894072951620L;

	GsRegulatoryGraph graph;
	
	ModelSimplifierConfigDialog(GsRegulatoryGraph graph) {
		super(graph.getGraphManager().getMainFrame(), "modelSimplifier", 600, 500);
		this.graph = graph;
		setTitle("select nodes to remove");
		setMainPanel(new SimplifierConfigPanel());
		setVisible(true);
	}
	
	protected void run() {
		// TODO: make remove list configurable
		
		// temporary hack
		ModelSimplifierConfig cfg = new ModelSimplifierConfig();
		Iterator it = graph.getNodeOrder().iterator();
		while (it.hasNext()) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
			if (vertex.getId().startsWith("r_")) {
				cfg.m_removed.put(vertex, null);
			}
		}
		new ModelSimplifier(graph, cfg, this);
	}
	
    public void endSimu(GsGraph graph) {
        if (null == graph) {
            GsEnv.error("no state transition graph", this.graph.getGraphManager().getMainFrame());
        } else {
            GsEnv.whatToDoWithGraph(this.graph.getGraphManager().getMainFrame(), graph);
        }
        cancel();
    }

}

class SimplifierConfigPanel extends JPanel {
	private static final long	serialVersionUID	= 1112333567261768396L;

	public SimplifierConfigPanel() {
		add(new JTextArea("TODO: config UI.\n" +
				"For now all nodes with a name starting with \"r_\" will be removed\n"+
				"\nWARNING: this is work in progress and not yet ready: it does _NOT_ work"));
	}
}