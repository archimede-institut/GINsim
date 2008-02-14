package fr.univmrs.ibdm.GINsim.connectivity;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.tagc.global.ProgressListener;
import fr.univmrs.tagc.global.Tools;
import fr.univmrs.tagc.manageressources.Translator;

/**
 * config frame for the connectivity plugin.
 */
public class ConnectivityFrame extends JDialog implements ProgressListener {

	private static final long serialVersionUID = 2671795894716799300L;
	private javax.swing.JPanel jContentPane = null;
	private JFrame frame;
	protected GsGraph graph;
    private javax.swing.JButton buttonRun = null;
    private javax.swing.JButton buttonCancel = null;
	private javax.swing.JLabel labelProgression = null;
    private AlgoConnectivity algo = new AlgoConnectivity();
	/**
	 * This is the default constructor
	 * @param frame
	 * @param graph
	 */
	public ConnectivityFrame(JFrame frame, GsGraph graph) {
		super(frame);
		this.frame = frame;
		if (graph == null) {
			Tools.error("no graph", frame);
		}
		this.graph = graph;
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(400, 100);
		this.setContentPane(getJContentPane());
		this.setTitle(Translator.getString("STR_connectivity"));
		this.setVisible(true);
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {
			    close();
			}
		});
	}
	/**
     * 
     */
    protected void close() {
        if (algo != null && algo.isAlive()) {
            algo.cancel();
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
			
            GridBagConstraints s_b_run = new GridBagConstraints();
            GridBagConstraints s_b_cancel = new GridBagConstraints();
			GridBagConstraints s_l_progress = new GridBagConstraints();
			
            s_l_progress.gridx = 0;
            s_l_progress.gridy = 0;
            s_l_progress.gridwidth = 2;
            s_l_progress.anchor = GridBagConstraints.WEST;
            s_l_progress.fill = GridBagConstraints.HORIZONTAL;
            s_l_progress.weightx = 1;
            s_b_cancel.gridx = 1;
            s_b_cancel.gridy = 1;
            s_b_cancel.anchor = GridBagConstraints.EAST;
            s_b_run.gridx = 2;
            s_b_run.gridy = 1;
            s_b_run.anchor = GridBagConstraints.EAST;
			
            jContentPane.add(getLabelProgression(), s_l_progress);
            jContentPane.add(getJButtonRun(), s_b_run);
            jContentPane.add(getJButtonCancel(), s_b_cancel);
		}
		return jContentPane;
	}
	/**
	 * Verify if the specified String is an integer
	 * @param s - string to be tested
	 * @return true if s is an integer, false if it isn't
	 */
	public boolean isInteger(String s){
		try{
			Integer.parseInt(s);
		 }
		catch (NumberFormatException e) {				
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
        if(buttonRun == null) {
            buttonRun = new javax.swing.JButton(Translator.getString("STR_run"));
            buttonRun.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    clicked();
                }
            });
        }
        return buttonRun;
    }
    
    /**
     * This method initializes buttonCancel
     * 
     * @return buttonRun
     */
    private javax.swing.JButton getJButtonCancel() {
        if(buttonCancel == null) {
            buttonCancel = new javax.swing.JButton(Translator.getString("STR_cancel"));
            buttonCancel.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    close();
                }
            });
        }
        return buttonCancel;
    }
    
    protected void clicked() {
        if (algo.isAlive()) {
            algo.cancel();
        } else {
            algo.configure(graph, this, AlgoConnectivity.MODE_FULL);
            algo.start();
            buttonRun.setText(Translator.getString("STR_cancel"));
            jContentPane.remove(buttonCancel);
        }
    }
    
    public void setProgressText(String text) {
        getLabelProgression().setText(text);
    }
    
    public void setResult(Object result) {
        if (result != null && result instanceof GsReducedGraph) {
            GsEnv.whatToDoWithGraph(frame, (GsReducedGraph)result, true);
        }
        close();
    }

    /**
	 * This method initializes labelProgression
	 * 
	 * @return javax.swing.JLabel
	 */
	public javax.swing.JLabel getLabelProgression() {
		if(labelProgression == null) {
			labelProgression = new javax.swing.JLabel();
			labelProgression.setText(Translator.getString("STR_connectivity_ask"));
		}
		return labelProgression;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="11,9"  
