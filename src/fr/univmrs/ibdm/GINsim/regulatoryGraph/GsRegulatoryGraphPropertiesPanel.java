package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.xml.GsGinmlHelper;
/**
 * edit graph's properties: name, node order and annotations
 */
public class GsRegulatoryGraphPropertiesPanel extends JPanel implements GsGraphListener {

	private static final long serialVersionUID = 5806013832972989703L;
	private JLabel jLabel = null;
	private JTextField jTextField = null;
	private JLabel jLabelDTD = null;
	private JTextField jTextFieldDTD = null;
	private JButton JButtonDefaultDTD = null;
	private JButton LocalDTD = null;
	private GsAnnotationPanel gsAnnotationPanel = null;
	private javax.swing.JList orderList = null;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JButton upButton = null;
	private javax.swing.JButton downButton = null;
	private GsListModel orderModel;
	private GsGraph graph;

	/**
	 * This method initializes 
	 * @param graph
	 * 
	 */
	public GsRegulatoryGraphPropertiesPanel(GsGraph graph) {
		super();
        this.graph = graph;
		graph.addGraphListener(this);
		initialize();
        
        gsAnnotationPanel.setGraph(graph);
	}
	/**
	 * This method initializes this
	 */
	private void initialize()
	{
        jLabel = new JLabel();
        jLabelDTD = new JLabel();
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
        
        this.setLayout(new GridBagLayout());
        this.setSize(237, 62);
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        jLabel.setText(Translator.getString("STR_name"));
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.gridwidth = 2;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridx = 3;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.gridwidth = 5;
        gridBagConstraints3.gridheight = 3;
        gridBagConstraints3.weightx = 4;
        gridBagConstraints3.weighty = 1;
        gridBagConstraints3.fill = GridBagConstraints.BOTH;
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.weightx = 1;
        gridBagConstraints4.weighty = 1;
        gridBagConstraints4.gridheight = 3;
        gridBagConstraints4.gridwidth = 2;
        gridBagConstraints4.fill = GridBagConstraints.BOTH;
        gridBagConstraints5.gridx = 2;
        gridBagConstraints5.gridy = 2;
        gridBagConstraints6.gridx = 2;
        gridBagConstraints6.gridy = 3;
        gridBagConstraints7.gridx = 3;
        gridBagConstraints7.gridy = 0;
        jLabelDTD.setText(Translator.getString("STR_DTD"));
        gridBagConstraints8.gridx = 4;
        gridBagConstraints8.gridy = 0;
        gridBagConstraints8.weightx = 1;
        gridBagConstraints8.fill = GridBagConstraints.BOTH;
        gridBagConstraints9.gridx = 5;
        gridBagConstraints9.gridy = 0;
        gridBagConstraints10.gridx = 6;
        gridBagConstraints10.gridy = 0;
        
        this.add(jLabel, gridBagConstraints1);
        this.add(getJTextField(), gridBagConstraints2);
        this.add(getGsAnnotationPanel(), gridBagConstraints3);
        this.add(getJScrollPane(), gridBagConstraints4);
        if (graph instanceof GsRegulatoryGraph) {
            this.add(getUpButton(), gridBagConstraints5);
            this.add(getDownButton(), gridBagConstraints6);
        }
		this.add(jLabelDTD, gridBagConstraints7);
		this.add(getJTextFieldDTD(), gridBagConstraints8);
		this.add(getJButtonDefaultDTD(), gridBagConstraints9);
		this.add(getLocalDTD(), gridBagConstraints10);
	}
	/**
	 * @return the annotationPanel
	 */
	private Component getGsAnnotationPanel() {
		if (gsAnnotationPanel == null) {
			gsAnnotationPanel = new GsAnnotationPanel();
			gsAnnotationPanel.setEditedObject(graph.getAnnotation());
		}
		return gsAnnotationPanel;
	}
	/**
	 * @return the jTextField
	 */
	private Component getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setText(graph.getGraphName());
			jTextField.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {
					doChangeGraphName();
				}
			});
		}
		return jTextField;
	}
	
	protected void doChangeGraphName() {
		try {
			graph.setGraphName(jTextField.getText());
		} catch (GsException e) {
			GsEnv.error(e, null);
			jTextField.setText(graph.getGraphName());
		}
	}
	
	/**
	 * This method initializes orderList
	 * 
	 * @return javax.swing.JList
	 */
	private javax.swing.JList getOrderList() {
		if(orderList == null) {
			orderModel = new GsListModel(graph.getNodeOrder());
			orderList = new javax.swing.JList(orderModel);
			orderList.setVisibleRowCount(5);
		}
		return orderList;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getOrderList());
			jScrollPane.setMinimumSize(new java.awt.Dimension(30,20));
		}
		return jScrollPane;
	}
	/**
	 * This method initializes upButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getUpButton() {
		if(upButton == null) {
			upButton = new javax.swing.JButton(GsEnv.getIcon("upArrow.gif"));
			upButton.setName("upButton");
			upButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    moveUp();
				}
			});
		}
		return upButton;
	}

    protected void moveUp() {
		int[] index=orderList.getSelectedIndices();
		for (int i=0;i<index.length;i++) {
			if (index[i]>0) {
				((GsListModel)orderList.getModel()).moveElementAt(index[i],index[i]-1);
				index[i]--;
			} else return;
		}
		orderList.setSelectedIndices(index);
    }
    /**
	 * This method initializes downButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getDownButton() {
		if(downButton == null) {
			downButton = new javax.swing.JButton(GsEnv.getIcon("downArrow.gif"));
			downButton.setName("downButton");
			downButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    moveDown();
				}
			});
		}
		return downButton;
	}

    protected void moveDown() {
		int[] index=orderList.getSelectedIndices();
		for (int i=index.length-1;i>=0;i--) {
			if (index[i]>=0 && index[i]+1<((GsListModel)orderList.getModel()).getSize()) {
				((GsListModel)orderList.getModel()).moveElementAt(index[i],index[i]+1);
				index[i]++;
			} else return;
		}
		orderList.setSelectedIndices(index);
    }

    public GsGraphEventCascade edgeAdded(Object data) {
        return null;
    }
	public GsGraphEventCascade edgeRemoved(Object data) {
        return null;
	}
	public GsGraphEventCascade vertexAdded(Object data) {
		orderModel.fireAllChanged();
        return null;
	}
	public GsGraphEventCascade vertexRemoved(Object data) {
		orderModel.fireAllChanged();
        return null;
	}
    public GsGraphEventCascade vertexUpdated(Object data) {
        return null;
    }
    public GsGraphEventCascade edgeUpdated(Object data) {
        return null;
    }

	private JButton getJButtonDefaultDTD() {
		if (JButtonDefaultDTD == null) {
			JButtonDefaultDTD = new JButton(Translator.getString("STR_defaultDTD"));
			JButtonDefaultDTD.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    applyDTD(GsGinmlHelper.DEFAULT_URL_DTD_FILE);
				}
			});
		}
		return JButtonDefaultDTD;
	}
	/**
	 * @param DTD
     */
    protected void applyDTD(String DTD) {
        graph.setDTD(DTD);
		jTextFieldDTD.setText(graph.getDTD());
    }
    protected JTextField getJTextFieldDTD() {
		if (jTextFieldDTD == null) {
			jTextFieldDTD = new JTextField();
			jTextFieldDTD.setText(graph.getDTD());
            jTextFieldDTD.addFocusListener(new FocusListener() {
                public void focusLost(FocusEvent e) {
                    applyDTD(getJTextFieldDTD().getText());
                }
                public void focusGained(FocusEvent e) {
                }
            });
		}
		return jTextFieldDTD;
	}
	private JButton getLocalDTD() {
		if (LocalDTD == null) {
			LocalDTD = new JButton(Translator.getString("STR_localDTD"));
			LocalDTD.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    applyDTD(GsGinmlHelper.LOCAL_URL_DTD_FILE);
				}
			});
		}
		return LocalDTD;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"

class GsListModel extends AbstractListModel {
    private static final long serialVersionUID = 5591479457883433089L;
    
    private Vector vec;
	
	protected GsListModel(Vector v) {
		vec=v;
	}
	
	public Object getElementAt(int index) {
		return vec.get(index);
	}

	public int getSize() {
		return vec.size();
	}
	
	/**
	 * the REAL job of this model: altering the node-order...
	 * @param index
	 * @param to
	 */
	protected void moveElementAt(int index,int to) {
		Object obj=vec.remove(index);
		vec.insertElementAt(obj,to);
		fireContentsChanged(this, 0, vec.size());
	}
	
	protected void setVector(Vector v) {
		vec=v;
        fireContentsChanged(this, 0, vec.size());
	}
    
    protected void fireAllChanged() {
        fireContentsChanged(this, 0, vec.size());
    }
}

