package org.ginsim.graph.regulatorygraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ginsim.annotation.Annotation;
import org.ginsim.core.notification.ErrorNotification;
import org.ginsim.core.notification.Notification;
import org.ginsim.core.notification.resolvable.ResolvableWarningNotification;
import org.ginsim.core.notification.resolvable.resolution.NotificationResolution;


import org.ginsim.graph.common.AbstractGraph;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.ToolTipsable;
import org.ginsim.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.graph.regulatorygraph.logicalfunction.LogicalParameterList;
import org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeElement;
import org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeExpression;
import org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeString;
import org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeValue;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.TreeInteractionsModel;
import org.ginsim.gui.resource.Translator;
import org.ginsim.utils.DataUtils;
import org.python.antlr.PythonParser.parameters_return;


import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;

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
public class RegulatoryNode implements ToolTipsable, XMLize {

	private byte 	maxValue = 1;
	private boolean isInput = false;
	private final LogicalParameterList v_logicalParameters = new LogicalParameterList();

	private String 		name = "";
	private Annotation	gsa = new Annotation();
	private String 		id;

	private TreeInteractionsModel interactionsModel;
	private RegulatoryGraph graph;

    private static final String S_ID   = Translator.getString("STR_id")+" : ";
    private static final String S_NAME = " | "+ Translator.getString("STR_name")+" : ";
    private static final String S_MAX  = " | "+ Translator.getString("STR_max") +" : ";

    public static final int MAXVALUE = 9;

	/**
	 * Constructs an empty vector and set the baseValue (0) and the maxValue (1)
	 * @param id
	 */
	public RegulatoryNode(String id, RegulatoryGraph graph) {
		super();
		this.id = id;
		this.graph = graph;
		interactionsModel = new TreeInteractionsModel(graph);
	}

	/**
	 * @param num number of the gene.
	 */
	public RegulatoryNode(int num, RegulatoryGraph graph) {
		super();
		this.id = "G"+num;
		this.graph = graph;
		interactionsModel = new TreeInteractionsModel(graph);
	}

    public boolean isInput() {
        return isInput;
    }
    public void setInput(boolean input, Graph graph) {
        if (input != this.isInput) {
            this.isInput = input;
            ((AbstractGraph) graph).fireGraphChange( Graph.CHANGE_VERTEXUPDATED, this);
        }
    }
	/**
	 * @return the max value of the node
	 */
	public byte getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets the max value to the node
	 * @param max the new max value
	 * @param graph the graph (to propagate changes if needed)
	 */
	public void setMaxValue(byte max, RegulatoryGraph graph) {
	    if (max>0 && max<= MAXVALUE && max != maxValue) {
	    	byte oldmax = maxValue;
	    	if (oldmax > max) {
	    		List l_fixable = new ArrayList();
	    		List l_conflict = new ArrayList();
	    		List l_parameters = new ArrayList();
			    if (!getInteractionsModel().isMaxCompatible(max)) {
			    	new ErrorNotification( this, "Max value (" + max + ") is inconsistent with some boolean function value.");
			    	return;
			    }
	    		graph.canApplyNewMaxValue(this, max, l_fixable, l_conflict);
	    		v_logicalParameters.applyNewMaxValue(max, graph, l_parameters);
	    		if (l_fixable.size() > 0 || l_conflict.size() > 0 || l_parameters.size() > 0) {

	    			// Define the options of the Notification Resolution
	    			String[] option_names;
	    			if (l_conflict.size() == 0) {
	    				option_names = new String[2];
	    				option_names[1] = "Fix";
	    			} else {
	    				option_names = new String[1];
	    			}
	    			option_names[0] = "Detail";
	    			
	    			// Create the notification resolution
	    			NotificationResolution resolution = new NotificationResolution( option_names){
	    				
	    				public boolean perform( Graph graph, Object[] data, int index){
	    					RegulatoryNode node = (RegulatoryNode) data[0];
	    					byte max = (byte) ((Integer) data[1]).byteValue();
	    					List conflict = (List) data[2];
	    					List fixable = (List) data[3];
	    					List parameters = (List) data[4];
	    					
	    					if (index == 1) {
	    						if (conflict.size() > 0) {
	    							return false;
	    						}
	    						Iterator it = fixable.iterator();
	    						while (it.hasNext()) {
	    							((RegulatoryMultiEdge)it.next()).applyNewMaxValue(max);
	    						}
	    						it = parameters.iterator();
	    						while (it.hasNext()) {
	    							((LogicalParameter)it.next()).setValue( max, (RegulatoryGraph) graph);
	    						}
	    						node.setMaxValue(max, (RegulatoryGraph) graph);
	    						return true;
	    					}
	    					return true;
	    				}
	    			};
	    			
	    			// Create the Notification
	    			Object[] data = new Object[5];
	    			data[0] = this;
	    			data[1] = new Integer( max);
	    			data[2] = l_conflict;
	    			data[3] = l_fixable;
	    			data[4] = l_parameters;
	    			
	    			new ResolvableWarningNotification( this, "max value decrease is blocked", graph, data, resolution);
	    			
	    			return;
	    		}
	    	}
	    	maxValue = max;
    		((AbstractGraph) graph).fireGraphChange( Graph.CHANGE_VERTEXUPDATED, this);
    		getInteractionsModel().refreshNode();
	    }
	}

