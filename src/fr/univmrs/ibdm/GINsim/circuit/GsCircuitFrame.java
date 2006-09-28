package fr.univmrs.ibdm.GINsim.circuit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.univmrs.ibdm.GINsim.connectivity.AlgoConnectivity;
import fr.univmrs.ibdm.GINsim.connectivity.GsNodeReducedData;
import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsProgressListener;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.gui.GsListCellRenderer;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsEdgeIndex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * configuration/status frame for circuit search/analyse
 */
public class GsCircuitFrame extends JDialog implements GsProgressListener {

    private static final long serialVersionUID = 2671795894716799300L;

    private AlgoConnectivity algoC = null;
    protected GsRegulatoryGraph graph;

    protected static final int STATUS_NONE = 0;
    private static final int STATUS_SCC = 1;
    private static final int STATUS_SEARCH_CIRCUIT = 2;
    private static final int STATUS_SHOW_CIRCUIT = 3;
    private static final int STATUS_SHOW_RESULT = 4;
    private int status = STATUS_NONE;

    private Vector v_circuit = new Vector();
    protected JTree tree = null;
    private GsCircuitTreeModel treemodel = null;
    private JPanel configDialog = null;
    private JSplitPane splitPane = null;
    private javax.swing.JPanel jContentPane = null;
    private javax.swing.JButton buttonRun = null;
    private javax.swing.JButton buttonCancel = null;
    private javax.swing.JLabel labelProgression = null;
    private JScrollPane sp = null;
    private JScrollPane sp2 = null;
    private JPanel infoPanel = null;
    private JLabel infoText = null;
    private JTextArea jta = null;
    private GsCircuitSearchStoreConfig config = null;

