package fr.univmrs.tagc.GINsim.circuit;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.univmrs.tagc.GINsim.connectivity.AlgoConnectivity;
import fr.univmrs.tagc.GINsim.connectivity.GsNodeReducedData;
import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.ProgressListener;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.Label;
import fr.univmrs.tagc.common.widgets.StackDialog;
import fr.univmrs.tagc.common.widgets.treetable.AbstractTreeTableModel;
import fr.univmrs.tagc.common.widgets.treetable.JTreeTable;
import fr.univmrs.tagc.common.widgets.treetable.TreeTableModel;
import fr.univmrs.tagc.common.widgets.treetable.TreeTableModelAdapter;

/**
 * configuration/status frame for circuit search/analyse
 */
public class GsCircuitFrame extends StackDialog implements ProgressListener {

    private static final long serialVersionUID = 2671795894716799300L;

	protected static final boolean DO_CLEANUP	= true;
    
    private AlgoConnectivity algoC = null;
    protected GsRegulatoryGraph graph;

    protected static final int STATUS_NONE = 0;
    private static final int STATUS_SCC = 1;
    private static final int STATUS_SEARCH_CIRCUIT = 2;
    private static final int STATUS_SHOW_CIRCUIT = 3;
    private static final int STATUS_SHOW_RESULT = 4;
    private int status = STATUS_NONE;

    private Vector v_circuit = new Vector();
    protected JTreeTable tree = null;
    private GsCircuitTreeModel treemodel = null;
    private JPanel configDialog = null;
    private JPanel resultPanel = null;
    private CardLayout cards;
    private JSplitPane splitPane = null;
    private javax.swing.JPanel jContentPane = null;
    private javax.swing.JLabel labelProgression = null;
    private JScrollPane sp = null;
    private JScrollPane sp2 = null;
    private JTextArea jta = null;
    private GsCircuitSearchStoreConfig config = null;
    ObjectStore mutantstore = new ObjectStore();
    MutantSelectionPanel mutantPanel;

	private JButton	but_pyexport;

