package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.data.GsAnnotation;
import fr.univmrs.ibdm.GINsim.data.ToolTipsable;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;
import fr.univmrs.ibdm.GINsim.xml.GsXMLize;

/**
 * the Class in which we store biological data for vertices (genes).
 * 
 * a gene has:
 * <ul>
 *		<li> a baseValue, to which it tends when no interactions is active</li>
 *		<li> a maxValue</li>
 *		<li> notes (comment and URLs)</li>
 *		<li> a list of interactions: each of them describes states to 
 *			which the gene tends when some incoming edges are actives </li>
 * </ul>
 */
public class GsRegulatoryVertex implements ToolTipsable, GsXMLize {

	private short 			baseValue;
	private short 			maxValue;
	private Vector 			interactions;
	private String 			name;
	private GsAnnotation	gsa;
	private String 			id;

    private static final String S_ID   = Translator.getString("STR_id")+" : ";
    private static final String S_NAME = " | "+ Translator.getString("STR_name")+" : ";
    private static final String S_MAX  = " | "+ Translator.getString("STR_max") +" : ";
    private static final String S_BASE = " | "+ Translator.getString("STR_base")+" : ";
    
    
	/**
	 * Constructs an empty vector and set the baseValue (0) and the maxValue (1)
	 * @param id
	 */
	public GsRegulatoryVertex(String id) {
		super();		
		name			= "";
		baseValue 		= 0;
		maxValue 		= 1;
		gsa				= new GsAnnotation();
		interactions 	= new Vector();
		this.id = id;
	}

	/**
	 * @param num number of the gene.
	 */
	public GsRegulatoryVertex(int num) {
		super();		
		name			= "";
		baseValue 		= 0;
		maxValue 		= 1;
		gsa				= new GsAnnotation();
		interactions 	= new Vector();
		this.id = "G"+num;
	}

	/**
	 * @return the base value of the node
	 */
	public short getBaseValue() {
		return baseValue;
	}

	/**
	 * @return the max value of the node
	 */
	public short getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets the base value to the node
	 * @param i
	 */
	public void setBaseValue(short i) {
	    if (i > -1) {
			baseValue = i;
			if (baseValue > maxValue) {
			    maxValue = baseValue;
			}
	    }
	}

	/**
	 * Sets the max value to the node
	 * @param max the new max value
	 * @param graph the graph (to propagate changes if needed)
	 */
	public void setMaxValue(short max, GsRegulatoryGraph graph) {
	    if (max>0) {
	    		short oldmax = maxValue;
			maxValue = max;
			if (maxValue < baseValue) {
			    baseValue = maxValue;
			}
            if (oldmax != maxValue) {
                graph.fireMetaChange();
            }
			if (oldmax > maxValue) {
				graph.applyNewMaxValue(this);
                for (int i=0 ; i<interactions.size() ; i++) {
                    GsLogicalParameter gsi = (GsLogicalParameter)interactions.get(i);
                    if (gsi.getValue() > maxValue) {
                        gsi.setValue(maxValue);
                    }
                }
			}
	    }
	}
	
	/**
	 * @return the id of this vertex.
	 */
	public String getId() {
	    return id;
	}
	
	/**
	 * change the id of this vertex.
	 * @param id the new id.
	 */
	public void setId(String id) {
	    this.id = id;
	}
	/**
	 * Adds the specified interaction I to the interactions of the node
	 * @param I
	 * @return true if the logicalparameter has been added
	 */
	public boolean addInteraction (GsLogicalParameter I) {
	    if (I.EdgeCount() == 0) {
	        return false;
	    }
		for (int i=0 ; i<interactions.size() ; i++) {
			if (interactions.get(i).equals(I)) {
				return false;
			}
		}
		interactions.add(I);
        return true;
	}
	
	/**
	 * Removes the specified interaction to the interactions of the node
	 * @param I
	 */ 
	public void removeInteraction (GsLogicalParameter I) {
		interactions.remove(I);
	}
	
	/**
	 * Returns the interaction at the specified position index 
	 * @param index
	 * @return GsLogicalParameter
	 */
	public GsLogicalParameter getInteraction(int index) {
		try
		{
			return((GsLogicalParameter)interactions.get(index));
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e) 
		{
			return(null);
		}
	}
	
