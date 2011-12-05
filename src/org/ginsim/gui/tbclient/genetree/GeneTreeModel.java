package org.ginsim.gui.tbclient.genetree;

import java.awt.Color;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.ginsim.annotation.AnnotationLink;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;



public class GeneTreeModel implements TreeModel {
  private AbstractTreeElement root;
  private Vector treeModelListeners = new Vector();

  public GeneTreeModel(AbstractTreeElement r) {
    root = r;
  }
  public void init(Hashtable genes) {
    RegulatoryNode vertex;
    TreeElement te;
    TreeElementNode ten;
    TreeElementValue tev;
    TreeElementLink tel;
    TreeElementNodeNote tevn;
    AnnotationLink al;
    AbstractTreeElement nodeNode, gsNode, ezNode, desNode, swNode, rsNode, rsvNode, upNode, upvNode, alNode, huNode;
    Vector v, v2 = new Vector();
    String[] field, sfield;
    int i, j;
    boolean ezSel = false;

		((TreeElementNode)root).sons.clear();
    try {
      for (Enumeration enu = genes.keys(); enu.hasMoreElements(); ) {
        vertex = (RegulatoryNode)enu.nextElement();
        te = new TreeElement(vertex.toString());
        ten = new TreeElementNode(te);
        nodeNode = new TreeElementSelectable(ten, false, vertex, this);
        if (root.indexOfChild(nodeNode) != -1) nodeNode = (AbstractTreeElement)root.getChild(root.indexOfChild(nodeNode));
        v = (Vector)genes.get(vertex);
        for (i = 0; i < v.size(); i++) {
          field = ((String)v.elementAt(i)).split("\t", 8);
          te = new TreeElement("Gene symbol: ");
          te.setFgColor(Color.blue);
          tev = new TreeElementValue(te, field[0]);
          ten = new TreeElementNode(tev);
          gsNode = new TreeElementSelectable(ten, false, vertex, this);
          if (nodeNode.indexOfChild(gsNode) == -1)
            nodeNode.addElement(gsNode);
          else
            gsNode = (AbstractTreeElement)nodeNode.getChild(nodeNode.indexOfChild(gsNode));
          te = new TreeElement("Entrez ID: ");
          te.setFgColor(Color.blue);
          v2.clear();
          v2.addElement(vertex);
          v2.addElement("entrez");
          v2.addElement(field[3]);
          tev = new TreeElementValue(te, field[3]);
          tevn = new TreeElementNodeNote(tev, v2);
          al = new AnnotationLink(tevn.toString(), vertex.getInteractionsModel().getGraph());
          tevn.setSelected(vertex.getAnnotation().containsLink(al));
          tel = new TreeElementLink(tevn, new URL(IOUtils.getLink("entrez", field[3])), vertex);
          ten = new TreeElementNode(tel);
          ezNode = new TreeElementSelectable(ten, false, vertex, this);
          alNode = null;
          if (gsNode.indexOfChild(ezNode) == -1) {
            ezSel = vertex.getAnnotation().containsLink(al);
            gsNode.addElement(ezNode);
            if (!field[2].equals("")) {
              te = new TreeElement("Description: ");
              te.setFgColor(Color.blue);
              desNode = new TreeElementValue(te, field[2]);
              ezNode.addElement(desNode);
            }
            if (!field[7].equals("")) {
              te = new TreeElement("HUGO: ");
              te.setFgColor(Color.blue);
              v2.setElementAt("hugo", 1);
              v2.setElementAt(field[7], 2);
              tev = new TreeElementValue(te, field[7]);
              tevn = new TreeElementNodeNote(tev, v2);
              al = new AnnotationLink(tevn.toString(), vertex.getInteractionsModel().getGraph());
              tevn.setSelected(vertex.getAnnotation().containsLink(al));
              if (!ezSel) ezSel = vertex.getAnnotation().containsLink(al);
              huNode = new TreeElementLink(tevn, new URL(IOUtils.getLink("hugo", field[7])), vertex);
              ezNode.addElement(huNode);
            }
            if (!field[6].equals("")) {
              te = new TreeElement("SwissProt: ");
              te.setFgColor(Color.blue);
              v2.setElementAt("swissprot", 1);
              v2.setElementAt(field[6], 2);
              tev = new TreeElementValue(te, field[6]);
              tevn = new TreeElementNodeNote(tev, v2);
              al = new AnnotationLink(tevn.toString(), vertex.getInteractionsModel().getGraph());
              tevn.setSelected(vertex.getAnnotation().containsLink(al));
              if (!ezSel) ezSel = vertex.getAnnotation().containsLink(al);
              swNode = new TreeElementLink(tevn, new URL(IOUtils.getLink("swissprot", field[6])), vertex);
              ezNode.addElement(swNode);
            }
            if (!field[4].equals("")) {
              sfield = field[4].split(",");
              te = new TreeElement("RefSeq");
              rsNode = new TreeElementNode(te);
              v2.setElementAt("refseq", 1);
              for (j = 0; j < sfield.length; j++) {
                v2.setElementAt(sfield[j], 2);
                te = new TreeElement(sfield[j]);
                tevn = new TreeElementNodeNote(te, v2);
                al = new AnnotationLink(tevn.toString(), vertex.getInteractionsModel().getGraph());
                tevn.setSelected(vertex.getAnnotation().containsLink(al));
                if (!ezSel) ezSel = vertex.getAnnotation().containsLink(al);
                rsvNode = new TreeElementLink(tevn, new URL(IOUtils.getLink("refseq", sfield[j])), vertex);
                if (rsNode.indexOfChild(rsvNode) == -1) rsNode.addElement(rsvNode);
              }
              ezNode.addElement(rsNode);
            }
            if (!field[5].equals("")) {
              sfield = field[5].split(",");
              te = new TreeElement("UniProt");
              upNode = new TreeElementNode(te);
              v2.setElementAt("uniprot", 1);
              for (j = 0; j < sfield.length; j++) {
                v2.setElementAt(sfield[j], 2);
                te = new TreeElement(sfield[j]);
                tevn = new TreeElementNodeNote(te, v2);
                al = new AnnotationLink(tevn.toString(), vertex.getInteractionsModel().getGraph());
                tevn.setSelected(vertex.getAnnotation().containsLink(al));
                if (!ezSel) ezSel = vertex.getAnnotation().containsLink(al);
                upvNode = new TreeElementLink(tevn, new URL(IOUtils.getLink("uniprot", sfield[j])), vertex);
                if (upNode.indexOfChild(upvNode) == -1) upNode.addElement(upvNode);
              }
              ezNode.addElement(upNode);
            }
            if (!field[1].equals("") && !field[1].equalsIgnoreCase("null")) {
              te = new TreeElement("Alias: ");
              te.setFgColor(Color.blue);
              alNode = new TreeElementValue(te, field[1]);
              ezNode.addElement(alNode);
            }
          }
          else if (!field[1].equals("") && !field[1].equalsIgnoreCase("null")) {
            ezNode = (AbstractTreeElement)gsNode.getChild(gsNode.indexOfChild(ezNode));
            ezSel = vertex.getAnnotation().containsLink(al);
            for (j = 0; j < ezNode.getChildCount(); j++) {
              alNode = (AbstractTreeElement)ezNode.getChild(j);
              if (alNode.toString().startsWith("Alias")) break;
            }
            if (j == ezNode.getChildCount()) {
              alNode = new TreeElementValue(new TreeElement("Alias"), field[1]);
              if (ezNode.indexOfChild(alNode) == -1) ezNode.addElement(alNode);
            }
            else
              ((TreeElementValue)alNode).concat("," + field[1]);
          }
          ((TreeElementSelectable)ezNode).setSelected(ezSel);
        }
        if (root.indexOfChild(nodeNode) == -1) root.addElement(nodeNode);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  public Object getRoot() {
    return root;
  }
  public Hashtable getSelectedEntrezIDs() {
    Hashtable h = new Hashtable();
    Vector v;
    AbstractTreeElement node, symbol, entrez;

    for (int in = 0; in < root.getChildCount(); in++) {
      node = (AbstractTreeElement)root.getChild(in);
      v = new Vector();
      for (int is = 0; is < node.getChildCount(); is++) {
        symbol = (AbstractTreeElement)node.getChild(is);
        for (int iz = 0; iz < symbol.getChildCount(); iz++) {
          entrez = (AbstractTreeElement)symbol.getChild(iz);
          if (entrez.isSelected()) v.addElement(entrez.getValue());
        }
      }
      if (v.size() > 0) h.put(node.toString(), v);
    }
    return h;
  }

  public Object getChild(Object object, int _int) {
    return ((AbstractTreeElement)object).getChild(_int);
  }

  public int getChildCount(Object object) {
    return ((AbstractTreeElement)object).getChildCount();
  }

  public boolean isLeaf(Object object) {
    return ((AbstractTreeElement)object).isLeaf();
  }

  public void valueForPathChanged(TreePath treePath, Object object) {
  }

  public int getIndexOfChild(Object object, Object object1) {
    return ((AbstractTreeElement)object).indexOfChild(object1);
  }

  public void addTreeModelListener(TreeModelListener treeModelListener) {
    treeModelListeners.addElement(treeModelListener);
  }

  public void removeTreeModelListener(TreeModelListener treeModelListener) {
    treeModelListeners.removeElement(treeModelListener);
  }
  public Vector getGeneSymbols() {
    Vector v = new Vector();
    AbstractTreeElement nodeElement, gsElement;

    for (int i = 0; i < root.getChildCount(); i++) {
      nodeElement = (AbstractTreeElement)root.getChild(i);
      for (int k = 0; k < nodeElement.getChildCount(); k++) {
        gsElement = (AbstractTreeElement)nodeElement.getChild(k);
        if (!v.contains(gsElement.toString().toLowerCase()))
          v.addElement(gsElement.toString().toLowerCase());
      }
    }
    return v;
  }
  public void fireTreeStructureChanged(AbstractTreeElement element) {
    TreeModelEvent e = new TreeModelEvent(this, new Object[] {element});
    for (Iterator it = treeModelListeners.iterator(); it.hasNext(); )
      ((TreeModelListener)it.next()).treeStructureChanged(e);
  }
  public void fireTreeNodesChanged(AbstractTreeElement element) {
    TreeModelEvent e = new TreeModelEvent(this, new Object[] {element});
    for (Iterator it = treeModelListeners.iterator(); it.hasNext(); )
      ((TreeModelListener)it.next()).treeNodesChanged(e);
  }
}
