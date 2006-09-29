package fr.univmrs.ibdm.GINsim.dynamicGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GraphChangeListener;
import fr.univmrs.ibdm.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.gui.GsActions;
import fr.univmrs.ibdm.GINsim.gui.GsEditModeDescriptor;
import fr.univmrs.ibdm.GINsim.gui.GsFileFilter;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraphOptionPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraphPropertiesPanel;
import fr.univmrs.ibdm.GINsim.xml.GsGinmlHelper;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;

/**
 * the dynamic (state transition) graph.
 */
public final class GsDynamicGraph extends GsGraph implements GsGraphListener, GraphChangeListener {

    private String dtdFile = GsGinmlHelper.DEFAULT_URL_DTD_FILE;
	private GsRegulatoryGraphOptionPanel optionPanel;
    
    protected Vector v_stables = new Vector();
	private GsRegulatoryGraphPropertiesPanel parameterPanel = null;
    private GsParameterPanel vertexPanel = null;
    private GsParameterPanel edgePanel;

	/**
	 * create a new GsDynamicGraph.
	 * @param regGraph
	 */
	public GsDynamicGraph(GsRegulatoryGraph regGraph) {
	    this((String)null);
	    nodeOrder = (Vector)regGraph.getNodeOrder().clone();
	    String s_nodeOrder = "";
	    for (int i=0 ; i<nodeOrder.size() ; i++) {
	        s_nodeOrder += nodeOrder.get(i)+" ";
	    }
	    v_stables.add(s_nodeOrder);
	}
	
	/**
	 */
	public GsDynamicGraph() {
	    this((String)null);
	    
	}
	/**
	 * @param filename
	 */
	public GsDynamicGraph(String filename) {
        super(GsDynamicGraphDescriptor.getInstance(), filename);
	}
	
	/**
	 * @param map
	 * @param file
	 */
	public GsDynamicGraph(Map map, File file) {
	    this(file.getAbsolutePath());
        GsDynamicParser parser = new GsDynamicParser();
        parser.parse(file, map, this);
		graphManager.ready();
	}

	/**
	 * @see fr.univmrs.ibdm.GINsim.graph.GsGraph#doInteractiveAddVertex(int)
	 * 
	 * not used for this kind of graph: it's not interactivly editable
	 */
	public Object doInteractiveAddVertex(int param) {
		return null;
	}

	/**
	 * @see fr.univmrs.ibdm.GINsim.graph.GsGraph#doInteractiveAddEdge(java.lang.Object, java.lang.Object, int)
	 * 
	 * not used for this kind of graph: it's not interactivly editable
	 */
	public Object doInteractiveAddEdge(Object source, Object target, int param) {
		return null;
	}

	/*
	 * @see fr.univmrs.ibdm.GINsim.graph.GsGraph#doSave(java.lang.String, int, boolean)
	 */
	protected void doSave(OutputStreamWriter os, int mode, boolean selectedOnly) throws GsException {
        try {
            GsXMLWriter out = new GsXMLWriter(os, dtdFile);
	  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
			out.write("\t<graph id=\"" + graphName + "\"");
			out.write(" class=\"dynamical\"");
			out.write(" nodeorder=\"" + stringNodeOrder() +"\"");
			out.write(">\n");
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
            throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_unableToSave")+": "+ e.getMessage());
        }
	}

