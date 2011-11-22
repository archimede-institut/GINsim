package org.ginsim.gui.tbclient.nodetree;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Vector;

import org.ginsim.annotation.AnnotationLink;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.tbclient.decotreetable.decotree.AbstractDTreeElement;
import org.ginsim.gui.tbclient.decotreetable.decotree.DTreeModel;

import fr.univmrs.tagc.common.managerresources.ImageLoader;

public class NodeTreeModel extends DTreeModel {
	
  public void addGene(RegulatoryNode vertex, Vector par) {
    AnnotationLink al;
    Vector v2 = new Vector();
    String[] field, sfield;
    int i, j;
    boolean ezSel = false;

    boolean deja = false;
    for (i = 0; i < root.getChildCount(); i++) {
    	deja = root.getChild(i).toString().equals(vertex.toString());
    	if (deja) break;
    }
    if (!deja) {
      NodeBuilder nb = new NodeBuilder(false);
      NodeTreeBuilder tb = new NodeTreeBuilder(nb);
      tb.setTree(tree);
      
      tb.newNode(vertex.toString(), Color.black, vertex);
      nb.setSelectable(false, null);
      nb.setNode();
      tb.addNode(nb.getNode());

      Hashtable dejanodes = new Hashtable();
      
      for (i = 0; i < par.size(); i++) {
        field = ((String)par.elementAt(i)).split("\t", 8);
        if (dejanodes.containsKey(field[0].toUpperCase())) 
        	tb.setCurrentNode((AbstractDTreeElement)dejanodes.get(field[0].toUpperCase()));
        else {
        	tb.newNode("Gene symbol: ", Color.blue, vertex);
        	nb.addLabel(field[0].toUpperCase(), Color.black);
        	nb.setSelectable(false, null);
        	nb.setNode();
        	tb.addNode(nb.getNode());
        	dejanodes.put(field[0].toUpperCase(), nb.getNode());
        }
        v2.clear();
        v2.addElement(vertex);
        v2.addElement("entrez");
        v2.addElement(field[3]);
        nb.addLabel(field[3], Color.black);
        tb.newNode("Entrez ID: ", Color.blue, v2);
        nb.addNodeNote(ImageLoader.getImageIcon("annotation_off.png"), v2, 
        		ImageLoader.getImageIcon("annotation_on.png"));
        tb.addNode(nb.getNode());
        /*te = new TreeElement("Entrez ID: ");
        te.setFgColor(Color.blue);
        v2.clear();
        v2.addElement(vertex);
        v2.addElement("entrez");
        v2.addElement(field[3]);
        tev = new TreeElementValue(te, field[3]);
        tevn = new TreeElementNodeNote(tev, v2);
        al = new AnnotationLink(tevn.toString(), vertex.getInteractionsModel().getGraph());
        tevn.setSelected(vertex.getAnnotation().containsLink(al));
        tel = new TreeElementLink(tevn, new URL(Tools.getLink("entrez", field[3])), vertex);
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
              huNode = new TreeElementLink(tevn, new URL(Tools.getLink("hugo", field[7])), vertex);
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
              swNode = new TreeElementLink(tevn, new URL(Tools.getLink("swissprot", field[6])), vertex);
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
                rsvNode = new TreeElementLink(tevn, new URL(Tools.getLink("refseq", sfield[j])), vertex);
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
                upvNode = new TreeElementLink(tevn, new URL(Tools.getLink("uniprot", sfield[j])), vertex);
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
          ((TreeElementSelectable)ezNode).setSelected(ezSel);*/
        	tb.decreaseLevel();
        }
        //if (root.indexOfChild(nodeNode) == -1) root.addElement(nodeNode);
      tree = tb.getTree();
      
      
    }
  }
}
