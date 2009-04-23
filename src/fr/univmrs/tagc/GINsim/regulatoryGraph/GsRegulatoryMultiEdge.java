package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.io.IOException;
import java.util.List;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.data.ToolTipsable;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;

/**
 * This edge object allows to have several edges from a vertex to another
 */
public class GsRegulatoryMultiEdge implements XMLize, ToolTipsable, GsDirectedEdge {

	/** array of sign's names */
	static public final String[] SIGN = {"positive","negative","unknown"};
	/** array of sign's short names */
	static public final String[] SIGN_SHORT = {"+","-","?"};
	/** a positive edge */
	static public final short SIGN_POSITIVE = 0;
	/** a negative edge */
	static public final short SIGN_NEGATIVE = 1;
	/** an unknown edge */
	static public final short SIGN_UNKNOWN = 2;

	private GsRegulatoryEdge[] edges = new GsRegulatoryEdge[GsRegulatoryVertex.MAXVALUE+1];
	private int edgecount = 0;
    private GsRegulatoryVertex source, target;
    private int sign = 0;

    /**
     * @param source
     * @param target
     * @param param
     */
    public GsRegulatoryMultiEdge(GsRegulatoryVertex source, GsRegulatoryVertex target, int param) {
    	this(source, target, param, (short)1);
    }
    public GsRegulatoryMultiEdge(GsRegulatoryVertex source, GsRegulatoryVertex target, int param, short threshold) {
        this.source = source;
        this.target = target;
        GsRegulatoryEdge edge = new GsRegulatoryEdge(this);
        edge.sign = (short)param;
        if (threshold <= source.getMaxValue()) {
        	edge.threshold = threshold;
        } else {
        	edge.threshold = 1;
        }
        edges[edgecount++] = edge;
        sign = param;
    }

    /**
     * @param source
     * @param target
     *
     */
    public GsRegulatoryMultiEdge(GsRegulatoryVertex source, GsRegulatoryVertex target) {
    	this(source, target, 0, (short)1);
    }

    public void addEdge(GsGraph graph) {
    	addEdge(SIGN_POSITIVE, 1, graph);
    }
    public void addEdge(int sign, GsGraph graph) {
    	addEdge(sign, 1, graph);
    }
    public int addEdge(int sign, int threshold, GsGraph graph) {
    	int index = doAddEdge(sign, threshold);
    	if (index != -1) {
    		rescanSign(graph);
    		target.incomingEdgeAdded(this);
    	}
    	return index;
    }
    private int doAddEdge(int sign, int threshold) {
    	if (edgecount >= edges.length) {
    		return -1;
    	}
    	GsRegulatoryEdge edge = new GsRegulatoryEdge(this);
    	edge.sign = (short)sign;
    	edge.threshold = (short)threshold;
    	for (int i=0 ; i<edgecount ; i++) {
    		if (threshold < edges[i].threshold) {
    			for (int j=edgecount-1 ; j>=i ; j--) {
    				edges[j].index++;
    				edges[j+1] = edges[j];
    			}
    			edgecount++;
    			edges[i] = edge;
    			edge.index = (short)i;
    			return i;
    		}
    	}
    	edge.index = (short)edgecount;
    	edges[edgecount] = edge;
    	return edgecount++;
    }
    /**
     * remove an edge from this multiEdge
     *
     * @param index index of the edge to remove.
     * @param graph
     */
    public void removeEdge(int index, GsRegulatoryGraph graph) {
        if (edgecount == 0) {
        	graph.removeEdge(this);
        	return;
        }
        edges[index].index = -1;
    	if (index >= 0 && index < edgecount) {
    		for (int i=index ; i<edgecount ; i++) {
    			if (edges[i+1] != null) {
    				edges[i+1].index--;
    			}
    			edges[i] = edges[i+1];
    		}
    		edgecount--;
        	target.removeEdgeFromInteraction(this, index);
            rescanSign(graph);
    	}
    }

    /**
     * @return the number of edges in this multiEdge
     */
    public int getEdgeCount() {
        return edgecount;
    }
    /**
     * get the id of the corresponding subedge.
     * warning: this will return a shortened ID to put in the table, to get the real (full) id,
     * use <code>getFullId</code> instead.
     * @param index index of an edge of this multiEdge
     * @return the id of the given sub edge.
     */
    public String getId(int index) {
        return source+"_"+index;
    }

