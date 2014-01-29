package org.ginsim.common.xml;

import java.util.ArrayList;

/**
 * Store all warnings found while parsing an XML document.
 *
 * @author Aurelien Naldi
 */
public class ParsingWarningReport extends ArrayList<ParsingWarning> {

	public String getMessage() {
		return "Parsing warnings";
	}

	public String getDetail() {
		StringBuffer sb = new StringBuffer();
		for (ParsingWarning w: this) {
			sb.append(w.getMessage());
			sb.append(". Line "+w.getFirstLine());
			int extra = w.getExtraCount();
			if (extra > 0) {
				sb.append(" (and "+extra+" other)");
			}
			sb.append(".\n");
		}
		return sb.toString();
	}

}