    /**
     * This is the default constructor
     * 
     * @param frame
     * @param graph
     */
    public GsCircuitFrame(JFrame frame, GsGraph graph) {
        super(frame);
        if (graph == null || !(graph instanceof GsRegulatoryGraph))
            GsEnv.error("no graph", frame);
        this.graph = (GsRegulatoryGraph) graph;
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(500, 300);
        this.setContentPane(getJContentPane());
        this.setTitle(Translator.getString("STR_circuit"));
        this.setVisible(true);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                close();
            }
        });
    }

    /**
     * close the circuit search/analyse dialog. stop running algo and close
     * configuration dialog if appropriate.
     */
    protected void close() {
        if (algoC != null && algoC.isAlive()) {
            algoC.cancel();
        }
        graph = null;
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
            jContentPane.setLayout(new GridBagLayout());

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 5;
            c.anchor = GridBagConstraints.EAST;
            jContentPane.add(getJButtonRun(), c);
            
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 5;
            c.weightx = 1;
            c.anchor = GridBagConstraints.EAST;
            jContentPane.add(getJButtonCancel(), c);
            
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 3;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = 1;
            jContentPane.add(getConfigPanel(), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 3;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            jContentPane.add(getLabelProgression(), c);
        }
        return jContentPane;
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

    /**
     * This method initializes buttonRun
     * 
     * @return buttonRun
     */
    private javax.swing.JButton getJButtonRun() {
        if (buttonRun == null) {
            buttonRun = new javax.swing.JButton(Translator.getString("STR_run"));
            buttonRun.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    clicked();
                }
            });
        }
        return buttonRun;
    }

    private javax.swing.JButton getJButtonCancel() {
        if (buttonCancel == null) {
            buttonCancel = new javax.swing.JButton(Translator.getString("STR_cancel"));
            buttonCancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    close();
                }
            });
        }
        return buttonCancel;
    }

    protected JPanel getConfigPanel() {
        if (configDialog == null) {
            if (config == null) {
                config = new GsCircuitSearchStoreConfig();
                config.v_list = graph.getNodeOrder();
                config.minlen = 0;
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

    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            infoPanel = new JPanel();
            infoPanel.add(getInfoText());
            infoPanel.setSize(infoPanel.getWidth(), 30);
        }
        return infoPanel;
    }

    private JLabel getInfoText() {
        if (infoText == null) {
            infoText = new JLabel();
        }
        return infoText;
    }

    protected void clicked() {
        switch (status) {
        case STATUS_NONE:
            updateStatus(STATUS_SCC);
            algoC = new AlgoConnectivity();
            algoC.configure(graph, this, AlgoConnectivity.MODE_COMPO);
            algoC.start();
            break;
        case STATUS_SCC:
            algoC.cancel();
            close();
            break;
        case STATUS_SEARCH_CIRCUIT:
            break;
        case STATUS_SHOW_CIRCUIT:
            if (configDialog != null) {
                configDialog.setVisible(false);
            }
            runAnalyse();
            break;
        case STATUS_SHOW_RESULT:
            close();
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
            buttonRun.setText(Translator.getString("STR_run"));
            break;
        case STATUS_SCC:
            this.status = status;
            configDialog.setVisible(false);
            labelProgression.setVisible(true);
            buttonRun.setText(Translator.getString("STR_cancel"));
            break;
        case STATUS_SEARCH_CIRCUIT:
            break;
        case STATUS_SHOW_CIRCUIT:
            this.status = status;
            setProgressText(v_circuit.size() + " circuit found:");
            buttonRun.setText(Translator.getString("STR_circuit_analyse"));
            break;
        case STATUS_SHOW_RESULT:
            this.status = status;
            buttonRun.setText(Translator.getString("STR_close"));
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
                for (int i = 0; i < graph.getNodeOrder().size(); i++) {
                    GsRegulatoryVertex vertex = (GsRegulatoryVertex) graph
                            .getNodeOrder().get(i);
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
            GridBagConstraints s_sp = new GridBagConstraints();
            s_sp.gridx = 0;
            s_sp.gridy = 1;
            s_sp.weightx = 1;
            s_sp.weighty = 1;
            s_sp.gridwidth = 2;
            s_sp.fill = GridBagConstraints.BOTH;
            jContentPane.add(getSplitPane(), s_sp);
            treemodel = new GsCircuitTreeModel(v_circuit);
            tree = new JTree(treemodel);

            getSp().setViewportView(tree);
            tree.setCellRenderer(new GsListCellRenderer());

            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            
            tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    showInfo(tree.getLastSelectedPathComponent());
                }
            });
        }
        this.setSize(this.getWidth(), 400);
    }

    protected void showInfo(Object selected) {
        if (jta == null) {
            return;
        }
        if (!(selected instanceof GsCircuitDescrInTree)) {
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
                jta.setText("contains "+count+" sub-circuits");
                return;
            }
            switch (cdtree.key) {
                case GsCircuitDescr.ALL:
                    index = 0;
                    break;
                case GsCircuitDescr.POSITIVE:
                    index = ((GsCircuitDescrInTree)cdtree.circuit.v_positive.get(0)).key;
                    break;
                case GsCircuitDescr.NEGATIVE:
                    index = ((GsCircuitDescrInTree)cdtree.circuit.v_negative.get(0)).key;
                    break;
                case GsCircuitDescr.BROKEN:
                    index = ((GsCircuitDescrInTree)cdtree.circuit.v_broken.get(0)).key;
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
            jta.setText("not functional");
            return;
        }
        String s = circuit.t_context[index].getString(0, graph.getNodeOrder()).trim();
        if (s.equals("")) {
            jta.setText("empty data");
        } else {
            jta.setText("score: " + circuit.t_mark[index][0] + ", sign: " + circuit.t_mark[index][1]
                    + "\n" + s);
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

    private void runAnalyse() {
        treemodel.analyse(graph, config);

        updateStatus(STATUS_SHOW_RESULT);
        GridBagConstraints s_sp = new GridBagConstraints();
        s_sp.gridx = 0;
        s_sp.gridy = 2;
        s_sp.weightx = 1;
        s_sp.gridwidth = 2;
        s_sp.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints s_sp2 = new GridBagConstraints();
        s_sp2.gridx = 0;
        s_sp2.gridy = 3;
        s_sp2.weightx = 1;
        s_sp2.weighty = 1;
        s_sp2.gridwidth = 2;
        s_sp2.fill = GridBagConstraints.BOTH;

        jContentPane.add(getInfoPanel(), s_sp);
        getSp2().setViewportView(getJTextArea());

        sp2.setSize(sp2.getWidth(), 80);
        getSplitPane().setBottomComponent(sp2);
        int h = splitPane.getHeight();
        splitPane.setDividerLocation(h + 100);
        setSize(getWidth(), Math.max(h + 50, Math.min(h + 250, 500)));
        treemodel.reload(this);
    }

    private JTextArea getJTextArea() {
        if (jta == null) {
            jta = new JTextArea();
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
            labelProgression = new javax.swing.JLabel();
            labelProgression.setText(Translator.getString("STR_circuit_ask"));
            labelProgression.setVisible(false);
        }
        return labelProgression;
    }
}

class GsCircuitDescr {
    /** vertices in this circuit */
    public GsRegulatoryVertex[] t_vertex;
    /** GsRegulatoryMultiEdges in this circuit */
    public GsRegulatoryMultiEdge[] t_me;

    protected static final int BROKEN = -1;
    protected static final int ALL = 0;
    protected static final int FALSE = 0;
    protected static final int POSITIVE = 1;
    protected static final int NEGATIVE = 2;
    protected static final int FUNCTIONNAL = 3;
    
    // data on all subcircuits
    protected OmsddNode[] t_context;
    protected int[][] t_mark;
    protected int[][] t_sub;
    
    // which sub circuits go in which category ?
    Vector v_positive = null;
    Vector v_negative = null;
    Vector v_broken = null;
    Vector v_all = new Vector();
    Vector v_functionnal = null;
    
    // to iterate through "subcircuits"
    private int[] t_pos;
    private int[] t_posMax;

    int score;
    int sign;

    /**
     * @return the sign of the circuit as a String
     */
    public String sign() {
        switch (sign) {
        case BROKEN:
            return "%";
        case ALL:
            return ".";
        case POSITIVE:
            return "+";
        case NEGATIVE:
            return "-";
        }
        return null;
    }

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
     * check if this circuit is functionnal.
     * 
     * @param algo
     * @param nodeOrder 
     * 
     * @return true if the circuit is functionnal
     */
    public boolean check(GsCircuitAlgo algo, Vector nodeOrder) {
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
        GsEdgeIndex ei = new GsEdgeIndex(null, 0);
        GsEdgeIndex next_ei = new GsEdgeIndex(null, 0);
        boolean goon;
        int sub = 0;
        t_context = new OmsddNode[nbSub];
        t_mark = new int[nbSub][];
        t_sub = new int[nbSub][];
        do {
            OmsddNode context = OmsddNode.POSITIVE;
            ei.data = t_me[t_me.length - 1];
            ei.index = t_pos[t_pos.length - 1];
            for (int i = 0; i < t_me.length; i++) {
                next_ei.data = t_me[i];
                next_ei.index = t_pos[i];
                OmsddNode node = algo.checkEdge(ei, t_circuit,
                        next_ei.data.getMin(next_ei.index), next_ei.data
                        .getMax(next_ei.index));
                ei.data = next_ei.data;
                ei.index = next_ei.index;
                context = context.merge(node, OmsddNode.AND);
            }

            GsCircuitDescrInTree cdtree = new GsCircuitDescrInTree(this, false, sub);
            v_all.add(cdtree);
            t_context[sub] = context.cleanup(t_circuit).reduce();
            t_mark[sub] = algo.score(t_context[sub]);
            t_sub[sub] = (int[])t_pos.clone();
            switch (t_mark[sub][1]) {
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
                case BROKEN:
                    if (v_broken == null) {
                        v_broken = new Vector();
                    }
                    v_broken.add(cdtree);
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
                    t_circuit[ nodeOrder.indexOf(t_me[i].getTargetVertex()) ] = t_me[i].getMin(0);
                } else {
                    t_pos[i]++;
                    t_circuit[ nodeOrder.indexOf(t_me[i].getTargetVertex()) ] = t_me[i].getMin(t_pos[i]);
                    goon = true;
                    break;
                }
            }
            sub++;
        } while (goon);
        
        return v_positive != null || v_negative != null || v_broken != null;
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
}

class GsCircuitTreeModel implements TreeModel {

    Vector v_listeners = new Vector();
    Vector v_circuit;
    Vector v_root = new Vector();
    Map m_parent = new HashMap();

    private static final String s_root = "circuits";

    /**
     * @param v_circuit
     */
    public GsCircuitTreeModel(Vector v_circuit) {
        v_root.add("all");
        this.v_circuit = v_circuit;
        m_parent.put("all", v_circuit);
        m_parent.put(s_root, v_root);
    }

    protected void analyse(GsRegulatoryGraph graph, GsCircuitSearchStoreConfig config) {
        GsCircuitAlgo circuitAlgo = new GsCircuitAlgo(graph, config == null ? null : config.t_constraint);
        Vector v_functionnal = new Vector();
        Vector v_positive = new Vector();
        Vector v_negative = new Vector();
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
                } else if (cdescr.v_negative != null) {
                    cdtree = new GsCircuitDescrInTree(cdescr, true, GsCircuitDescr.NEGATIVE);
                    placeCircuit(v_negative, cdtree);
                    if (cdescr.v_negative.size() > 1) {
                        m_parent.put(cdtree, cdescr.v_negative);
                    }
                }
            }
        }
        if (v_functionnal.size() > 0) {
            v_root.add("functionnal");
            m_parent.put("functionnal", v_functionnal);
            if (v_positive.size() > 0) {
                v_root.add("positive");
                m_parent.put("positive", v_positive);
            }
            if (v_negative.size() > 0) {
                v_root.add("negative");
                m_parent.put("negative", v_negative);
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

    public Object getRoot() {
        return s_root;
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

    public boolean isLeaf(Object node) {
        return !m_parent.containsKey(node);
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
        if (summary) {
            String s = circuit.t_vertex[0].toString();
            for (int i = 1; i < circuit.t_vertex.length; i++) {
                s += " " + circuit.t_vertex[i];
            }
            if (circuit.t_sub != null && circuit.t_sub.length > 1) {
                switch (key) {
                    case GsCircuitDescr.ALL:
                        s += "  ("+circuit.t_sub.length+")";
                        break;
                    case GsCircuitDescr.FUNCTIONNAL:
                        s += "  ("+circuit.v_functionnal.size()+"/"+circuit.t_sub.length+")";
                        break;
                    case GsCircuitDescr.POSITIVE:
                        s += "  ("+circuit.v_positive.size()+"/"+circuit.t_sub.length+")";
                        break;
                    case GsCircuitDescr.NEGATIVE:
                        s += "  ("+circuit.v_negative.size()+"/"+circuit.t_sub.length+")";
                        break;
                    case GsCircuitDescr.BROKEN:
                        s += "  ("+circuit.v_broken.size()+"/"+circuit.t_sub.length+")";
                        break;
                }
            }
            return s;
        }
        int[] t_pos = circuit.t_sub[key];
        String s = circuit.t_vertex[0].toString();
        if (t_pos[0] != 0) {
            s += "["+t_pos[0]+"]";
        }
        for (int i = 1; i < circuit.t_vertex.length; i++) {
            s += " " + circuit.t_vertex[i];
            if (t_pos[i] != 0) {
                s += "["+t_pos[i]+"]";
            }
        }
        return s;
    }

    protected int getScore() {
        if (summary) {
            return circuit.score;
        }
        return circuit.t_mark[key][0];
    }
}
