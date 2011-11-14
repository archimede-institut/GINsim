package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.exception.GsException;
import org.ginsim.graph.AbstractAssociatedGraphFrontend;
import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;
import org.ginsim.gui.service.tools.decisionanalysis.GsDecisionOnEdge;
import org.ginsim.gui.service.tools.dynamicalhierarchicalsimplifier.NodeInfo;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParameters;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphOptionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.xml.XMLWriter;

/* SUMMARY
 * 
 * **************** CONSTRUCTORS ************/	
/* **************** EDITION OF VERTEX AND EDGE ************/	
/* **************** SPECIFIC ACTIONS & CO ************/	
/* **************** PANELS ************/	
/* **************** SAVE ************/	
/* **************** NODE SEARCH ************/
/* **************** GETTER AND SETTERS ************/
/* **************** UNIMPLEMENTED METHODS ************/

public class GsHierarchicalTransitionGraph extends AbstractAssociatedGraphFrontend<GsHierarchicalNode, GsDecisionOnEdge, GsRegulatoryGraph, GsRegulatoryVertex, GsRegulatoryMultiEdge>{

	public static final int MODE_SCC = 1;
	public static final int MODE_HTG = 2;

	public final static String zip_mainEntry = "hierarchicalTransitionGraph.ginml";
	private String dtdFile = GsGinmlHelper.DEFAULT_URL_DTD_FILE;
	private JPanel optionPanel = null;
	
	private List<NodeInfo> nodeOrder = new ArrayList<NodeInfo>();
	
	/**
	 * Mode is either SCC or HTG depending if we group the transients component by their atteignability of attractors.
	 */
	private int transientCompactionMode;
	
	/**
	 * An array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	private byte[] childsCount = null;
	private GsHierarchicalVertexParameterPanel vertexPanel = null;
	private long saveEdgeId;
	private GsSimulationParameters simulationParameters;
	private GsHierarchicalEdgeParameterPanel edgePanel;

	
/* **************** CONSTRUCTORS ************/	
	
	
	/**
	 * create a new empty GsDynamicalHierarchicalGraph.
	 */
	public GsHierarchicalTransitionGraph() {
		this( false);
	}
				
	/**
	 * create a new GsDynamicalHierarchicalGraph with a nodeOrder.
	 * @param nodeOrder the node order
	 * @param transientCompactionMode MODE_SCC or MODE_HTG
	 */
	public GsHierarchicalTransitionGraph(List<GsRegulatoryVertex> nodeOrder, int transientCompactionMode) {
		
	    this();
	    for (GsRegulatoryVertex vertex: nodeOrder) {
	    	this.nodeOrder.add(new NodeInfo(vertex));
	    }
	    this.transientCompactionMode = transientCompactionMode;
	}

	public GsHierarchicalTransitionGraph( boolean parsing) {
		
        super(GsHierarchicalTransitionGraphDescriptor.getInstance(), parsing);
	}

	public GsHierarchicalTransitionGraph(Map map, File file) {
		
	    this( true);
        GsHierarchicalTransitionGraphParser parser = new GsHierarchicalTransitionGraphParser();
        parser.parse(file, map, this);
	}

	/**
	 * Return the node order as a List of NodeInfo
	 * 
	 * @return the node order as a List of NodeInfo
	 */
	public List<NodeInfo> getNodeOrder() {
		return nodeOrder;
	}
	
    /**
     * Return the size of the node order
     * 
     * @return the size of the node order
     */
    @Override
	public int getNodeOrderSize(){
		
		if( nodeOrder != null){
			return nodeOrder.size();
		}
		else{
			return 0;
		}
	}
	
    
	/**
	 * Set a list of NodeInfo representing the order of vertex as defined by the model
	 * 
	 * @param list the list of NodeInfo representing the order of vertex as defined by the model
	 */
	public void setNodeOrder( List<NodeInfo> node_order){
		
		nodeOrder = node_order;
	}


/* **************** EDITION OF VERTEX AND EDGE ************/	

//	/**
//	 * add a vertex to this graph.
//	 * @param vertex
//	 */
	// TODO To remove since it duplicates a method existing on AbstractGraphFrontend
//	public boolean addVertex(GsHierarchicalNode vertex) {
//		return graphManager.addVertex(vertex);
//	}
	/**
	 * add an edge between source and target
	 * @param source a GsHierarchicalNode
	 * @param target a GsHierarchicalNode
	 * @return the new edge
	 */
	public Object addEdge(GsHierarchicalNode source, GsHierarchicalNode target) {
		
		Object e = getEdge(source, target);
		if (e != null) return e;
		// FIXME: creating an empty GsDecisionOnEdge object: is it even possible?
		GsDecisionOnEdge edge = new GsDecisionOnEdge( source, target, nodeOrder);
		return addEdge(edge);
	}

		
		
/* **************** PANELS ************/	
		
    
	public GsParameterPanel getEdgeAttributePanel() {
	    if (edgePanel == null) {
	    	edgePanel  = new GsHierarchicalEdgeParameterPanel(this);
	    }
		return edgePanel;
	}