	/**
	 * @return the id of this node.
	 */
	public String getId() {
	    return id;
	}

	/**
	 * change the id of this node.
	 * @param id the new id.
	 */
	public boolean setId(String id) {
		if (DataUtils.isValidId(id)) {
			this.id = id;
			return true;
		}
		return false;
	}
	/**
	 * Adds the specified interaction I to the interactions of the node
	 * @param I
	 * @return true if the logical parameter has been added
	 */
	public boolean addLogicalParameter (LogicalParameter I, boolean manual) {
		return v_logicalParameters.addLogicalParameter(I, manual);
	}

	/**
	 * Removes the specified interaction to the interactions of the node
	 * @param I
	 */
	public void removeInteraction (LogicalParameter I) {
		v_logicalParameters.remove(I);
	}

	/**
	 * Returns the interaction at the specified position index
	 * @param index
	 * @return LogicalParameter
	 */
	public LogicalParameter getInteraction(int index) {
		try
		{
			return (LogicalParameter)v_logicalParameters.get(index);
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e)
		{
			return null;
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
		v_logicalParameters.updateInteraction(index, edges);
	}

	/**
	 * Returns the number of the interaction in the node.
	 * @return the number of user defined interactions on this node
	 */
	public int interactionCount() {
		return v_logicalParameters.getRealSize();
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
	public Annotation getAnnotation() {
		return gsa;
	}

	/**
	 * @return the list of all interactions on this gene.
	 */
	public LogicalParameterList getV_logicalParameters() {
		return v_logicalParameters;
	}
	public OMDDNode getTreeParameters(RegulatoryGraph graph) {
		return getTreeParameters(graph.getNodeOrder());
	}

    /**
     * get the DAG representation of logical parameters.
     *
     * @param graph
     * @return an OMDDNode representing logical parameters associated to this node.
     */
    public OMDDNode getTreeParameters(List<RegulatoryNode> nodeOrder) {
        OMDDNode root;
        if (isInput) {
            root = new OMDDNode();
            root.level = nodeOrder.indexOf(this);
            root.next = new OMDDNode[maxValue+1];
            for (int i=0 ; i<root.next.length ; i++) {
                root.next[i] = OMDDNode.TERMINALS[i];
            }
        } else {
            root = OMDDNode.TERMINALS[0];
            OMDDNode curNode;
            Iterator it = v_logicalParameters.iterator();
            while (it.hasNext()) {
                LogicalParameter gsi = (LogicalParameter)it.next();
                curNode = gsi.buildTree(graph, this, nodeOrder);
                if (curNode != null) {
                    root = root.merge(curNode, OMDDNode.OR);
                }
            }
        }
        return root;
    }

	public String toToolTip() {
		return    S_ID  + id
				+ S_NAME+ name
                + S_MAX + maxValue;
	}

	public void toXML(XMLWriter out, Object param, int mode) throws IOException {

			out.openTag("node");
	    	out.addAttr("id", getId());
			if (name.length()>0) {
			    out.addAttr("name", name);
			}
		    out.addAttr("maxvalue", ""+maxValue);
		    
		    if (isInput) {
	            out.addAttr("input", ""+isInput);
		    }	    
		    // TODO: at some point stop saving logical parameters
		 	LogicalParameterList lpl = this.getV_logicalParameters();
		 	Iterator it = lpl.iterator();
    	    while (it.hasNext()) {
		 		LogicalParameter lp = (LogicalParameter) it.next();
		 		if(lpl.isManual(lp))
		 			 lp.toXML(out, null, mode);		 			    
		 	} 
		    // save logical function
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
	public Annotation getGsa() {
		return gsa;
	}

	/**
	 * @param annotation for this node.
	 */
	public void setGsa(Annotation annotation) {
		gsa = annotation;
	}

	public Object clone() {
		RegulatoryNode clone = new RegulatoryNode(id, graph);
		clone.maxValue = maxValue;
		clone.name = name;
		clone.setGsa((Annotation)gsa.clone());
		clone.isInput = isInput;
		return clone;
	}

	/**
	 * update logical parameters when an interaction is deleted.
	 */
	public void cleanupInteraction() {
		v_logicalParameters.cleanupInteraction();
	}


	/**
	 * when an incoming edge is deleted, we don't want to see it appear in interactions anymore.
	 * This will remove a subedge.
	 * NOTE: ALL interaction involving the deleted subedge will be suppressed.
	 * @param multiEdge
	 * @param index
	 */
	public void removeEdgeFromInteraction(RegulatoryMultiEdge multiEdge, int index) {
		cleanupInteraction();
		interactionsModel.removeEdge(multiEdge, index);
	}

	/**
	 * when an incoming edge is deleted, we don't want to see it appear in interactions anymore.
	 * This will remove all subedges of a multiedge.
	 * NOTE: ALL interaction involving the deleted multiedge will be suppressed.
	 * @param multiEdge
	 */
	public void removeEdgeFromInteraction(RegulatoryMultiEdge multiEdge) {
		cleanupInteraction();
		interactionsModel.removeEdge(multiEdge);
	}

    public String toString() {
        return id;
    }

    /**
     * @param copyMap
     */
    public void cleanupInteractionForNewGraph(Map copyMap) {
        RegulatoryNode myClone = (RegulatoryNode) copyMap.get(this);
        Iterator it = v_logicalParameters.iterator();
        while (it.hasNext()) {
            ((LogicalParameter)it.next()).applyNewGraph(myClone, copyMap);
            // TODO: copy the logical functions as well
            // if pasted into a new graph, the "interactionModel" should be
            // recreated/updated for the new graph
        }
    }

    public void setInteractionsModel(TreeInteractionsModel model) {
      interactionsModel = model;
      v_logicalParameters.setFunctionParameters(interactionsModel.getLogicalParameters());
    }

    public TreeInteractionsModel getInteractionsModel() {
      return interactionsModel;
    }

    public void saveInteractionsModel(XMLWriter out, int mode) throws IOException {
      TreeString root = (TreeString)interactionsModel.getRoot();
      TreeValue val;
      TreeElement exp;

      for (int i = 0; i < root.getChildCount(); i++) {
        val = (TreeValue)root.getChild(i);
        out.openTag("value");
        out.addAttr("val", ""+val.getValue());
        for (int j = 0; j < val.getChildCount(); j++) {
          exp = val.getChild(j);
          if (exp instanceof TreeExpression) {
            out.openTag("exp");
            ((TreeExpression)exp).refreshRoot();
            out.addAttr("str", exp.toString());
            out.closeTag();
          }
        }
        out.closeTag();
      }
    }
    public void incomingEdgeAdded(RegulatoryMultiEdge me) {
      interactionsModel.addEdge(me);
    }
}

