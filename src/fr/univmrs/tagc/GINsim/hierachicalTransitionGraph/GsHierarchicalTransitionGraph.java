package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphOptionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.manageressources.Translator;
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

public class GsHierarchicalTransitionGraph extends GsGraph {

	public static final int MODE_SCC = 1;
	public static final int MODE_HTG = 2;

	public final static String zip_mainEntry = "hierarchicalTransitionGraph.ginml";
	private String dtdFile = GsGinmlHelper.DEFAULT_URL_DTD_FILE;
	private JPanel optionPanel = null;
	
	/**
	 * Mode is either SCC or HTG depending if we group the transients component by their atteignability of attractors.
	 */
	private int transientCompactionMode;
	
	/**
	 * An array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	private byte[] childsCount = null;
	private GsHierarchicalParameterPanel vertexPanel = null;

	
/* **************** CONSTRUCTORS ************/	
	
	
	/**
	 * create a new empty GsDynamicalHierarchicalGraph.
	 */
	public GsHierarchicalTransitionGraph() {
		this((String)null, false);
	}
				
	/**
	 * create a new GsDynamicalHierarchicalGraph with a nodeOrder.
	 * @param nodeOrder the node order
	 * @param transientCompactionMode MODE_SCC or MODE_HTG
	 */
	public GsHierarchicalTransitionGraph(List nodeOrder, int transientCompactionMode) {
	    this();
	    this.nodeOrder = new ArrayList(nodeOrder);
	    this.transientCompactionMode = transientCompactionMode;
	}

	public GsHierarchicalTransitionGraph(String filename, boolean parsing) {
        super(GsHierarchicalTransitionGraphDescriptor.getInstance(), filename, parsing);
        System.out.println("ERROR"); //TODO fix this
	}

	public GsHierarchicalTransitionGraph(Map map, File file) {
	    this(file.getAbsolutePath(), true);
        GsHierarchicalTransitionGraphParser parser = new GsHierarchicalTransitionGraphParser();
        parser.parse(file, map, this);
		graphManager.ready();
	}

		
/* **************** EDITION OF VERTEX AND EDGE ************/	

	/**
	 * add a vertex to this graph.
	 * @param vertex
	 */
	public boolean addVertex(GsHierarchicalNode vertex) {
		return graphManager.addVertex(vertex);
	}
	/**
	 * add an edge between source and target
	 * @param source a GsHierarchicalNode
	 * @param target a GsHierarchicalNode
	 * @return the new edge
	 */
	public Object addEdge(Object source, Object target) {
		return graphManager.addEdge(source, target, null);
	}

		
		
/* **************** SPECIFIC MAPPING OF DESCRIPTOR FOR ACTIONS & CO ************/	
		
	/* GsHierarchicalTransitionGraphDescriptor mapping */

	public List getSpecificLayout() {
		return GsHierarchicalTransitionGraphDescriptor.getLayout();
	}
	public List getSpecificExport() {
		return GsHierarchicalTransitionGraphDescriptor.getExport();
	}
    public List getSpecificAction() {
        return GsHierarchicalTransitionGraphDescriptor.getAction();
    }
    public List getSpecificObjectManager() {
        return GsHierarchicalTransitionGraphDescriptor.getObjectManager();
    }
	
		
/* **************** PANELS ************/	
		
    
	public GsParameterPanel getEdgeAttributePanel() {
		return null;
	}

	public GsParameterPanel getVertexAttributePanel() {
	    if (vertexPanel == null) {
	        vertexPanel  = new GsHierarchicalParameterPanel(this);
	    }
		return vertexPanel;
	}
	
