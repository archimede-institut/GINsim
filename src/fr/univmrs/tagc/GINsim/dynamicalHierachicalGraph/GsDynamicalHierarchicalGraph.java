package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

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

public class GsDynamicalHierarchicalGraph extends GsGraph {

	public final static String zip_mainEntry = "dynamicalHierarchicalGraph.ginml";
	private String dtdFile = GsGinmlHelper.DEFAULT_URL_DTD_FILE;
	private JPanel optionPanel = null;
	
	
	private byte[] childsCount = null;
	private GsDynamicalHierarchicalParameterPanel vertexPanel = null;

	/**
	 * create a new GsDynamicalHierarchicalGraph with a nodeOrder.
	 * @param nodeOrder the node order
	 */
	public GsDynamicalHierarchicalGraph(List nodeOrder) {
	    this((String)null, false);
	    this.nodeOrder = new ArrayList(nodeOrder);
	}
	
	/**
	 * create a new empty GsDynamicalHierarchicalGraph.
	 */
	public GsDynamicalHierarchicalGraph() {
		this((String)null, false);
	}
	
	public GsDynamicalHierarchicalGraph(String filename, boolean parsing) {
        super(GsDynamicalHierarchicalGraphDescriptor.getInstance(), filename, parsing);
	}


	public GsDynamicalHierarchicalGraph(Map map, File file) {
	    this(file.getAbsolutePath(), true);
        GsDynamicalHierarchicalParser parser = new GsDynamicalHierarchicalParser();
        parser.parse(file, map, this);
		graphManager.ready();
	}


	/* Files */
	
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

	
	/* GsDynamicalHierarchicalGraphDescriptor mapping */

	public List getSpecificLayout() {
		return GsDynamicalHierarchicalGraphDescriptor.getLayout();
	}
	public List getSpecificExport() {
		return GsDynamicalHierarchicalGraphDescriptor.getExport();
	}
    public List getSpecificAction() {
        return GsDynamicalHierarchicalGraphDescriptor.getAction();
    }
    public List getSpecificObjectManager() {
        return GsDynamicalHierarchicalGraphDescriptor.getObjectManager();
    }
    
    /* Save */
    
	protected void doSave(OutputStreamWriter os, int mode, boolean selectedOnly) throws GsException {
	       try {
	            XMLWriter out = new XMLWriter(os, dtdFile);
		  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
				out.write("\t<graph id=\"" + graphName + "\"");
				out.write(" class=\"dynamicalHierarchical\">\n");
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
        switch (mode) {
        	default:
		        while (it.hasNext()) {
		        	Object o_edge = it.next();
		        	if (o_edge instanceof GsDirectedEdge) {
		        		GsDirectedEdge edge = (GsDirectedEdge)o_edge;
			            String source = edge.getSourceVertex().toString();
			            String target = edge.getTargetVertex().toString();
			            out.write("\t\t<edge id=\""+ source +"_"+target+"\" from=\""+source+"\" to=\""+target+"\"/>\n");
		        	}
		        }
		        break;
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
        	GsVertexAttributesReader vReader = null;
        	switch (mode) {
	    		case 1:
	    			vReader = graphManager.getVertexAttributesReader();
	                while (it.hasNext()) {
	                	GsDynamicalHierarchicalNode vertex = (GsDynamicalHierarchicalNode)it.next();
	                    out.write("\t\t<node id=\""+vertex+"\">\n");
                        out.write("<attr name=\"type\"><string>"+vertex.typeToString()+"</string></attr>");
                        out.write("<attr name=\"states\"><string>"+vertex.statesToString(graphManager.getVertexCount())+"</string></attr>");
	                    out.write(GsGinmlHelper.getShortNodeVS(vReader));
	                    out.write("\t\t</node>\n");
	                }
	    			break;
				case 2:
					vReader = graphManager.getVertexAttributesReader();
	                while (it.hasNext()) {
	                	GsDynamicalHierarchicalNode vertex = (GsDynamicalHierarchicalNode)it.next();
	                    vReader.setVertex(vertex);
	                    out.write("\t\t<node id=\""+vertex+"\">\n");
                        out.write("<attr name=\"type\"><string>"+vertex.typeToString()+"</string></attr>");
                        out.write("<attr name=\"states\"><string>"+vertex.statesToString(graphManager.getVertexCount())+"</string></attr>");
	                    out.write(GsGinmlHelper.getFullNodeVS(vReader));
	                    out.write("\t\t</node>\n");
	                }
	    			break;
        		default:
        	        while (it.hasNext()) {
        	            Object vertex = it.next();
	                    String content = "";//FIXME ((GsDynamicalHierarchicalNode)vertex).getContentString();
	                    out.write("\t\t<node id=\""+vertex+"\">\n");
                        out.write("<attr name=\"content\"><string>"+content+"</string></attr>");
                        out.write("</node>");
        	        }
        }
    }
    
    /* edge and vertex panels */
    
	public GsParameterPanel getEdgeAttributePanel() {
		return null;
	}

	public GsParameterPanel getVertexAttributePanel() {
	    if (vertexPanel == null) {
	        vertexPanel  = new GsDynamicalHierarchicalParameterPanel(this);
	    }
		return vertexPanel;
	}


	
	/* adding edge and vertex */
  
	/**
	 * add a vertex to this graph.
	 * @param vertex
	 */
	public boolean addVertex(GsDynamicalHierarchicalNode vertex) {
		return graphManager.addVertex(vertex);
	}
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @return the new edge
	 */
	public Object addEdge(Object source, Object target) {
		return graphManager.addEdge(source, target, null);
	}


	
	/* somethign else */
	
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
	
	/* Not used methods */
	
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
			GsDynamicalHierarchicalNode vertex = (GsDynamicalHierarchicalNode) it.next();
			matcher.reset(vertex.statesToString(this.getNodeOrder().size()));
			if (matcher.find()) {
				v.add(vertex);
			}
		}
		return v;
	}





}
