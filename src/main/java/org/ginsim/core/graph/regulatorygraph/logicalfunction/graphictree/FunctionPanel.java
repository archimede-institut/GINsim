package org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree;

public interface FunctionPanel {

	void setText(String s, int i);

	void validateText(String oldExp);

	String getCurrentText();

	int getCaretPosition();

	TreeExpression getTreeExpression();

}