    /**
     * This is the default constructor
     * 
     * @param frame
     * @param graph
     */
    public GsCircuitFrame(JFrame frame, GsGraph graph) {
        super(frame, "display.circuit", 500, 300);
        if (graph == null || !(graph instanceof GsRegulatoryGraph)) {
			Tools.error("no graph", frame);
		}
        this.graph = (GsRegulatoryGraph) graph;
        initialize();
        updateStatus(STATUS_NONE);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setMainPanel(getJContentPane());
        this.setTitle(Translator.getString("STR_circuit"));
        this.setVisible(true);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancel();
            }
        });
    }
    /**
     * close the circuit search/analyse dialog. stop running algo and close
     * configuration dialog if appropriate.
     */
    protected void cancel() {
        if (algoC != null && algoC.isAlive()) {
            algoC.cancel();
        }
        graph = null;
        super.cancel();
        dispose();
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            cards = new CardLayout();
            jContentPane.setLayout(cards);
            jContentPane.add(getConfigPanel(), "config");
            jContentPane.add(getResultPanel(), "result");
            cards.show(jContentPane, "config");
        }
        return jContentPane;
    }

    private javax.swing.JPanel getResultPanel() {
        if (resultPanel == null) {
        	resultPanel = new javax.swing.JPanel();
        	resultPanel.setLayout(new GridBagLayout());
        	GridBagConstraints c = new GridBagConstraints();
        	c.gridx = 0;
        	c.gridy = 0;
        	c.weightx = 1;
        	c.fill = GridBagConstraints.BOTH;
        	mutantPanel = new MutantSelectionPanel(this, graph, mutantstore);
        	resultPanel.add(mutantPanel, c);

        	c = new GridBagConstraints();
        	c.gridx = 0;
        	c.gridy = 1;
        	c.weightx = 1;
        	c.fill = GridBagConstraints.BOTH;
        	resultPanel.add(getLabelProgression(), c);
        	
        	c = new GridBagConstraints();
        	c.gridx = 0;
        	c.gridy = 2;
        	c.weightx = 1;
        	c.weighty = 1;
        	c.fill = GridBagConstraints.BOTH;
        	resultPanel.add(getSplitPane(), c);
        	
        	// python button
        	c = new GridBagConstraints();
        	c.gridx = 0;
        	c.gridy = 3;
        	c.anchor = GridBagConstraints.WEST;
        	resultPanel.add(get_pythonPanel(), c);
        }
        return resultPanel;
    }
    
    private JButton get_pythonPanel() {
    	if (but_pyexport == null) {
    		but_pyexport = new JButton("to python");
    		but_pyexport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pyExport();
				}
			});
    	}
    	return but_pyexport;
    }
    
    protected void pyExport() {
    	List no = graph.getNodeOrder();
    	List l_func = (List)treemodel.m_parent.get(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.FUNCTIONNAL]);
    	if (l_func == null) {
    		System.out.println("no func...");
    		return;
    	}
    	StringBuffer s = new StringBuffer("#!/usr/bin/env python\n"
    			+ "import circuittest\n"
    			+ "if __name__ == \"__main__\":\n"
    			+ "    ct = circuittest.CircuitTester()\n");
    	Iterator it = l_func.iterator();
    	while (it.hasNext()) {
    		GsCircuitDescrInTree cit = (GsCircuitDescrInTree)it.next();
			// FIXME: we are using reg graph's sign, we should not rely on it!
			if (treemodel.isLeaf(cit)) {
				circuitToPython(no, s, cit);
			} else {
				// TODO: call it for all sub-circuit!
			}
    	}
    	s.append("\n\n    ct.do_analyse((\n"
    			+ "        # TODO: add path definition here\n"
    			+ "        \n"
    			+ "    ))\n");
    	JFrame f = new JFrame("functional circuits in python");
    	JScrollPane sp = new JScrollPane();
    	sp.setViewportView(new JTextArea(s.toString()));
    	f.add(sp);
    	f.setSize(400, 300);
    	f.setVisible(true);
    }
    
    protected void circuitToPython(List no, StringBuffer s, GsCircuitDescrInTree cit) {
    	s.append("    ct.add_circuit((");
    	for (int i=0 ; i<cit.circuit.t_me.length ; i++) {
    		int idx = 0; // FIXME: get the right edge!
    		GsRegulatoryMultiEdge me = cit.circuit.t_me[i];
    		int src = no.indexOf(me.getSource());
    		int dst = no.indexOf(me.getTarget());
        	s.append("("+src+","+dst+","
        		     + me.getMin(idx)+","
        			 + (me.getSign(idx)==GsRegulatoryMultiEdge.SIGN_NEGATIVE?"-1":"1")
        			 + "),");
    	}
    	s.append("), ");
    	s.append(mdd2py(cit.circuit.t_context[0]));
    	s.append(")\n");
    }
    protected String mdd2py(OmsddNode node) {
    	if (node.next == null) {
    		return node.value == 0 ? "False" : "True";
    	}
    	String s = "("+node.level;
    	for (int i=0 ; i<node.next.length ; i++) {
    		s += "," + mdd2py(node.next[i]);
    	}
    	return s + ")";
    }
    /**
     * Verify if the specified String is an integer
     * 
     * @param s -
     *            string to be tested
     * @return true if s is an integer, false if it isn't
     */
    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    protected JPanel getConfigPanel() {
        if (configDialog == null) {
            if (config == null) {
                config = new GsCircuitSearchStoreConfig();
                config.v_list = graph.getNodeOrder();
                config.minlen = 1;
                config.maxlen = config.v_list.size();
                config.t_status = new short[graph.getNodeOrder().size()];
                config.t_constraint = new short[graph.getNodeOrder().size()][3];
                for (int i = 0; i < config.t_status.length; i++) {
                    config.t_status[i] = 3;
                    short max = ((GsRegulatoryVertex) graph.getNodeOrder().get(
                            i)).getMaxValue();
                    config.t_constraint[i][0] = 0;
                    config.t_constraint[i][1] = max;
                    config.t_constraint[i][2] = max;
                }
            }
            configDialog = new GsCircuitConfigureSearch(this, config, graph.getNodeOrder());
        }
        return configDialog;
    }

    private JScrollPane getSp() {
        if (sp == null) {
            sp = new JScrollPane();
        }
        return sp;
    }

    private JScrollPane getSp2() {
        if (sp2 == null) {
            sp2 = new JScrollPane();
        }
        return sp2;
    }

    private JSplitPane getSplitPane() {
        if (splitPane == null) {
            splitPane = new JSplitPane();
            splitPane.setTopComponent(getSp());
            splitPane.setBottomComponent(null);
            splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane.setDividerLocation(20);
            splitPane.setDividerSize(4);
        }
        return splitPane;
    }

    protected void run() {
        switch (status) {
        case STATUS_NONE:
            updateStatus(STATUS_SCC);
            algoC = new AlgoConnectivity();
            algoC.configure(graph, this, AlgoConnectivity.MODE_COMPO);
            algoC.start();
            break;
        case STATUS_SCC:
            algoC.cancel();
            cancel();
            break;
        case STATUS_SEARCH_CIRCUIT:
            break;
        case STATUS_SHOW_CIRCUIT:
        case STATUS_SHOW_RESULT:
            if (configDialog != null) {
                configDialog.setVisible(false);
            }
            runAnalyse();
            break;
        }
    }

    public void setProgressText(String text) {
        getLabelProgression().setText(text);
    }

    protected void updateStatus(int status) {
        switch (status) {
        case STATUS_NONE:
            this.status = status;
            setRunText(Translator.getString("STR_circuit_search"),Translator.getString("STR_circuit_search_descr"));
            break;
        case STATUS_SCC:
            this.status = status;
            cards.show(jContentPane, "result");
            setRunText(Translator.getString("STR_cancel"), null);
            break;
        case STATUS_SEARCH_CIRCUIT:
            break;
        case STATUS_SHOW_CIRCUIT:
            this.status = status;
            setProgressText("Number of circuits satisfying the requirements: "+ v_circuit.size());
            setRunText(Translator.getString("STR_circuit_analyse"), Translator.getString("STR_circuit_analyse_tooltip"));
            break;
        }
    }

    public void setResult(Object result) {
        updateStatus(STATUS_SEARCH_CIRCUIT);
        if (result != null && result instanceof List) {
            List l = (List) result;
            if (config != null) {
                config.setReady();
            }
            v_circuit.clear();
            for (int i = 0; i < l.size(); i++) {
                Vector v_cc = ((GsNodeReducedData) l.get(i)).getContent();
                searchCircuitInSCC(v_cc);
            }
            if (config == null || config.minlen < 2) { // search
                                                       // autoregulation-like
                                                       // circuits
            	if (config.minMust < 2) {
	                for (int i = 0; i < graph.getNodeOrder().size(); i++) {
	                    GsRegulatoryVertex vertex = (GsRegulatoryVertex) graph
	                            .getNodeOrder().get(i);
	                    if (config.minMust == 1 && config.t_status[i] == 1 ||
	                        config.minMust == 0 && config.t_status[i] == 3) {
		                    Object edge = graph.getGraphManager().getEdge(vertex,
		                            vertex);
		                    if (edge != null) {
		                        GsCircuitDescr circuit = new GsCircuitDescr();
		                        circuit.t_vertex = new GsRegulatoryVertex[] { vertex };
		                        circuit.t_me = new GsRegulatoryMultiEdge[] { (GsRegulatoryMultiEdge) ((GsDirectedEdge) edge)
		                                .getUserObject() };
		                        v_circuit.add(new GsCircuitDescrInTree(circuit, true, GsCircuitDescr.ALL));
		                    }
	                    }
	                }
                }
            }
            showCircuit();
        }
    }

    /**
     * find circuits in the SCC (Strongly Connected Component) v_cc. add found
     * circuits in v_circuit.
     * 
     * @param v_cc
     */
    private void searchCircuitInSCC(Vector v_cc) {
        GsGraphManager graphm = graph.getGraphManager();
        if (v_cc.size() < 2) {
            return;
        }
        short[][] t_cc = buildTCC(graphm, v_cc); // int[][] giving edges in
                                                    // the current SCC
        short[] t_visited = new short[t_cc.length]; // remember visited nodes
                                                    // and their position
        short[][] t_path = new short[t_cc.length][3]; // remember the followed
                                                        // path: series of
                                                        // nodes, index of the
                                                        // followed edge and
                                                        // score
        short[] t_map = new short[t_cc.length];
        short cur = 0; // num of the current node
        short pos = 0; // position of the current node in the current path
        boolean[] t_history = new boolean[t_cc.length]; // avoid refinding
                                                        // cycles not starting
                                                        // at the first gene
        t_visited[0] = 0;
        t_path[0][0] = 0;
        t_path[0][1] = 0;
        int score = 0; // score of the current circuit (to know if it can be
                        // accepted)
        t_map[0] = (short) graph.getNodeOrder().indexOf(v_cc.get(0));
        if (config != null && config.t_status[t_map[0]] == 1) {
            score++;
        }
        for (int j = 1; j < t_visited.length; j++) {
            t_visited[j] = -1;
            t_history[j] = false;
            t_map[j] = (short) graph.getNodeOrder().indexOf(v_cc.get(j));
            if (config != null && config.t_status[t_map[j]] == 1) {
                score++;
            }
        }
        if (config != null) {
            t_path[0][2] = config.t_status[t_map[0]];
        } else {
            t_path[0][2] = 3;
        }

        // if the SCC doesn't contain enough "must have" node, don't even go on
        if (config != null && score < config.minMust) {
            return;
        }

        // while we can go on
        while (true) {
            // simply follow the path until finding an already visited node
            cur = t_cc[cur][t_path[pos][1]];
            while (t_visited[cur] == -1) {
                // test "must have" and forbiden nodes
                // mark it as visited, add it to the path
                t_visited[cur] = ++pos;
                t_path[pos][0] = cur;
                t_path[pos][1] = 0;
                if (config != null) {
                    t_path[pos][2] = config.t_status[t_map[cur]];
                } else {
                    t_path[pos][2] = 3;
                }
                // go to next node
                cur = t_cc[cur][0];
            }

            // if we are here, we have just found a path!
            // first choose if it is acceptable!
            boolean accepted = true;
            if (config != null) {
                int a = t_visited[cur];
                score = pos - a + 1;
                if (score >= config.minlen && score <= config.maxlen) {
                    score = 0;
                    for (; a <= pos; a++) {
                        if (t_path[a][2] == 1) {
                            score++;
                        } else if (t_path[a][2] == 2) {
                            accepted = false;
                            break;
                        }
                    }

                } else {
                    accepted = false;
                }
            }
            if (accepted && (config == null || score >= config.minMust)
                    && !t_history[cur]) {
                GsCircuitDescr circuit = new GsCircuitDescr();
                v_circuit.add(new GsCircuitDescrInTree(circuit, true, GsCircuitDescr.ALL));
                circuit.t_vertex = new GsRegulatoryVertex[pos - t_visited[cur]
                        + 1];
                int p = 0;
                int a = t_visited[cur];
                circuit.t_vertex[p++] = (GsRegulatoryVertex) v_cc
                        .get(t_path[a][0]);
                a++;
                for (; a <= pos; a++) {
                    circuit.t_vertex[p++] = (GsRegulatoryVertex) v_cc
                            .get(t_path[a][0]);
                }

                circuit.t_me = new GsRegulatoryMultiEdge[circuit.t_vertex.length];
                GsRegulatoryVertex source = circuit.t_vertex[0];
                GsRegulatoryVertex target = null;
                for (int i = 1; i < circuit.t_vertex.length; i++) {
                    target = circuit.t_vertex[i];
                    circuit.t_me[i - 1] = (GsRegulatoryMultiEdge) ((GsDirectedEdge) graphm
                            .getEdge(source, target)).getUserObject();
                    source = target;
                }
                circuit.t_me[circuit.t_me.length - 1] = (GsRegulatoryMultiEdge) ((GsDirectedEdge) graphm
                        .getEdge(target, circuit.t_vertex[0])).getUserObject();
            }

            // rewind the path and get ready for the next search, stop if
            // nothing more can be done
            boolean goon = false;
            do {
                cur = t_path[pos][0];
                // if the current node has remaining edges
                if (++t_path[pos][1] < t_cc[cur].length) {
                    goon = true;
                    break;
                }
                // else rewind
                t_visited[cur] = -1;
                t_history[cur] = true;
                pos--;
            } while (pos >= 0);
            if (!goon) {
                break;
            }
        }
    }

    private void showCircuit() {
        updateStatus(STATUS_SHOW_CIRCUIT);
        if (tree == null) {
        	treemodel = new GsCircuitTreeModel(v_circuit);
            tree = new JTreeTable(treemodel);
        	cards.show(jContentPane, "result");
            
            getSp().setViewportView(tree);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            tree.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
				public void valueChanged(ListSelectionEvent e) {
					showInfo(((TreeTableModelAdapter)tree.getModel()).nodeForRow(tree.getSelectedRow()));
				}
			});
        }
    }

    protected void showInfo(Object selected) {
        if (jta == null) {
            return;
        }
        if (!(selected instanceof GsCircuitDescrInTree)) {
        	if (selected == null || GsCircuitTreeModel.s_root.equals(selected)) {
    			jta.setText("");
    			return;
			}
			jta.setText("no data");
            int count = treemodel.getChildCount(selected);
            if (count > 0) {
                jta.setText("contains "+count+" circuits");
            }
            return;
        }
        GsCircuitDescrInTree cdtree = (GsCircuitDescrInTree)selected;
        int index = 0;
        if (cdtree.summary) {
            if (!treemodel.isLeaf(selected)) {
                int count = treemodel.getChildCount(selected);
                jta.setText("contains "+count+" layered-circuits");
                return;
            }
            switch (cdtree.key) {
                case GsCircuitDescr.ALL:
                    index = 0;
                    break;
                case GsCircuitDescr.FUNCTIONNAL:
                    index = ((GsCircuitDescrInTree)cdtree.circuit.v_functionnal.get(0)).key;
                    break;
                case GsCircuitDescr.POSITIVE:
                    index = ((GsCircuitDescrInTree)cdtree.circuit.v_positive.get(0)).key;
                    break;
                case GsCircuitDescr.NEGATIVE:
                    index = ((GsCircuitDescrInTree)cdtree.circuit.v_negative.get(0)).key;
                    break;
                case GsCircuitDescr.DUAL:
                    index = ((GsCircuitDescrInTree)cdtree.circuit.v_dual.get(0)).key;
                    break;
            }
        } else if (cdtree.key >= cdtree.circuit.t_context.length) {
            index = 0;
        } else {
            index = cdtree.key;
        }
        
        GsCircuitDescr circuit = cdtree.circuit;
        if (circuit.t_context == null) {
            jta.setText("no data");
            return;
        }

        if (circuit.t_context[index] == OmsddNode.FALSE) {
            jta.setText(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.FALSE]);
            return;
        }
        String s = circuit.t_context[index].getString(0, graph.getNodeOrder()).trim();
        if (s.equals("")) {
            jta.setText("empty data");
        } else {
            jta.setText(s);
        }
    }

    private short[][] buildTCC(GsGraphManager graphm, Vector v_cc) {
        short[][] t_cc = new short[v_cc.size()][];
        for (short i = 0; i < t_cc.length; i++) {
            short[] t = new short[t_cc.length - 1];
            int last = 0;
            Object source = v_cc.get(i);
            for (short j = 0; j < t_cc.length; j++) {
                if (i != j && graphm.containsEdge(source, v_cc.get(j))) {
                    t[last++] = j;
                }
            }
            if (t.length != last) {
                short[] t2 = new short[last];
                for (short j = 0; j < last; j++) {
                    t2[j] = t[j];
                }
                t = t2;
            }
            t_cc[i] = t;
        }
        return t_cc;
    }

    protected void runAnalyse() {
    	brun.setEnabled(false);
        treemodel.analyse(graph, config, (GsRegulatoryMutantDef)mutantstore.getObject(0));
        brun.setEnabled(true);

        if (sp2 == null) {
	        getSp2().setViewportView(getJTextArea());
	        sp2.setSize(sp2.getWidth(), 80);
	        getSplitPane().setBottomComponent(sp2);
	        int h = splitPane.getHeight();
	        splitPane.setDividerLocation(h - 100);
        }
        treemodel.reload(this);
    }

    private JTextArea getJTextArea() {
        if (jta == null) {
            jta = new JTextArea();
            jta.setEditable(false);
        }
        return jta;
    }

    /**
     * This method initializes labelProgression
     * 
     * @return javax.swing.JLabel
     */
    public javax.swing.JLabel getLabelProgression() {
        if (labelProgression == null) {
            labelProgression = new Label("", Label.MESSAGE_NORMAL);
        }
        return labelProgression;
    }
}