	private String stringNodeOrder() {
		String s = "";
		for (int i=0 ; i<nodeOrder.size() ; i++) {
			s += nodeOrder.get(i)+" ";
		}
		if (s.length() > 0) {
			return s.substring(0, s.length()-1);
		}
		return s;
	}

    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveEdge(GsXMLWriter out, int mode, boolean selectedOnly) throws IOException {
        Iterator it;
        if (selectedOnly) {
        		it = graphManager.getSelectedEdgeIterator();
        } else {
        		it = graphManager.getEdgeIterator();
        }
        
        switch (mode) {
        	case 2:
		        while (it.hasNext()) {
		        	Object o_edge = it.next();
		        	if (o_edge instanceof GsDirectedEdge) {
		        	    eReader.setEdge(o_edge);
		        		GsDirectedEdge edge = (GsDirectedEdge)o_edge;
			            String source = edge.getSourceVertex().toString();
			            String target = edge.getTargetVertex().toString();
			            out.write("\t\t<edge id=\"s"+ source +"_s"+target+"\" from=\"s"+source+"\" to=\"s"+target+"\">\n");
			            out.write(GsGinmlHelper.getEdgeVS(eReader));
			            out.write("</edge>");
		        	}
		        }
        	    break;
	    	default:
		        while (it.hasNext()) {
		        	Object o_edge = it.next();
		        	if (o_edge instanceof GsDirectedEdge) {
		        		GsDirectedEdge edge = (GsDirectedEdge)o_edge;
			            String source = edge.getSourceVertex().toString();
			            String target = edge.getTargetVertex().toString();
			            out.write("\t\t<edge id=\"s"+ source +"_s"+target+"\" from=\"s"+source+"\" to=\"s"+target+"\"/>\n");
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
    private void saveNode(GsXMLWriter out, int mode, boolean selectedOnly) throws IOException {
    	Iterator it;
    	if (selectedOnly) {
    		it = graphManager.getSelectedVertexIterator();
    	} else {
    		it = graphManager.getVertexIterator();
    	}
        	switch (mode) {
	    		case 1:
	    			vReader = graphManager.getVertexAttributesReader();
	                while (it.hasNext()) {
	                    Object vertex = it.next();
	                    vReader.setVertex(vertex);
	                    String svs = GsGinmlHelper.getShortNodeVS(vReader);
	                    out.write("\t\t<node id=\""+((GsDynamicNode)vertex).getId()+"\">\n");
	                    out.write(svs);
	                    out.write("\t\t</node>\n");
	                }
	    			break;
				case 2:
					vReader = graphManager.getVertexAttributesReader();
	                while (it.hasNext()) {
	                    Object vertex = it.next();
	                    vReader.setVertex(vertex);
	                    String svs = GsGinmlHelper.getFullNodeVS(vReader);
	                    out.write("\t\t<node id=\""+((GsDynamicNode)vertex).getId()+"\">\n");
	                    out.write(svs);
	                    out.write("\t\t</node>\n");
	                }
	    			break;
        		default:
        	        while (it.hasNext()) {
        	            Object vertex = it.next();
        	            out.write("\t\t<node id=\""+((GsDynamicNode)vertex).getId()+"\"/>\n");
        	        }
        }
    }
	
	public GsParameterPanel getEdgeAttributePanel() {
	    if (edgePanel == null) {
	        edgePanel = new GsDynamicItemAttributePanel(this);
	    }
		return edgePanel;
	}

	public GsParameterPanel getVertexAttributePanel() {
	    if (vertexPanel == null) {
	        vertexPanel = new GsDynamicItemAttributePanel(this);
	    }
		return vertexPanel;
	}

	/**
	 * @see fr.univmrs.ibdm.GINsim.graph.GsGraph#changeVertexId(java.lang.Object, java.lang.String)
	 * 
	 * not used for this kind of graph: it's not interactivly editable
	 */
	public void changeVertexId(Object vertex, String newId) throws GsException {
	}

	/**
	 * @see fr.univmrs.ibdm.GINsim.graph.GsGraph#removeEdge(java.lang.Object)
	 * 
	 * not used for this kind of graph: it's not interactivly editable
	 */
	public void removeEdge(Object obj) {
	}

	/**
	 * add a vertex to this graph.
	 * @param state the state we want to add
	 * @return the new GsDynamicNode.
	 */
	public boolean addVertex(int[] state) {
		return graphManager.addVertex(new GsDynamicNode(state));
	}
	/**
	 * add a vertex to this graph.
	 * @param vertex
	 * @return the new GsDynamicNode.
	 */
	public boolean addVertex(GsDynamicNode vertex) {
		return graphManager.addVertex(vertex);
	}
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @param multiple 
	 * @return the new edge
	 */
	public Object addEdge(Object source, Object target, boolean multiple) {
		Object edge = graphManager.addEdge(source, target, null);
		if (multiple) {
			eReader.setEdge(edge);
			eReader.setDash(GsEdgeAttributesReader.DASH_PATTERN);
		}
		return edge;
	}

	protected FileFilter doGetFileFilter() {
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] {"ginml"}, "ginml files");
		return ffilter;
	}
	
	public String getAutoFileExtension() {
		return ".ginml";
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
	public Vector getSpecificLayout() {
		return GsDynamicGraphDescriptor.getLayout();
	}
	public Vector getSpecificExport() {
		return GsDynamicGraphDescriptor.getExport();
	}
    public Vector getSpecificAction() {
        return GsDynamicGraphDescriptor.getAction();
    }
    public Vector getSpecificObjectManager() {
        return GsDynamicGraphDescriptor.getObjectManager();
    }
    protected GsGraph getCopiedGraph() {
        return null;
    }
    protected Vector doMerge(GsGraph otherGraph) {
        
        // first check if this merge is allowed!
        if (!(otherGraph instanceof GsDynamicGraph)) {
            return null;
        }
        Vector v_order = ((GsDynamicGraph)otherGraph).nodeOrder;
        if (v_order.size() != nodeOrder.size()) {
            return null;
        }
        for (int i=0 ; i<nodeOrder.size() ; i++) {
            if (!nodeOrder.get(i).toString().equals(v_order.get(i).toString())) {
                return null;
            }
        }
        
        Vector ret = new Vector();
        Iterator it = otherGraph.getGraphManager().getVertexIterator();
        GsVertexAttributesReader cvreader = otherGraph.getGraphManager().getVertexAttributesReader();
        while (it.hasNext()) {
            GsDynamicNode vertex = (GsDynamicNode)it.next();
            addVertex(vertex);
            cvreader.setVertex(vertex);
            vReader.setVertex(vertex);
            vReader.copyFrom(cvreader);
            vReader.refresh();
            ret.add(vertex);
        }
        
        it = otherGraph.getGraphManager().getEdgeIterator();
        while (it.hasNext()) {
            GsDirectedEdge edge = (GsDirectedEdge)it.next();
            GsDynamicNode from = (GsDynamicNode)edge.getSourceVertex();
            GsDynamicNode to = (GsDynamicNode)edge.getTargetVertex();
            int c = 0;
            for ( int i=0 ; i<from.state.length ; i++) {
            	if (from.state[i] != to.state[i]) {
            		c++;
            	}
            }
            ret.add(addEdge(from, to, c>1));
        }
        
        return ret;
    }
    protected GsGraph doCopySelection(Vector vertex, Vector edges) {
        // no copy for state transition graphs
        return null;
    }
    protected void setCopiedGraph(GsGraph graph) {
    }

    /**
     * override getInfoPanel to show stable states.
     * @return the info panel for the "whattodo" frame
     */
    public JPanel getInfoPanel() {
        JPanel pinfo = new JPanel();
        // look for stable states
        Iterator it = graphManager.getVertexIterator();
        while (it.hasNext()) {
            GsDynamicNode node = (GsDynamicNode)it.next();
            if (node.isStable()) {
                v_stables.add(node);
            }
        }
        
        // just display the number of stable states here and a "show more" button
        if (v_stables.size() > 1) {
            pinfo.add(new JLabel("nb stables: "+(v_stables.size()-1)));
            JButton b_view = new JButton("view");
            // show all stables: quickly done but, it is "good enough" :)
            b_view.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFrame frame = new JFrame("stables");
                    frame.setSize(10*((String)v_stables.get(0)).length(), 40+25*v_stables.size());
                    JScrollPane scroll = new JScrollPane();
                    JList list = new JList(v_stables);
                    scroll.setViewportView(list);
                    frame.setContentPane(scroll);
                    frame.setVisible(true);
                }
            });
            pinfo.add(b_view);
        } else if (v_stables.size() > 1) {
            pinfo.add(new JLabel("no stable state."));
        }
        
        return pinfo;
    }

