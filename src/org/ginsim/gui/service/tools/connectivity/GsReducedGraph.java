package org.ginsim.gui.service.tools.connectivity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.exception.GsException;
import org.ginsim.graph.AbstractAssociatedGraphFrontend;
import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;

import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphOptionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * reduced Graph.
 */
public class GsReducedGraph extends AbstractAssociatedGraphFrontend<GsNodeReducedData, Edge<GsNodeReducedData>, GsRegulatoryGraph, GsRegulatoryVertex, GsRegulatoryMultiEdge>{

	private ReducedParameterPanel parameterPanel = null;
    private JPanel optionPanel = null;

    public static final String zip_mainEntry = "connectedComponent.ginml";
    
	/**
	 * @param parent
	 */
	public GsReducedGraph( Graph parent) {
		
	    this( false);
        setAssociatedGraph( (GsRegulatoryGraph) parent);
	}
	
    protected String getGraphZipName() {
    	return zip_mainEntry;
    }

	/**
	 * @param map
	 * @param file
	 */
	public GsReducedGraph(Map map, File file) {
		
	    this( true);
        GsReducedGraphParser parser = new GsReducedGraphParser();
        parser.parse(file, map, this);
	}

	/**
	 * @param filename
	 */
	public GsReducedGraph( boolean parsing) {
		
        super( GsReducedGraphDescriptor.getInstance(), parsing);
	}
	/**
     * 
     */
    public GsReducedGraph() {
    	
        this( false);
    }
    
    /**
     * Return the Object Managers specialized for this class
     * 
     * @return a List of Object Managers
     */
    @Override
    public List getSpecificObjectManager() {
    	
        return GsReducedGraphDescriptor.getObjectManager();
    }
    
    /**
     * Return 0 since no node order is defined on this king of graph
     * 
     * @return 0 since no node order is defined on this king of graph
     */
    @Override
	public int getNodeOrderSize(){
		
		return 0;
	}


	/*
	 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#doSave(java.lang.String, int, boolean)
	 */
	protected void doSave(OutputStreamWriter os, int mode, String dtd_file, Collection<GsNodeReducedData> vertices, Collection<Edge<GsNodeReducedData>> edges) throws GsException {
        try {
            XMLWriter out = new XMLWriter(os, dtd_file);
	  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
			out.write("\t<graph id=\"" + graphName + "\"");
			out.write(" class=\"reduced\">\n");
			saveVertices(out, mode, vertices);
			saveEdge(out, mode, edges);
            if (graphAnnotation != null) {
            	graphAnnotation.toXML(out, null, 0);
            }
            // save the ref of the associated regulatory graph!
            if (associatedGraph != null) {
                associatedID = GraphManager.getInstance().getGraphPath( associatedGraph);
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

    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveEdge(XMLWriter out, int mode, Collection<Edge<GsNodeReducedData>> edges) throws IOException {
        Iterator<Edge<GsNodeReducedData>> it;
    	if (edges == null) {
    		edges = getEdges();
    	}
        switch (mode) {
        	default:
		        for (Edge<GsNodeReducedData> edge: edges) {
		            GsNodeReducedData source = edge.getSource();
		            GsNodeReducedData target = edge.getTarget();
		            out.write("\t\t<edge id=\""+ source +"_"+target+"\" from=\""+source+"\" to=\""+target+"\"/>\n");
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
    private void saveVertices(XMLWriter out, int mode, Collection<GsNodeReducedData> vertices) throws IOException {
    	Iterator it;
    	if (vertices == null) {
    		vertices = getVertices();
    	}
    	GsVertexAttributesReader vReader = getVertexAttributeReader();
    	switch (mode) {
    		case 1:

                for (GsNodeReducedData vertex: vertices) {
                    String content = vertex.getContentString();
                    out.write("\t\t<node id=\""+vertex+"\">\n");
                    out.write("<attr name=\"content\"><string>"+content+"</string></attr>");
                    out.write(GsGinmlHelper.getShortNodeVS(vReader));
                    out.write("\t\t</node>\n");
                }
    			break;
			case 2:
                for (GsNodeReducedData vertex: vertices) {
                    vReader.setVertex(vertex);
                    String content = ((GsNodeReducedData)vertex).getContentString();
                    out.write("\t\t<node id=\""+vertex+"\">\n");
                    out.write("<attr name=\"content\"><string>"+content+"</string></attr>");
                    out.write(GsGinmlHelper.getFullNodeVS(vReader));
                    out.write("\t\t</node>\n");
                }
    			break;
    		default:
                for (GsNodeReducedData vertex: vertices) {
                    String content = vertex.getContentString();
                    out.write("\t\t<node id=\""+vertex+"\">\n");
                    out.write("<attr name=\"content\"><string>"+content+"</string></attr>");
                    out.write("</node>");
    	        }
        }
    }
	
//	/**
//	 * add a vertex to this graph.
//	 * @param vertex the vertex to add.
//	 */
	// TODO remove since it duplicate a method existing on AbstractGraphFrontEnd
//	public void addVertex(GsNodeReducedData vertex) {
//		graphManager.addVertex(vertex);
//	}
	/**
	 * add an edge to this graph.
	 * @param source source vertex of this edge.
	 * @param target target vertex of this edge.
	 */
	public void addEdge(GsNodeReducedData source, GsNodeReducedData target) {
		Edge<GsNodeReducedData> edge = new Edge<GsNodeReducedData>(source, target);
		addEdge( edge);
	}
	
    protected Graph getCopiedGraph() {
        return null;
    }
    protected List doMerge(Graph otherGraph) {
        return null;
    }
    public Graph getSubgraph(Collection vertex, Collection edges) {
        // no copy for reduced graphs
        return null;
    }

    protected void setCopiedGraph( Graph graph) {
    }

    /**
     * @return a map referencing all real nodes in the selected CC
     */
    public Map getSelectedMap() {
        Map map = new HashMap();
        Iterator it = graphManager.getSelectedVertexIterator();
        while (it.hasNext()) {
            GsNodeReducedData node = (GsNodeReducedData) it.next();
            Vector content = node.getContent();
            for (int i=0 ; i<content.size() ; i++) {
                map.put(content.get(i).toString(), null);
            }
        }
        return map;
    }

    
    // FIXME: move all this to the GUI Helper

	protected FileFilter doGetFileFilter() {
		return null;
	}

	public GsParameterPanel getEdgeAttributePanel() {
		return null;
	}

	public GsParameterPanel getVertexAttributePanel() {
		if (parameterPanel == null) {
			parameterPanel = new ReducedParameterPanel(this);
		}
		return parameterPanel;
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
		return optionPanel;
	}
}
