package fr.univmrs.tagc.GINsim.connectivity;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.univmrs.tagc.GINsim.css.CascadingStyle;
import fr.univmrs.tagc.GINsim.css.EdgeStyle;
import fr.univmrs.tagc.GINsim.css.VertexStyle;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.interactionAnalysis.InteractionAnalysisSelector;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.common.ColorPalette;
import fr.univmrs.tagc.common.ProgressListener;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;

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
    private javax.swing.JButton buttonColorize = null;
	private javax.swing.JLabel labelProgression = null;
	private javax.swing.JComboBox actionComboBox = null;
    private AlgoConnectivity algo = new AlgoConnectivity();
	private String[] possibleActions;
	private CascadingStyle cs;
	private Color[] colorPalette;
	private List components;
	private boolean isColored = false;
	
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
		this.possibleActions = new String[2];
		this.possibleActions[0] = Translator.getString("STR_connectivity_create_reducedGraph");
		this.possibleActions[1] = Translator.getString("STR_connectivity_colorize_currentGraph");
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(400, 130);
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
		if (isColored) {
			int res = JOptionPane.showConfirmDialog(this, Translator.getString("STR_sure_close_undo_colorize"));
			if (res == JOptionPane.OK_OPTION) undoColorize();
			else if (res == JOptionPane.CANCEL_OPTION) return;
		}
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
			
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 3;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.ipady = 10;
            c.weightx = 1;
            jContentPane.add(getLabelProgression(), c);

            c.gridx = 0;
            c.gridy++;
            jContentPane.add(getActions(), c);
            
            c.gridx = 0;
            c.gridy++;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.EAST;
            jContentPane.add(getJButtonColorize(), c);
            
            c.gridx++;
            c.anchor = GridBagConstraints.EAST;
            jContentPane.add(getJButtonCancel(), c);
 
            c.gridx++;
            c.anchor = GridBagConstraints.EAST;
            jContentPane.add(getJButtonRun(), c);
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
    private javax.swing.JComboBox getActions() {
        if(actionComboBox  == null) {
        	actionComboBox = new javax.swing.JComboBox(this.possibleActions);
        }
        return actionComboBox;
    }
   
   /** This method initializes buttonRun
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
            buttonCancel = new javax.swing.JButton(Translator.getString("STR_close"));
            buttonCancel.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    close();
                }
            });
        }
        return buttonCancel;
    }
    
    /**
     * This method initializes buttonCancel
     * 
     * @return buttonRun
     */
    private javax.swing.JButton getJButtonColorize() {
        if(buttonColorize == null) {
        	buttonColorize = new javax.swing.JButton(Translator.getString("STR_undo_colorize"));
        	buttonColorize.setEnabled(false);
        	buttonColorize.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                	colorize();
                }
            });
        }
        return buttonColorize;
    }
   
    protected void clicked() {
        if (algo.isAlive()) {
            algo.cancel();
        } else {
            algo.configure(graph, this, getModeFromActionComboBox());
            algo.start();
            buttonRun.setText(Translator.getString("STR_cancel"));
        }
    }
    
    
    private int getModeFromActionComboBox() {
    	if (this.actionComboBox.getSelectedIndex() == 0) return AlgoConnectivity.MODE_FULL;
    	else return AlgoConnectivity.MODE_COLORIZE;
	}
    
	public void setProgressText(String text) {
        getLabelProgression().setText(text);
    }
    
    public void setResult(Object result) {
        if (result != null) {
        	if (result instanceof GsReducedGraph) {
        		GsEnv.whatToDoWithGraph(frame, (GsReducedGraph)result, true);
                close();
            }
        }
        buttonRun.setText(Translator.getString("STR_run"));
        buttonRun.setEnabled(false);
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
	
	public void doColorize() {
		GsGraphManager gm = graph.getGraphManager();
		colorPalette = ColorPalette.defaultPalette;
		cs = new CascadingStyle(true);
		GsVertexAttributesReader vreader = gm.getVertexAttributesReader();		
		int color_index = 2;
		for (Iterator it = components.iterator(); it.hasNext();) {
			GsNodeReducedData scc = (GsNodeReducedData) it.next();
			if (scc.getType(gm) == GsNodeReducedData.SCC_TYPE_UNIQUE_NODE) {
				Object node = scc.getContent().get(0);
				vreader.setVertex(node);
				if (gm.getOutgoingEdges(node) == null || gm.getOutgoingEdges(node).size() == 0) {
					cs.applyOnNode(new VertexStyle(colorPalette[1], VertexStyle.NULL_FOREGROUND, VertexStyle.NULL_BORDER, VertexStyle.NULL_SHAPE), node, vreader);
				} else {
					cs.applyOnNode(new VertexStyle(colorPalette[0], VertexStyle.NULL_FOREGROUND, VertexStyle.NULL_BORDER, VertexStyle.NULL_SHAPE), node, vreader);
				}
				vreader.refresh();
			} else {
				for (Iterator it2 = scc.getContent().iterator(); it2.hasNext();) {
					Object node = (Object) it2.next();
					vreader.setVertex(node);
					cs.applyOnNode(new VertexStyle(colorPalette[color_index], VertexStyle.NULL_FOREGROUND, VertexStyle.NULL_BORDER, VertexStyle.NULL_SHAPE), node, vreader);
					vreader.refresh();
				}
				color_index++;
			}
		}		
    	buttonColorize.setEnabled(true);
    	buttonColorize.setText(Translator.getString("STR_undo_colorize"));
    	isColored = true;
 	}
	
	public void undoColorize() {
		cs.restoreAllNodes(graph.getGraphManager().getVertexAttributesReader());
    	buttonColorize.setText(Translator.getString("STR_do_colorize"));
    	isColored = false;
	}
	
	public void colorize() {
		if (isColored) {
			undoColorize();
		} else {
			doColorize();
		}
	}
	public void setComponents(List components) {
		this.components = components;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="11,9"  
