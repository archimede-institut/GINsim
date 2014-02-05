package org.ginsim.core.graph.reducedgraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.ginsim.common.application.GsException;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.io.parser.GinmlHelper;
import org.ginsim.core.io.parser.GsXMLHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * Load a SCC graph from GINML.
 *
 * @author Aurelien Naldi
 */
public class ReducedGraphParser extends GsXMLHelper {
    
    private static final int POS_OUT = 0;
    private static final int POS_FILTERED = 50;
    private static final int POS_GRAPH_NOTES = 1;
    private static final int POS_GRAPH_NOTES_LINKLIST = 2;
    private static final int POS_VERTEX = 10;
    private static final int POS_VERTEX_VS = 11;
    private static final int POS_VERTEX_CONTENT = 12;
    private static final int POS_VERTEX_CONTENT_S = 13;
    private static final int POS_EDGE = 20;
    private static final int POS_EDGE_VS = 21;

    private int pos = POS_OUT;
    private ReducedGraph graph;
    
    private NodeReducedData vertex = null;
    private Edge<NodeReducedData> edge = null;
    private List v_content = null;
	private StyleManager styleManager;
    private NodeAttributesReader vareader = null;
    private EdgeAttributesReader ereader = null;
    private Annotation annotation = null;
    private Set set;
    
    /**
     * @param set
     * @param attributes
     * @param s_dtd
     * @param s_filename
     */
    public ReducedGraphParser(Set<String> set, Attributes attributes, String s_dtd, String s_filename) throws GsException{
    	
    	this.graph = GraphManager.getInstance().getNewGraph( ReducedGraph.class, true);
    	this.set = set;
    	styleManager = graph.getStyleManager();
		vareader = graph.getNodeAttributeReader();
		ereader = graph.getEdgeAttributeReader();
		
		try {
			graph.setGraphName(attributes.getValue("id"));
		} catch (GsException e) {
			throw new GsException(GsException.GRAVITY_ERROR, "invalidGraphName");
		}
    }
    
    public ReducedGraphParser(Set<String> set, Attributes attributes, String s_dtd) throws GsException {
    	this.graph = GraphManager.getInstance().getNewGraph( ReducedGraph.class, true);
    	this.set = set;
    	styleManager = graph.getStyleManager();
		vareader = graph.getNodeAttributeReader();
		ereader = graph.getEdgeAttributeReader();
		
		try {
			graph.setGraphName(attributes.getValue("id"));
		} catch (GsException e) {
			throw new GsException(GsException.GRAVITY_ERROR, "invalidGraphName");
		}
    }


    /**
     * 
     */
    public ReducedGraphParser() {
    }

