package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;
import fr.univmrs.ibdm.GINsim.xml.GsXMLize;

/**
 * the Class in which we store biological data for logical parameters
 * (in the vertex)
 */
public class GsLogicalParameter implements GsXMLize {

	//value of the parameter
	private int value;
	//vector of incoming active interaction
	private Vector edge_index;
	private GsEdgeIndex tmp_ei = new GsEdgeIndex(null, 0);

	/**
	 * Constructs an empty vector and set the value
	 * 
	 * @param v of the interaction
	 */
	public GsLogicalParameter(int v) {
		value = v;
		edge_index = new Vector();
	}

	/**
     * @param newEI
     * @param v
     */
    public GsLogicalParameter(Vector newEI, int v) {
        value = v;
        edge_index = newEI;
    }

    /**
	 * get value of the interaction
	 * @return int
	 */
	public int getValue() {
		return value;
	}

	/**
	 * set value of the interaction
	 * @param i
	 */
	public void setValue(int i) {
		value = i;
	}

	/**
	 * Adds the GsEdgeIndex to the interaction
	 * @param me
	 * @param index
	 */
	public void addEdge(GsRegulatoryMultiEdge me,int index) {
		edge_index.addElement(new GsEdgeIndex(me,index));
	}
	
	/**
	 * Adds the GsEdgeIndex to the interaction
	 * @param ei
	 */
	public void addEdge(GsEdgeIndex ei) {
		edge_index.addElement(ei);
	}
	
