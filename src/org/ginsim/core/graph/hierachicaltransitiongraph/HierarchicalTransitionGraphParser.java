package org.ginsim.core.graph.hierachicaltransitiongraph;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.io.parser.GinmlHelper;
import org.ginsim.core.io.parser.GsXMLHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class HierarchicalTransitionGraphParser extends GsXMLHelper {
    
    private static final int POS_OUT = 0;
    private static final int POS_FILTERED = 50;
    private static final int POS_GRAPH_NOTES = 1;
    private static final int POS_GRAPH_NOTES_LINKLIST = 2;
    private static final int POS_VERTEX = 10;
    private static final int POS_VERTEX_VS = 11;
    private static final int POS_VERTEX_TYPE = 12;
    private static final int POS_VERTEX_TYPE_S = 13;
    private static final int POS_VERTEX_STATES = 14;
    private static final int POS_VERTEX_STATES_S = 15;
    private static final int POS_EDGE = 20;
    private static final int POS_EDGE_VS = 21;

    private int pos = POS_OUT;
    private HierarchicalTransitionGraph htg;
    private int vslevel = 0;
    
    private HierarchicalNode vertex = null;
    private NodeAttributesReader vareader = null;
    private EdgeAttributesReader ereader = null;
    private Annotation annotation = null;
    private Map map;
    private Map<String, HierarchicalNode> oldIdToNode = new HashMap<String, HierarchicalNode>();
	private byte[] childCount;
    
    /**
     * @param map
     * @param attributes
     * @param s_dtd
     * @param s_filename
     */
    public HierarchicalTransitionGraphParser(Map map, Attributes attributes, String s_dtd, String s_filename) throws GsException{
    	
    	this.htg = GraphManager.getInstance().getNewGraph( HierarchicalTransitionGraph.class, true);
    	this.map = map;
		vareader = htg.getNodeAttributeReader();
		ereader = htg.getEdgeAttributeReader();
		
		try {
			htg.setGraphName(attributes.getValue("id"));
		} catch (GsException e) {
			throw new GsException(GsException.GRAVITY_ERROR, "invalidGraphName");
		}
		try {
			String[] t_nodeOrder = attributes.getValue("nodeorder").split(" ");
			Vector<NodeInfo> nodeOrder = new Vector<NodeInfo>(t_nodeOrder.length);
			childCount = new byte[t_nodeOrder.length];
			for (int i=0 ; i<t_nodeOrder.length ; i++) {
				String[] args = t_nodeOrder[i].split(":");
			    nodeOrder.add( new NodeInfo( args[0]));
			    childCount[i] = (byte) (Byte.parseByte(args[1])+1);
			}
			htg.setNodeOrder(nodeOrder);
			htg.setChildsCount(childCount);
		} catch (NumberFormatException e) {
			throw new GsException( GsException.GRAVITY_ERROR, e);
		}
	}

    /**
     * 
     */
    public HierarchicalTransitionGraphParser() {
    }

    /**
     * create a new ReducedGraph from a file.
     * 
     * @param file the file to read.
     * @param map
     * @param graph the graph to fill with this data.
     */
    public void parse(File file, Map map, Graph graph) {
    	
    	this.htg = (HierarchicalTransitionGraph) graph;
    	this.map = map;
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
            case POS_VERTEX_TYPE_S:
                if (qName.equals("string")) {
                    pos = POS_VERTEX_TYPE;
                    vertex.setTypeFromString(curval);
                	curval = null;
                }
                break; // POS_VERTEX_TYPE_S
            case POS_VERTEX_TYPE:
                if (qName.equals("attr")) {
                    pos = POS_VERTEX;
                }
                break; // POS_VERTEX_TYPE
            case POS_VERTEX_STATES_S:
                if (qName.equals("string")) {
                    pos = POS_VERTEX_STATES;
                    vertex.parse(curval);
                	curval = null;
                }
                break; // POS_VERTEX_STATES_S
            case POS_VERTEX_STATES:
                if (qName.equals("attr")) {
                    pos = POS_VERTEX;
                }
                break; // POS_VERTEX_STATES
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
                    htg.setSaveMode(vslevel);
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
                        vertex = new HierarchicalNode(childCount);
                        oldIdToNode.put(id, vertex);
                        htg.addNode(vertex);
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("edge")) {
                    String s_from = attributes.getValue("from");
                    String s_to = attributes.getValue("to");
                    if (map == null || map.containsKey(s_from) && map.containsKey(s_to)) {
                        pos = POS_EDGE;
                        htg.addEdge(oldIdToNode.get(s_from), oldIdToNode.get(s_to));
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("graph")) {
            			if (!"hierarchicalTransitionGraph".equals(attributes.getValue("class"))) {
            				throw new SAXException( new GsException( GsException.GRAVITY_ERROR, "STR_HTG_NotHierarchicalTransitionGraph"));
            			}
            			try {
							htg.setGraphName( attributes.getValue("id"));
						} catch (GsException e) {
							throw new SAXException( new GsException(GsException.GRAVITY_ERROR, "STR_InvalidGraphName"));
						}
						try {
							String[] t_nodeOrder = attributes.getValue("nodeorder").split(" ");
							Vector<NodeInfo> nodeOrder = new Vector<NodeInfo>(t_nodeOrder.length);
							byte[] childCount = new byte[t_nodeOrder.length];
							for (int i=0 ; i<t_nodeOrder.length ; i++) {
								String[] args = t_nodeOrder[i].split(":");
							    nodeOrder.add( new NodeInfo( args[0]));
							    childCount[i] = (byte) (Byte.parseByte(args[1])+1);
							}
							htg.setNodeOrder(nodeOrder);
							htg.setChildsCount(childCount);
						} catch (NumberFormatException e) {
							throw new SAXException( new GsException( "STR_InvalidNodeOrder", e));
						}
						int mode = Integer.parseInt(attributes.getValue("iscompact"));
						if (mode != 1 && mode != 2){
							throw new SAXException( new GsException(GsException.GRAVITY_ERROR, "STR_HTG_InvalidModeHTGorSCC"));
						}
						else{
							htg.setMode(mode);
						}
                } else if (qName.equals("link")) {
                    htg.setAssociatedGraphID(attributes.getValue("xlink:href"));
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
                    annotation.addLink(attributes.getValue("xlink:href"), htg);
                }
                break; // POS_GRAPH_NOTES_LINKLIST

            case POS_VERTEX:
            	if (qName.equals("attr") && attributes.getValue("name").equals("type")) {
            		pos = POS_VERTEX_TYPE;
                } else if (qName.equals("attr") && attributes.getValue("name").equals("states")) {
                    pos = POS_VERTEX_STATES;
                } else if (vareader != null && qName.equals("nodevisualsetting")) {
                    pos = POS_VERTEX_VS;
                    vareader.setNode(vertex);
                }
                break; // POS_VERTEX

            case POS_VERTEX_TYPE:
                if (qName.equals("string")) {
                    pos = POS_VERTEX_TYPE_S;
                    curval = "";
                }
                break; // POS_VERTEX_TYPE
                
            case POS_VERTEX_STATES:
                if (qName.equals("string")) {
                    pos = POS_VERTEX_STATES_S;
                    curval = "";
                }
                break; // POS_VERTEX_STATES
                
            case POS_EDGE:
                if (qName.equals("edgevisualsetting")) {
                	pos = POS_EDGE_VS;
                }
                break; // POS_EDGE
                
            case POS_EDGE_VS:
            	
            	GinmlHelper.applyEdgeVisualSettings(null, ereader, vareader, qName, attributes);
                break; // POS_EDGE_VS
            case POS_VERTEX_VS:
            	vslevel = GinmlHelper.applyNodeVisualSettings(vareader, qName, attributes);
                break; // POS_VERTEX_VS
        }
    }

    public Graph getGraph() {
    	
        return htg;
    }
}
