package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicPathItemCellRenderer;
import javax.swing.event.ListSelectionListener;
import java.util.Vector;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsTableInteractionsModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import java.util.Iterator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsEdgeIndex;
import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

public class GsLogicalFunctionTreePanel extends JPanel {
  private JTable functionTable;
  private GsTableInteractionsModel interactionList = null;
  private GsRegulatoryGraph graph;

  public GsLogicalFunctionTreePanel(GsRegulatoryGraph graph) {
    super();
    setLayout(new BorderLayout());
    add(new JScrollPane(getJTable(graph)), BorderLayout.CENTER);
    interactionList = new GsTableInteractionsModel(graph, new Vector());
    this.graph = graph;
  }
  public void paint_(Graphics g) {
    g.setColor(Color.white);
    g.fillRect(0, 0, this.getWidth(), this.getHeight());
    g.setColor(Color.red);
    g.drawLine(0, 0, this.getWidth(), this.getHeight());
    g.drawLine(0, this.getHeight(), this.getWidth(), 0);
  }
  private JTable getJTable(GsRegulatoryGraph graph) {
    if (functionTable == null) {
      Vector v_ok = new Vector();
      interactionList = new GsTableInteractionsModel(graph, v_ok);
      functionTable = new GsJTable(interactionList);
      functionTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
      functionTable.getColumn(functionTable.getColumnName(0)).setMaxWidth(50);
      functionTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      functionTable.setDefaultRenderer(Object.class, new GsDynamicPathItemCellRenderer(v_ok));
      functionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          //selectLeft2Right();
        }
      });
    }
    return functionTable;
  }
  public void addFunctionList(GsLogicalFunctionList list, short val, GsRegulatoryVertex currentVertex) {
    Iterator it = list.getData().iterator(), it2;
    Vector v = new Vector();
    GsEdgeIndex edgeIndex;
    GsLogicalFunctionListElement element;

    interactionList.setNode(currentVertex);
    if (functionTable.getSelectedRow() == -1) {
      int i = interactionList.getRowCount();
      functionTable.getSelectionModel().setSelectionInterval(i, i);
    }

    while (it.hasNext()) {
      it2 = ((Vector)it.next()).iterator();
      while (it2.hasNext()) {
        element = (GsLogicalFunctionListElement)it2.next();
        edgeIndex = new GsEdgeIndex(element.getEdge(), element.getIndex());
        v.addElement(edgeIndex);
      }
    }
    interactionList.setActivesEdges(interactionList.getRowCount(), v, val);
  }
}
