package fr.univmrs.ibdm.GINsim.jgraph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.ListenableGraph;
import org._3pq.jgrapht.ext.JGraphModelAdapter;
import org.jgraph.graph.*;
import org.jgraph.graph.Edge.Routing;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.global.Tools;

/**
 * extract info on jgraph's edges.
 */
public class GsJgraphEdgeAttribute extends GsEdgeAttributesReader {

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

    /**
     * create a jgraph edge attribute reader
     * 
     * @param graphmanager
     */
    public GsJgraphEdgeAttribute(GsJgraphtGraphManager graphmanager) {
        this.m_adaptor = graphmanager.getM_jgAdapter();
        this.g = graphmanager.getG();
        this.defaultEdgeAttr = graphmanager.getDefaultEdgeAttr();
    	applyDefault(defaultEdgeAttr);
        glc = graphmanager.getJgraph().getGraphLayoutCache();
        // routing aren't static: they need knowledge of the graphManager.
    	v_routingRenderer = new Object[NBROUTING];
    	v_routingRenderer[ROUTING_AUTO] = graphmanager.getPedgerouting();
    	v_routingRenderer[ROUTING_NONE] = null;
    }

    public static void applyDefault(AttributeMap defaultEdgeAttr) {
        GraphConstants.setLineColor(defaultEdgeAttr, GsEdgeAttributesReader.color);
    }
    
	public void setDefaultEdgeSize(float s) {
		defaultLineWidth = s;
		GraphConstants.setLineWidth(defaultEdgeAttr, s);
	}

	public void setDefaultEdgeEndFill(boolean b) {
		GraphConstants.setEndFill(defaultEdgeAttr, b);
	}

	public void setDefaultEdgeColor(Color color) {
        GsEdgeAttributesReader.color = color;
		GraphConstants.setLineColor(defaultEdgeAttr, color);
	}

	public void setEdge(Object obj) {
	    Edge edge;
	    
	    if (obj instanceof GsDirectedEdge) {
	        edge = g.getEdge(((GsDirectedEdge)obj).getSourceVertex(), ((GsDirectedEdge)obj).getTargetVertex());
	    } else {
	        edge = null;
	        attr = null;
	        cell = null;
	        return;
	    }
		
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
		        return GsEdgeAttributesReader.ARROW_NEGATIVE;
            case GraphConstants.ARROW_CIRCLE:
                return GsEdgeAttributesReader.ARROW_UNKNOWN;
            case GsEdgeAttributesReader.ARROW_DOUBLE:
                return GsEdgeAttributesReader.ARROW_DOUBLE;
        	default: 
        	    return 0;
        }
    }
    
    public List getPoints(boolean border) {
        CellView cv = glc.getMapping(cell, true);
        List pt = ((EdgeView)cv).getPoints();
        List list = new Vector();
        Point2D previousPoint = null;
        VertexView previous = null;
        for (int i=0 ; i<pt.size() ; i++) {
            Object point = pt.get(i);
            if (previous != null) {
	            if (point instanceof PortView) {
	                VertexView parent = (VertexView) ((PortView)point).getParentView();
                    list.add(previous.getPerimeterPoint(null, previousPoint, ((PortView)point).getLocation()));
                    list.add(parent.getPerimeterPoint(null, ((PortView)point).getLocation(), previousPoint));
                    previousPoint = ((PortView)point).getLocation();
	            } else {
                    list.add(previous.getPerimeterPoint(null, previousPoint, (Point2D)point));
	                previousPoint = (Point2D)point;
	                list.add(point);
	            }
	            
	            // remove the "wait" status
                previous = null;
            } else {
	            if (point instanceof PortView) {
	                if (border) {
	                    VertexView parent = (VertexView) ((PortView)point).getParentView();
	                    if (previousPoint != null) {
                            list.add(parent.getPerimeterPoint(null, ((PortView)point).getLocation(), previousPoint));
	                    } else {
	                        previousPoint = ((PortView)point).getLocation();
	                        previous = parent;
	                    }
	                    previousPoint = ((PortView)point).getLocation();
	                } else {
	                    previousPoint = ((PortView)point).getLocation();
	                    list.add(previousPoint);
	                }
	            } else {
	                previousPoint = (Point2D)point;
	                list.add(point);
	            }
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