    public JPanel getGraphParameterPanel() {
		if (parameterPanel == null) {
			parameterPanel = new GsRegulatoryGraphPropertiesPanel(this);
		}
		return parameterPanel;
    }
    
    /**
     * look for the shortest path between two states.
     * @param source
     * @param target
     * @return the List describing the path or null if none is found
     */
    public List shortestPath(int[] source, int[] target) {
        GsDynamicNode n = new GsDynamicNode(source);
        GsDynamicNode n2 = new GsDynamicNode(target);
        if (graphManager.containsVertex(n) && graphManager.containsVertex(n2)) {
            return graphManager.getShortestPath(n, n2);    
        }
        return null;
    }
    
    protected boolean isAssociationValid(GsGraph graph) {
        if (graph == null) {
            return true;
        }
        if (!(graph instanceof GsRegulatoryGraph)) {
            return false;
        }
        return GsRegulatoryGraph.associationValid((GsRegulatoryGraph)graph, this);
    }

    public Vector getEditingModes() {
        Vector v_mode = new Vector();
        v_mode.add(new GsEditModeDescriptor("STR_addEdgePoint", "STR_addEdgePoint_descr", GsEnv.getIcon("custumizeedgerouting.gif"), GsActions.MODE_ADD_EDGE_POINT, 0));
        return v_mode;
    }
}
