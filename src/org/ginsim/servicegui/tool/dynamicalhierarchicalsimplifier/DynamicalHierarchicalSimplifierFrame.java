package org.ginsim.servicegui.tool.dynamicalhierarchicalsimplifier;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.core.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraph;
import org.ginsim.core.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;


public class DynamicalHierarchicalSimplifierFrame extends StackDialog {
	
	private static final long serialVersionUID = -8030923820262034000L;
	private JFrame frame;
	private DynamicalHierarchicalGraph graph;
	private JPanel mainPanel;

	public DynamicalHierarchicalSimplifierFrame(JFrame frame, DynamicalHierarchicalGraph graph) {
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
    public void simplify_through_delete(DynamicalHierarchicalGraph graph) {

		Set vertexSet = new HashSet(); //cause can't remove and iterate on the same structure.
		for (Iterator it = graph.getNodes().iterator(); it.hasNext();) {
			vertexSet.add(it.next());
		}
		for (Iterator it = vertexSet.iterator(); it.hasNext();) {
			DynamicalHierarchicalNode source = (DynamicalHierarchicalNode) it.next();
			if (source.getType() == DynamicalHierarchicalNode.TYPE_TRANSIENT_COMPONENT) {
				graph.removeNode( source);
			}
		}
		GUIManager.getInstance().whatToDoWithGraph(graph, true);
    }
}