    /**
     * create a new ReducedGraph from a file.
     * 
     * @param file the file to read.
     * @param set
     * @param graph the graph to fill with this data.
     */
    public void parse(File file, Set<String> set, Graph graph)  throws GsException{
    	
    	this.graph = (ReducedGraph) graph;
    	this.set = set;
		vareader = graph.getNodeAttributeReader();
		ereader = graph.getEdgeAttributeReader();

		startParsing(file);
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        
        switch (pos) {
			case POS_FILTERED:
			    if (qName.equals("node") || qName.equals("edge")) {
			        pos = POS_OUT;
			    }
			    break;
			case POS_VERTEX:
			    if (qName.equals("node")) {
			        pos = POS_OUT;
			    }
			    break; // POS_VERTEX
			case POS_GRAPH_NOTES:
			    if (qName.equals("annotation")) {
			    		pos = POS_OUT;
			    } else if (qName.equals("comment")) {
			        annotation.setComment(curval);
			        curval = null;
			    }
			    break; // POS_GRAPH_NOTES
			case POS_GRAPH_NOTES_LINKLIST:
			    if (qName.equals("linklist")) {
			        pos = POS_GRAPH_NOTES;
			    }
			    break; // POS_GRAPH_NOTES_LINKLIST
            case POS_VERTEX_VS:
                if (qName.equals("nodevisualsetting")) {
                    pos = POS_VERTEX;
                }
                break; // POS_VERTEX_VS
            case POS_VERTEX_CONTENT_S:
                if (qName.equals("string")) {
                    pos = POS_VERTEX_CONTENT;
                    String[] t = curval.split(",");
                    for (int i=0 ; i<t.length ; i++) {
                        v_content.add(t[i]);
                    }
                    curval = null;
                }
                break; // POS_VERTEX_CONTENT_S
            case POS_VERTEX_CONTENT:
                if (qName.equals("attr")) {
                    pos = POS_VERTEX;
                }
                break; // POS_VERTEX_CONTENT
			case POS_EDGE:
			    if (qName.equals("edge")) {
			    	edge = null;
			        pos = POS_OUT;
			    }
			    break; // POS_EDGE
			case POS_EDGE_VS:
			    if (qName.equals("edgevisualsetting")) {
			        pos = POS_EDGE;
			    }
			    break; // POS_EDGE_VS
        }
        super.endElement(uri, localName, qName);
    }
    
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        switch(pos) {
        	case POS_OUT:
        		if (qName.equals("nodestyle") || qName.equals("edgestyle")) {
                	styleManager.parseStyle(qName, attributes);
        		} else if (qName.equals("node")) {
                    String id = attributes.getValue("id");
                    if (set == null || set.contains(id)) {
                        pos = POS_VERTEX;
                        v_content = new ArrayList();
                        vertex = new NodeReducedData(id, v_content);
                        graph.addNode(vertex);
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("edge")) {
                    String s_from = attributes.getValue("from");
                    String s_to = attributes.getValue("to");
                    if (set == null || set.contains(s_from) && set.contains(s_to)) {
                        pos = POS_EDGE;
                        edge = graph.addEdge(new NodeReducedData(s_from), new NodeReducedData(s_to));
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("graph")) {
            			if (!"reduced".equals(attributes.getValue("class"))) {
            				throw new SAXException( new GsException( GsException.GRAVITY_ERROR, "STR_NotReducedGraph"));
            			}
            			try {
							graph.setGraphName(attributes.getValue("id"));
						} catch (GsException e) {
							throw new SAXException( new GsException(GsException.GRAVITY_ERROR, "STR_InvalidGraphName"));
						}
                } else if (qName.equals("link")) {
                    graph.setAssociatedGraphID(attributes.getValue("xlink:href"));
                }
                break; // POS_OUT
            case POS_GRAPH_NOTES:
                if (qName.equals("linklist")) {
                    pos = POS_GRAPH_NOTES_LINKLIST;
			    } else if (qName.equals("comment")) {
			        curval = "";
			    }
            		break; // POS_GRAPH_NOTES
            case POS_GRAPH_NOTES_LINKLIST:
                if (qName.equals("link")) {
                    annotation.addLink(attributes.getValue("xlink:href"), graph);
                }
                break; // POS_GRAPH_NOTES_LINKLIST

            case POS_VERTEX:
                if (qName.equals("attr") && "content".equals(attributes.getValue("name"))) {
                    pos = POS_VERTEX_CONTENT;
                } else if (vareader != null && qName.equals("nodevisualsetting")) {
                	vareader.setNode(vertex);
                	if (GinmlHelper.loadNodeStyle(styleManager, vareader, attributes)) {
                		pos = POS_VERTEX_VS;
            		}
                }
                break; // POS_VERTEX

            case POS_VERTEX_CONTENT:
                if (qName.equals("string")) {
                    pos = POS_VERTEX_CONTENT_S;
                    curval = "";
                }
                break; // POS_VERTEX_CONTENT
                
            case POS_EDGE:
                if (qName.equals("edgevisualsetting")) {
                    ereader.setEdge(edge);
                	if (GinmlHelper.loadEdgeStyle(styleManager, ereader, attributes)) {
                		pos = POS_EDGE_VS;
                	}
                }
                break; // POS_EDGE
                
            case POS_EDGE_VS:
            	if (edge != null) {
            		ereader.setEdge(edge);
            		GinmlHelper.applyEdgeVisualSettings(edge, styleManager, ereader, vareader, qName, attributes);
            	}
                break; // POS_EDGE_VS
            case POS_VERTEX_VS:
            	GinmlHelper.applyNodeVisualSettings(vareader, styleManager, qName, attributes);
                break; // POS_VERTEX_VS
        }
    }

    public Graph getGraph() {
    	
        return graph;
    }
}
