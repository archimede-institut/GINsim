package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import java.util.Vector;
import java.util.Iterator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanOperand;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanData;

public class GsBooleanGene extends TBooleanOperand {
  public GsBooleanGene() {
    super();
  }
  public TBooleanData getValue() {
    Vector allData = parser.getAllData(), v = new Vector();
    Iterator it_allData = allData.iterator();
    Vector data;
    Iterator it_data;
    GsLogicalFunctionListElement element;

    while (it_allData.hasNext()) {
      data = (Vector)it_allData.next();
      it_data = data.iterator();
      while (it_data.hasNext()) {
        element = (GsLogicalFunctionListElement)it_data.next();
        if (element.toString().indexOf(value + "_") != -1) {
          v.addElement(data);
          break;
        }
      }
    }
    GsLogicalFunctionList il = new GsLogicalFunctionList();
    il.setData(v);
    return il;
  }
}
