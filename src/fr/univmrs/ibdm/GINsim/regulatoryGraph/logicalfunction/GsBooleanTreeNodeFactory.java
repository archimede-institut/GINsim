package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNodeFactory;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanParser;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanOperand;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

public class GsBooleanTreeNodeFactory extends TBooleanTreeNodeFactory {
  private String operandClassName;
  private GsBooleanParser parser;
  private static HashMap genePool = null;

  public GsBooleanTreeNodeFactory(String className, String operandClassName, TBooleanParser parser) throws ClassNotFoundException {
    super(className, operandClassName, parser);
    this.operandClassName = operandClassName;
    this.parser = (GsBooleanParser)parser;
    if (genePool == null) genePool = new HashMap();
  }
  public TBooleanOperand createOperand(String value) throws Exception {
    GsBooleanGene bg = (GsBooleanGene)Class.forName(operandClassName).newInstance();
    bg.setValue(value);
    bg.setParser(parser);
    Vector p = (Vector)genePool.get(value);

    //if (p == null) {
      p = new Vector();
      Object[] allParams = parser.getAllParams();
      Vector allData = parser.getAllData();
      int nb_params = allParams.length;
      Vector data;
      Iterator it_data;
      GsLogicalFunctionListElement element;
      String testString = "";

      if (value.indexOf("#") >= 0) testString = value.replaceAll("#", "_");

      for (int i = 0; i < nb_params; i++) {
        data = (Vector)allParams[i];
        it_data = data.iterator();
        while (it_data.hasNext()) {
          element = (GsLogicalFunctionListElement)it_data.next();
          if (((value.indexOf("#") < 0) && (element.toString().indexOf(value + "_") != -1)) ||
              ((value.indexOf("#") >= 0) && (element.toString().equals(testString)))) {
            p.addElement(allData.elementAt(i));
            break;
          }
        }
      }
      genePool.put(value, p);
    //}
    GsLogicalFunctionList il = new GsLogicalFunctionList();
    il.setParser(parser);
    il.setData(new Vector(p));
    bg.setLogicalFunctionList(il);
    return bg;
  }
}
