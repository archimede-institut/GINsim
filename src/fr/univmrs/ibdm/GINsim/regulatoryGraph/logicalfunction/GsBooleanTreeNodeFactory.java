package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanOperand;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanParser;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNodeFactory;

public class GsBooleanTreeNodeFactory extends TBooleanTreeNodeFactory {
  private Class operandClass;
  private GsBooleanParser parser;

  // FIXME: caching does not work
  //private static Map genePool = new HashMap();

  public GsBooleanTreeNodeFactory(String className, String operandClassName, TBooleanParser parser) throws ClassNotFoundException {
    super(className, operandClassName, parser);
    this.operandClass = Class.forName(operandClassName);
    this.parser = (GsBooleanParser)parser;
  }
  public TBooleanOperand createOperand(String value) throws Exception {
    GsBooleanGene bg = (GsBooleanGene)operandClass.newInstance();
    bg.setInteractionName(parser, value);

//    List p = (List) genePool.get(value);
//    if (true || p == null) {
    	List p = new ArrayList();
	    fillLogicalFunctionList(p, bg);
//	    genePool.put(value, p);
//    }
    GsLogicalFunctionList il = new GsLogicalFunctionList();
    il.setParser(parser);
    il.setData(new Vector(p));
    bg.setLogicalFunctionList(il);
    return bg;
  }
  
  private void fillLogicalFunctionList(List p, GsBooleanGene bg) {
  	Object[] allParams = parser.getAllParams();
    Vector allData = parser.getAllData();
    int nb_params = allParams.length;
    Vector data;
    Iterator it_data;
    GsLogicalFunctionListElement element;
//    String testString = "";

//    if (value.indexOf("#") >= 0) {
//		testString = value.replaceAll("#", "_");
//	}

    for (int i = 0; i < nb_params; i++) {
      data = (Vector)allParams[i];
      it_data = data.iterator();
      while (it_data.hasNext()) {
        element = (GsLogicalFunctionListElement)it_data.next();
//        if (value.indexOf("#") < 0 && element.toString().indexOf(value + "_") != -1 ||
//            value.indexOf("#") >= 0 && element.toString().equals(testString)) {
        if (bg.hasEdge(element)) {
          p.add(allData.get(i));
          break;
        }
      }
    }
  }
}
