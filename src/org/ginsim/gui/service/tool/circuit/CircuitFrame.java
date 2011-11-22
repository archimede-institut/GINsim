package org.ginsim.gui.service.tool.circuit;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.reducedgraph.NodeReducedData;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.regulatorygraph.mutant.RegulatoryMutantDef;
import org.ginsim.graph.tree.Tree;
import org.ginsim.graph.tree.TreeParser;
import org.ginsim.graph.tree.TreeParserFromCircuit;
import org.ginsim.graph.tree.TreeImpl;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.service.tool.connectivity.AlgoConnectivity;

import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.ProgressListener;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.StackDialog;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.Label;
import fr.univmrs.tagc.common.widgets.treetable.AbstractTreeTableModel;
import fr.univmrs.tagc.common.widgets.treetable.JTreeTable;
import fr.univmrs.tagc.common.widgets.treetable.TreeTableModel;
import fr.univmrs.tagc.common.widgets.treetable.TreeTableModelAdapter;

/**
 * configuration/status frame for circuit search/analyse
 */
public class CircuitFrame extends StackDialog implements ProgressListener {

    private static final long serialVersionUID = 2671795894716799300L;

    private AlgoConnectivity algoC = null;
    protected RegulatoryGraph graph;

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
    private CircuitSearchStoreConfig config = null;
    private JCheckBox cb_cleanup = null;
    ObjectStore mutantstore = new ObjectStore();
    MutantSelectionPanel mutantPanel;

	private JButton	but_pyexport;

	private JButton viewContextButton;