	protected JPanel doGetFileChooserPanel() {
		return getOptionPanel();
	}
	private JPanel getOptionPanel() {
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
		
	protected void doSave(OutputStreamWriter os, int mode, boolean selectedOnly) throws GsException {
	       try {
	            XMLWriter out = new XMLWriter(os, dtdFile);
		  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
				out.write("\t<graph id=\"" + graphName + "\"");
				out.write(" class=\"hierarchicalTransition\"");
				out.write(" nodeorder=\"" + stringNodeOrder() +"\">\n");
				saveNode(out, mode, selectedOnly);
				saveEdge(out, mode, selectedOnly);
	            if (gsAnnotation != null) {
	                gsAnnotation.toXML(out, null, 0);
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
	
 private void saveEdge(XMLWriter out, int mode, boolean selectedOnly) throws IOException {
     Iterator it;
     if (selectedOnly) {
     		it = graphManager.getSelectedEdgeIterator();
     } else {
     		it = graphManager.getEdgeIterator();
     }
     while (it.hasNext()) {
     	Object o_edge = it.next();
     	if (o_edge instanceof GsDirectedEdge) {
     		GsDirectedEdge edge = (GsDirectedEdge)o_edge;
	            String source = ""+((GsHierarchicalNode)edge.getSourceVertex()).getUniqueId();
	            String target =""+((GsHierarchicalNode) edge.getTargetVertex()).getUniqueId();
	            out.write("\t\t<edge id=\"e"+ source +"_"+target+"\" from=\"s"+source+"\" to=\"s"+target+"\"/>\n");
     	}
     }
 }
 
 /**
  * @param out
  * @param mode
  * @param selectedOnly
  * @throws IOException
  */
 private void saveNode(XMLWriter out, int mode, boolean selectedOnly) throws IOException {
 	Iterator it;
 	if (selectedOnly) {
 		it = graphManager.getSelectedVertexIterator();
 	} else {
 		it = graphManager.getVertexIterator();
 	}
 	GsVertexAttributesReader vReader = graphManager.getVertexAttributesReader();
     while (it.hasNext()) {
    	 GsHierarchicalNode vertex = (GsHierarchicalNode)it.next();
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
		
		for (Iterator it = this.getGraphManager().getVertexIterator(); it.hasNext();) {
			GsHierarchicalNode vertex = (GsHierarchicalNode) it.next();
			matcher.reset(vertex.statesToString());
			if (matcher.find()) {
				v.add(vertex);
			}
		}
		return v;
	}
	
	public GsHierarchicalNode getNodeForState(byte[] state) {
		for (Iterator it = this.getGraphManager().getVertexIterator(); it.hasNext();) {
			GsHierarchicalNode v = (GsHierarchicalNode) it.next();
			if (v.contains(state)) return v;
		}
		return null;
	}
	
	/**
	 * 
	 * @param sid a string representation of the id with a letter in first position eg. "s102"
	 * @return the node with the corresponding id. eg. 102.
	 */
	public GsHierarchicalNode getNodeById(String sid) {
		int id = Integer.parseInt(sid.substring(1));
		for (Iterator it = this.getGraphManager().getVertexIterator(); it.hasNext();) {
			GsHierarchicalNode v = (GsHierarchicalNode) it.next();
			if (v.getUniqueId() == id) return v;
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
		for (int i=0 ; i<nodeOrder.size() ; i++) {
			GsRegulatoryVertex v = (GsRegulatoryVertex) nodeOrder.get(i);
			s += v+":"+v.getMaxValue()+" ";
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

    
  	
		
/* **************** UNIMPLEMENTED METHODS ************/
	

		
		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#removeEdge(java.lang.Object)
		 *
		 * not used for this kind of graph: it's not interactivly editable
		 */
		public void removeEdge(Object obj) {
		}
		
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
		protected void setCopiedGraph(GsGraph graph) {
		}
		
		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#getCopiedGraph()
		 *
		 * not used for this kind of graph: it's not interactivly editable
		 */
		protected GsGraph getCopiedGraph() {
			return null;
		}


		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#doCopySelection(java.utils.Vector, java.utils.Vector)
		 * 
		 * not used for this kind of graph: it's not interactivly editable
		 */
		protected GsGraph doCopySelection(Vector vertex, Vector edges) {
			return null;
		}
		
		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#doInteractiveAddEdge(java.lang.Object, java.lang.Object, int)
		 *
		 * not used for this kind of graph: it's not interactivly editable
		 */
		protected Object doInteractiveAddEdge(Object source, Object target, int param) {
			return null;
		}

		/**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#doInteractiveAddVertex(int)
		 *
		 * not used for this kind of graph: it's not interactivly editable
		 */
		protected Object doInteractiveAddVertex(int param) {
			return null;
		}

	    /**
		 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#doMerge(fr.univmrs.tagc.GINsim.graph.GsGraph)
		 * 
		 * not used for this kind of graph: it has no meaning
	     */
		protected List doMerge(GsGraph otherGraph) {
	        return null;
	    }







}
