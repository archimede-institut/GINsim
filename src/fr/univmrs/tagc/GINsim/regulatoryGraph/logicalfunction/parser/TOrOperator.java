package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.ArrayList;
import java.util.List;


public class TOrOperator extends TBinaryOperator {

	public static final int	priority	= 0;
	protected static String	SYMBOL		= "|";

	public TOrOperator() {
		super();
	}

	public TBooleanData getValue() throws Exception {
		List leftData = leftArg.getValue().getData();
		List rightData = rightArg.getValue().getData();
		List orData = new ArrayList(leftData);
		for (int i = 0 ; i < rightData.size() ; i++) {
			if (!orData.contains(rightData.get(i))) {
				orData.add(rightData.get(i));
			}
		}
		// orData.addAll(rightData);
		TBooleanData data = (TBooleanData)Class.forName(returnClassName)
				.newInstance();
		data.setParser(parser);
		data.setData(orData);
		return data;
	}

	public String getSymbol() {
		return SYMBOL;
	}
}
