package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class GsDynamicalHierarchicalSimplifierFrame extends StackDialog {
	
	private static final long serialVersionUID = -8030923820262034000L;
	private JFrame frame;
	private GsDynamicalHierarchicalGraph graph;
	private JPanel mainPanel;

	public GsDynamicalHierarchicalSimplifierFrame(JFrame frame, GsDynamicalHierarchicalGraph graph) {
		super(frame, Translator.getString("STR_dynHier_simplify"), 800, 600);
		this.frame = frame;
		this.graph = graph;
        initialize();
        this.addWindowListener(new java.awt.event.WindowAdapter() { 
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancel();
            }
        });	
	}

	private void initialize() {
		setMainPanel(getMainPanel());
		Dimension preferredSize = getPreferredSize();
		setSize(preferredSize.width+20, preferredSize.height+20); //Padding 10px;		
	}

	private Component getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.add(new JLabel(Translator.getString("STR_dynHier_simplify")));
		}
		return mainPanel;
	}
	
	public void run() {
		simplify_through_delete(graph);
		cancel();
	}
    
	/**
	 * Will delete all the transients components.
	 * 
	 * Careful : the graph will change
	 * 
	 * @param frame a parent frame for the whatToDoFrame
	 * @param graph the graph that will be altered
	 */
    public void simplify_through_delete(GsDynamicalHierarchicalGraph graph) {

		Set vertexSet = new HashSet(); //cause can't remove and iterate on the same structure.
		for (Iterator it = graph.getVertices().iterator(); it.hasNext();) {
			vertexSet.add(it.next());
		}
		for (Iterator it = vertexSet.iterator(); it.hasNext();) {
			GsDynamicalHierarchicalNode source = (GsDynamicalHierarchicalNode) it.next();
			if (source.getType() == GsDynamicalHierarchicalNode.TYPE_TRANSIENT_COMPONENT) {
				graph.removeVertex( source);
			}
		}
		GsEnv.whatToDoWithGraph(frame, graph, true);
    }
}
