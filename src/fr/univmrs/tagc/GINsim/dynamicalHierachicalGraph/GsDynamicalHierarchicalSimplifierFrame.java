package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org._3pq.jgrapht.edge.DirectedEdge;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class GsDynamicalHierarchicalSimplifierFrame extends StackDialog {
	
	private static final long serialVersionUID = -8030923820262034000L;
	private JFrame frame;
	private GsDynamicalHierarchicalGraph graph;
	private JPanel mainPanel;
	private JRadioButton actionMerge, actionDelete;
	private ButtonGroup actions;
//	private JCheckBox duplicateGraph;

	public GsDynamicalHierarchicalSimplifierFrame(JFrame frame, GsDynamicalHierarchicalGraph graph) {
		super(frame, Translator.getString("STR_dynHier_simplify"), 800, 600);
		this.frame = frame;
		this.graph = graph;
        initialize();
//        this.setTitle(Translator.getString("STR_dynHier_simplify"));
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
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
		    actions = new ButtonGroup();

			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			String name = Translator.getString("STR_dynHier_simplify_merge");
		    actionMerge = new JRadioButton(name);
		    actionMerge.setActionCommand(name);
		    actionMerge.setSelected(true);
		    actions.add(actionMerge);
		    mainPanel.add(actionMerge);
		    
			c.gridy++;
		    name = Translator.getString("STR_dynHier_simplify_delete");
		    actionDelete = new JRadioButton(name);
		    actionDelete.setActionCommand(name);
		    actions.add(actionDelete);
		    mainPanel.add(actionDelete);

//			c.gridy++;
//		    duplicateGraph = new JCheckBox(Translator.getString("STR_DH_simplifier_create_new_graph"));
//		    duplicateGraph.setSelected(true);
//		    mainPanel.add(duplicateGraph);
		}
		return mainPanel;
	}
	
	public void run() {
		//GsDynamicalHierarchicalGraph graph = graph;
		//if (duplicateGraph.isSelected()) graph = (GsDynamicalHierarchicalGraph) graph.clone();
		if (actionMerge.isSelected())
			try {
				simplify_through_merge(graph);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else if (actionDelete.isSelected()) simplify_through_delete(graph);
		cancel();
	}

	/**
	 * Will merge all the linked transients components.
	 * In other words, each transient children of a transient component will be merge with it. 
	 * 
	 * Careful : the graph will change
	 * 
	 * @param frame a parent frame for the whatToDoFrame
	 * @param graph the graph that will be altered
	 * @throws Exception 
	 */
    public void simplify_through_merge(GsDynamicalHierarchicalGraph graph) throws Exception {
		GsGraphManager gm = graph.getGraphManager();
		Set vertexSet = new HashSet(); //cause can't remove and iterate on the same structure.
//		
//		for (Iterator it = gm.getVertexIterator(); it.hasNext();) {
//			vertexSet.add(it.next());
//		}
//		for (Iterator it = vertexSet.iterator(); it.hasNext();) {
//			GsDynamicalHierarchicalNode source = (GsDynamicalHierarchicalNode) it.next();
//			if (source.getType() == GsDynamicalHierarchicalNode.TYPE_TRANSIENT_COMPONENT) {
//				Set childs = new HashSet();
//				for (Iterator itc = gm.getOutgoingEdges(source).iterator(); itc.hasNext();) {
//					DirectedEdge e = (DirectedEdge) itc.next();
//					childs.add(e.getTarget());					
//				}
//				for (Iterator itc2 = childs.iterator(); itc2.hasNext();) {
//					GsDynamicalHierarchicalNode target = (GsDynamicalHierarchicalNode) itc2.next();
//					if (target.getType() == GsDynamicalHierarchicalNode.TYPE_TRANSIENT_COMPONENT) {
//						for (Iterator itc3 = gm.getOutgoingEdges(target).iterator(); itc3.hasNext();) {
//							DirectedEdge e = (DirectedEdge) itc3.next();
//							gm.addEdge(source, e.getSource(), null);
//						}
//						gm.removeVertex(target);
//						source.merge(target, new HashSet(), graph.getNodeOrder().size());
//					}					
//				}
//			}
//		}
		GsEnv.whatToDoWithGraph(frame, graph, true);
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
		GsGraphManager gm = graph.getGraphManager();
		Set vertexSet = new HashSet(); //cause can't remove and iterate on the same structure.
		for (Iterator it = gm.getVertexIterator(); it.hasNext();) {
			vertexSet.add(it.next());
		}
		for (Iterator it = vertexSet.iterator(); it.hasNext();) {
			GsDynamicalHierarchicalNode source = (GsDynamicalHierarchicalNode) it.next();
			if (source.getType() == GsDynamicalHierarchicalNode.TYPE_TRANSIENT_COMPONENT) {
				gm.removeVertex(source);
			}
		}
		GsEnv.whatToDoWithGraph(frame, graph, true);
    }
}