	public GsParameterPanel getVertexAttributePanel() {
	    if (vertexPanel == null) {
	        vertexPanel  = new GsHierarchicalVertexParameterPanel(this);
	    }
		return vertexPanel;
	}
	
	protected JPanel doGetFileChooserPanel() {
		if (optionPanel == null) {
            Object[] t_mode = { Translator.getString("STR_saveNone"),
                    Translator.getString("STR_savePosition"),
                    Translator.getString("STR_saveComplet") };
            optionPanel = new GsRegulatoryGraphOptionPanel(t_mode, mainFrame != null ? 2 : 0);
		}
		return optionPanel ;
	}
	
	protected FileFilter doGetFileFilter() {
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
		return ffilter;
	}
	
		
/* **************** SAVE ************/	
		
	protected String getGraphZipName() {
		return zip_mainEntry;
    }

	
	protected void doSave(OutputStreamWriter os, int mode, Collection<GsHierarchicalNode> nodes, Collection<GsDecisionOnEdge> edges) throws GsException {
       try {
            XMLWriter out = new XMLWriter(os, dtdFile);
	  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
			out.write("\t<graph id=\"" + graphName + "\"");
			out.write(" class=\"hierarchicalTransitionGraph\"");
			out.write(" iscompact=\""+this.transientCompactionMode+"\"");
			out.write(" nodeorder=\"" + stringNodeOrder() +"\">\n");
			saveNode(out, mode, nodes);
			saveEdge(out, mode, edges);
            if (graphAnnotation != null) {
            	graphAnnotation.toXML(out, null, 0);
            }
            // save the ref of the associated regulatory graph!
            if (associatedGraph != null) {
                associatedID = associatedGraph.getSaveFileName();
            }
            if (associatedID != null) {
                out.write("<link xlink:href=\""+associatedID+"\"/>\n");
            }
	  		out.write("\t</graph>\n");
	  		out.write("</gxl>\n");
        } catch (IOException e) {
            throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_unableToSave")+": " +e.getMessage());
        }
	}
	
	private void saveEdge(XMLWriter out, int mode, Collection<GsDecisionOnEdge> edges) throws IOException {
	     for (GsDecisionOnEdge edge: edges) {
            String source = "" + edge.getSource().getUniqueId();
            String target = "" + edge.getTarget().getUniqueId();
            out.write("\t\t<edge id=\"e"+(++saveEdgeId)+"\" from=\"s"+source+"\" to=\"s"+target+"\"/>\n");
	     }
	 }
	 
	 /**
	  * @param out
	  * @param mode
	  * @param vertices
	  * @throws IOException
	  */
	 private void saveNode(XMLWriter out, int mode, Collection<GsHierarchicalNode> vertices) throws IOException {
	 	GsVertexAttributesReader vReader = getVertexAttributeReader();
	     for (GsHierarchicalNode vertex: vertices) {
	         vReader.setVertex(vertex);
	         out.write("\t\t<node id=\"s"+vertex.getUniqueId()+"\">\n");
	         out.write("<attr name=\"type\"><string>"+vertex.typeToString()+"</string></attr>");
	         out.write("<attr name=\"states\"><string>"+vertex.write().toString()+"</string></attr>");
	         out.write(GsGinmlHelper.getFullNodeVS(vReader));
	         out.write("\t\t</node>\n");
	     }
	 }		
		
/* **************** NODE SEARCH ************/
		
