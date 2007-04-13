package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import fr.univmrs.ibdm.GINsim.gui.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanParser;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import java.util.Vector;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;

public class GsLogicalFunctionPanel extends GsParameterPanel {
  private static final long serialVersionUID = -87854595177707062L;
  private GsIncomingEdgeListModel edgeList = null;
  private JTextField manualEntry = null;
  private JTextField manualLevel = null;
  private JButton manualHelp = null;
  private GsRegulatoryVertex currentVertex = null;
  private GsLogicalFunctionTreePanel treePanel = null;
  private GsRegulatoryGraph graph;

  public GsLogicalFunctionPanel(GsRegulatoryGraph graph) {
    super();
    setMainFrame(graph.getGraphManager().getMainFrame());
    this.graph = graph;
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
    add(getTreePanel(), c_split);

    edgeList = new GsIncomingEdgeListModel();
  }
  public void setEditedObject(Object obj) {
    if (currentVertex != null) {
      treePanel.setEditedObject(obj);
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

  private JTextField getManualLevel() {
    if (manualLevel == null) {
      manualLevel = new JTextField("1");
      manualLevel.setMinimumSize(new Dimension(35, 18));
      manualLevel.setPreferredSize(new Dimension(35, 18));
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

  protected JPanel getTreePanel() {
    if (treePanel == null) {
      treePanel = new GsLogicalFunctionTreePanel(graph);
    }
    return treePanel;
  }

  protected void manualHelp() {
    // TODO: help for formula
  }

  protected void manualActivate() {
    Vector nodeOrder = graph.getNodeOrder();
    GsGraphManager manager = graph.getGraphManager();
    short value = (short)(currentVertex.getMaxValue() + 1);
    try {
      value = (short)Integer.parseInt(manualLevel.getText().trim());
    }
    catch (Exception e) {
    }
    if (value > currentVertex.getMaxValue()) {
      graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "invalid value: " + manualLevel.getText().trim(),
          GsGraphNotificationMessage.NOTIFICATION_WARNING));
      return;
    }
    Vector allowedEdges = new Vector();
    for (int i = 0 ; i < nodeOrder.size() ; i++) {
      GsDirectedEdge o = (GsDirectedEdge)manager.getEdge(nodeOrder.get(i), currentVertex);
      if (o != null) allowedEdges.addElement(o);
    }

    //Iterator it = l_edge.iterator();

    GsLogicalFunctionList functionList;
    try {
      TBooleanParser tbp = new GsBooleanParser("fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionList",
                                               "fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsBooleanGene",
                                               allowedEdges);
      if (!tbp.compile(this.getManualEntry().getText())) {
        graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "invalid formula",
            GsGraphNotificationMessage.NOTIFICATION_WARNING));
      }
      else {
        functionList = (GsLogicalFunctionList)tbp.eval();
        treePanel.addFunctionList(functionList, value, currentVertex, ((GsBooleanParser)tbp).getRoot());
        //functionList.print();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}