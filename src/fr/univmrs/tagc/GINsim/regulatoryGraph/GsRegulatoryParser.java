package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.io.File;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.ginsim.exception.GsException;
import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageAction;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.Graph;
import org.ginsim.gui.GUIManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsBooleanParser;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;
import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.GINsim.xml.GsXMLHelper;
import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.widgets.StackDialog;

/**
 * parses a ginml regulatory graph.
 */
public final class GsRegulatoryParser extends GsXMLHelper {

    private static final int POS_OUTSIDE = -1;  // outside of the graph (or in an ignored one)
    private static final int POS_OUT = 0;       // in the graph, outside of all vertices/edges
    private static final int POS_FILTERED = 50;
    private static final int POS_GRAPH_NOTES = 1;
    private static final int POS_GRAPH_NOTES_LINKLIST = 2;
    private static final int POS_VERTEX = 10;
    private static final int POS_VERTEX_VS = 11;
    private static final int POS_VERTEX_NOTES = 12;
    private static final int POS_VERTEX_NOTES_LINKLIST = 13;
    private static final int POS_EDGE = 20;
    private static final int POS_EDGE_VS = 21;
    private static final int POS_EDGE_NOTES = 22;
    private static final int POS_EDGE_NOTES_LINKLIST = 23;

    private int pos = POS_OUTSIDE;
    private GsRegulatoryGraph graph;

    private int vslevel = 0;

    private GsRegulatoryVertex vertex = null;
    private GsVertexAttributesReader vareader = null;
    private GsEdgeAttributesReader ereader = null;
    private GsRegulatoryEdge edge = null;
    private Annotation annotation = null;
    private Map m_edges = new HashMap();
    private Vector v_waitingInteractions = new Vector();
    private String s_nodeOrder;
    private Map map;

    private Hashtable values;
    private Vector v_function;

    /** some more stuff to check consistency of "old" models (with explicit and free maxvalue) */
    Map m_checkMaxValue;

    /**
     */
    public GsRegulatoryParser() {
    }

    /**
     * @param map
     * @param attributes
     * @param s_dtd
     * @param s_filename
     * @throws SAXException
     */
    public GsRegulatoryParser(Map map, Attributes attributes, String s_dtd,String file_name) throws GsException {
    	
        graph = new GsRegulatoryGraph( true);
        this.map = map;
		s_nodeOrder = attributes.getValue("nodeorder");
        if (s_nodeOrder == null) {
            throw new GsException( GsException.GRAVITY_ERROR, "missing nodeOrder");
        }
		try {
			graph.setGraphName(attributes.getValue("id"));
		} catch (GsException e) {
			throw new GsException(GsException.GRAVITY_ERROR, "invalidGraphName");
		}

		vareader = graph.getVertexAttributeReader();
		ereader = graph.getEdgeAttributeReader();
        pos = POS_OUT;
        values = new Hashtable();
    }

    /**
     * create a new regulatory graph from a file.
     *
     * @param file the file to read.
     * @param map
     * @param graph the graph to fill with this data.
     */
    public void parse(File file, Map map, Graph<?,?> graph) {
    	this.graph = (GsRegulatoryGraph) graph;
    	this.map = map;
		  vareader = graph.getVertexAttributeReader();
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
			case POS_VERTEX_NOTES:
			    if (qName.equals("annotation")) {
			        pos = POS_VERTEX;
			    } else if (qName.equals("comment")) {
			        annotation.setComment(curval);
			        curval = null;
			    }
			    break; // POS_VERTEX_NOTES
			case POS_EDGE_NOTES:
			    if (qName.equals("annotation")) {
			        pos = POS_EDGE;
			    } else if (qName.equals("comment")) {
			        annotation.setComment(curval);
			        curval = null;
			    }
			    break; // POS_EDGE_NOTES
			case POS_VERTEX_NOTES_LINKLIST:
			    if (qName.equals("linklist")) {
			        pos = POS_VERTEX_NOTES;
			    }
			    break; // POS_VERTEX_NOTES_LINKLIST
			case POS_EDGE_NOTES_LINKLIST:
			    if (qName.equals("linklist")) {
			        pos = POS_EDGE_NOTES;
			    }
			    break; // POS_EDGE_NOTES_LINKLIST
			case POS_OUT:
				if (qName.equals("graph")) {
					placeInteractions();
					placeNodeOrder();
                    graph.setSaveMode(vslevel);
                    if (!values.isEmpty()) {
                    	parseBooleanFunctions();
                    }
                    Iterator it = graph.getNodeOrder().iterator();
                    while (it.hasNext()) {
                    	GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
                    	vertex.getV_logicalParameters().cleanupDup();
                    }
				}
                pos = POS_OUTSIDE;
				break;
        }
        super.endElement(uri, localName, qName);
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        super.startElement(uri, localName, qName, attributes);

