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
      Vector allData = parser.getAllData();
      Iterator it_allData = allData.iterator();
      Vector data;
      Iterator it_data;
      GsLogicalFunctionListElement element;
      String testString = "";

      if (value.indexOf("#") >= 0) testString = value.replaceAll("#", "_");

      while (it_allData.hasNext()) {
        data = (Vector)it_allData.next();
        it_data = data.iterator();
        while (it_data.hasNext()) {
          element = (GsLogicalFunctionListElement)it_data.next();
          if (((value.indexOf("#") < 0) && (element.toString().indexOf(value + "_") != -1)) ||
              ((value.indexOf("#") >= 0) && (element.toString().equals(testString)))) {
            p.addElement(new Vector(data));
            break;
          }
        }
      }
      genePool.put(value, p);
    //}
    GsLogicalFunctionList il = new GsLogicalFunctionList();
    il.setData(p);
    bg.setLogicalFunctionList(il);
    return bg;
  }
}
