package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNode;

public class GsTreeExpression extends GsTreeElement {
  private String expression;
  private TBooleanTreeNode root;

  public GsTreeExpression(GsTreeElement parent, TBooleanTreeNode root) {
    super(parent);
    this.root = root;
    expression = root.toString();
  }
  public String toString() {
    return expression;
  }
}
