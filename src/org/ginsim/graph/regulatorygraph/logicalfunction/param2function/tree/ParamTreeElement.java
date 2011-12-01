package org.ginsim.graph.regulatorygraph.logicalfunction.param2function.tree;

import java.util.Hashtable;
import java.util.Vector;

interface ParamTreeElement {
  public boolean isLeaf();
  public int getNbSons();
  public ParamTreeElement getSon(int index);
  public void print(int depth);
  public void addSon(ParamTreeElement el, int index);
  public String toString();
  public boolean equals(Object e2);
  public ParamTreeNode getParent();
  public int getParentIndex();
  public void setParent(ParamTreeNode e);
  public void setParentIndex(int i);
  public void makeFunctions(Hashtable h, String f, int dv, boolean pattern);
  public void makeDNF(Vector v, String s, int value);
  public int hashCode();
}