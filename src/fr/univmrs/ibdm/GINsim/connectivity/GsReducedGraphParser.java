package fr.univmrs.ibdm.GINsim.connectivity;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.ibdm.GINsim.data.GsAnnotation;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.xml.GsGinmlHelper;
import fr.univmrs.ibdm.GINsim.xml.GsXMLHelper;

/**
 * parses a ginml regulatory graph.
 */
public class GsReducedGraphParser extends GsXMLHelper {
    
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
    private GsReducedGraph graph;
    private int vslevel = 0;
    
    private GsNodeReducedData vertex = null;
    private Vector v_content = null;
    private GsVertexAttributesReader vareader = null;
    private GsEdgeAttributesReader ereader = null;
    private GsAnnotation annotation = null;
    private Map map;
    
    /**
     * @param map
     * @param attributes
     * @param s_dtd
     * @param s_filename
     */
    public GsReducedGraphParser(Map map, Attributes attributes, String s_dtd, String s_filename) {
    	this.graph = new GsReducedGraph(s_filename);
    	this.map = map;
    	graph.setDTD(s_dtd);
		vareader = graph.getGraphManager().getVertexAttributesReader();
		ereader = graph.getGraphManager().getEdgeAttributesReader();
		
		try {
			graph.setGraphName(attributes.getValue("id"));
		} catch (GsException e) {
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, "invalidGraphName"), null);
		}
    }

    /**
     * 
     */
    public GsReducedGraphParser() {
    }

    /**
     * create a new GsReducedGraph from a file.
     * 
     * @param file the file to read.
     * @param map
     * @param graph the graph to fill with this data.
     */
    public void parse(File file, Map map, GsGraph graph) {
    	this.graph = (GsReducedGraph) graph;
    	this.map = map;
		vareader = graph.getGraphManager().getVertexAttributesReader();
		ereader = graph.getGraphManager().getEdgeAttributesReader();

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
			        pos = POS_OUT;
			    }
			    break; // POS_EDGE
			case POS_EDGE_VS:
			    if (qName.equals("edgevisualsetting")) {
			        pos = POS_EDGE;
			    }
			    break; // POS_EDGE_VS
            case POS_OUT:
                if (qName.equals("graph")) {
                    graph.setSaveFileName(graph.getSaveFileName(), vslevel);
                }
                break;
        }
        super.endElement(uri, localName, qName);
    }
    
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        switch(pos) {
        	case POS_OUT:
                if (qName.equals("node")) {
                    String id = attributes.getValue("id");
                    if (map == null || map.containsKey(id)) {
                        pos = POS_VERTEX;
                        v_content = new Vector();
                        vertex = new GsNodeReducedData(id, v_content);
                        graph.addVertex(vertex);
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("edge")) {
                    String s_from = attributes.getValue("from");
                    String s_to = attributes.getValue("to");
                    if (map == null || (map.containsKey(s_from) && map.containsKey(s_to))) {
                        pos = POS_EDGE;
                        graph.addEdge(new GsNodeReducedData(s_from), new GsNodeReducedData(s_to));
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("graph")) {
            			if (!("reduced".equals(attributes.getValue("class")))) {
            				throw new SAXException("not a reduced graph");
            			}
            			try {
							graph.setGraphName(attributes.getValue("id"));
							graph.setDTD(s_dtd);
						} catch (GsException e) {
							GsEnv.error(new GsException(GsException.GRAVITY_ERROR, "invalidGraphName"), null);
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
                    annotation.getLinkList().add(attributes.getValue("xlink:href"));
                }
                break; // POS_GRAPH_NOTES_LINKLIST

            case POS_VERTEX:
                if (qName.equals("attr") && "content".equals(attributes.getValue("name"))) {
                    pos = POS_VERTEX_CONTENT;
                } else if (vareader != null && qName.equals("nodevisualsetting")) {
                    pos = POS_VERTEX_VS;
                    vareader.setVertex(vertex);
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
                	pos = POS_EDGE_VS;
                }
                break; // POS_EDGE
                
            case POS_EDGE_VS:
            	GsGinmlHelper.applyEdgeVisualSettings(ereader, qName, attributes);
                break; // POS_EDGE_VS
            case POS_VERTEX_VS:
            	vslevel = GsGinmlHelper.applyNodeVisualSettings(vareader, qName, attributes);
                break; // POS_VERTEX_VS
        }
    }

    public GsGraph getGraph() {
        return graph;
    }

    public String getFallBackDTD() {
        return GsGinmlHelper.LOCAL_URL_DTD_FILE;
    }
}
