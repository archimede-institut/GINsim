package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.List;

public interface TBooleanData {
  List getData();
  void setData(List v);
  void setParser(TBooleanParser p);
}