class GsCircuitDescr {
    /** vertices in this circuit */
    public GsRegulatoryVertex[] t_vertex;
    /** GsRegulatoryMultiEdges in this circuit */
    public GsRegulatoryMultiEdge[] t_me;

    protected static final int FALSE = 0;
    protected static final int ALL = 1;
    protected static final int FUNCTIONNAL = 2;
    protected static final int POSITIVE = 3;
    protected static final int NEGATIVE = 4;
    protected static final int DUAL = 5;
    
    protected static final String[] SIGN_NAME = {
    	Translator.getString("STR_not-functional"),
    	Translator.getString("STR_all"),
    	Translator.getString("STR_functional"),
    	Translator.getString("STR_positive"), 
    	Translator.getString("STR_negative"),
    	Translator.getString("STR_dual")};
    
    // data on all subcircuits
    protected OmsddNode[] t_context;
    protected long[][] t_mark;
    protected int[][] t_sub;
    
    // which sub circuits go in which category ?
    Vector v_positive = null;
    Vector v_negative = null;
    Vector v_dual = null;
    Vector v_all = new Vector();
    Vector v_functionnal = null;
    
    // to iterate through "subcircuits"
    private int[] t_pos;
    private int[] t_posMax;

    long score;
    int sign;

    /**
     * print the circuit in a nice order.
     * 
     * @param nodeOrder
     * @return the tree of members of the circuit
     */
    public String printMe(Vector nodeOrder) {
        int min = nodeOrder.indexOf(t_vertex[0]);
        int minIndex = 0;
        for (int i = 0; i < t_vertex.length; i++) {
            int tmp = nodeOrder.indexOf(t_vertex[i]);
            if (tmp < min) {
                min = tmp;
                minIndex = i;
            }
        }

        String s = "";
        for (int i = minIndex; i < t_vertex.length; i++) {
            s += "" + (nodeOrder.indexOf(t_vertex[i]) + 1);
        }
        for (int i = 0; i < minIndex; i++) {
            s += "" + (nodeOrder.indexOf(t_vertex[i]) + 1);
        }
        return s;
    }