	public Vector searchNodes(String regexp) {
		Vector v = new Vector();
		
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < regexp.length(); i++) {
			char c = regexp.charAt(i);
			if (c == '\\') {
				s.append(regexp.charAt(++i));
			} else if (c == '*') {
				s.append("[0-9\\*]");
			} else if (c == '0' || (c >= '1' && c <= '9')){
				s.append("["+c+"\\*]");
			} else if (c == ' ' || c == '\t') {
				//pass
			} else {
				s.append(c);
			}
		}
		Pattern pattern = Pattern.compile(s.toString(), Pattern.COMMENTS | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher("");
		
		for (Iterator it = this.getVertices().iterator(); it.hasNext();) {
			GsHierarchicalNode vertex = (GsHierarchicalNode) it.next();
			matcher.reset(vertex.statesToString());
			if (matcher.find()) {
				v.add(vertex);
			}
		}
		return v;
	}
	
	public GsHierarchicalNode getNodeForState(byte[] state) {
		for (Iterator it = this.getVertices().iterator(); it.hasNext();) {
			GsHierarchicalNode v = (GsHierarchicalNode) it.next();
			if (v.contains(state)) return v;
		}
		return null;
	}
	
		
	
		
/* **************** GETTER AND SETTERS ************/
		
	/**
	 * return an array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	public byte[] getChildsCount() {
		if (childsCount == null) {
			childsCount = new byte[nodeOrder.size()];
			int i = 0;
			for (Iterator it = nodeOrder.iterator(); it.hasNext();) {
				GsRegulatoryVertex v = (GsRegulatoryVertex) it.next();
				childsCount[i++] = (byte) (v.getMaxValue()+1);
			}			
		}
		return childsCount;
	}
	
	public void setChildsCount(byte[] cc) {
		childsCount = cc;
	}
	
	/**
	 * Return a string representation of the nodeOrder
	 *  
	 * ex : <tt>G0:1 G1:2 G2:1 G3:3</tt>
	 * 
	 * @return
	 */
	private String stringNodeOrder() {
		String s = "";
		for (NodeInfo v: nodeOrder) {
			s += v.name+":"+v.max+" ";
		}
		if (s.length() > 0) {
			return s.substring(0, s.length()-1);
		}
		return s;
	}
	
	/**
	 * Return <b>true</b> if the transients are compacted into component by their atteignability of attractors.
	 * @return
	 */
	public boolean areTransientCompacted() {
		return transientCompactionMode == MODE_HTG;
	}
	
	public void setMode(int mode) {
		transientCompactionMode = mode;
	}

    protected boolean isAssociationValid( Graph graph) {
    	
        if (graph instanceof GsRegulatoryGraph) {
            return true;
        }
        return false;
    }
    
    /**
     * Return the Object Managers specialized for this class
     * 
     * @return a List of Object Managers
     */
    @Override
    public List getSpecificObjectManager() {
    	
        return GsHierarchicalTransitionGraphDescriptor.getObjectManager();
    }
		
/* **************** UNIMPLEMENTED METHODS ************/
	

		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#changeVertexId(java.lang.Object, java.lang.String)
		 *
		 * not used for this kind of graph: it's not interactivly editable
		 */
		public void changeVertexId(Object vertex, String newId) throws GsException {
		}
		
		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#setCopiedGraph(fr.univmrs.tagc.GINsim.graph.GsGraph)
		 *
		 * not used for this kind of graph: it's not interactivly editable
		 */
		protected void setCopiedGraph( Graph graph) {
		}
		
		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#getCopiedGraph()
		 *
		 * not used for this kind of graph: it's not interactivly editable
		 */
		protected Graph getCopiedGraph() {
			return null;
		}


		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#getSubGraph(java.utils.Vector, java.utils.Vector)
		 * 
		 * not used for this kind of graph: it's not interactively editable
		 */
		public Graph getSubgraph(Collection<GsHierarchicalNode> vertex, Collection<GsDecisionOnEdge> edges) {
			return null;
		}
		
		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#doInteractiveAddEdge(java.lang.Object, java.lang.Object, int)
		 *
		 * not used for this kind of graph: it's not interactively editable
		 */
		protected GsDecisionOnEdge doInteractiveAddEdge(GsHierarchicalNode source, GsHierarchicalNode target, int param) {
			return null;
		}

		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#doInteractiveAddVertex(int)
		 *
		 * not used for this kind of graph: it's not interactivly editable
		 */
		protected GsHierarchicalNode doInteractiveAddVertex(int param) {
			return null;
		}

	    /**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#doMerge(fr.univmrs.tagc.GINsim.graph.GsGraph)
		 * 
		 * not used for this kind of graph: it has no meaning
	     */
		protected List doMerge( Graph otherGraph) {
	        return null;
	    }


}
