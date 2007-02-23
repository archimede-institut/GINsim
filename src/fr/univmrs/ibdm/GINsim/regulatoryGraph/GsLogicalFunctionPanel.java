package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsIncomingEdgeListModel;

public class GsLogicalFunctionPanel extends GsParameterPanel {
	private static final long serialVersionUID = -87854595177707062L;

	private JSplitPane jSplitPane = null;
	private JList jList = null;
	private JScrollPane scrollIncoming = null;

	private GsIncomingEdgeListModel edgeList = null;

    private JTextField manualEntry = null;
    private JTextField manualLevel = null;
    private JButton manualHelp = null;

	private GsRegulatoryVertex currentVertex = null;
	
	public GsLogicalFunctionPanel(GsRegulatoryGraph graph) {
		super();
		setMainFrame(graph.getGraphManager().getMainFrame());
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
        setLayout(new GridBagLayout());
        
        GridBagConstraints c_manualEntry = new GridBagConstraints();
        GridBagConstraints c_manualLevel = new GridBagConstraints();
        GridBagConstraints c_manualHelp = new GridBagConstraints();
        GridBagConstraints c_split = new GridBagConstraints();

        c_split.gridx = 0;
        c_split.gridy = 0;
        c_split.gridwidth = 3;
        c_split.fill = GridBagConstraints.BOTH;
        c_split.weightx = 1;
        c_split.weighty = 1;
        
        c_manualLevel.gridx = 0;
        c_manualLevel.gridy = 1;
        c_manualLevel.fill = GridBagConstraints.BOTH;
        c_manualEntry.gridx = 1;
        c_manualEntry.gridy = 1;
        c_manualEntry.fill = GridBagConstraints.BOTH;
        c_manualEntry.weightx = 1;
        c_manualHelp.gridx = 2;
        c_manualHelp.gridy = 1;
        
        add(getManualEntry(), c_manualEntry);
        add(getManualLevel(), c_manualLevel);
        add(getManualHelp(), c_manualHelp);
        add(getJSplitPane(), c_split);
        
        edgeList = new GsIncomingEdgeListModel();
        jList.setModel(edgeList);
	}

	public void setEditedObject(Object obj) {
        if (currentVertex != null) {
            // apply pending changes
        }
        if (obj != null && obj instanceof GsRegulatoryVertex) {
            currentVertex = (GsRegulatoryVertex)obj;
            edgeList.setEdge(mainFrame.getGraph().getGraphManager().getIncomingEdges(currentVertex));
//            interactionList.setNode(currentVertex);
//            if (jTable.getSelectedRow() == -1) {
//                int i = interactionList.getRowCount();
//                jTable.getSelectionModel().setSelectionInterval(i, i);
//            }
            //cplModel.setNode(currentVertex, graph);
            manualEntry.setText("");
            manualLevel.setText("1");
        }
	}
	
    /**
	 * @return the jSplitPane
	 */
	private Component getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setName("jSplitPane");
			jSplitPane.setDividerSize(2);
			jSplitPane.setResizeWeight(1);
			jSplitPane.setLeftComponent(getFunctionTree());
			jSplitPane.setRightComponent(getScrollIncoming());
			jSplitPane.setDividerLocation(380);
		}
		return jSplitPane;
	}
	
	private JScrollPane getScrollIncoming() {
		if (scrollIncoming == null) {
			scrollIncoming = new JScrollPane();
			scrollIncoming.setViewportView(getJList());
		}
		return scrollIncoming;
	}
	
	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			jList.setModel(new GsIncomingEdgeListModel());
			jList.setName("jList");
		}
		return jList;
	}

    private JTextField getManualLevel() {
        if (manualLevel == null) {
            manualLevel = new JTextField("1");
            manualLevel.setMinimumSize(new Dimension(35, 18));
            
        }
        return manualLevel;
    }
    private JTextField getManualEntry() {
        if (manualEntry == null) {
            manualEntry = new JTextField();
            manualEntry.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    manualActivate();
                }
            });
        }
        return manualEntry;
    }
    
    private JButton getManualHelp() {
        if (manualHelp == null) {
            manualHelp = new JButton("?");
            manualHelp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    manualHelp();
                }
            });
        }
        return manualHelp;
    }

    protected JPanel getFunctionTree() {
    	return new JPanel();
    }
    
    protected void manualHelp() {
        // TODO: help for formula
    }

    protected void manualActivate() {
    	// TODO: manual activate
    }
}