    /**
     * This is the default constructor
     * 
     * @param frame
     * @param graph
     */
    public CircuitFrame(JFrame frame, Graph<?,?> graph) {
        super(frame, "display.circuit", 500, 300);
        if (graph == null || !(graph instanceof RegulatoryGraph)) {
			Tools.error("no graph", frame);
		}
        this.graph = (RegulatoryGraph) graph;
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
        	c.gridwidth = 2;
        	c.weightx = 1;
        	c.fill = GridBagConstraints.BOTH;

        	c.gridx = 0;
        	c.gridy = 0;
        	mutantPanel = new MutantSelectionPanel(this, graph, mutantstore);
        	resultPanel.add(mutantPanel, c);

        	c.gridy++;
        	resultPanel.add(getLabelProgression(), c);
        	
        	c.gridy++;
        	c.weighty = 1;
        	resultPanel.add(getSplitPane(), c);
        	
            // actions button
        	c.gridy++;
        	c.weighty = 0;
        	c.anchor = GridBagConstraints.WEST;
        	c.fill = GridBagConstraints.NONE;
        	c.gridwidth = 1;
            resultPanel.add(getViewContextButton(), c);
            
        	c.gridx++;
            resultPanel.add(get_pythonPanel(), c);

            // cleanup checkbox
            c.gridx = 0;
            c.gridy++;
        	c.gridwidth = 2;
        	c.fill = GridBagConstraints.BOTH;
        	cb_cleanup = new JCheckBox(Translator.getString("STR_do_cleanup"));
            cb_cleanup.setSelected(true);           // FIXME: remember this as a setting
            resultPanel.add(cb_cleanup, c);
        }
        return resultPanel;
    }
    
    private JButton getViewContextButton() {
    	if (viewContextButton == null) {
    		viewContextButton = new JButton(Translator.getString("STR_circuit_viewContext"));
    		viewContextButton.setEnabled(false);
    		viewContextButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					viewContext();
				}
			});
    	}
		return viewContextButton;
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
    	List l_func = (List)treemodel.m_parent.get(CircuitDescr.SIGN_NAME[CircuitDescr.FUNCTIONNAL]);
    	if (l_func == null) {
    		System.out.println("no func...");
    		return;
    	}
    	StringBuffer s = new StringBuffer("#!/usr/bin/env python\n"
    			+ "import circuittest\n"
    			+ "if __name__ == \"__main__\":\n"
    			+ "    ct = circuittest.CircuitTester(node_order=[");
        Iterator it = no.iterator();
        while (it.hasNext()) {
            s.append("\""+((RegulatoryNode)it.next()).getId()+"\",");
        }
    	s.append("])\n");
    	it = l_func.iterator();
    	while (it.hasNext()) {
    		CircuitDescrInTree cit = (CircuitDescrInTree)it.next();
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
    
    protected void circuitToPython(List no, StringBuffer s, CircuitDescrInTree cit) {
    	for (int i=0 ; i<cit.getCircuit().t_context.length ; i++) {
    	    if (cit.getCircuit().t_context[i].next != null) {
    	        s.append("    ct.add_circuit((");

    	        for (int j=0 ; j<cit.getCircuit().t_me.length ; j++) {
    	            int idx = 0; // FIXME: get the right edge!
    	            RegulatoryMultiEdge me = cit.getCircuit().t_me[j];
    	            int src = no.indexOf(me.getSource());
    	            int dst = no.indexOf(me.getTarget());
    	            s.append("("+src+","+dst+","
    	                     + me.getMin(idx)+","
    	                     + (me.getSign(idx)==RegulatoryMultiEdge.SIGN_NEGATIVE?"-1":"1")
    	                     + "),");
    	        }
    	        s.append("), ");
        	    s.append(mdd2py(cit.getCircuit().t_context[i]));
                s.append(")\n");
    	    }
    	}
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
                config = new CircuitSearchStoreConfig();
                config.v_list = graph.getNodeOrder();
                config.minlen = 1;
                config.maxlen = config.v_list.size();
                config.t_status = new byte[graph.getNodeOrderSize()];
                config.t_constraint = new byte[graph.getNodeOrderSize()][3];
                for (int i = 0; i < config.t_status.length; i++) {
                    config.t_status[i] = 3;
                    byte max = ((RegulatoryNode) graph.getNodeOrder().get(
                            i)).getMaxValue();
                    config.t_constraint[i][0] = 0;
                    config.t_constraint[i][1] = max;
                    config.t_constraint[i][2] = max;
                }
            }
            configDialog = new CircuitConfigureSearch(this, config, graph.getNodeOrder());
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
                Vector v_cc = ((NodeReducedData) l.get(i)).getContent();
                searchCircuitInSCC(v_cc);
            }
            if (config == null || config.minlen < 2) { // search
                                                       // autoregulation-like
                                                       // circuits
            	if (config.minMust < 2) {
	                for (int i = 0; i < graph.getNodeOrderSize(); i++) {
	                    RegulatoryNode vertex = (RegulatoryNode) graph
	                            .getNodeOrder().get(i);
	                    if (config.minMust == 1 && config.t_status[i] == 1 ||
	                        config.minMust == 0 && config.t_status[i] == 3) {
	                    	RegulatoryMultiEdge edge = graph.getEdge(vertex, vertex);
		                    if (edge != null) {
		                        CircuitDescr circuit = new CircuitDescr();
		                        circuit.t_vertex = new RegulatoryNode[] { vertex };
		                        circuit.t_me = new RegulatoryMultiEdge[] { edge };
		                        v_circuit.add(new CircuitDescrInTree(circuit, true, CircuitDescr.ALL));
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
        
        if (v_cc.size() < 2) {
            return;
        }
        byte[][] t_cc = buildTCC(graph, v_cc); // int[][] giving edges in
                                                    // the current SCC
        byte[] t_visited = new byte[t_cc.length]; // remember visited nodes
                                                    // and their position
        byte[][] t_path = new byte[t_cc.length][3]; // remember the followed
                                                        // path: series of
                                                        // nodes, index of the
                                                        // followed edge and
                                                        // score
        byte[] t_map = new byte[t_cc.length];
        byte cur = 0; // num of the current node
        byte pos = 0; // position of the current node in the current path
        boolean[] t_history = new boolean[t_cc.length]; // avoid refinding
                                                        // cycles not starting
                                                        // at the first gene
        t_visited[0] = 0;
        t_path[0][0] = 0;
        t_path[0][1] = 0;
        int score = 0; // score of the current circuit (to know if it can be
                        // accepted)
        t_map[0] = (byte) graph.getNodeOrder().indexOf(v_cc.get(0));
        if (config != null && config.t_status[t_map[0]] == 1) {
            score++;
        }
        for (int j = 1; j < t_visited.length; j++) {
            t_visited[j] = -1;
            t_history[j] = false;
            t_map[j] = (byte) graph.getNodeOrder().indexOf(v_cc.get(j));
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
                CircuitDescr circuit = new CircuitDescr();
                v_circuit.add(new CircuitDescrInTree(circuit, true, CircuitDescr.ALL));
                circuit.t_vertex = new RegulatoryNode[pos - t_visited[cur]
                        + 1];
                int p = 0;
                int a = t_visited[cur];
                circuit.t_vertex[p++] = (RegulatoryNode) v_cc
                        .get(t_path[a][0]);
                a++;
                for (; a <= pos; a++) {
                    circuit.t_vertex[p++] = (RegulatoryNode) v_cc
                            .get(t_path[a][0]);
                }

                circuit.t_me = new RegulatoryMultiEdge[circuit.t_vertex.length];
                RegulatoryNode source = circuit.t_vertex[0];
                RegulatoryNode target = null;
                for (int i = 1; i < circuit.t_vertex.length; i++) {
                    target = circuit.t_vertex[i];
                    circuit.t_me[i - 1] = graph.getEdge(source, target);
                    source = target;
                }
                circuit.t_me[circuit.t_me.length - 1] = graph.getEdge(target, circuit.t_vertex[0]);
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
					showInfo();
				}
			});
        }
    }

    protected void showInfo() {
        CircuitDescrInTree cdtree = getSelectedContextFromTreeTable();
        int index = 0;
        if (cdtree.summary) {
            if (!treemodel.isLeaf(cdtree)) {
                int count = treemodel.getChildCount(cdtree);
                jta.setText("contains "+count+" layered-circuits");
                return;
            }
            switch (cdtree.key) {
                case CircuitDescr.ALL:
                    index = 0;
                    break;
                case CircuitDescr.FUNCTIONNAL:
                    index = ((CircuitDescrInTree)cdtree.getCircuit().v_functionnal.get(0)).key;
                    break;
                case CircuitDescr.POSITIVE:
                    index = ((CircuitDescrInTree)cdtree.getCircuit().v_positive.get(0)).key;
                    break;
                case CircuitDescr.NEGATIVE:
                    index = ((CircuitDescrInTree)cdtree.getCircuit().v_negative.get(0)).key;
                    break;
                case CircuitDescr.DUAL:
                    index = ((CircuitDescrInTree)cdtree.getCircuit().v_dual.get(0)).key;
                    break;
            }
        } else if (cdtree.key >= cdtree.getCircuit().t_context.length) {
            index = 0;
        } else {
            index = cdtree.key;
        }
        
        CircuitDescr circuit = cdtree.getCircuit();
        if (circuit.t_context == null) {
            jta.setText("no data");
            return;
        }

        if (circuit.t_context[index] == OmsddNode.FALSE) {
            jta.setText(CircuitDescr.SIGN_NAME[CircuitDescr.FALSE]);
            return;
        }
        String s = circuit.t_context[index].getString(0, graph.getNodeOrder()).trim();
        if (s.equals("")) {
            jta.setText("empty data");
        } else {
            jta.setText(s);
        }
    }

    private byte[][] buildTCC( Graph graph, Vector v_cc) {
        byte[][] t_cc = new byte[v_cc.size()][];
        for (byte i = 0; i < t_cc.length; i++) {
            byte[] t = new byte[t_cc.length - 1];
            int last = 0;
            Object source = v_cc.get(i);
            for (byte j = 0; j < t_cc.length; j++) {
                if (i != j && graph.containsEdge(source, v_cc.get(j))) {
                    t[last++] = j;
                }
            }
            if (t.length != last) {
                byte[] t2 = new byte[last];
                for (byte j = 0; j < last; j++) {
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
        treemodel.analyse(graph, config, (RegulatoryMutantDef)mutantstore.getObject(0), cb_cleanup.isSelected());
        brun.setEnabled(true);

        if (sp2 == null) {
	        getSp2().setViewportView(getJTextArea());
	        sp2.setSize(sp2.getWidth(), 80);
	        getSplitPane().setBottomComponent(sp2);
	        int h = splitPane.getHeight();
	        splitPane.setDividerLocation(h - 100);
        }
        treemodel.reload(this);
        viewContextButton.setEnabled(true);
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
    
    /**
     * Launch the treeViewer to analyse the contexts of functionalities
     * if a context is selected in the treeTable, it will use it, else it will use a "random" context (the first in v_circuit)
     */
	private void viewContext() {
		TreeParser parser = new TreeParserFromCircuit();
		Tree tree = new TreeImpl(parser);
			
		parser.setParameter(TreeParser.PARAM_NODEORDER, graph.getNodeOrder());
		parser.setParameter(TreeParserFromCircuit.PARAM_INITIALCIRCUITDESC, getSelectedContextFromTreeTable().getCircuit());
		parser.setParameter(TreeParserFromCircuit.PARAM_ALLCONTEXTS, getCircuitDescriptors());
		GUIManager.getInstance().newFrame(tree);
	}
	
	/**
	 * Return a vector of FunctionalityContext for each functional context of functionality.
	 */
	private Vector getCircuitDescriptors() {
		Vector contexts = new Vector(v_circuit.size());
		for (Iterator it = v_circuit.iterator(); it.hasNext();) {
			CircuitDescrInTree cdit = (CircuitDescrInTree) it.next();
			CircuitDescr cd = cdit.getCircuit();
			OmsddNode[] context = cd.getContext();
			Debugger.log_collection(context);
			for (int i = 0; i < context.length; i++) {
				OmsddNode o = context[i];
				if (o != OmsddNode.FALSE) {
					contexts.add(new FunctionalityContext(cd, i));
				}
			}
		}
		return contexts;
	}
	/**
	 * Return the selected context from the treeTable
	 * if none is selected, then return the first circuit in v_circuit
	 */
	private CircuitDescrInTree getSelectedContextFromTreeTable() {
		CircuitDescrInTree circuitDescrInTree  = null;
		for (Iterator it = v_circuit.iterator(); it.hasNext();) {
			circuitDescrInTree = (CircuitDescrInTree) it.next();
			if (circuitDescrInTree.getCircuit().sign != CircuitDescr.FALSE) break;
		}
		Object selected = ((TreeTableModelAdapter)tree.getModel()).nodeForRow(tree.getSelectedRow());
		if (!(selected instanceof CircuitDescrInTree)) {
        	if (selected == null || GsCircuitTreeModel.s_root.equals(selected)) {
    			jta.setText("");
    			return circuitDescrInTree;
			}
			jta.setText("no data");
            int count = treemodel.getChildCount(selected);
            if (count > 0) {
                jta.setText("contains "+count+" circuits");
            }
            return circuitDescrInTree;
        }
		return (CircuitDescrInTree)selected;
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
        v_root.add(CircuitDescr.SIGN_NAME[CircuitDescr.ALL]);
        this.v_circuit = v_circuit;
        m_parent.put(CircuitDescr.SIGN_NAME[CircuitDescr.ALL], v_circuit);
        m_parent.put(s_root, v_root);
    }

    protected void analyse(RegulatoryGraph graph, CircuitSearchStoreConfig config, RegulatoryMutantDef mutant, boolean do_cleanup) {
        CircuitAlgo circuitAlgo = new CircuitAlgo(graph, config == null ? null : config.t_constraint, mutant, do_cleanup);
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
        	CircuitDescr cdescr = ((CircuitDescrInTree) v_circuit.get(i)).getCircuit();
        	cdescr.clear();
        }
        
        for (int i = 0; i < v_circuit.size(); i++) {
            CircuitDescr cdescr = ((CircuitDescrInTree) v_circuit.get(i)).getCircuit();
            cdescr.check(circuitAlgo, graph.getNodeOrder());
            CircuitDescrInTree cdtree;
            if (cdescr.v_all.size() > 1) {
                m_parent.put(v_circuit.get(i), cdescr.v_all);
            }
            if (cdescr.v_functionnal != null) {
                cdtree = new CircuitDescrInTree(cdescr, true, CircuitDescr.FUNCTIONNAL);
                placeCircuit(v_functionnal, cdtree);
                if (cdescr.v_functionnal.size() > 1) {
                    m_parent.put(cdtree, cdescr.v_functionnal);
                }
                if (cdescr.v_positive != null) {
                    cdtree = new CircuitDescrInTree(cdescr, true, CircuitDescr.POSITIVE);
                    placeCircuit(v_positive, cdtree);
                    if (cdescr.v_positive.size() > 1) {
                        m_parent.put(cdtree, cdescr.v_positive);
                    }
                } 
                if (cdescr.v_negative != null) {
                    cdtree = new CircuitDescrInTree(cdescr, true, CircuitDescr.NEGATIVE);
                    placeCircuit(v_negative, cdtree);
                    if (cdescr.v_negative.size() > 1) {
                        m_parent.put(cdtree, cdescr.v_negative);
                    }
                }
                if (cdescr.v_dual != null) {
                    cdtree = new CircuitDescrInTree(cdescr, true, CircuitDescr.DUAL);
                    placeCircuit(v_dual, cdtree);
                    if (cdescr.v_dual.size() > 1) {
                        m_parent.put(cdtree, cdescr.v_dual);
                    }
                }
            }
        }
        if (v_functionnal.size() > 0) {
            v_root.add(CircuitDescr.SIGN_NAME[CircuitDescr.FUNCTIONNAL]);
            m_parent.put(CircuitDescr.SIGN_NAME[CircuitDescr.FUNCTIONNAL], v_functionnal);
            if (v_positive.size() > 0) {
                v_root.add(CircuitDescr.SIGN_NAME[CircuitDescr.POSITIVE]);
                m_parent.put(CircuitDescr.SIGN_NAME[CircuitDescr.POSITIVE], v_positive);
            }
            if (v_negative.size() > 0) {
                v_root.add(CircuitDescr.SIGN_NAME[CircuitDescr.NEGATIVE]);
                m_parent.put(CircuitDescr.SIGN_NAME[CircuitDescr.NEGATIVE], v_negative);
            }
            if (v_dual.size() > 0) {
                v_root.add(CircuitDescr.SIGN_NAME[CircuitDescr.DUAL]);
                m_parent.put(CircuitDescr.SIGN_NAME[CircuitDescr.DUAL], v_dual);
            }
        }
        // TODO: add a sorting by context!
        reload(this);
    }

    private void placeCircuit(Vector v, CircuitDescrInTree cdescr) {
        for (int i = 0; i < v.size(); i++) {
            if (cdescr.getScore() < ((CircuitDescrInTree) v.get(i)).getScore()) {
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
				if (!(node instanceof CircuitDescrInTree)) {
					return "";
				}
		        CircuitDescrInTree cdtree = (CircuitDescrInTree)node;
                int count = getChildCount(cdtree);
	            if (count != 0) {
	                return count+" sub-circuits";
	            }
	            
	            int index = 0;
	            if (cdtree.summary) {
	                switch (cdtree.key) {
	                    case CircuitDescr.ALL:
	                        index = 0;
	                        break;
	                    case CircuitDescr.FUNCTIONNAL:
	                        index = ((CircuitDescrInTree)cdtree.getCircuit().v_functionnal.get(0)).key;
	                        break;
	                    case CircuitDescr.POSITIVE:
	                        index = ((CircuitDescrInTree)cdtree.getCircuit().v_positive.get(0)).key;
	                        break;
	                    case CircuitDescr.NEGATIVE:
	                        index = ((CircuitDescrInTree)cdtree.getCircuit().v_negative.get(0)).key;
	                        break;
	                    case CircuitDescr.DUAL:
	                        index = ((CircuitDescrInTree)cdtree.getCircuit().v_dual.get(0)).key;
	                        break;
	                }
	            } else if (cdtree.key >= cdtree.getCircuit().t_context.length) {
	                index = 0;
	            } else {
	                index = cdtree.key;
	            }
	            if (cdtree.getCircuit().t_mark != null 
	            	&& cdtree.getCircuit().t_mark.length > index 
	            	&& cdtree.getCircuit().t_mark[index] != null) {
	            	return CircuitDescr.SIGN_NAME[(int)cdtree.getCircuit().t_mark[index][1]];
	            }
				return "??";
		}
		return "";
	}

	public Class getColumnClass(int column) {
    	return cTypes[column];
    }	
}

