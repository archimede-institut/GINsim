package org.ginsim.graph.regulatorygraph.logicalfunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.ginsim.graph.regulatorygraph.logicalfunction.parser.TBooleanOperand;
import org.ginsim.graph.regulatorygraph.logicalfunction.parser.TBooleanParser;
import org.ginsim.graph.regulatorygraph.logicalfunction.parser.TBooleanTreeNodeFactory;


public class BooleanTreeNodeFactory extends TBooleanTreeNodeFactory {
  private Class operandClass;
  private BooleanParser parser;

  // FIXME: caching does not work
  //private static Map genePool = new HashMap();

  public BooleanTreeNodeFactory(String className, String operandClassName, TBooleanParser parser) throws ClassNotFoundException {
    super(className, operandClassName, parser);
    this.operandClass = Class.forName(operandClassName);
    this.parser = (BooleanParser)parser;
  }
  public TBooleanOperand createOperand(String value) throws Exception {
    BooleanGene bg = (BooleanGene)operandClass.newInstance();
    bg.setInteractionName(parser, value);

//    List p = (List) genePool.get(value);
//    if (true || p == null) {
    	List p = new ArrayList();
	    fillLogicalFunctionList(p, bg);
//	    genePool.put(value, p);
//    }
    LogicalFunctionList il = new LogicalFunctionList();
    il.setParser(parser);
    il.setData(new Vector(p));
    bg.setLogicalFunctionList(il);
    return bg;
  }
  
  private void fillLogicalFunctionList(List p, BooleanGene bg) {
  	Object[] allParams = parser.getAllParams();
    List allData = parser.getAllData();
    int nb_params = allParams.length;
    Vector data;
    Iterator it_data;
    LogicalFunctionListElement element;
//    String testString = "";

//    if (value.indexOf("#") >= 0) {
//		testString = value.replaceAll("#", "_");
//	}

    for (int i = 0; i < nb_params; i++) {
      data = (Vector)allParams[i];
      it_data = data.iterator();
      while (it_data.hasNext()) {
        element = (LogicalFunctionListElement)it_data.next();
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
