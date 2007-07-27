package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import fr.univmrs.ibdm.GINsim.jgraph.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.*;
import fr.univmrs.ibdm.GINsim.util.widget.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;
import javax.swing.tree.TreePath;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;

public class GsParameterChoiceWindow extends JDialog implements ActionListener, ListSelectionListener {

  class GsCellRenderer extends JLabel implements ListCellRenderer {
    public GsCellRenderer() {
      setOpaque(true);
    }
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
      setText(value.toString());
      setBackground(isSelected ? Color.pink : Color.white);
      return this;
    }
  }
  class GsListElement {
    private GsRegulatoryMultiEdge grme;
    private int index;

    public GsListElement(GsRegulatoryMultiEdge e, int i) {
      grme = e;
      index = i;
    }
    public GsRegulatoryMultiEdge getEdge() {
      return grme;
    }
    public int getIndex() {
      return index;
    }
    public String toString() {
      return grme.getId(index).replace('_', ' ') + " " + grme.getEdgeName(index).substring(0,
        grme.getEdgeName(index).indexOf((int)';')) + ((grme.getSign(index) == 0) ? "+" : "-");
    }
  }

  private JList interactionList = new JList();
  private JButton okButton = new GsJButton(null, 4, 4, 0, 0);
  private JButton cancelButton = new GsJButton(null, 4, 4, 0, 0);
  private GsTreeParam treeParam;
  private int value;
  private JTree tree;
  private int[] oldSelection = null;

  public GsParameterChoiceWindow(JTree t) {
    super();
    tree = t;
    try {
      UIManager.put("ScrollBar.width", new Integer(12));
      jbInit();
      setUndecorated(true);
      setModal(true);
      pack();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  public void init(List interactions, Font f, GsTreeParam tp, int v) {
    GsRegulatoryMultiEdge o;
    Vector vec = new Vector();

    for (int i = 0; i < interactions.size(); i++) {
      o = (GsRegulatoryMultiEdge)((GsJgraphDirectedEdge)interactions.get(i)).getUserObject();
      for (int j = 0; j < o.getEdgeCount(); j++)
        vec.addElement(new GsListElement(o, j));
    }
    interactionList.setListData(vec);
    interactionList.setFont(f);
    treeParam = tp;
    value = v;
    oldSelection = null;
  }
  public void init(List interactions, Font f, GsTreeElement el) {
    GsRegulatoryMultiEdge o;
    Vector vec = new Vector(), vec2;
    GsEdgeIndex edgeIndex;
    GsListElement lElem;

    for (int i = 0; i < interactions.size(); i++) {
      o = (GsRegulatoryMultiEdge)((GsJgraphDirectedEdge)interactions.get(i)).getUserObject();
      for (int j = 0; j < o.getEdgeCount(); j++)
        vec.addElement(new GsListElement(o, j));
    }
    interactionList.setListData(vec);
    interactionList.setFont(f);
    treeParam = (GsTreeParam)el;
    value = ((GsTreeValue)el.getParent().getParent()).getValue();
    vec2 = treeParam.getEdgeIndexes();
    for (Enumeration enu = vec2.elements(); enu.hasMoreElements(); ) {
      edgeIndex = (GsEdgeIndex)enu.nextElement();
      for (int k = 0; k < interactionList.getModel().getSize(); k++) {
        lElem = (GsListElement)interactionList.getModel().getElementAt(k);
        if ((edgeIndex.data == lElem.getEdge()) && (edgeIndex.index == lElem.getIndex())) {
          interactionList.addSelectionInterval(k, k);
          break;
        }
      }
    }
    oldSelection = interactionList.getSelectedIndices();
  }

  private void jbInit() throws Exception {
    JPanel mainPanel = new JPanel(new GridBagLayout());
    mainPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    getContentPane().add(mainPanel);
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setPreferredSize(new Dimension(200, 150));
    scrollPane.getViewport().setView(interactionList);
    scrollPane.setBorder(null);

    okButton.setText("OK");
    cancelButton.setToolTipText("");
    cancelButton.setText("Cancel");
    okButton.addActionListener(this);
    cancelButton.addActionListener(this);
    JPanel buttonPanel = new JPanel(new GridBagLayout());
    interactionList.setBorder(BorderFactory.createEtchedBorder());
    interactionList.setCellRenderer(new GsCellRenderer());
    mainPanel.add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
      , GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    buttonPanel.add(cancelButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
      , GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    buttonPanel.add(okButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
      , GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
    mainPanel.add(buttonPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
      , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    interactionList.addListSelectionListener(this);
  }
  public void actionPerformed(ActionEvent e) {
    GsTreeInteractionsModel model = (GsTreeInteractionsModel)tree.getModel();
    if (e.getSource() == cancelButton) {
      if (oldSelection == null) {
        Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
        treeParam.remove(false);
        model.refreshVertex();
        model.setRootInfos();
        tree.stopEditing();
        model.fireTreeStructureChanged(treeParam.getParent());
        while (enu.hasMoreElements())tree.expandPath((TreePath)enu.nextElement());
      }
      else {
        interactionList.setSelectedIndices(oldSelection);
        model.refreshVertex();
        model.setRootInfos();
      }
      tree.repaint();
    }
    else if (e.getSource() == okButton) {
      model.refreshVertex();
    }
    dispose();
  }
  public void valueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
      Object[] sel = interactionList.getSelectedValues();
      GsLogicalParameter par = new GsLogicalParameter(value);
      for (int i = 0; i < sel.length; i++)
        par.addEdge(((GsListElement)sel[i]).getEdge(), ((GsListElement)sel[i]).getIndex());
      treeParam.setEdgeIndexes(par.getEdges());
      ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
      tree.repaint();
    }
  }
}
