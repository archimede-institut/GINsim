package org.ginsim.servicegui.tool.stateinregulatorygraph;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.css.EdgeStyle;
import org.ginsim.core.graph.view.css.NodeStyle;
import org.ginsim.core.graph.view.css.Selector;


/**
 * A selector to colorize the regulatory graph depending on a state value.
 * The color of a gene vary from white to green depending on level/level_max
 *
 */
public class StateInRegGraphSelector extends Selector {
	public static final String IDENTIFIER = "animator";

	public static final String CAT_EDGE_ON	 = "on";
	public static final String CAT_EDGE_OFF	 = "off";

	public static final EdgeStyle STYLE_EDGE_ON		= new EdgeStyle();
	public static final EdgeStyle STYLE_EDGE_OFF 	= new EdgeStyle(new Color(192, 192, 192));

	public static final int MAX_STYLES = DynamicGraph.MAXLEVEL; 
	public static final int STAR = DynamicGraph.STARLEVEL;

	public static final NodeStyle[] STYLE_NODES = new NodeStyle[MAX_STYLES+2];
	public static final String[] CAT_NODES = new String[MAX_STYLES+2];


	static {	
		int step = 256/(MAX_STYLES+1);
		for (int k = 0 ; k <= MAX_STYLES ; k++) {
			STYLE_NODES[k] = new NodeStyle(new Color(255-k*step,  255, 255-k*step), Color.black);
			CAT_NODES[k] = String.valueOf(k);
		}
		STYLE_NODES[STAR] = new NodeStyle(new Color(0, 127, 255), Color.black);
		CAT_NODES[STAR] = "*";
	}	

	private byte[] state = null;
	private int size;

	private Map<RegulatoryNode, Integer> nodeToOrder;

	public StateInRegGraphSelector(RegulatoryGraph g) {
		super(IDENTIFIER);
		this.nodeToOrder = new HashMap<RegulatoryNode, Integer>(g.getNodeOrderSize()*2);
		int i = 0;
		for (Iterator<RegulatoryNode> it = g.getNodeOrder().iterator(); it.hasNext();) {
			this.nodeToOrder.put(it.next(), new Integer(i++));
		}
		this.size = g.getNodeOrderSize();
	}

	public void resetDefaultStyle() {
		addCategory(CAT_EDGE_OFF, STYLE_EDGE_OFF);
		addCategory(CAT_EDGE_ON, STYLE_EDGE_ON);
		for (int k = 0 ; k <= STAR ; k++) {
			addCategory(CAT_NODES[k], STYLE_NODES[k]);
		}
	}

	/**
	 * Define the state used to color the graph
	 * @param state
	 */
	public void setState(byte[] state) {
		this.state = state;
	}
	public void setState(String state) {
		if (state.length() != size) {
			this.state = null;
		}
		this.state = new byte[size];

		for (int i = 0; i < state.length(); i++) {
			switch (state.charAt(i)) {
			case '*':
				this.state[i] = STAR;
				break;
			case '0':
				this.state[i] = 0;
				break;
			case '1':
				this.state[i] = 1;
				break;
			case '2':
				this.state[i] = 2;
				break;
			case '3':
				this.state[i] = 3;
				break;
			case '4':
				this.state[i] = 4;
				break;
			case '5':
				this.state[i] = 5;
				break;
			case '6':
				this.state[i] = 6;
				break;
			case '7':
				this.state[i] = 7;
				break;
			case '8':
				this.state[i] = 8;
				break;
			case '9':
				this.state[i] = 9;
				break;
			default:
				break;
			}
		}
	}
	public byte[] getState() {
		return state;
	}

	public String getCategoryForEdge(Object obj) {
		if (state == null) {
			return null;
		}
		RegulatoryMultiEdge me = (RegulatoryMultiEdge) obj;
		int threshold = me.getMin(0);
		int order = ((Integer)nodeToOrder.get(me.getSource())).intValue();
		if (state[order] >= threshold) return CAT_EDGE_ON;
		return CAT_EDGE_OFF;
	}

	public String getCategoryForNode(Object obj) {
		if (state == null) {
			return null;
		}
		RegulatoryNode v = (RegulatoryNode) obj;
		int max = v.getMaxValue();
		int order = ((Integer)nodeToOrder.get(v)).intValue();
		int val = state[order];
		if (val == STAR) {
			return CAT_NODES[STAR];
		}
		return CAT_NODES[val*MAX_STYLES/max];
	}
}
