package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.data.GsAnnotation;
import fr.univmrs.ibdm.GINsim.data.ToolTipsable;
import fr.univmrs.ibdm.GINsim.global.Tools;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeString;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;
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
	private Vector 			v_logicalParameters;

	private String 			name;
	private GsAnnotation	gsa;
	private String 			id;

  private GsTreeInteractionsModel interactionsModel;
  private GsRegulatoryGraph graph;

    private static final String S_ID   = Translator.getString("STR_id")+" : ";
    private static final String S_NAME = " | "+ Translator.getString("STR_name")+" : ";
    private static final String S_MAX  = " | "+ Translator.getString("STR_max") +" : ";
    private static final String S_BASE = " | "+ Translator.getString("STR_base")+" : ";


	/**
	 * Constructs an empty vector and set the baseValue (0) and the maxValue (1)
	 * @param id
	 */
	public GsRegulatoryVertex(String id, GsRegulatoryGraph graph) {
		super();
		name			= "";
		baseValue 		= 0;
		maxValue 		= 1;
		gsa				= new GsAnnotation();
		v_logicalParameters 	= new Vector();
		this.id = id;
    this.graph = graph;
    interactionsModel = new GsTreeInteractionsModel(graph);
	}

	/**
	 * @param num number of the gene.
	 */
	public GsRegulatoryVertex(int num, GsRegulatoryGraph graph) {
		super();
		name			= "";
		baseValue 		= 0;
		maxValue 		= 1;
		gsa				= new GsAnnotation();
		v_logicalParameters 	= new Vector();
		this.id = "G"+num;
    this.graph = graph;
    interactionsModel = new GsTreeInteractionsModel(graph);
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
     * @param graph
	 */
	public void setBaseValue(short i, GsRegulatoryGraph graph) {
	    if (i > -1) {
			baseValue = i;
			if (baseValue > maxValue) {
			    maxValue = baseValue;
			}
            graph.fireGraphChange(GsGraph.CHANGE_VERTEXUPDATED, this);
	    }
	}

	/**
	 * Sets the max value to the node
	 * @param max the new max value
	 * @param graph the graph (to propagate changes if needed)
	 */
	public void setMaxValue(short max, GsRegulatoryGraph graph) {
    if (!getInteractionsModel().isMaxCompatible(max)) {
      graph.addNotificationMessage( new GsGraphNotificationMessage(graph, "Max value (" + max + ") is inconsistent with some boolean function value.", GsGraphNotificationMessage.NOTIFICATION_ERROR) );
    }
    else
	    if (max>0) {
            String s = "";
    		short oldmax = maxValue;
			maxValue = max;
			if (maxValue < baseValue) {
			    baseValue = maxValue;
			}
			if (oldmax > maxValue) {
				s += graph.applyNewMaxValue(this);
                for (int i=0 ; i<v_logicalParameters.size() ; i++) {
                    GsLogicalParameter gsi = (GsLogicalParameter)v_logicalParameters.get(i);
                    if (gsi.getValue() > maxValue) {
                        gsi.setValue(maxValue);
                        s += Translator.getString("STR_parameter_value_sup_max\n");
                    }
                }
			}
            if (!"".equals(s)) {
                graph.addNotificationMessage( new GsGraphNotificationMessage(graph, s.trim(), GsGraphNotificationMessage.NOTIFICATION_WARNING) );
            }
            if (oldmax != maxValue) {
                graph.fireGraphChange(GsGraph.CHANGE_VERTEXUPDATED, this);
                getInteractionsModel().refreshVertex();
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
		if (Tools.isValidId(id)) {
			this.id = id;
		}
	}
	/**
	 * Adds the specified interaction I to the interactions of the node
	 * @param I
	 * @return true if the logical parameter has been added
	 */
	public boolean addLogicalParameter (GsLogicalParameter I) {
	    if (I.EdgeCount() == 0) {
	        return false;
	    }
		for (int i=0 ; i<v_logicalParameters.size() ; i++) {
			if (v_logicalParameters.get(i).equals(I)) {
				return false;
			}
		}
		v_logicalParameters.add(I);
        return true;
	}

	/**
	 * Removes the specified interaction to the interactions of the node
	 * @param I
	 */
	public void removeInteraction (GsLogicalParameter I) {
		v_logicalParameters.remove(I);
	}

	/**
	 * Returns the interaction at the specified position index
	 * @param index
	 * @return GsLogicalParameter
	 */
	public GsLogicalParameter getInteraction(int index) {
		try
		{
			return((GsLogicalParameter)v_logicalParameters.get(index));
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
	    GsLogicalParameter I = (GsLogicalParameter)v_logicalParameters.get(index);
	    Vector oldList = I.getEdges();
	    I.setEdges(edges);
		for (int i=0 ; i<v_logicalParameters.size() ; i++) {
			if ( i!= index && v_logicalParameters.get(i).equals(I)) {
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
		return(v_logicalParameters.size());
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
	public Vector getV_logicalParameters() {
		return v_logicalParameters;
	}

    /**
     * get the DAG representation of logical parameters.
     *
     * @param graph
     * @return an OmddNode representing logical parameters associated to this vertex.
     */
    public OmddNode getTreeParameters(GsRegulatoryGraph graph) {
        OmddNode root;
        GsLogicalParameter pbaseValue = new GsLogicalParameter(0);
        if (this.baseValue != 0) {
            pbaseValue.setValue(baseValue);
            root = pbaseValue.buildTree(graph, this);
            if (root == null) {
                root = OmddNode.TERMINALS[baseValue];
            }
        } else {
            root = OmddNode.TERMINALS[0];
        }
        OmddNode curNode;
        for (int j=0 ; j<v_logicalParameters.size() ; j++) {
            GsLogicalParameter gsi = (GsLogicalParameter)v_logicalParameters.get(j);
            curNode = gsi.buildTree(graph, this);
            if (curNode != null) {
                root = root.merge(curNode, OmddNode.OR);
            }
        }
        return root;
    }

	public String toToolTip() {
		return    S_ID  + id
				+ S_NAME+ name
                + S_MAX + maxValue
				+ S_BASE+ baseValue;
	}

	public void toXML(GsXMLWriter out, Object param, int mode) throws IOException {

			out.openTag("node");
	    	out.addAttr("id", getId());
			if (name.length()>0) {
			    out.addAttr("name", name);
			}
		    out.addAttr("basevalue", ""+baseValue);
		    out.addAttr("maxvalue", ""+maxValue);

			// TODO: at some point stop saving logical parameters
			for (int i = 0; i < v_logicalParameters.size(); i++) {
				((GsLogicalParameter) v_logicalParameters.elementAt(i)).toXML(out, null, mode);
			}
			// save logical functions
			saveInteractionsModel(out, mode);

			gsa.toXML(out, null, mode);

			if (param != null) {
			    out.addContent("\n");
			    out.write(param.toString());
			}
			out.closeTag();
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
	public void setV_logicalParameters(Vector vector) {
		v_logicalParameters = vector;
	}

	public Object clone() {
		GsRegulatoryVertex clone = new GsRegulatoryVertex(id, graph);
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
		for (int i=v_logicalParameters.size()-1 ; i>=0 ; i--) {
			GsLogicalParameter interaction = (GsLogicalParameter)v_logicalParameters.get(i);
			if (interaction.removeEdge(multiEdge,index)) {
				v_logicalParameters.remove(i);
            }
		}
    interactionsModel.removeEdge(multiEdge, index);
    }

    /**
     * when an incoming edge is deleted, we don't want to see it appear in interactions anymore.
     * This will remove all subedges of a multiedge.
     * NOTE: ALL interaction involving the deleted multiedge will be suppressed.
     * @param multiEdge
     */
    public void removeEdgeFromInteraction(GsRegulatoryMultiEdge multiEdge) {
		for (int i=v_logicalParameters.size()-1 ; i>=0 ; i--) {
			GsLogicalParameter interaction = (GsLogicalParameter)v_logicalParameters.get(i);
			if (interaction.removeEdge(multiEdge)) {
				v_logicalParameters.remove(i);
            }
		}
    interactionsModel.removeEdge(multiEdge);
    }

    public String toString() {
        return id;
    }

    /**
     * @param copyMap
     */
    public void cleanupInteractionForNewGraph(HashMap copyMap) {
        GsRegulatoryVertex myClone = (GsRegulatoryVertex) copyMap.get(this);
        for (int i=0 ; i<v_logicalParameters.size() ; i++) {
            ((GsLogicalParameter)v_logicalParameters.get(i)).applyNewGraph(myClone, copyMap);

        }
    }

    public void setInteractionsModel(GsTreeInteractionsModel model) {
      GsLogicalParameter param;
      boolean basalValueDefined = false;

      interactionsModel = model;
      v_logicalParameters = interactionsModel.getLogicalParameters();
      for (Iterator it = v_logicalParameters.iterator(); it.hasNext(); ) {
        param = (GsLogicalParameter)it.next();
        if (param.EdgeCount() == 0) {
          v_logicalParameters.removeElement(param);
          setBaseValue((short)param.getValue(), graph);
          basalValueDefined = true;
          break;
        }
      }
      if (!basalValueDefined) setBaseValue((short)0, graph);
    }

    public GsTreeInteractionsModel getInteractionsModel() {
      return interactionsModel;
    }

    public void saveInteractionsModel(GsXMLWriter out, int mode) throws IOException {
      GsTreeString root = (GsTreeString)interactionsModel.getRoot();
      GsTreeValue val;
      GsTreeExpression exp;
      GsTreeParam param;
      String chk;

      for (int i = 0; i < root.getChildCount(); i++) {
        val = (GsTreeValue)root.getChild(i);
        out.openTag("value");
        out.addAttr("val", ""+val.getValue());
        for (int j = 0; j < val.getChildCount(); j++) {
          exp = (GsTreeExpression)val.getChild(j);
          chk = "";
          for (int k = 0; k < exp.getChildCount(); k++) {
            param = (GsTreeParam)exp.getChild(k);
            if (param.isChecked())
              chk += "1";
            else
              chk += "0";
          }
          out.openTag("exp");
          out.addAttr("str", exp.toString());
          out.addAttr("chk", chk);
          out.closeTag();
        }
        out.closeTag();
      }
    }
    public void incomingEdgeAdded(GsRegulatoryMultiEdge me) {
      interactionsModel.addEdge(me);
    }
}