    /**
     * check if this circuit is functional.
     * 
     * @param algo
     * @param nodeOrder 
     * 
     * @return true if the circuit is functional
     */
    public boolean check(GsCircuitAlgo algo, List nodeOrder) {
        t_pos = new int[t_me.length];
        t_posMax = new int[t_me.length];
        int nbSub = 1;
        for (int i = 0; i < t_pos.length; i++) {
            t_posMax[i] = t_me[i].getEdgeCount() - 1;
            nbSub *= t_posMax[i]+1;
            t_pos[i] = 0;
        }

        int[] t_circuit = new int[nodeOrder.size()]; // filled with "0"
        for (int i=0 ; i< t_me.length ; i++) {
            t_circuit[ nodeOrder.indexOf(t_me[i].getSourceVertex()) ] = t_me[i].getMin(0);
        }
        GsRegulatoryEdge edge, next_edge;
        boolean goon;
        int sub = 0;
        t_context = new OmsddNode[nbSub];
        t_mark = new long[nbSub][];
        t_sub = new int[nbSub][];
        do {
            OmsddNode context = OmsddNode.POSITIVE;
            edge = t_me[t_me.length - 1].getEdge(t_pos[t_pos.length - 1]);
            for (int i = 0; i < t_me.length; i++) {
            	next_edge = t_me[i].getEdge(t_pos[i]);
                OmsddNode node = algo.checkEdge(edge, t_circuit,
                        next_edge.getMin(), next_edge.getMax());
                edge = next_edge;
                context = context.merge(node, OmsddNode.AND);
            }

            GsCircuitDescrInTree cdtree = new GsCircuitDescrInTree(this, false, sub);
            v_all.add(cdtree);
            if (GsCircuitFrame.DO_CLEANUP) {
            	t_context[sub] = context.cleanup(t_circuit).reduce();
            } else {
            	t_context[sub] = context.reduce();
            }
            t_mark[sub] = algo.score(t_context[sub]);
            t_sub[sub] = (int[])t_pos.clone();
            switch ((int)t_mark[sub][1]) {
                case POSITIVE:
                    if (v_positive == null) {
                        v_positive = new Vector();
                    }
                    v_positive.add(cdtree);
                    if (v_functionnal == null) {
                        v_functionnal = new Vector();
                    }
                    v_functionnal.add(cdtree);
                    break;
                case NEGATIVE:
                    if (v_negative == null) {
                        v_negative = new Vector();
                    }
                    v_negative.add(cdtree);
                    if (v_functionnal == null) {
                        v_functionnal = new Vector();
                    }
                    v_functionnal.add(cdtree);
                    break;
                case DUAL:
                    if (v_dual == null) {
                        v_dual = new Vector();
                    }
                    v_dual.add(cdtree);
                    if (v_functionnal == null) {
                        v_functionnal = new Vector();
                    }
                    v_functionnal.add(cdtree);
                    break;
            }
            if (t_mark[sub][0] > score) {
                score = t_mark[sub][0];
            }

            // find next subcircuit
            goon = false;
            for (int i = t_pos.length - 1; i >= 0; i--) {
                if (t_pos[i] == t_posMax[i]) {
                    t_pos[i] = 0;
                    t_circuit[ nodeOrder.indexOf(t_me[i].getSourceVertex()) ] = t_me[i].getMin(0);
                } else {
                    t_pos[i]++;
                    t_circuit[ nodeOrder.indexOf(t_me[i].getSourceVertex()) ] = t_me[i].getMin(t_pos[i]);
                    goon = true;
                    break;
                }
            }
            sub++;
        } while (goon);
        
        return v_positive != null || v_negative != null || v_dual != null;
    }

