package org.ginsim.core.graph.regulatorygraph.logicalfunction.param2function.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;


public class ParamTree {
  private ParamTreeElement root = null;
  private int depth = -1, defaultValue = 0;
  private ArrayList vertexList;

  public ParamTree(ArrayList l, int dv) {
    super();
    vertexList = l;
    Iterator it = vertexList.iterator();
    Entry e;

    while (it.hasNext()) {
      e = (Entry)it.next();
      if (e.getValue() != null) {
				addLevel(++depth, (RegulatoryNode)e.getKey(), (RegulatoryMultiEdge)e.getValue());
      }
    }
    addLeaves(dv);
    defaultValue = dv;
  }
  private void addLevel(int level, RegulatoryNode v, RegulatoryMultiEdge me) {
    ParamTreeNode e;
    if (level == 0) {
		root = new ParamTreeNode(v, me, null, -1);
	} else {
      Vector nodes = new Vector();
      getNodes(level - 1, 0, root, nodes);

      for (int i = 0; i < nodes.size(); i++) {
        e = (ParamTreeNode)nodes.elementAt(i);
        for (int j = 0; j <= e.getNbEdge(); j++) {
			e.addSon(new ParamTreeNode(v, me, e, j), j);
		}
      }
    }
  }
  private void getNodes(int wantedDepth, int currentDepth, ParamTreeElement node, Vector v) {
    if (currentDepth == wantedDepth) {
		v.addElement(node);
	} else {
		for (int i = 0; i < node.getNbSons(); i++) {
			if (!node.getSon(i).isLeaf()) {
				getNodes(wantedDepth, currentDepth + 1, node.getSon(i), v);
			}
		}
	}
  }
  private void addLeaves(int dv) {
    ParamTreeNode e;
    ParamTreeLeafValue value;
    Vector v = new Vector();
    getNodes(depth, 0, root, v);
    for (int i = 0; i < v.size(); i++) {
      e = (ParamTreeNode)v.elementAt(i);
      for (int j = 0; j <= e.getNbEdge(); j++) {
        value = new ParamTreeLeafValue(e, j);
        value.setValue(new Integer(dv));
        e.addSon(value, j);
      }
    }
  }
  public void init(Collection<LogicalParameter> interactions, boolean comp) {
	  if (comp) defaultValue = 1234;
	  for (LogicalParameter p: interactions) {
		  init(p, comp);
	  }
  }
  private void init(LogicalParameter lp, boolean comp) {
    getLeaf(lp).setValue(new Integer(comp ? 1234 : lp.getValue()));
  }
  private ParamTreeLeaf getLeaf(LogicalParameter lp) {
    ParamTreeElement currentNode = root;
    boolean found;

    while (!currentNode.isLeaf()) {
      found = false;
      for (int i = 0; i < lp.EdgeCount(); i++) {
		if (lp.getEdge(i).me.getSource().getId().equals(currentNode.toString())) {
          currentNode = currentNode.getSon(lp.getEdge(i).index + 1);
          found = true;
          break;
        }
	}
      if (!found) {
		currentNode = currentNode.getSon(0);
	}
    }
    return (ParamTreeLeaf)currentNode;
  }
  public void process() {
    Vector nodes = new Vector();
    ParamTreeElement e, s;
    ParamTreeNode p;
    boolean b;

    for (int i = depth; i >= 0; i--) {
      nodes.clear();
      getNodes(i, 0, root, nodes);
      for (Enumeration enu = nodes.elements(); enu.hasMoreElements(); ) {
        e = (ParamTreeElement)enu.nextElement();
        b = true;
        for (int j = 1; j < e.getNbSons(); j++) {
			b = b && e.getSon(0).equals(e.getSon(j));
		}
        if (b) {
          if (e.getParent() == null) {
            root = e.getSon(0);
            root.setParent(null);
          }
          else {
            p = e.getParent();
            s = e.getSon(0);
            p.setSon(e.getParentIndex(), s);
            s.setParent(p);
            s.setParentIndex(e.getParentIndex());
          }
        }
      }
    }
  }
  public void findPatterns() {
    Vector lastNodes = new Vector();
    HashMap hm;
    ParamTreeNode node, lastn, parent;
    ParamTreeLeafPattern treeLeaf;
    int np = 1;
    boolean ok = true;

    while (ok) {
      lastNodes.clear();
      if (!root.isLeaf()) {
        getLastNodes(lastNodes, root);
        hm = new HashMap();
        for (Enumeration enu = lastNodes.elements(); enu.hasMoreElements(); ) {
          node = (ParamTreeNode)enu.nextElement();
          if (!hm.containsKey(node)) {
			hm.put(node, new Integer(1));
		} else {
			hm.put(node, new Integer(((Integer)hm.get(node)).intValue() + 1));
		}
        }
        Set set = hm.entrySet();
        Iterator it = set.iterator();
        ok = false;
        while (it.hasNext()) {
          node = (ParamTreeNode)((Entry)it.next()).getKey();
          if (((Integer)hm.get(node)).intValue() > 1) {
            treeLeaf = new ParamTreeLeafPattern();
            treeLeaf.setName("P" + np);
            treeLeaf.buildFunctions(node, defaultValue);
            for (Enumeration enu = lastNodes.elements(); enu.hasMoreElements(); ) {
              lastn = (ParamTreeNode)enu.nextElement();
              if (lastn.hashCode() == node.hashCode()) {
                parent = lastn.getParent();
                for (int i = 0; i < parent.getNbSons(); i++) {
                  if (parent.getSon(i).equals(node)) {
                    parent.setSon(i, treeLeaf);
                  }
                }
              }
            }
            np++;
            ok = true;
          }
        }
      } else {
		ok = false;
	}
    }
  }
  public void print() {
    root.print(0);
  }
  public Hashtable getFunctions() {
    Hashtable h = new Hashtable();
    root.makeFunctions(h, "", defaultValue, false);
    return h;
  }
  public String getDNFForm(int value, Vector params) {
    Vector v = new Vector();
    root.makeDNF(v, "", value);
    String s = "", tmp;
    if (v.size() > 0) {
      tmp = s = (String)v.firstElement();
      if (tmp.indexOf('&') >= 0)
		    s = "(" + tmp + ")";
      for (int i = 1; i < v.size(); i++) {
        tmp = (String)v.elementAt(i);
        if (tmp.indexOf('&') >= 0)
			    s += " | (" + tmp + ")";
		    else
			    s += "| " + tmp;
      }
    }
    return s;
  }
  private void getLastNodes(Vector v, ParamTreeElement node) {
    boolean ok = true;
    for (int i = 0; i < node.getNbSons(); i++) {
		ok = ok & node.getSon(i).isLeaf();
	}
    if (ok) {
		v.addElement(node);
	} else {
		for (int i = 0; i < node.getNbSons(); i++) {
			if (!node.getSon(i).isLeaf()) {
				getLastNodes(v, node.getSon(i));
			}
		}
	}
  }
}
