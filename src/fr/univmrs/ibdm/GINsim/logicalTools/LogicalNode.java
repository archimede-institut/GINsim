package fr.univmrs.ibdm.GINsim.logicalTools;

public class LogicalNode {

	public static final int DATA = 0;
	public static final int NOT = 1;
	public static final int AND = 2;
	public static final int OR = 3;
	
	int type;
	Object data;
	LogicalNode fg;
	LogicalNode bg;
	
	public LogicalNode(int type, Object data, LogicalNode fg, LogicalNode bg) {
		this.type = type;
		this.data = data;
		this.fg = fg;
		this.bg = bg;
	}

	public LogicalNode(Object data) {
		this(DATA, data, null, null);
	}
	
	public String getString() {
		StringBuffer sb = new StringBuffer();
		
		return sb.toString();
	}
	
	static public LogicalNode parse(String s, LogicalHelper helper) {

		
		return null;
	}
}

class FormulaParser {

	String s;
	boolean done;
	int pos;
	
	LogicalNode parse(String s) {
		this.s = s.trim();
		this.pos = 0;
		done = false;
		return parseElement();
	}
	
	private char next() {
		char c = s.charAt(pos++);
		return c;
	}
	
	private LogicalNode parseElement() {
		char c = next();
		switch (c) {
		case '(':
			break;
		case '\0':
			break;
		default:
			break;
		}
		return null;
	}
}