    protected int getChildCount(int key) {
        switch (key) {
            case ALL:
                break;
            case POSITIVE:
                break;
            case NEGATIVE:
                break;
        }
        return 0;
    }

	public void clear() {
		if (v_all != null) {
			v_all.clear();
		}
    	if (v_functionnal != null) {
    		v_functionnal.clear();
    		v_functionnal = null;
    	}
    	if (v_positive != null) {
    		v_positive.clear();
    		v_positive = null;
    	}
    	if (v_negative != null) {
    		v_negative.clear();
    		v_negative = null;
    	}
    	if (v_dual != null) {
    		v_dual.clear();
    		v_dual = null;
    	}
	}
}

class GsCircuitTreeModel extends AbstractTreeTableModel {

    Vector v_listeners = new Vector();
    Vector v_circuit;
    Vector v_root = new Vector();
    Map m_parent = new HashMap();

    protected static final String s_root = "Circuits";
    static protected Class[]  cTypes = {TreeTableModel.class, String.class, String.class};

    /**
     * @param v_circuit
     */
    public GsCircuitTreeModel(Vector v_circuit) {
    	super(s_root);
        v_root.add(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.ALL]);
        this.v_circuit = v_circuit;
        m_parent.put(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.ALL], v_circuit);
        m_parent.put(s_root, v_root);
    }

    protected void analyse(GsRegulatoryGraph graph, GsCircuitSearchStoreConfig config, GsRegulatoryMutantDef mutant) {
        GsCircuitAlgo circuitAlgo = new GsCircuitAlgo(graph, config == null ? null : config.t_constraint, mutant);
        Vector v_functionnal = new Vector();
        Vector v_positive = new Vector();
        Vector v_negative = new Vector();
        Vector v_dual = new Vector();

        // first some cleanups, to allow running it several times in a row, with different mutants
        v_root.clear();
        v_root.add("All");
        m_parent.clear();
        m_parent.put("All", v_circuit);
        m_parent.put(s_root, v_root);
        for (int i = 0; i < v_circuit.size(); i++) {
        	GsCircuitDescr cdescr = ((GsCircuitDescrInTree) v_circuit.get(i)).circuit;
        	cdescr.clear();
        }
        
        for (int i = 0; i < v_circuit.size(); i++) {
            GsCircuitDescr cdescr = ((GsCircuitDescrInTree) v_circuit.get(i)).circuit;
            cdescr.check(circuitAlgo, graph.getNodeOrder());
            GsCircuitDescrInTree cdtree;
            if (cdescr.v_all.size() > 1) {
                m_parent.put(v_circuit.get(i), cdescr.v_all);
            }
            if (cdescr.v_functionnal != null) {
                cdtree = new GsCircuitDescrInTree(cdescr, true, GsCircuitDescr.FUNCTIONNAL);
                placeCircuit(v_functionnal, cdtree);
                if (cdescr.v_functionnal.size() > 1) {
                    m_parent.put(cdtree, cdescr.v_functionnal);
                }
                if (cdescr.v_positive != null) {
                    cdtree = new GsCircuitDescrInTree(cdescr, true, GsCircuitDescr.POSITIVE);
                    placeCircuit(v_positive, cdtree);
                    if (cdescr.v_positive.size() > 1) {
                        m_parent.put(cdtree, cdescr.v_positive);
                    }
                } 
                if (cdescr.v_negative != null) {
                    cdtree = new GsCircuitDescrInTree(cdescr, true, GsCircuitDescr.NEGATIVE);
                    placeCircuit(v_negative, cdtree);
                    if (cdescr.v_negative.size() > 1) {
                        m_parent.put(cdtree, cdescr.v_negative);
                    }
                }
                if (cdescr.v_dual != null) {
                    cdtree = new GsCircuitDescrInTree(cdescr, true, GsCircuitDescr.DUAL);
                    placeCircuit(v_dual, cdtree);
                    if (cdescr.v_dual.size() > 1) {
                        m_parent.put(cdtree, cdescr.v_dual);
                    }
                }
            }
        }
        if (v_functionnal.size() > 0) {
            v_root.add(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.FUNCTIONNAL]);
            m_parent.put(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.FUNCTIONNAL], v_functionnal);
            if (v_positive.size() > 0) {
                v_root.add(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.POSITIVE]);
                m_parent.put(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.POSITIVE], v_positive);
            }
            if (v_negative.size() > 0) {
                v_root.add(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.NEGATIVE]);
                m_parent.put(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.NEGATIVE], v_negative);
            }
            if (v_dual.size() > 0) {
                v_root.add(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.DUAL]);
                m_parent.put(GsCircuitDescr.SIGN_NAME[GsCircuitDescr.DUAL], v_dual);
            }
        }
        // TODO: add a sorting by context!
        reload(this);
    }

    private void placeCircuit(Vector v, GsCircuitDescrInTree cdescr) {
        for (int i = 0; i < v.size(); i++) {
            if (cdescr.getScore() < ((GsCircuitDescrInTree) v.get(i)).getScore()) {
                v.add(i, cdescr);
                return;
            }
        }
        v.add(cdescr);
    }

    public Object getChild(Object parent, int index) {
        Vector v = (Vector) m_parent.get(parent);
        if (v != null && v.size() > index) {
            return v.get(index);
        }
        return null;
    }

    public int getChildCount(Object parent) {
        Vector v = (Vector) m_parent.get(parent);
        if (v != null) {
            return v.size();
        }
        return 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        reload(this);
    }

    public int getIndexOfChild(Object parent, Object child) {
        Vector v = (Vector) m_parent.get(parent);
        if (v != null) {
            return v.indexOf(child);
        }
        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        if (!v_listeners.contains(l)) {
            v_listeners.add(l);
        }
    }

    public void removeTreeModelListener(TreeModelListener l) {
        v_listeners.remove(l);
    }

    protected void reload(Object source) {
        for (int i = 0; i < v_listeners.size(); i++) {
            ((TreeModelListener) v_listeners.get(i))
                    .treeStructureChanged(new TreeModelEvent(source,
                            new Object[] { getRoot() }));
        }
    }

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return "Circuit";
			case 1:
				return "Sign/children";
		}
		return "";
	}

	public Object getValueAt(Object node, int column) {
		switch (column) {
			case 0:
				return node.toString();
			case 1:
				if (!(node instanceof GsCircuitDescrInTree)) {
					return "";
				}
		        GsCircuitDescrInTree cdtree = (GsCircuitDescrInTree)node;
                int count = getChildCount(cdtree);
	            if (count != 0) {
	                return count+" sub-circuits";
	            }
	            
	            int index = 0;
	            if (cdtree.summary) {
	                switch (cdtree.key) {
	                    case GsCircuitDescr.ALL:
	                        index = 0;
	                        break;
	                    case GsCircuitDescr.FUNCTIONNAL:
	                        index = ((GsCircuitDescrInTree)cdtree.circuit.v_functionnal.get(0)).key;
	                        break;
	                    case GsCircuitDescr.POSITIVE:
	                        index = ((GsCircuitDescrInTree)cdtree.circuit.v_positive.get(0)).key;
	                        break;
	                    case GsCircuitDescr.NEGATIVE:
	                        index = ((GsCircuitDescrInTree)cdtree.circuit.v_negative.get(0)).key;
	                        break;
	                    case GsCircuitDescr.DUAL:
	                        index = ((GsCircuitDescrInTree)cdtree.circuit.v_dual.get(0)).key;
	                        break;
	                }
	            } else if (cdtree.key >= cdtree.circuit.t_context.length) {
	                index = 0;
	            } else {
	                index = cdtree.key;
	            }
	            if (cdtree.circuit.t_mark != null 
	            	&& cdtree.circuit.t_mark.length > index 
	            	&& cdtree.circuit.t_mark[index] != null) {
	            	return GsCircuitDescr.SIGN_NAME[(int)cdtree.circuit.t_mark[index][1]];
	            }
				return "??";
		}
		return "";
	}

	public Class getColumnClass(int column) {
    	return cTypes[column];
    }	
}

