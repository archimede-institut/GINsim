package fr.univmrs.tagc.GINsim.reg2dyn;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialState;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.InitialStateList;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * frame to set up the simulation
 */
public class GsBatchSimulationFrame extends GsBaseSimulationFrame {
    private static final long serialVersionUID = -4386183125281770860L;
    
    GsSimulationParameterList paramList;
    Simulation sim;
    private JPanel mainPanel;
    private JScrollPane sp;
    private JTextArea result;
    Iterator initIterator;
    int nbParam, curParam;
    Map m_init = new HashMap();
    GsSimulationParameters param;
    String s_init;
    
    /**
     * @param frame
     * @param paramList
     */
    public GsBatchSimulationFrame(JFrame frame, GsSimulationParameterList paramList) {
        super(frame, "display.batchsimulation", 800, 400);
        this.paramList = paramList;
        paramList.graph.addBlockEdit(this);
        initialize();
        this.setTitle(Translator.getString("STR_reg2dynRunningTitle"));
        this.addWindowListener(new java.awt.event.WindowAdapter() { 
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancel();
            }
        });
    }

    private void initialize() {
        setMainPanel(getMainPanel());
    }
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            // TODO: config UI ?
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            mainPanel.add(new JLabel("TODO: config UI. For now: runs all set up parameters"), c);
            result = new JTextArea();
            result.setEditable(false);
            sp = new JScrollPane();
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 1;
            c.weighty = 1;
            c.fill = GridBagConstraints.BOTH;
            sp.setViewportView(result);
            mainPanel.add(sp, c);
        }
        return mainPanel;
    }
    

    /**
     * 
     */
    protected void run() {
        nbParam = paramList.getNbElements();
        if (nbParam == 0) {
            setMessage("Error: nothing to do");
            return;
        }
        setMessage(Translator.getString("STR_wait_msg"));
        bcancel.setText(Translator.getString("STR_abort"));

        curParam = 0;
        endSimu(null);
    }

    /**
     * simulation is done (or interrupted), now choose what to do with the new graph.
     * @param graph the dynamic graph
     */
    public void endSimu(GsGraph graph) {
        if (graph != null) {
            // TODO: add other anlyses (SCC,... ?)
        }
        if (initIterator != null && initIterator.hasNext()) {
            GsInitialState o_init = (GsInitialState)initIterator.next();
            s_init = o_init.getName();
            m_init.clear();
            m_init.put(o_init, null);
            sim = new Simulation(paramList.graph, this, param, false);
            result.append(param.name + " ; " + s_init+"\n");
            // FIXME: adapt to input definitions
            sim.startSimulation(paramList.graph.getNodeOrder(), null, m_init);
        } else if (curParam < nbParam) {
            param = (GsSimulationParameters)paramList.getElement(null, curParam);
            initIterator = param.m_initState.keySet().iterator();
            curParam++;
            endSimu(null);
        } else {
            bcancel.setText(Translator.getString("STR_close"));
        }
    }

    /**
     * close the frame, eventually end the simulation first 
     */
    protected void cancel() {
        if (sim != null) {
            sim.interrupt();    
        }
        paramList.graph.removeBlockEdit(this);
        super.cancel();
    }
    
    public void addStableState(SimulationQueuedState item) {
        super.addStableState(item);
        byte[] stable = item.state;
        String name = nameState(stable, paramList.graph);
        result.append("   " + name + ": ");
        for (int i=0 ; i<stable.length ; i++) {
            result.append(""+stable[i]);
        }
        result.append("\n");
    }

    public static String nameState(byte[] state, GsGraph graph) {
        InitialStateList init = ((GsInitialStateList)graph.getObject(GsInitialStateManager.key, false)).getInitialStates();
        // FIXME: adapt it to deal with input configs !!
        if (init != null && init.getNbElements(null) > 0) {
            List no = graph.getNodeOrder();
            for (int i=0 ; i<init.getNbElements(null) ; i++) {
                GsInitialState istate = (GsInitialState)init.getElement(null, i);
                Map m_istate = istate.getMap();
                boolean ok = true;
                for (int j=0 ; j<no.size() ; j++) {
                    List values = (List)m_istate.get(no.get(j));
                    if (values != null) {
                        ok = false;
                        int val = state[j];
                        Iterator it = values.iterator();
                        while (it.hasNext()) {
                            if (((Integer)it.next()).intValue() == val) {
                                ok = true;
                                break;
                            }
                        }
                        if (!ok) {
                            break;
                        }
                    }
                }
                if (ok) {
                    return istate.getName();
                }
            }
        }
        return null;
    }
}
