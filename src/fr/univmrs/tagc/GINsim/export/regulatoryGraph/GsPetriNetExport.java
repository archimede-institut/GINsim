package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JPanel;

import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.reg2dyn.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStatePanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateStore;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.InitialStatesIterator;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.widgets.StackDialog;

/**
 * Export a regulatory graph to petri net (shared methods).
 *
 *<p> translating a regulatory graph to a petri net is done as follow:
 * <ul>
 *  <li>each node will be represented by two places, a negative one and a positive one.
 *      Markers in the positive place represent it's level. if it is not at it's maximum
 *      missing marker(s) will be in it's negative place: the number of markers in the petri net is constant</li>
 *  <li>each logical parameter will be represented by transition(s) with "test" arcs to
 *      non-modified places and "normal" arcs to the positive and negative place of the modified place.</li>
 * </ul>
 *
 * with some simplifications:
 * <ul>
 *  <li>work on the tree representation of logical parameters and use ranges instead of exact values as precondition of transitions</li>
 *  <li>"input" nodes are specials: no transition will affect them and their basal value will be used as initial markup</li>
 *  <li>autoregulation can trigger some cases where a transition can't be fired, these are not created</li>
 * </ul>
 *
 *<p>references:
 *<ul>
 *  <li>Simao, E., Remy, E., Thieffry, D. and Chaouiya, C.: Qualitative modelling of
 *      Regulated Metabolic Pathways: Application to the Tryptophan biosynthesis in E. Coli.</li>
 *  <li>Chaouiya, C., Remy, E. and Thieffry, D.: Petri Net Modelling of Biological Regulatory
 *      Networks</li>
 *</ul>
 */
public class GsPetriNetExport extends GsAbstractExport {
	static Vector v_format = new Vector();
	static final String PNFORMAT = "export.petriNet.defaultFormat";
	static {
		v_format = new Vector();
		v_format.add(new GsPetriNetExportINA());
		v_format.add(new GsPetriNetExportPNML());
		v_format.add(new GsPetriNetExportAPNN());
	}

	public GsPetriNetExport() {
		id = "PetriNet";
	}

	public Vector getSubFormat() {
		return v_format;
	}

    /**
     * extract transitions from a tree view of logical parameters.
     *
     * @param v_result
     * @param node tree view of logical parameters on one node
     * @param nodeIndex index of the considered node (in the regulatory graph)
     * @param v_node all nodes
     * @param len number of nodes in the original graph
     */
    protected static void browse(List v_result, OmddNode node, int[][] t_priorities, int nodeIndex, List v_node, int len) {
        if (node.next == null) {
            TransitionData td = new TransitionData();
            td.value = node.value;
            td.maxValue = ((GsRegulatoryVertex)v_node.get(nodeIndex)).getMaxValue();
            td.nodeIndex = nodeIndex;
            td.t_cst = null;
            if (t_priorities != null) {
				td.increasePriority = t_priorities[nodeIndex][0];
				td.decreasePriority = t_priorities[nodeIndex][1];
			}
            v_result.add(td);
        } else {
            int[][] t_cst = new int[len][3];
            for (int i=0 ; i<t_cst.length ; i++) {
                t_cst[i][0] = -1;
            }
            browse(v_result, t_cst, 0, node, t_priorities, nodeIndex, v_node);
        }
    }

    private static void browse(List v_result, int[][] t_cst, int level, OmddNode node, int[][] t_priorities, int nodeIndex, List v_node) {
        if (node.next == null) {
            TransitionData td = new TransitionData();
            td.value = node.value;
            td.maxValue = ((GsRegulatoryVertex)v_node.get(nodeIndex)).getMaxValue();
            td.nodeIndex = nodeIndex;
            if (t_priorities != null) {
				td.increasePriority = t_priorities[nodeIndex][0];
				td.decreasePriority = t_priorities[nodeIndex][1];
			}
            td.t_cst = new int[t_cst.length][3];
            int ti = 0;
            for (int i=0 ; i<t_cst.length ; i++) {
                int index = t_cst[i][0];
                if (index == -1) {
                    break;
                }
                if (index == nodeIndex) {
                    td.minValue = t_cst[i][1];
                    td.maxValue = t_cst[i][2];
                } else {
                    td.t_cst[ti][0] = index;
                    td.t_cst[ti][1] = t_cst[i][1];
                    td.t_cst[ti][2] = ((GsRegulatoryVertex)v_node.get(index)).getMaxValue() - t_cst[i][2];
                    if (td.t_cst[ti][1] > 0 || td.t_cst[ti][2] > 0) {
                        ti++;
                    }
                }
            }
            if (ti == 0) {
                td.t_cst = null;
            } else {
                td.t_cst[ti][0] = -1;
            }
            v_result.add(td);
            return;
        }

        // specify on which node constraints are added
        t_cst[level][0] = node.level;
        for (int i=0 ; i<node.next.length ; i++) {
            OmddNode next = node.next[i];
            int j=i+1;
            while(j<node.next.length) {
                if (node.next[j] == next) {
                    j++;
                } else {
                    break;
                }
            }
            j--;
            t_cst[level][1] = i;
            t_cst[level][2] = j;
            browse(v_result, t_cst, level+1, next, t_priorities, nodeIndex, v_node);
            i = j;
        }
        // "forget" added constraints
        t_cst[level][0] = -1;
    }

	protected void doExport(GsExportConfig config) {
		// nothing needed here: subformat do all the job
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (graph instanceof GsRegulatoryGraph) {
        	return new GsPluggableActionDescriptor[] {
        			new GsPluggableActionDescriptor("STR_PetriNet", "STR_PetriNet_descr", null, this, ACTION_EXPORT, 0)
        	};
        }
        return null;
	}