        switch(pos) {
            case POS_OUTSIDE:
                if (qName.equals("graph")) {
                    if (!"regulatory".equals(attributes.getValue("class"))) {
                        throw new SAXException("not a regulatory graph");
                    }
                    s_nodeOrder = attributes.getValue("nodeorder");
                    try {
                        graph.setGraphName(attributes.getValue("id"));
                    } catch (GsException e) {
                        GUIManager.error(new GsException(GsException.GRAVITY_ERROR, "invalidGraphName"), null);
                    }
                }
                pos = POS_OUT;
                break;
        	case POS_OUT:
                if (qName.equals("node")) {
                    String id = attributes.getValue("id");
                    if (map == null || map.containsKey(id)) {
                        pos = POS_VERTEX;
                        try {
                            byte maxvalue = (byte)Integer.parseInt(attributes.getValue("maxvalue"));
                            String name = attributes.getValue("name");
                            vertex = graph.addNewVertex(id, name, maxvalue);
                            vertex.getV_logicalParameters().setUpdateDup(false);
                        	String s_basal = attributes.getValue("basevalue");
                        	if (s_basal != null) {
                        		byte basevalue = (byte)Integer.parseInt(s_basal);
                        		if (basevalue != 0) {
                        			vertex.addLogicalParameter(new GsLogicalParameter(basevalue), true);
                        		}
                        	}
                        	String input = attributes.getValue("input");
                        	if (input != null) {
                        	    vertex.setInput(input.equalsIgnoreCase("true") || input.equals("1"), graph);
                        	}
                            values.put(vertex, new Hashtable());
                        } catch (NumberFormatException e) { throw new SAXException("malformed node's parameters"); }
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("edge")) {
                    String from = attributes.getValue("from");
                    String to = attributes.getValue("to");
                    if (map == null || map.containsKey(from) && map.containsKey(to)) {
                        pos = POS_EDGE;
                        try {
                            String id = attributes.getValue("id");
                            byte minvalue = (byte)Integer.parseInt(getAttributeValueWithDefault(attributes,"minvalue", "1"));
                            String smax = getAttributeValueWithDefault(attributes,"maxvalue", "-1");
                            byte maxvalue = -2;
                            String sign = attributes.getValue("sign");
                            try{
	                            edge = graph.addNewEdge(from, to, minvalue, sign);
	                            if (smax.startsWith("m")) {
	                            	maxvalue = -1;
	                            } else {
	                            	maxvalue = (byte)Integer.parseInt(smax);
	                            }
	                        	storeMaxValueForCheck(edge, maxvalue);
	                            m_edges.put(id, edge);
	                            edge.me.rescanSign(graph);
	                            ereader.setEdge(edge.me);
                            }
                            catch (GsException e) {
								Debugger.log( "Unable to create edge between nodes '" + from + "' and '" + to + "' : One of the node was not found in the graph");
							}
                        } catch (NumberFormatException e) { throw new SAXException("malformed interaction's parameters"); }
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("annotation")) {
	                	pos = POS_GRAPH_NOTES;
	                	annotation = graph.getAnnotation();
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
                if (vareader != null && qName.equals("nodevisualsetting")) {
                	pos = POS_VERTEX_VS;
                	vareader.setVertex(vertex);
                } else if (qName.equals("annotation")) {
                    pos = POS_VERTEX_NOTES;
                    annotation = vertex.getAnnotation();
                } else if (qName.equals("parameter")) {
                		v_waitingInteractions.add(vertex);
                		v_waitingInteractions.add(attributes.getValue("val"));
                		v_waitingInteractions.add(attributes.getValue("idActiveInteractions"));
                } else if (qName.equals("value")) {
                	v_function = new Vector();
                	((Hashtable)values.get(vertex)).put(attributes.getValue("val"), v_function);
                } else if (qName.equals("exp")) {
                	v_function.addElement(attributes.getValue("str"));
                }
                break; // POS_VERTEX

            case POS_EDGE:
                if (qName.equals("edgevisualsetting")) {
                	pos = POS_EDGE_VS;
                } else if (qName.equals("annotation")) {
                    pos = POS_EDGE_NOTES;
                    annotation = edge.me.getGsAnnotation(edge.index);
                }
                break; // POS_EDGE

            case POS_EDGE_VS:
            	GsGinmlHelper.applyEdgeVisualSettings(ereader, qName, attributes);
                break; // POS_EDGE_VS
            case POS_VERTEX_VS:
            	vslevel = GsGinmlHelper.applyNodeVisualSettings(vareader, qName, attributes);
                break; // POS_VERTEX_VS
            case POS_VERTEX_NOTES:
                if (qName.equals("linklist")) {
                    pos = POS_VERTEX_NOTES_LINKLIST;
			    } else if (qName.equals("comment")) {
			        curval = "";
                }
                break; // POS_VERTEX_NOTES
            case POS_EDGE_NOTES:
                if (qName.equals("linklist")) {
                    pos = POS_EDGE_NOTES_LINKLIST;
			    } else if (qName.equals("comment")) {
			        curval = "";
                }
                break; // POS_EDGE_NOTES
            case POS_VERTEX_NOTES_LINKLIST:
                if (qName.equals("link")) {
                    annotation.addLink(attributes.getValue("xlink:href"), graph);
                }
                break; // POS_VERTEX_NOTES
            case POS_EDGE_NOTES_LINKLIST:
                if (qName.equals("link")) {
                    annotation.addLink(attributes.getValue("xlink:href"), graph);
                }
                break; // POS_EDGE_NOTES
        }
    }

    private void storeMaxValueForCheck(GsRegulatoryEdge key, byte maxvalue) {
    	if (m_checkMaxValue == null) {
    		m_checkMaxValue = new HashMap();
    	}
    	m_checkMaxValue.put(key, new Integer(maxvalue));
	}

	/**
     * use the constructed v_waitingInteraction to add the accurate interaction to the nodes.
     */
    private void placeInteractions() {
    	// check the maxvalues of all interactions first
    	if (m_checkMaxValue != null) {
        	Map m = null;
    		Iterator it = m_checkMaxValue.entrySet().iterator();
    		while (it.hasNext()) {
    			Entry entry = (Entry)it.next();
    			byte m1 = ((GsRegulatoryEdge)entry.getKey()).getMax();
    			byte m2 = ((Integer)entry.getValue()).byteValue();
    			byte max = ((GsRegulatoryEdge)entry.getKey()).me.getSource().getMaxValue();
    			if ( m1 != m2 ) {
					if (m == null) {
    					m = new HashMap();
    				}
    				if (m1 == -1 && m2 == max || m2 == -1 && m1 == max) {
    					m.put(entry, "");
    				} else {
	    				m.put(entry, null);
    				}
    			}
    		}
    		if (m != null) {
    			graph.addNotificationMessage(new NotificationMessage(graph,
    					"inconsistency in some interactions",
    					new InteractionInconsistencyAction(graph),
    					m,
    					NotificationMessage.NOTIFICATION_WARNING_LONG));
    		}
    	}

    	for (int i=0 ; i<v_waitingInteractions.size() ; i+=3) {
    		GsRegulatoryVertex vertex = (GsRegulatoryVertex)v_waitingInteractions.get(i);
    		GsLogicalParameter gsi = new GsLogicalParameter(Integer.parseInt( (String)v_waitingInteractions.get(i+1)));
    		String s_interactions = (String) v_waitingInteractions.get(i+2);
    		if (s_interactions != null) {
	    		String[] t_interactions = s_interactions.split(" ");

	    		for (int j=0 ; j<t_interactions.length ; j++) {
	    			GsRegulatoryEdge e = (GsRegulatoryEdge) m_edges.get(t_interactions[j]);
	    			if (e == null) {
	    				// we have a problem
	    			} else {
	    				gsi.addEdge(e);
	    			}
	    		}
    		}
    		vertex.addLogicalParameter(gsi, true);
    	}
    }

    /**
     * install the correct nodeOrder in the graph: it should match the saved one.
     */
    private void placeNodeOrder() {
    		Vector v_order = new Vector();
    		String[] t_order = s_nodeOrder.split(" ");
    		boolean ok = true;
    		if (map == null) {
	    		for (int i=0 ; i<t_order.length ; i++) {
	    			GsRegulatoryVertex vertex = (GsRegulatoryVertex)graph.getVertexByName(t_order[i]);
	    			if (vertex == null) {
	    				ok = false;
	    				break;
	    			}
	    			v_order.add(vertex);
	    		}
    		} else {
	    		for (int i=0 ; i<t_order.length ; i++) {
	    		    if (map.containsKey(t_order[i])) {
	    		        GsRegulatoryVertex vertex = (GsRegulatoryVertex)graph.getVertexByName(t_order[i]);
	    		        if (vertex == null) {
	    		            ok = false;
	    		            break;
	    		        }
	    		        v_order.add(vertex);
	    		    }
	    		}
    		}
    		if (!ok || v_order.size() != graph.getVertexCount()) {
    			// error
    			Tools.error("incoherent nodeOrder, not restoring it");
    		} else {
    			graph.setNodeOrder(v_order);
    		}
    }

    private void parseBooleanFunctions() {
      Collection<GsRegulatoryMultiEdge> allowedEdges;
      GsRegulatoryVertex vertex;
      String value, exp;
      try {
        for (Enumeration enu_vertex = values.keys(); enu_vertex.hasMoreElements(); ) {
          vertex = (GsRegulatoryVertex)enu_vertex.nextElement();
          allowedEdges = graph.getIncomingEdges(vertex);
          if (allowedEdges.size() > 0) {
            for (Enumeration enu_values = ((Hashtable)values.get(vertex)).keys(); enu_values.hasMoreElements(); ) {
              value = (String)enu_values.nextElement();
              for (Enumeration enu_exp = ((Vector)((Hashtable)values.get(vertex)).get(value)).elements(); enu_exp.hasMoreElements(); ) {
                exp = (String)enu_exp.nextElement();
                addExpression(Byte.parseByte(value), vertex, exp);
              }
            }
            vertex.getInteractionsModel().parseFunctions();
            if (vertex.getMaxValue() + 1 == ((Hashtable)values.get(vertex)).size()) {
              ((GsTreeElement)vertex.getInteractionsModel().getRoot()).setProperty("add", new Boolean(false));
            }
          }
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    public void addExpression(byte val, GsRegulatoryVertex vertex, String exp) {
    	try {
        GsBooleanParser tbp = new GsBooleanParser( graph.getIncomingEdges(vertex));
        GsTreeInteractionsModel interactionList = vertex.getInteractionsModel();
        if (!tbp.compile(exp, graph, vertex)) {
        	InvalidFunctionNotificationAction a = new InvalidFunctionNotificationAction();
        	Vector o = new Vector();
        	o.addElement(new Short(val));
        	o.addElement(vertex);
        	o.addElement(exp);
          graph.addNotificationMessage( new NotificationMessage(graph, "Invalid formula : " + exp,
            a, o, NotificationMessage.NOTIFICATION_WARNING));
        }
        else {
          interactionList.addExpression(val, vertex, tbp);
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    public void addParam(byte val, GsRegulatoryVertex vertex, String par) throws Exception {
      GsTreeInteractionsModel interactionList = vertex.getInteractionsModel();
//      Set<GsDirectedEdge> l = interactionList.getGraph().getGraphManager().getIncomingEdges(vertex);
      GsTreeParam param = interactionList.addEmptyParameter(val, vertex);
      String[] t_interaction = par.split(" ");
      Vector v = new Vector();
      String srcString, indexString;
      GsRegulatoryMultiEdge o;
      for (int i = 0; i < t_interaction.length; i++) {
        if (t_interaction[i].lastIndexOf("_") != -1) {
          srcString = t_interaction[i].substring(0, t_interaction[i].lastIndexOf("_"));
          indexString = t_interaction[i].substring(t_interaction[i].lastIndexOf("_") + 1);
        }
        else {
          srcString = t_interaction[i];
          indexString = "1";
        }
        Collection<GsRegulatoryMultiEdge> edges;
        edges = interactionList.getGraph().getIncomingEdges(vertex);
        for (GsRegulatoryMultiEdge e: edges) {
          if (e.getSource().getId().equals(srcString)) {
        	  // FIXME: edge definition changed, consistency should be checked
                  // FIXED ... I hope
            v.add(e.getEdge(Integer.parseInt(indexString) - 1));
            break;
          }
        }
      }
      param.setEdgeIndexes(v);
    }
    public Graph getGraph() {
    	
        return graph;
    }
}

class InteractionInconsistencyAction implements NotificationMessageAction {

	private final GsRegulatoryGraph graph;
	
	public InteractionInconsistencyAction(GsRegulatoryGraph graph) {
		this.graph = graph;
	}
	
	public String[] getActionName() {
		String t[] = { "view" };
		return t;
	}

	public boolean perform( NotificationMessageHolder holder, Object data, int index) {
		StackDialog d = new InteractionInconsistencyDialog((Map)data,
				graph,
				"interactionInconststancy",
				200, 150);
		d.setVisible(true);
		return true;
	}

	public boolean timeout( NotificationMessageHolder holder, Object data) {
		return true;
	}
}

class InvalidFunctionNotificationAction implements NotificationMessageAction {
	
	public InvalidFunctionNotificationAction() {
		super();
	}
	
	public boolean timeout( NotificationMessageHolder graph, Object data) {
		
		return false;
	}

	public boolean perform( NotificationMessageHolder graph, Object data, int index) {
		
		Vector v = (Vector)data;
		byte value = ((Short)v.elementAt(0)).byteValue();
		GsRegulatoryVertex vertex = (GsRegulatoryVertex)v.elementAt(1);
		String exp = (String)v.elementAt(2);
		boolean ok = true;
		switch (index) {
		  case 0 :
		  	try {
		  	  GsTreeInteractionsModel interactionList = vertex.getInteractionsModel();
		  	  GsTreeExpression texp = interactionList.addEmptyExpression(value, vertex);
      	  texp.setText(exp);
          texp.setProperty("invalid", new Boolean("true"));
        }
		  	catch (Exception ex) {
		  		ex.printStackTrace();
		  		ok = false;
		  	}
			  break;
		  case 1 :
		  	break;
		}
		return ok;
	}
	public String[] getActionName() {
		String[] t = {"Keep function", "Discard function"};
		return t;
	}
}

class InteractionInconsistencyDialog extends StackDialog {
	private static final long serialVersionUID = 4607140440879983498L;

	GsRegulatoryGraph graph;
	Map m;
	JPanel panel = null;

	public InteractionInconsistencyDialog(Map m, Graph graph,
			String msg, int w, int h) {
		
		super( graph, msg, w, h);
		this.graph = (GsRegulatoryGraph)graph;
		this.m = m;

		setMainPanel(getMainPanel());
	}

	private JPanel getMainPanel() {
		if (panel == null) {
			panel = new JPanel();
			JTextArea txt = new JTextArea();
			String s1 = "";
			String s2 = "";
			Iterator it = m.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry)it.next();
				Entry e2 = (Entry)entry.getKey();
				GsRegulatoryEdge edge = (GsRegulatoryEdge)e2.getKey();
				byte oldmax = ((Integer)e2.getValue()).byteValue();
				if (entry.getValue() == null) {
					s1 += edge.getLongDetail(" ")+": max should be "+(oldmax == -1 ? "max" : ""+oldmax)+"\n";
				} else {
					s2 += edge.getLongDetail(" ")+ ": max was explicitely set to "+oldmax+"\n";
				}
			}

			if (s1 != "") {
				s1 = "potential problems:\n" + s1+"\n\n";
			}
			if (s2 != "") {
				s1 = s1 + "warnings only:\n"+s2;
			}
			txt.setText(s1);
			txt.setEditable(false);
			panel.add(txt);
		}
		return panel;
	}
	public void run() {
		// TODO: propose some automatic corrections
	}
}
