package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.io.IOException;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.data.GsAnnotation;
import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.data.ToolTipsable;
import fr.univmrs.ibdm.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;
import fr.univmrs.ibdm.GINsim.xml.GsXMLize;

/**
 * This edge object allows to have several edges from a vertex to another
 */
public class GsRegulatoryMultiEdge implements GsXMLize, ToolTipsable, GsDirectedEdge {

    private Vector edges = new Vector(1);
    private GsRegulatoryVertex source, target;
    private int sign = 0;
    
    /**
     * @param source
     * @param target
     * @param param
     */
    public GsRegulatoryMultiEdge(GsRegulatoryVertex source, GsRegulatoryVertex target, int param) {
        this.source = source;
        this.target = target;
        edges.add(new GsRegulatoryEdge(param));
        sign = ((GsRegulatoryEdge)edges.get(0)).getSign();
    }

    /**
     * @param source
     * @param target
     * 
     */
    public GsRegulatoryMultiEdge(GsRegulatoryVertex source, GsRegulatoryVertex target) {
        this.source = source;
        this.target = target;
        edges.add(new GsRegulatoryEdge());
        sign = ((GsRegulatoryEdge)edges.get(0)).getSign();
    }

    /**
     * @param source
     * @param target
     * @param edge
     * @param graph
     */
    public GsRegulatoryMultiEdge(GsRegulatoryVertex source, GsRegulatoryVertex target, GsRegulatoryEdge edge, GsGraph graph) {
        this.source = source;
        this.target = target;
        edges.add(edge);
        sign = edge.getSign();
        rescanSign(graph);
    }
    /**
     * add an edge to this multiEdge
     * 
     * @param edge the new edge
     * @param graph
     */
    public void addEdge(GsRegulatoryEdge edge, GsGraph graph) {
        edges.add(edge);
        rescanSign(graph);
    }
    /**
     * remove an edge from this multiEdge
     * 
     * @param index index of the edge to remove.
     * @param graph
     */
    public void removeEdge(int index, GsRegulatoryGraph graph) {
        edges.remove(index);
        if (target != null) {
    			target.removeEdgeFromInteraction(this, index);
        }
        if (edges.size() == 0) {
        		graph.removeEdge(this);
        		return;
        }
        rescanSign(graph);
    }
    