	public boolean needConfig(GsExportConfig config) {
		return true;
	}

	protected JComponent getConfigPanel(GsExportConfig config, StackDialog dialog) {
		return new PNExportConfigPanel(config, dialog);
	}

	/**
	 * prepare the PN export:
	 *   - read/set initial markup
	 *   - build the set of transitions
	 *
	 * @param config
	 * @param t_transition
	 * @param t_tree
	 * @return the initial markup
	 */
    protected static short[][] prepareExport(GsExportConfig config, List[] t_transition, OmddNode[] t_tree) {
    	List nodeOrder = config.getGraph().getNodeOrder();
		int len = nodeOrder.size();
		// get the selected initial state
		Iterator it_state = new InitialStatesIterator(nodeOrder, ((GsInitialStateStore)config.getSpecificConfig()));
		int[] t_state = (int[])it_state.next();

		PNConfig specConfig = (PNConfig)config.getSpecificConfig();
		// apply mutant
		GsRegulatoryMutantDef mutant = (GsRegulatoryMutantDef)specConfig.store.getObject(0);
		if (mutant != null) {
			mutant.apply(t_tree, (GsRegulatoryGraph)config.getGraph());
		}
		// use priority classes
		PriorityClassDefinition priorities = (PriorityClassDefinition)specConfig.store.getObject(1);
		int[][] t_priorities = null;
		if (priorities != null) {
			t_priorities = new int[len][2];
			int[][] t_pclass = priorities.getPclass(nodeOrder);
			for (int i=0 ; i<t_pclass.length ; i++) {
				int[] t_class = t_pclass[i];
				int priority = t_class[0];
				for (int j=2 ; j<t_class.length ; j++) {
					int index = t_class[j++];
					System.out.println("priority of "+priority+" for "+index+" ("+t_class[j]+")");
					switch (t_class[j]) {
						case 1:
							t_priorities[index][0] = priority;
							break;
						case -1:
							t_priorities[index][1] = priority;
							break;
						default:
							t_priorities[index][0] = priority;
							t_priorities[index][1] = priority;
					}
				}
			}
		}
		
		short[][] t_markup = new short[len][2];
        for (int i=0 ; i<len ; i++) {
            OmddNode node = t_tree[i];
            GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(i);

//            if (manager.getIncomingEdges(vertex).size() == 0) {
//                // input node: no regulator, use basal value as initial markup ??
//                t_markup[i][0] = vertex.getBaseValue();
//                t_markup[i][1] = (short)(vertex.getMaxValue() - vertex.getBaseValue());
//            } else {
                // normal node, initial markup = 0
                t_markup[i][0] = (short)t_state[i];
                t_markup[i][1] = (short)(vertex.getMaxValue()-t_state[i]);
                Vector v_transition = new Vector();
                t_transition[i] = v_transition;
                GsPetriNetExport.browse(v_transition, node, t_priorities, i, nodeOrder, len);
//            }
        }
		return t_markup;
    }
}

class TransitionData {
    /** target value of this transition */
    public int value;
    
    /** index of the concerned node */
    public int nodeIndex;

    /** minvalue for the concerned node (0 unless an autoregulation is present) */
    public int minValue;
    /** maxvalue for the concerned node (same as node's maxvalue unless an autoregulation is present) */
    public int maxValue;
    
    /** priority when decreasing */
    public int decreasePriority = 0;
    /** priority when increasing */
    public int increasePriority = 0;
    
    /** constraints of this transition: each row express range constraint for one of the nodes
     * and contains 3 values:
     *  <ul>
     *      <li>index of the node (or -1 after the last constraint)</li>
     *      <li>bottom and top limit of the range (top limit is pre-processed: maxvalue - realLimit)</li>
     *  </ul>
     */
    public int[][] t_cst;
    
    public GsRegulatoryMutantDef mutant;
    public GsRegulatoryMutantDef getMutant() {
        return mutant;
    }
	public void setMutant(GsRegulatoryMutantDef mutant) {
		this.mutant = mutant;
	}
	
}

class PNExportConfigPanel extends JPanel {
    private static final long serialVersionUID = 9043565812912568136L;

	PrioritySelectionPanel priorityPanel = null;
	PNConfig specConfig = new PNConfig();

	protected PNExportConfigPanel (GsExportConfig config, StackDialog dialog) {
    	config.setSpecificConfig(specConfig);
    	
    	GsGraph graph = config.getGraph();
    	MutantSelectionPanel mutantPanel = null;
    	
    	GsInitialStatePanel initPanel = new GsInitialStatePanel(dialog, graph, false);
    	initPanel.setParam(specConfig);
    	
    	setLayout(new GridBagLayout());
    	mutantPanel = new MutantSelectionPanel(dialog, (GsRegulatoryGraph)graph, specConfig.store);
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(mutantPanel, c);

		GsSimulationParameterList paramList = (GsSimulationParameterList)graph.getObject(GsSimulationParametersManager.key, true);
        priorityPanel = new PrioritySelectionPanel(dialog, paramList.pcmanager);
        priorityPanel.setStore(specConfig.store, 1);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(priorityPanel, c);
		priorityPanel.setFilter(PriorityClassManager.FILTER_NO_SYNCHRONOUS);

		c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 2;
    	c.gridwidth = 2;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.fill = GridBagConstraints.BOTH;
    	add(initPanel, c);
    }
}

class PNConfig implements GsInitialStateStore {

    Map m_init = new HashMap();
    Map m_input = new HashMap();
	ObjectStore store = new ObjectStore(2);

	public Map getInitialState() {
		return m_init;
	}

    public Map getInputState() {
        return m_input;
    }
}