    /**
     * @param index
     * @return the full id of the given sub edge.
     */
    public String getFullId(int index) {
        return source+"_"+target+"_"+index;
    }

    public void toXML(XMLWriter out, Object param, int mode) throws IOException {
        for (int i=0 ; i<edgecount ; i++) {
            GsRegulatoryEdge edge = edges[i];

            int max = i<edgecount-1 ? edges[i+1].threshold-1 : -1;
            out.write("\t\t<edge id=\""+ edge.getLongInfo(":") + "\" from=\""+source+"\" to=\""+target+"\" minvalue=\""+edge.threshold+"\""+ (max == -1 ? "" : " maxvalue=\""+max+"\"")+" sign=\""+ SIGN[edge.sign] +"\">\n");
            edge.annotation.toXML(out, null, mode);
            if (param != null) {
                out.write(""+param);
            }
            out.write("\t\t</edge>\n");
        }
    }

    /**
     * @return the source vertex of this edge
     */
    public GsRegulatoryVertex getSource() {
        return source;
    }
    /**
     * @return the target vertex of this edge
     */
    public GsRegulatoryVertex getTarget() {
        return target;
    }

	/**
	 * @see fr.univmrs.tagc.GINsim.data.ToolTipsable#toToolTip()
	 */
	public String toToolTip() {
		return ""+source+" -> "+target+ (edgecount > 1 ? " ; "+edgecount : "");
	}
	/**
	 * @return Returns the sign.
	 */
	public int getSign() {
		return sign;
	}
	/**
	 * @param index
	 * @return the sign of this subedge
	 */
	public short getSign(int index) {
		if (index >= edgecount) {
			return 0;
		}
		return edges[index].sign;
	}
	/**
	 * change the sign of a sub edge.
	 *
	 * @param index index of the sub edge
	 * @param sign the new sign
	 * @param graph
	 */
	public void setSign(int index, short sign, GsGraph graph) {
		if (index >= edgecount) {
			return;
		}
		edges[index].sign = sign;
		rescanSign(graph);
	}
	/**
	 * @param index index of a subedge.
	 * @return annotation attached to this sub edge.
	 */
	public Annotation getGsAnnotation(int index) {
		return edges[index].annotation;
	}

	/**
	 * @param index index of a subedge.
	 * @return name of this sub edge.
	 */
	public String getEdgeName(int index) {
		if (index >= edgecount) {
			return null;
		}
		return edges[index].getShortDetail(" ");
	}

	/**
	 * @param vertex
	 */
	public void applyNewMaxValue(short max) {
		for (int i=0 ; i<edgecount ; i++) {
			if (edges[i].threshold > max) {
				edges[i].threshold = max;
			}
		}
	}
	public void canApplyNewMaxValue(short max, List l_fixable, List l_conflict) {
		for (int i=0 ; i<edgecount ; i++) {
			if (edges[i].threshold > max) {
				if (i == edgecount-1 && (i == 0 || edges[i-1].threshold < max)) {
					l_fixable.add(this);
				} else {
					l_conflict.add(this);
				}
			}
		}
	}
	/**
	 * @param index index of a sub edge.
	 * @return the min value of the source vertex for which this sub edge is active
	 */
	public short getMin(int index) {
		if (index >= edgecount) {
			return 0;
		}
		return edges[index].threshold;
	}
	/**
	 * @param index index of a sub edge.
	 * @return the max value of the source vertex for which this sub edge is active
	 */
	public short getMax(int index) {
		if (index >= edgecount) {
			return 0;
		}
		if (index == edgecount-1) {
			return -1;
		}
		return (short)(edges[index+1].threshold - 1);
	}
	/**
	 * change a sub edge's min value.
	 * @param index index of a sub edge.
	 * @param min the new min value.
	 */
	public void setMin(int index, short min) {
		if (index >= edgecount || min < 1 || min > source.getMaxValue() ||
				edges[index].threshold == min) {
			return;
		}
		if (min > edges[index].threshold) {
			for (int i=index+1 ; i<edgecount ; i++) {
				if (edges[i].threshold <= min) {
					return;
				}
			}
		} else{
			for (int i=index-1 ; i>=0 ; i--) {
				if (edges[i].threshold >= min) {
					return;
				}
			}
		}
		edges[index].threshold = min;
	}

