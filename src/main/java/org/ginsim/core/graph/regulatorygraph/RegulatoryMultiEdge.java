package org.ginsim.core.graph.regulatorygraph;

import java.io.IOException;
import java.util.List;

import org.ginsim.common.utils.ToolTipsable;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.GraphChangeType;


/**
 * Edges of the regulatory graph: provide a list of interactions.
 *
 * @author Aurelien Naldi
 */
public class RegulatoryMultiEdge extends Edge<RegulatoryNode> implements ToolTipsable {

	private RegulatoryEdge[] edges = new RegulatoryEdge[RegulatoryNode.MAXVALUE+1];
	private int edgecount = 0;
    private RegulatoryEdgeSign sign = RegulatoryEdgeSign.POSITIVE;
	private Annotation annotation = new Annotation();
	
    /**
     * @param source
     * @param target
     * @param param
     */
    public RegulatoryMultiEdge(RegulatoryGraph g, RegulatoryNode source, RegulatoryNode target, RegulatoryEdgeSign param) {
    	this(g, source, target, param, (byte)1);
    }

    public RegulatoryMultiEdge(RegulatoryGraph g, RegulatoryNode source, RegulatoryNode target, RegulatoryEdgeSign param, byte threshold) {
    	super(g, source, target);
    	source.setOutput(false, g);
        RegulatoryEdge edge = new RegulatoryEdge(this);
        edge.setSign(param);
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
    public RegulatoryMultiEdge(RegulatoryGraph g, RegulatoryNode source, RegulatoryNode target) {
    	this(g, source, target, RegulatoryEdgeSign.POSITIVE, (byte)1);
    }

    public void addEdge( Graph graph) {
    	addEdge(RegulatoryEdgeSign.POSITIVE, 1, graph);
    }
    public void addEdge(RegulatoryEdgeSign sign, Graph graph) {
    	addEdge(sign, 1, graph);
    }
    public int addEdge(RegulatoryEdgeSign sign, int threshold, Graph graph) {
    	int index = doAddEdge(sign, threshold);
    	if (index != -1) {
    		rescanSign(graph);
    		getTarget().incomingEdgeAdded(this);
    	}
    	return index;
    }
    private int doAddEdge(RegulatoryEdgeSign sign, int threshold) {
    	if (edgecount >= edges.length) {
    		return -1;
    	}
    	RegulatoryEdge edge = new RegulatoryEdge(this);
    	edge.setSign(sign);
    	edge.threshold = (byte)threshold;
    	for (int i=0 ; i<edgecount ; i++) {
    		if (threshold < edges[i].threshold) {
    			for (int j=edgecount-1 ; j>=i ; j--) {
    				edges[j].index++;
    				edges[j+1] = edges[j];
    			}
    			edgecount++;
    			edges[i] = edge;
    			edge.index = (byte)i;
    			return i;
    		}
    		else if (threshold == edges[i].threshold){
    			edges[i] = edge;
    			edge.index = (byte)i;
    			return i;
    		}
    	}
    	edge.index = (byte)edgecount;
    	edges[edgecount] = edge;
    	return edgecount++;
    }
    /**
     * remove an edge from this multiEdge
     *
     * @param index index of the edge to remove.
     * @param graph
     */
    public void removeEdge(int index, RegulatoryGraph graph) {
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
     * warning: this will return a byteened ID to put in the table, to get the real (full) id,
     * use <code>getFullId</code> instead.
     * @param index index of an edge of this multiEdge
     * @return the id of the given sub edge.
     */
    public String getId(int index) {
        return source+"_"+index;
    }

    public void toXML(XMLWriter out) throws IOException {

    	out.addAttr("id", source+":"+target);
        out.addAttr("from", source.toString());
        out.addAttr("to", target.toString());
        
        if (edgecount == 1) {
        	RegulatoryEdge edge = edges[0];
            out.addAttr("minvalue", ""+edge.threshold);
            out.addAttr("sign", edge.getSign().getLongDesc());
        	
        } else {
            String s = "";
            for (int i=0 ; i<edgecount ; i++) {
            	RegulatoryEdge edge = edges[i];
            	s += edge.threshold+":"+edge.getSign().getLongDesc()+" ";
            }
            out.addAttr("effects", s.trim());
        }

    	annotation.toXML(out);
    }

    @Override
	public String toToolTip() {
		return ""+source+" -> "+target+ (edgecount > 1 ? " ; "+edgecount : "");
	}
	/**
	 * @return Returns the sign.
	 */
	public RegulatoryEdgeSign getSign() {
		return sign;
	}
	/**
	 * @param index
	 * @return the sign of this subedge
	 */
	public RegulatoryEdgeSign getSign(int index) {
		if (index >= edgecount) {
			return RegulatoryEdgeSign.UNKNOWN;
		}
		return edges[index].getSign();
	}
	/**
	 * change the sign of a sub edge.
	 *
	 * @param index index of the sub edge
	 * @param sign the new sign
	 * @param graph
	 */
	public void setSign(int index, RegulatoryEdgeSign sign, Graph graph) {
		if (index >= edgecount) {
			return;
		}
		edges[index].setSign(sign);
		rescanSign(graph);
		graph.fireGraphChange(GraphChangeType.EDGEUPDATED, this);
	}

	public Annotation getAnnotation() {
		return annotation;
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
	 * 
	 */
	public void applyNewMaxValue(byte max) {
		for (int i=0 ; i<edgecount ; i++) {
			if (edges[i].threshold > max) {
				edges[i].threshold = max;
			}
		}
	}
	public void canApplyNewMaxValue(byte max, List l_fixable, List l_conflict) {
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
	 * @return the min value of the source node for which this sub edge is active
	 */
	public byte getMin(int index) {
		if (index >= edgecount) {
			return 0;
		}
		return edges[index].threshold;
	}
	/**
	 * @param index index of a sub edge.
	 * @return the max value of the source node for which this sub edge is active
	 */
	public byte getMax(int index) {
		if (index >= edgecount) {
			return 0;
		}
		if (index == edgecount-1) {
			return -1;
		}
		return (byte)(edges[index+1].threshold - 1);
	}
	/**
	 * change a sub edge's min value.
	 * @param index index of a sub edge.
	 * @param min the new min value.
	 */
	public void setMin(int index, byte min, RegulatoryGraph graph) {
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
		graph.fireGraphChange(GraphChangeType.GRAPHSAVED, this);
	}

	public void rescanSign( Graph graph) {
		this.sign = edges[0].getSign();
		for (int i=0 ; i<edgecount ; i++) {
			if ( edges[i].getSign() != sign) {
                if (this.sign == RegulatoryEdgeSign.UNKNOWN || edges[i].getSign() == RegulatoryEdgeSign.UNKNOWN) {
                    this.sign = RegulatoryEdgeSign.UNKNOWN;
                    break;
                }
                this.sign = RegulatoryEdgeSign.DUAL;
			}
		}
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
    public void copyFrom(RegulatoryMultiEdge edgeOri) {
    	edgecount = edgeOri.edgecount;
    	sign = edgeOri.sign;
    	for (int i=0 ; i<edgecount ; i++) {
    		edges[i] = (RegulatoryEdge)edgeOri.edges[i].clone(this);
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
    	sign = RegulatoryEdgeSign.UNKNOWN;
    	for (int i=0 ; i<t_required.length ; i++) {
    		if (t_required[i]) {
    			RegulatoryEdge edge = new RegulatoryEdge(this);
    			edge.index = (byte)edgecount;
    			edge.threshold = (byte)i;
    			edge.setSign(RegulatoryEdgeSign.UNKNOWN);
        		edges[edgecount++] = edge;
    		}
    	}
    	for (int i=edgecount ; i<edges.length ; i++) {
    		edges[i] = null;
    	}
    }

	public RegulatoryEdge getEdge(int index) {
		return edges[index];
	}
	public void markRemoved() {
		for (int i=0 ; i<edgecount ; i++) {
			edges[i].index = -1;
		}
	}

	public int[] getFreeValues() {
        int max = source.getMaxValue();
        int size = getEdgeCount();

        if (size >= max) {
            return new int[] {};
        }

        int[] t = new int[max-size];
        int cur = 1;
        int index = 0;
		for (int edgeIdx=0 ; edgeIdx<edgecount ; edgeIdx++) {
			int curMax = edges[edgeIdx].threshold;
            for ( ; cur<curMax ; cur++) {
                t[index++] = cur;
            }
            cur++;
		}
		for ( ; cur<=max ; cur++) {
			t[index++] = cur;
		}
		return t;
	}

	public RegulatoryEdge getEdgeForThreshold(int threshold) {
		for (int i=0 ; i<edgecount ; i++) {
			if (edges[i].threshold == threshold) {
				return edges[i];
			}
		}
		return null;
	}
}
