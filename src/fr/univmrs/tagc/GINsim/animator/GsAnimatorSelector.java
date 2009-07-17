package fr.univmrs.tagc.GINsim.animator;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.univmrs.tagc.GINsim.css.EdgeStyle;
import fr.univmrs.tagc.GINsim.css.Selector;
import fr.univmrs.tagc.GINsim.css.VertexStyle;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * A selector to colorize the regulatory graph depending on a state value.
 * The color of a gene vary from white to green depending on level/level_max
 *
 */
public class GsAnimatorSelector extends Selector {
	public static final String IDENTIFIER = "animator";
	
	public static final String CAT_EDGE_ON	 = "on";
	public static final String CAT_EDGE_OFF	 = "off";

	public static final EdgeStyle STYLE_EDGE_ON		= new EdgeStyle(EdgeStyle.NULL_LINECOLOR, 	EdgeStyle.NULL_LINEEND, EdgeStyle.NULL_SHAPE,  EdgeStyle.NULL_BORDER);
	public static final EdgeStyle STYLE_EDGE_OFF 	= new EdgeStyle(new Color(192, 192, 192), 	EdgeStyle.NULL_LINEEND, EdgeStyle.NULL_SHAPE,  EdgeStyle.NULL_BORDER);

	public static final int MAX_STYLES = 9; 
	public static final VertexStyle[] STYLE_NODES = new VertexStyle[MAX_STYLES+1];
	public static final String[] CAT_NODES = new String[MAX_STYLES+1];
	
	static {	
		int step = 256/(MAX_STYLES+1);
		for (int k = 0 ; k <= MAX_STYLES ; k++) {
			STYLE_NODES[k] = new VertexStyle(new Color(255-k*step,  255, 255-k*step), 	Color.black, VertexStyle.NULL_BORDER, VertexStyle.NULL_SHAPE);
			CAT_NODES[k] = String.valueOf(k);
		}
	}	
	
	
	private byte[] state = null;

	private Map nodeToOrder;

	protected GsAnimatorSelector(GsRegulatoryGraph g) {
		super(IDENTIFIER);
		this.nodeToOrder = new HashMap(g.getNodeOrder().size()*2);
		int i = 0;
		for (Iterator it = g.getNodeOrder().iterator(); it.hasNext();) {
			this.nodeToOrder.put(it.next(), new Integer(i++));
		}
	}
	
	public void resetDefaultStyle() {
		addCategory(CAT_EDGE_OFF, STYLE_EDGE_OFF);
		addCategory(CAT_EDGE_ON, STYLE_EDGE_ON);
		for (int k = 0 ; k <= MAX_STYLES ; k++) {
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
	public byte[] getState() {
		return state;
	}

	public String getCategoryForEdge(Object obj) {
		if (state == null) {
			return null;
		}
		GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge) obj;
		int threshold = me.getMin(0);
		int order = ((Integer)nodeToOrder.get(me.getSourceVertex())).intValue();
		if (state[order] >= threshold) return CAT_EDGE_ON;
		return CAT_EDGE_OFF;
	}

	public String getCategoryForNode(Object obj) {
		if (state == null) {
			return null;
		}
		GsRegulatoryVertex v = (GsRegulatoryVertex) obj;
		int max = v.getMaxValue();
		int order = ((Integer)nodeToOrder.get(v)).intValue();
		int val = state[order];
		return CAT_NODES[val*MAX_STYLES/max];
	}
}
