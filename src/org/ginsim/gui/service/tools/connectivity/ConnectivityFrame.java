package org.ginsim.gui.service.tools.connectivity;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.reducedgraph.GsNodeReducedData;
import org.ginsim.graph.reducedgraph.GsReducedGraph;
import org.ginsim.gui.GUIManager;

import fr.univmrs.tagc.GINsim.css.CascadingStyle;
import fr.univmrs.tagc.GINsim.css.VertexStyle;
import fr.univmrs.tagc.common.ColorPalette;
import fr.univmrs.tagc.common.ProgressListener;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * config frame for the connectivity plugin.
 */
public class ConnectivityFrame extends JDialog implements ProgressListener {

	private static final long serialVersionUID = 2671795894716799300L;
	private javax.swing.JPanel jContentPane = null;
	private JFrame frame;
	protected Graph graph;
    private javax.swing.JButton buttonOpenSCC = null;
    private javax.swing.JButton buttonCancel = null;
    private javax.swing.JButton buttonColorize = null;
	private javax.swing.JLabel labelProgression = null;
    private AlgoConnectivity algoOpenGraph = null;
    private AlgoConnectivity algoColorize = null;
	private CascadingStyle cs;
	private Color[] colorPalette;
	private List components;
	private boolean isColored = false;
	
	/**
	 * This is the default constructor
	 * @param frame
	 * @param graph
	 */
	public ConnectivityFrame(JFrame frame, Graph graph) {
		
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
        if (algoColorize != null && algoColorize.isAlive()) {
        	algoColorize.cancel();
        }
        if (algoOpenGraph != null && algoOpenGraph.isAlive()) {
        	algoOpenGraph.cancel();
        } 
		if (isColored) {
			int res = JOptionPane.showConfirmDialog(this, Translator.getString("STR_sure_close_undo_colorize"));
			if (res == JOptionPane.NO_OPTION) {
				dispose();
			} else if (res == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (res == JOptionPane.YES_OPTION) {
				undoColorize();
				dispose();
			}
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
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.EAST;
            jContentPane.add(getJButtonCancel(), c);
            
            c.gridx++;
            jContentPane.add(getJButtonColorize(), c);
 
            c.gridx++;
            jContentPane.add(getJButtonOpenSCC(), c);
		}
		return jContentPane;
	}	
   
   /** This method initializes getJButtonOpenSCC
    * 
    * @return buttonRun
    */
   private javax.swing.JButton getJButtonOpenSCC() {
       if(buttonOpenSCC == null) {
           buttonOpenSCC = new javax.swing.JButton(Translator.getString("STR_connectivity_create_reducedGraph"));
           buttonOpenSCC.addActionListener(new java.awt.event.ActionListener() { 
               public void actionPerformed(java.awt.event.ActionEvent e) {
                  buttonOpenSCC.setText(Translator.getString("STR_cancel"));
                  clicked(AlgoConnectivity.MODE_FULL);
               }
           });
       }
       return buttonOpenSCC;
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
        	buttonColorize = new javax.swing.JButton(Translator.getString("STR_connectivity_colorize_currentGraph"));
        	buttonColorize.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                	clicked(AlgoConnectivity.MODE_COLORIZE);
                }
            });
        }
        return buttonColorize;
    }
   
    protected void clicked(int mode) {
    	 if (algoOpenGraph != null && algoOpenGraph.isAlive()) {
         	algoOpenGraph.cancel();
         }
    	 if (algoColorize != null && algoColorize.isAlive()) {
    		 algoColorize.cancel();
         }
    	 else {
    		AlgoConnectivity algo;
    		if (mode == AlgoConnectivity.MODE_COLORIZE) {
    			algoColorize = new AlgoConnectivity();
    			algo = algoColorize;
    		} else {
    			algoOpenGraph = new AlgoConnectivity();
    			algo = algoOpenGraph;
    		}
            algo.configure(graph, this, mode);
            algo.start();
            buttonColorize.setEnabled(false);
            buttonOpenSCC.setEnabled(false);
        }
    }

    
	public void setProgressText(String text) {
        getLabelProgression().setText(text);
    }
    
    public void setResult(Object result) {
        if (result != null) {
        	if (result instanceof GsReducedGraph) {
        		GUIManager.getInstance().whatToDoWithGraph((GsReducedGraph)result, graph, true);
            }
        }
        buttonOpenSCC.setText(Translator.getString("STR_connectivity_create_reducedGraph"));
        buttonColorize.setEnabled(true);
        buttonOpenSCC.setEnabled(true);
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

		colorPalette = ColorPalette.defaultPalette;
		cs = new CascadingStyle(true);
		VertexAttributesReader vreader = graph.getVertexAttributeReader();		
		int color_index = 2;
		for (Iterator it = components.iterator(); it.hasNext();) {
			GsNodeReducedData scc = (GsNodeReducedData) it.next();
			if (scc.getType( graph) == GsNodeReducedData.SCC_TYPE_UNIQUE_NODE) {
				Object node = scc.getContent().get(0);
				vreader.setVertex(node);
				if (graph.getOutgoingEdges(node) == null || graph.getOutgoingEdges(node).size() == 0) {
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
    	buttonColorize.setText(Translator.getString("STR_undo_colorize"));
    	isColored = true;
 	}
	
	public void undoColorize() {
		if (cs != null) cs.restoreAllNodes(graph.getVertexAttributeReader());
    	buttonColorize.setText(Translator.getString("STR_connectivity_colorize_currentGraph"));
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
}