	public Object getUserObject() {
		return this;
	}

	public Object getSourceVertex() {
		return source;
	}

	protected void rescanSign(GsGraph graph) {
		this.sign = edges[0].sign;
		for (int i=0 ; i<edgecount ; i++) {
			if ( edges[i].sign != sign) {
                if (this.sign == SIGN_UNKNOWN || edges[i].sign == SIGN_UNKNOWN) {
                    this.sign = SIGN_UNKNOWN;
                    break;
                }
                this.sign = GsEdgeAttributesReader.ARROW_DOUBLE;
			}
		}
		GsEdgeAttributesReader ereader = graph.getGraphManager().getEdgeAttributesReader();
		ereader.setEdge(this);
		ereader.setLineEnd(sign);
		ereader.refresh();
	}

	public Object getTargetVertex() {
		return target;
	}

	/**
	 *
	 * @param index
	 * @param sourceStatus
	 * @return true if active
	 */
	public boolean isActive(int index, int sourceStatus) {
		if (sourceStatus < edges[index].threshold) {
			return false;
		}
		if (index < edgecount-1 && sourceStatus >= edges[index+1].threshold) {
			return false;
		}
		return true;
	}

	/**
	 * get the index of the subedge having a given id
	 *
	 * @param id
	 * @return index of the corresponding subedge or -1 if not avaible here
	 */
	public int getIndexof(String id) {
		String[] ts = id.split("_");
		int index = -1;
		if (ts.length == 3 && ts[0].equals(source.toString()) && ts[1].equals(target.toString())) {
			index = Integer.parseInt(ts[2]);
			if (index >= edgecount) {
				index = -1;
			}
		}
		return index;
	}

    /**
     * @param edgeOri
     */
    public void copyFrom(GsRegulatoryMultiEdge edgeOri) {
    	edgecount = edgeOri.edgecount;
    	sign = edgeOri.sign;
    	for (int i=0 ; i<edgecount ; i++) {
    		edges[i] = (GsRegulatoryEdge)edgeOri.edges[i].clone(this);
    	}
    	for (int i=edgecount ; i<edges.length ; i++) {
    		edges[i] = null;
    	}
    }

    /**
     *
     * @param t_required
     */
    public void copyFrom(boolean[] t_required) {
    	edgecount = 0;
    	sign = SIGN_UNKNOWN;
    	for (int i=0 ; i<t_required.length ; i++) {
    		if (t_required[i]) {
    			GsRegulatoryEdge edge = new GsRegulatoryEdge(this);
    			edge.index = (short)edgecount;
    			edge.threshold = (short)i;
    			edge.sign = SIGN_UNKNOWN;
        		edges[edgecount++] = edge;
    		}
    	}
    	for (int i=edgecount ; i<edges.length ; i++) {
    		edges[i] = null;
    	}
    }

    public void setUserObject(Object obj) {
    }

	public GsRegulatoryEdge getEdge(int index) {
		return edges[index];
	}
	public void markRemoved() {
		for (int i=0 ; i<edgecount ; i++) {
			edges[i].index = -1;
		}
	}

	public int[] getFreeValues() {
		int[] t = new int[source.getMaxValue()];
		int cur = 1;
		int index = 0;
		for (int i=0 ; i<=edgecount ; i++) {
			short nextval = i>=edgecount ? (short)(source.getMaxValue()+1) : edges[i].threshold;
			if (nextval > cur) {
				for ( ; cur<nextval ; cur++) {
					t[index++] = cur;
				}
			}
			cur = nextval+1;
		}
		for ( ; index<t.length ; index++) {
			t[index] = -1;
		}
		return t;
	}
	public GsRegulatoryEdge getEdgeForThreshold(int threshold) {
		for (int i=0 ; i<=edgecount ; i++) {
			if (edges[i].threshold == threshold) {
				return edges[i];
			}
		}
		return null;
	}
}
