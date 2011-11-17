package org.ginsim.gui.graph.backend;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.Edge.Routing;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.PortView;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;

import fr.univmrs.tagc.common.Tools;

/**
 * extract info on jgraph's edges.
 */
public class GsJgraphEdgeAttribute extends EdgeAttributesReader {

    private JGraphModelAdapter m_adaptor;
    private ListenableGraph g;
    private AttributeMap attr;
    private AttributeMap defaultEdgeAttr;
    private DefaultGraphCell cell;

    private Object[] v_routingRenderer = null;
    private static int[] v_styleRenderer = null;
    private GraphLayoutCache glc;
	
    static {
        	v_styleRenderer = new int[NBSTYLE];
        	v_styleRenderer[STYLE_STRAIGHT] = GraphConstants.STYLE_ORTHOGONAL;
        	v_styleRenderer[STYLE_CURVE] = GraphConstants.STYLE_BEZIER;
    }

    public GsJgraphEdgeAttribute(ListenableGraph g, JGraphModelAdapter adaptor, AttributeMap defaultEdgeAttr) {
        this.m_adaptor = adaptor;
        this.g = g;
        this.defaultEdgeAttr = defaultEdgeAttr;
        if (defaultEdgeAttr != null) {
        	applyDefault(defaultEdgeAttr);
        }
        //this.glc = graphmanager.getJgraph().getGraphLayoutCache();
        // routing aren't static: they need knowledge of the graphManager.
    	v_routingRenderer = new Object[NBROUTING];
    	//v_routingRenderer[ROUTING_AUTO] = graphmanager.getPedgerouting();
    	v_routingRenderer[ROUTING_NONE] = null;
    }

    public static void applyDefault(AttributeMap defaultEdgeAttr) {
        GraphConstants.setLineColor(defaultEdgeAttr, EdgeAttributesReader.color);
    }
    
	public void setDefaultEdgeSize(float s) {
		defaultLineWidth = s;
		GraphConstants.setLineWidth(defaultEdgeAttr, s);
	}

	public void setDefaultEdgeEndFill(boolean b) {
		GraphConstants.setEndFill(defaultEdgeAttr, b);
	}

	public void setDefaultEdgeColor(Color color) {
        EdgeAttributesReader.color = color;
		GraphConstants.setLineColor(defaultEdgeAttr, color);
	}

	public void setEdge(Object obj) {
	    if (!(obj instanceof Edge)) {
	        attr = null;
	        cell = null;
	        return;
	    }
	    
	    Edge edge = (Edge)obj;		
        cell = m_adaptor.getEdgeCell(edge);
        if (cell != null) {
            attr = cell.getAttributes();
        } else {
            attr = null;
        }
	}

	public void setLineColor(Color color) {
		if (attr == null) {
			return;
		}
		GraphConstants.setLineColor(attr, color);
	}

	public Color getLineColor() {
		if (attr == null) {
			return null;
		}
		return GraphConstants.getLineColor(attr);
	}

	public void refresh() {
		if (attr == null || cell == null) {
			return;
		}
		m_adaptor.cellsChanged(new Object[] {cell});
	}

	public float getLineWidth() {
		if (attr == null) {
			return defaultLineWidth;
		}
		return GraphConstants.getLineWidth(attr);
	}

	public void setLineWidth(float w) {
		if (attr == null) {
			return;
		}
		GraphConstants.setLineWidth(attr, w);
	}

	public int getRouting() {
		if (attr == null) {
			return 0;
		}
		Object routing = GraphConstants.getRouting(attr);
		int index = Tools.arrayIndexOf(v_routingRenderer,routing);
		if (index == -1) {
			index = 0;
		}
		return index;
	}

	public int getStyle() {
		if (attr == null) {
			return 0;
		}
		int index = Tools.arrayIndexOf(v_styleRenderer, GraphConstants.getLineStyle(attr));
		if (index == -1) {
			index = 0;
		}
		return index;
	}

	public void setRouting(int index) {
		if (attr == null || index < 0 || index > v_routing.size()) {
			return;
		}
        
		switch (index) {
			case ROUTING_AUTO:
			    GraphConstants.setRouting(attr, (Routing)v_routingRenderer[ROUTING_AUTO]);
                attr.remove(GraphConstants.POINTS);
			    break;
			default:
			    attr.remove(GraphConstants.ROUTING);
		}
	}

	public void setStyle(int index) {
		if (attr == null) {
			return;
		}
		if (index < 0 || index > v_style.size()) {
			return;
		}
		GraphConstants.setLineStyle(attr, v_styleRenderer[index]);
	}

    public void setLineEnd(int index) {
		int refend = GraphConstants.ARROW_TECHNICAL;
		int endsize = 10;
		switch (index) {
		case ARROW_NEGATIVE:
			refend = GraphConstants.ARROW_LINE;
			endsize = 20;
			break;
		case ARROW_UNKNOWN:
			refend = GraphConstants.ARROW_CIRCLE;
			break;
        case ARROW_DOUBLE:
            refend = ARROW_DOUBLE;
            break;
		}

		GraphConstants.setLineEnd( attr, refend);
		GraphConstants.setEndSize( attr, endsize);
    }

    public int getLineEnd() {
        if (attr == null) {
            return 0;
        }
        int jindex = GraphConstants.getLineEnd(attr);
        switch (jindex) {
		    case GraphConstants.ARROW_LINE:
		        return EdgeAttributesReader.ARROW_NEGATIVE;
            case GraphConstants.ARROW_CIRCLE:
                return EdgeAttributesReader.ARROW_UNKNOWN;
            case EdgeAttributesReader.ARROW_DOUBLE:
                return EdgeAttributesReader.ARROW_DOUBLE;
        	default: 
        	    return 0;
        }
    }
    
    public List getPoints() {
        EdgeView cv = (EdgeView)glc.getMapping(cell, true);
        List pt = cv.getPoints();
        List list = new ArrayList();
        Iterator it = pt.iterator();
        for ( ; it.hasNext() ; ) {
            Object point = it.next();
            if (point instanceof PortView) {
                list.add(((PortView)point).getLocation());
            } else {
                list.add(point);
            }
        }
        return list;
    }

    public void setPoints(List l) {
        if (attr != null && l != null) {
            GraphConstants.setPoints(attr, l);
        }
    }

    public void setDefaultStyle(int index) {
		GraphConstants.setLineStyle(defaultEdgeAttr, v_styleRenderer[index]);
    }

    public Color getDefaultEdgeColor() {
        return GraphConstants.getLineColor(defaultEdgeAttr);
    }

    public float getDefaultEdgeSize() {
        return GraphConstants.getLineWidth(defaultEdgeAttr);
    }

    public boolean getDefaultEdgeEndFill() {
        return false;
    }

    public int getDefaultStyle() {
        return 0;
    }

	public void setDash(float[] dashArray) {
		if (attr == null) {
			return;
		}
		if (dashArray != null) {
			GraphConstants.setDashPattern(attr, dashArray);
		} else {
			attr.remove(GraphConstants.DASHPATTERN);
		}
	}

	public float[] getDash() {
		return GraphConstants.getDashPattern(attr);
	}
}