	/**
	 * Removes the GsEdgeIndex from the parameter
	 * @param o
	 * @param index
     * @return true if this triggered a change
	 */
	public boolean removeEdge(GsRegulatoryMultiEdge o,int index) {
		try {
			for (int i=0 ; i<edge_index.size() ; i++) {
				//search the GsEdgeIndex corresponding to parameter
				GsEdgeIndex ei = (GsEdgeIndex)edge_index.get(i);
				if (ei.data == o) {
					if ( ei.index == index) {
						return true;
					} else if (ei.index > index) {
						ei.index--;
					}
				}
			}
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
        return false;
	}

	/**
	 * Removes all edges of the multiedge from the parameter 
	 * @param o
     * @return true if this triggered a change
	 */
	public boolean removeEdge(GsRegulatoryMultiEdge o) {
        boolean changed = false;
		try {
			for (int i=0 ; i<edge_index.size() ; i++) {
				//search the GsEdgeIndex corresponding to parameter
				GsEdgeIndex ei = (GsEdgeIndex)edge_index.get(i);
				if ( ei.data == o) {
					edge_index.remove(ei);
					i--;
                    changed = true;
				}
			}
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
        return changed;
	}

	/**
	 * @param index
	 * @return the GsEdgeIndex at the specified position index in the parameter. 
	 */
	public GsEdgeIndex getEdge(int index) {
		try{
			return (GsEdgeIndex)(edge_index.get(index));
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e) {return(null);}
	}

	/**
	 * @return the number of edges in this parameter.
	 */
	public int EdgeCount() {
		return(edge_index.size());
	}
	
	/**
	 * @return all the GsEdgeIndex
	 */
	public Vector getEdges() {
		return edge_index;
	}

	/**
	 * Set the vector of GsEdgeIndex
	 * @param vector
	 */
	public void setEdges(Vector vector) {
		edge_index = vector;
	}

    /**
     * build a structure reflecting expression constraints of the parameter
     * that should be faster to test.
     * call it before beginning the simulation!
     * 
     * details: the t_ac is a simple int[][].
     * each line represent allowed values for a gene:
     * <ul>
     *  <li> the first int is the index of this gene in the nodeOrder</li>
     *  <li> all other are either 0 or -1, -1 meaning that the value is forbidden</li>
     * </ul>
     *  
     *  the first line is special and only contains the value for this interaction
     *  
     *  <p>example: [ 3, 0, -1, -1, 0, 0, -1 ]
     *  the third gene can take the values 0, 3 or 4 ; values 1, 2 and 5 are forbidden
     * 
     * @param regGraph
     * @param node
     * @return the t_ac
     */
	private short[][] buildTac(GsRegulatoryGraph regGraph, GsRegulatoryVertex node) {
	    List incEdges = regGraph.getGraphManager().getIncomingEdges(node);
        Vector nodeOrder = regGraph.getNodeOrder();
        short[][] t_ac = new short[incEdges.size()+1][];
        t_ac[0] = new short[1];
        t_ac[0][0] = (short)value;
        boolean ok = false;
        for (int i=incEdges.size() ; i>0 ; i--) {
            GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)((GsDirectedEdge)incEdges.get(i-1)).getUserObject();
            GsRegulatoryVertex vertex = me.getSource();
            short[] t_val = new short[vertex.getMaxValue()+2];
            t_val[0] = (short)nodeOrder.indexOf(vertex);
            t_ac[i] = t_val;
            int nbedges = me.getEdgeCount();
            tmp_ei.data = me;
            int m = vertex.getMaxValue();
            for (int j=0 ; j<nbedges ; j++) {
                tmp_ei.index = j;
                int im = me.getMax(j);
                if (im == -1) {
                    im = m;
                }
                if (!edge_index.contains(tmp_ei)) {
                    // must be inactive
                    for (int l=me.getMin(j) ; l<=im ; l++) {
                        t_val[l+1] = -1;
                    }
                } else {
                    // must be active
                    for (int l=0 ; l<me.getMin(j) ; l++) {
                        t_val[l+1] = -1;
                    }
                    for (int l=im+1 ; l<=m ; l++) {
                        t_val[l+1] = -1;
                    }
                }
            }
            // once all constraints are in, check that it is activable..
            ok = false;
            for (int k=1 ; k<t_val.length ; k++) {
                if (t_val[k] != -1) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                break;
            }
        }
        if (!ok) {
            t_ac = null;
        }
        return t_ac;
    }

    /**
     * 
     * @param regGraph
     * @param node
     * @return true if this logical parameter is activable
     */
    public boolean activable(GsRegulatoryGraph regGraph, GsRegulatoryVertex node) {
        return buildTac(regGraph, node) != null;
    }

    /**
     * build a tree of condition of activation for this logical parameter
     * 
     * @param regGraph
     * @param node
     * @return the OmddNode representation of this logical parameter
     */
    public OmddNode buildTree(GsRegulatoryGraph regGraph, GsRegulatoryVertex node) {
        short[][] t_ac = buildTac(regGraph, node);
        short[] t_tmp;
        
        if (t_ac == null) {
            return null;
        }
        // simple sort on rows:
        for (int i=t_ac.length-1 ; i>0 ; i--) {
            for (int j=1 ; j<i ; j++) {
                if (t_ac[i][0] < t_ac[j][0]) {
                    t_tmp = t_ac[i];
                    t_ac[i] = t_ac[j];
                    t_ac[j] = t_tmp;
                }
            }
        }
        OmddNode root = OmddNode.TERMINALS[this.value];
        OmddNode curNode;
        for (int i=t_ac.length-1 ; i>0 ; i--) {
            curNode = new OmddNode();
            t_tmp = t_ac[i];
            curNode.level = t_tmp[0];
            curNode.next = new OmddNode[ t_tmp.length-1 ];
            for (int j=1 ; j<t_tmp.length ; j++) {
                if (t_tmp[j] != -1) {
                    curNode.next[j-1] = root;
                } else {
                    curNode.next[j-1] = OmddNode.TERMINALS[0];
                }
            }
            root = curNode;
        }
        return root;
    }
    
    public void toXML(GsXMLWriter out, Object param, int mode) throws IOException {
    	out.openTag("parameter");
    	out.addAttr("idActiveInteractions", stringEdges());
    	out.addAttr("val", ""+value);
    	out.closeTag();
	}
	
	private String stringEdges() {
		String sEdges = "";
		for (int i = 0; i < edge_index.size(); i++) {
			GsEdgeIndex ei = (GsEdgeIndex) edge_index.elementAt(i);
			sEdges = sEdges + ei.getSEdge() + " ";
		}

		return sEdges.substring(0,sEdges.length()-1);		
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof GsLogicalParameter)) {
			return false;
		}
		Vector o_edge = ((GsLogicalParameter)obj).edge_index;
		if (o_edge.size() != edge_index.size() || !o_edge.containsAll(edge_index)) {
			return false;
		}
		return true;
	}

    /**
     * @param clone
     * @param copyMap
     */
    public void applyNewGraph(GsRegulatoryVertex clone, HashMap copyMap) {
        Vector newEI = new Vector();
        for (int i=0 ; i<edge_index.size() ; i++) {
            GsEdgeIndex ei = (GsEdgeIndex)edge_index.get(i);
            GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)copyMap.get(ei.data);
            if (me != null) {
                newEI.add(new GsEdgeIndex(me, ei.index));
            } else {
                // if any of the nodes involved in the interaction hasn't been copied: don't restore the interaction!
                return;
            }
        }
        if (newEI.size() != 0) {
            clone.addLogicalParameter(new GsLogicalParameter(newEI, value));
        }
    }
    
    public String toString() {
        if (edge_index.size() == 0) {
            return "(basal value)";
        }
        String str = "";
        for (int i = 0; i < edge_index.size(); i++) {
            str += getEdge(i).data.getId(getEdge(i).index) + " ";
        }
        return str;
    }
}
