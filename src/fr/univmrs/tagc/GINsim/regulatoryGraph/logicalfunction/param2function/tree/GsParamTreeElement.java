package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function.tree;

import java.util.Hashtable;
import java.util.Vector;

interface GsParamTreeElement {
  public boolean isLeaf();
  public int getNbSons();
  public GsParamTreeElement getSon(int index);
  public void print(int depth);
  public void addSon(GsParamTreeElement el, int index);
  public String toString();
  public boolean equals(Object e2);
  public GsParamTreeNode getParent();
  public int getParentIndex();
  public void setParent(GsParamTreeNode e);
  public void setParentIndex(int i);
  public void makeFunctions(Hashtable h, String f, int dv, boolean pattern);
  public void makeDNF(Vector v, String s, int value);
  public int hashCode();
}