class GsCircuitDescrInTree {
    GsCircuitDescr circuit;
    boolean summary;
    int key;
    
    protected GsCircuitDescrInTree(GsCircuitDescr cdescr, boolean summary, int key) {
        this.circuit = cdescr;
        this.key = key;
        this.summary = summary;
    }

    public String toString() {
        if (circuit.t_vertex == null) {
            return "no name";
        }
        int nbChild = 1;
        int index;
        if (summary) {
        	Object o = null;
            switch (key) {
            case GsCircuitDescr.ALL:
            	if (circuit.t_sub != null) {
            		nbChild = circuit.t_sub.length;
            	} else {
            		nbChild = 1;
            	}
                break;
            case GsCircuitDescr.FUNCTIONNAL:
            	nbChild = circuit.v_functionnal.size();
            	o = circuit.v_functionnal.get(0);
                break;
            case GsCircuitDescr.POSITIVE:
            	nbChild = circuit.v_positive.size();
            	o = circuit.v_positive.get(0);
                break;
            case GsCircuitDescr.NEGATIVE:
            	nbChild = circuit.v_negative.size();
            	o = circuit.v_negative.get(0);
                break;
            case GsCircuitDescr.DUAL:
            	nbChild = circuit.v_dual.size();
            	o = circuit.v_dual.get(0);
                break;
            }
            if (nbChild == 1 && o != null) {
            		index = ((GsCircuitDescrInTree)o).key;
            } else {
            	index = 0;
            }
        } else {
        	index = key;
        }
        String s = "";
        // if the circuit has several children, then hide details
        if (summary && (nbChild > 1 || circuit.t_sub == null)) {
            for (int i=0 ; i < circuit.t_vertex.length ; i++) {
                s += " " + circuit.t_vertex[i];
            }
        } else { // if one child only, show details here and hide the child
	        int[] t_pos;
	        t_pos = circuit.t_sub[index];
	        for (int i=0 ; i < circuit.t_vertex.length ; i++) {
	            s += " " + circuit.t_vertex[i];
	            if (t_pos[i] != 0) {
	                s += "["+t_pos[i]+"]";
	            }
	        }
        }
        if (summary && circuit.t_sub != null && circuit.t_sub.length > 1) {
        	if (circuit.t_sub.length == nbChild) {
                s += "  ("+nbChild+")";
        	} else {
                s += "  ("+nbChild+"/"+circuit.t_sub.length+")";
        	} 
        }
        return s;
    }

    protected long getScore() {
        if (summary) {
            return circuit.score;
        }
        return circuit.t_mark[key][0];
    }
}