    /**
     * @return the number of edges in this multiEdge
     */
    public int getEdgeCount() {
        return edges.size();
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

    public void toXML(GsXMLWriter out, Object param, int mode) throws IOException {
        String name = source+"_"+target+"_";
        for (int i=0 ; i<edges.size() ; i++) {
            GsRegulatoryEdge edge = (GsRegulatoryEdge) edges.get(i);
            
            int max = edge.getMax();
            if (max == -1) {
                out.write("\t\t<edge id=\""+ name + i +"\" from=\""+source+"\" to=\""+target+"\" minvalue=\""+edge.getMin()+"\" sign=\""+ GsRegulatoryEdge.SIGN[edge.getSign()] +"\">\n");
            } else {
                out.write("\t\t<edge id=\""+ name + i +"\" from=\""+source+"\" to=\""+target+"\" minvalue=\""+edge.getMin()+"\" maxvalue=\""+max+"\" sign=\""+ GsRegulatoryEdge.SIGN[edge.getSign()] +"\">\n");
            }
            edge.getGsAnnotation().toXML(out, null, mode);
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
	 * @see fr.univmrs.ibdm.GINsim.data.ToolTipsable#toToolTip()
	 */
	public String toToolTip() {
		return ""+source+" -> "+target+" ; "+edges.size();
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
		if (index >= edges.size()) {
			return 0;
		}
		return ((GsRegulatoryEdge)edges.get(index)).getSign();
	}
	/**
	 * change the signe of a sub edge.
	 * 
	 * @param index index of the sub edge
	 * @param sign the new sign
	 * @param graph
	 */
	public void setSign(int index, short sign, GsGraph graph) {
		if (index >= edges.size()) {
			return;
		}
		((GsRegulatoryEdge)edges.get(index)).setSign(sign);
		rescanSign(graph);
	}
	/**
	 * @param index index of a subedge.
	 * @return annotation attached to this sub edge.
	 */
	public GsAnnotation getGsAnnotation(int index) {
		return ((GsRegulatoryEdge)edges.get(index)).getGsAnnotation();
	}
	
	/**
	 * @param index index of a subedge.
	 * @return name of this sub edge.
	 */	
	public String getEdgeName(int index) {
		return edges.get(index).toString();
	}
	
	/**
	 * @param vertex
	 */
	public void applyNewMaxValue(GsRegulatoryVertex vertex) {
		for (int i=0 ; i<edges.size() ; i++) {
			((GsRegulatoryEdge)edges.get(i)).applyNewMaxValue(vertex);
		}
		
	}
	/**
	 * @param index index of a sub edge.
	 * @return the min value of the source vertex for which this sub edge is active
	 */
	public short getMin(int index) {
		if (index >= edges.size()) {
			return 0;
		}
		return ((GsRegulatoryEdge)edges.get(index)).getMin();
	}
	/**
	 * @param index index of a sub edge.
	 * @return the max value of the source vertex for which this sub edge is active
	 */
	public short getMax(int index) {
		if (index >= edges.size()) {
			return 0;
		}
		return ((GsRegulatoryEdge)edges.get(index)).getMax();
	}
	/**
	 * change a sub edge's min value.
	 * @param index index of a sub edge.
	 * @param min the new min value.
	 */
	public void setMin(int index, short min) {
		if (index >= edges.size()) {
			return;
		}
		((GsRegulatoryEdge)edges.get(index)).setMin(min);
	}
	/**
	 * change a sub edge's max value.
	 * @param index index of a sub edge.
	 * @param max the new max value
	 */
	public void setMax(int index, short max) {
		if (index >= edges.size()) {
			return;
		}
        if ( (max!=-1 && max<1) || max > source.getMaxValue()) {
            return;
        }
		((GsRegulatoryEdge)edges.get(index)).setMax(max);
	}

	public Object getUserObject() {
		return this;
	}

	public Object getSourceVertex() {
		return source;
	}

	protected void rescanSign(GsGraph graph) {
		this.sign = ((GsRegulatoryEdge)edges.get(0)).getSign();
		for (int i=0 ; i<edges.size() ; i++) {
			if (((GsRegulatoryEdge)edges.get(i)).getSign() != sign) {
                if (this.sign == GsRegulatoryEdge.SIGN_UNKNOWN || ((GsRegulatoryEdge)edges.get(i)).getSign() == GsRegulatoryEdge.SIGN_UNKNOWN) {
                    this.sign = GsRegulatoryEdge.SIGN_UNKNOWN;
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
		GsRegulatoryEdge edge = (GsRegulatoryEdge)edges.get(index);
		if (edge.getMin() <= sourceStatus && (edge.getMax() == -1 || edge.getMax() >= sourceStatus)) {
			return true;
		}
		return false;
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
			if (index >= edges.size()) {
				index = -1;
			}
		}
		return index;
	}

    /**
     * @param edgeOri
     */
    public void copyFrom(GsRegulatoryMultiEdge edgeOri) {
        GsRegulatoryEdge edge = (GsRegulatoryEdge)edges.get(0);
        edge.setMin(edgeOri.getMin(0));
        edge.setMax(edgeOri.getMax(0));
        edge.setSign(edgeOri.getSign(0));
        edge.setGsAnnotation( (GsAnnotation)edgeOri.getGsAnnotation(0).clone() );
        for (int i=1 ; i<edgeOri.getEdgeCount() ; i++) {
            edge = new GsRegulatoryEdge();
            edge.setMin(edgeOri.getMin(i));
            edge.setMax(edgeOri.getMax(i));
            edge.setSign(edgeOri.getSign(i));
            edge.setGsAnnotation( (GsAnnotation)edgeOri.getGsAnnotation(i).clone() );
            edges.add(edge);
        }
    }

    public void setUserObject(Object obj) {
    }
	
}