	/**
	 * change an interaction's list of edges: we have to check that the new list of edges
	 * is not in an existing interaction.
	 * 
	 * @param index
	 * @param edges
	 */
	public void updateInteraction(int index, Vector edges) {
	    GsLogicalParameter I = (GsLogicalParameter)interactions.get(index);
	    Vector oldList = I.getEdges();
	    I.setEdges(edges);
		for (int i=0 ; i<interactions.size() ; i++) {
			if ( i!= index && interactions.get(i).equals(I)) {
			    I.setEdges(oldList);
				return;
			}
		}
	}
	
	/**
	 * Returns the number of the interaction in the node.
	 * @return the number of user defined interactions on this node
	 */
	public int interactionCount() {
		return(interactions.size());
	}

	/**
	 * get the long name of the node.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * set the name of the gene.
	 * @param name the new name
	 */
	public void setName(String name) {
		if (!this.name.equals(name)) {
			this.name = name;
		}
	}
	
	/**
	 * get notes on this gene.
	 * @return the annotation associated to this node
	 */
	public GsAnnotation getAnnotation() {
		return gsa;
	}
	
	/**
	 * @return the list of all interactions on this gene.
	 */
	public Vector getInteractions() {
		return interactions;
	}

	public String toToolTip() {
		return    S_ID  + id
				+ S_NAME+ name
                + S_MAX + maxValue
				+ S_BASE+ baseValue;
	}	

	public void toXML(GsXMLWriter out, Object param, int mode) throws IOException {

	    	out.write("\t\t<node id=\"" + this.getId() +"\""); 
			if (name.length()>0) {
			    out.write(" name=\"" + name + "\"");
			}
			out.write(" basevalue=\"" + baseValue +"\"");
			out.write(" maxvalue=\"" + maxValue +"\">\n");
			
			for (int i = 0; i < interactions.size(); i++) {
				((GsLogicalParameter) interactions.elementAt(i)).toXML(out, null, mode);
			}
			
			gsa.toXML(out, null, mode);
			
			if (param != null) {
			    out.write(param.toString());
			}
			out.write("\t\t</node>\n");
	}

	/**
	 * @return annotation for this node
	 */
	public GsAnnotation getGsa() {
		return gsa;
	}

	/**
	 * @param annotation for this node.
	 */
	public void setGsa(GsAnnotation annotation) {
		gsa = annotation;
	}

	/**
	 * Set the interactions vector.
	 * @param vector
	 */
	public void setInteractions(Vector vector) {
		interactions = vector;
	}

	public Object clone() {
		GsRegulatoryVertex clone = new GsRegulatoryVertex(id);
		clone.maxValue = maxValue;
		clone.baseValue = baseValue;
		clone.name = name;
		clone.setGsa((GsAnnotation)gsa.clone());
		return clone;
	}

    /**
     * when an incoming edge is deleted, we don't want to see it appear in interactions anymore.
     * This will remove a subedge.
     * NOTE: ALL interaction involving the deleted subedge will be suppressed.
     * @param multiEdge
     * @param index
     */
    public void removeEdgeFromInteraction(GsRegulatoryMultiEdge multiEdge, int index) {
    		for (int i=0 ; i<interactions.size() ; i++) {
    			GsLogicalParameter interaction = (GsLogicalParameter)interactions.get(i);
    			if (interaction.removeEdge(multiEdge,index)) {
                    interactions.remove(i--);
                }
    		}
    }

    /**
     * when an incoming edge is deleted, we don't want to see it appear in interactions anymore.
     * This will remove all subedges of a multiedge.
     * NOTE: ALL interaction involving the deleted multiedge will be suppressed.
     * @param multiEdge
     */
    public void removeEdgeFromInteraction(GsRegulatoryMultiEdge multiEdge) {
		for (int i=0 ; i<interactions.size() ; i++) {
			GsLogicalParameter interaction = (GsLogicalParameter)interactions.get(i);
			if (interaction.removeEdge(multiEdge)) {
                interactions.remove(i--);
            }
		}
    }

    public String toString() {
        return id;
    }

    /**
     * @param copyMap
     */
    public void cleanupInteractionForNewGraph(HashMap copyMap) {
        GsRegulatoryVertex myClone = (GsRegulatoryVertex) copyMap.get(this);
        for (int i=0 ; i<interactions.size() ; i++) {
            ((GsLogicalParameter)interactions.get(i)).applyNewGraph(myClone, copyMap);
            
        }
    }
    
}
